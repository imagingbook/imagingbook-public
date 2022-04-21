package imagingbook.common.filter;

import ij.process.ImageProcessor;
import imagingbook.common.filter.examples.ExampleFilter3x3Scalar;
import imagingbook.common.filter.examples.ExampleFilter3x3Vector;
import imagingbook.common.filter.examples.FilterShowProgressExample;
import imagingbook.common.image.access.GridIndexer2D;
import imagingbook.common.image.access.OutOfBoundsStrategy;
import imagingbook.common.image.data.PixelPack;
import imagingbook.common.image.data.PixelPack.PixelSlice;
import imagingbook.common.util.progress.ProgressMonitor;
import imagingbook.common.util.progress.ProgressReporter;
import imagingbook.common.util.progress.ij.ProgressBarMonitor;

/**
 * <p>This is the (abstract) root class of the generic filter hierarchy.
 * Generic filters are designed to work with all types of
 * ImageJ images, i.e., sub-classes of {@link ImageProcessor}.
 * Filters implemented with this framework use a set of unified
 * image data structures, all based on {@code float} values
 * (see {@link PixelPack} and {@link PixelSlice}).
 * Input images are transparently copied from and back to these
 * float data, thus authors of new filters do not need to concern themselves
 * with the original pixel data types.
 * Behind the scenes, classes {@link GridIndexer2D} and {@link OutOfBoundsStrategy} are relevant for
 * pixel indexing and out-of-boundary coordinates handling.
 * </p>
 * <p>
 * Generic filters support multiple passes, the associated control 
 * structures are implemented by this class {@link GenericFilter}.
 * Most filters only require a single pass.
 * If more passes are required, the concrete (terminal) filter class
 * should override the method {@link #passesRequired()} to return the 
 * required number, which may change dynamically during the execution 
 * of the filter (e.g., to reach convergence of some sort).
 * Sub-classes provide definitions for scalar filters, i.e., filters
 * that operate independently on the different components of color images
 * (e.g., see {@link GenericFilterScalar}),
 * as opposed to vector vector filters that deal with color datea as
 * vectors (e.g., see {@link GenericFilterVector}).
 * In addition, both types provide sub-classes for X/Y-separable versions
 * (see {@link GenericFilterScalarSeparable} and {@link GenericFilterVectorSeparable}).
 * </p>
 * <p>
 * In the simplest (though frequent) case a concrete filter class only needs 
 * to implement a single method to specify the work to be done for calculating the
 * value of a single image pixel (e.g., see 
 * {@link GenericFilterVector#doPixel(PixelPack, int, int)} and
 * {@link GenericFilterScalar#doPixel(PixelSlice, int, int)}).
 * See {@link ExampleFilter3x3Scalar} and {@link ExampleFilter3x3Vector} for examples.
 * </p>
 * <p>
 * To <strong>apply</strong> as filter to a given image, the key method is 
 * {@link #applyTo(ImageProcessor)} (or {@link #applyTo(PixelPack)} if
 * the pixel data are already available as a {@link PixelPack}).
 * All filters operate destructively, i.e., by modifying the supplied image.
 * </p>
 * <p>
 * {@link GenericFilter}s support asynchronous <strong>progress reporting</strong> based on the
 * {@link ProgressMonitor} framework. Here is a usage example (from one of the sample plugins):</p>
 * <pre>
 * ImageProcessor ip = ... // any image
 * ...
 * GenericFilter filter = new TschumperleDericheFilter();
 * try (ProgressMonitor m = new ProgressBarMonitor(filter)) {
 *     filter.applyTo(ip);
 * }</pre>
 * <p>
 * During execution of the filter, the {@link ProgressBarMonitor} instance queries the filter
 * periodically (in a separate thread) for its progress status and updates ImageJ's progress
 * bar.
 * </p>
 * 
 * @author WB
 * @version 2012/01/12
 */
public abstract class GenericFilter implements ProgressReporter {
	
	private class AbortFilterException extends RuntimeException {
		private static final long serialVersionUID = 1L;
	}
	
	private PixelPack source = null;
	private PixelPack target = null;
	private int pass = -1;
	
	public GenericFilter() {
	}
	
	// --------------------------------------------------------------------------------

	/**
	 * Returns the current filter pass number, starting with 0.
	 * @return the filter pass number
	 */
	protected int getPass() {
		return pass;
	}
	
	/**
	 * Returns the width of the currently processed image.
	 * @return the width of the image
	 */
	protected int getWidth() {
		if (source == null) {
			throw new IllegalStateException("width is unknown unless filter is invoked");
		}
		return source.getWidth();
	}
	
	/**
	 * Returns the height of the currently processed image.
	 * @return the height of the image
	 */
	protected int getHeight() {
		if (source == null) {
			throw new IllegalStateException("height is unknown unless filter is invoked");
		}
		return source.getHeight();
	}
	
	/**
	 * Returns the depth (number of components) of the currently processed image.
	 * @return the depth of the image
	 */
	protected int getDepth() {
		if (source == null) {
			throw new IllegalStateException("depth is unknown unless filter is invoked");
		}
		return source.getDepth();
	}
	
	// -----------------------------------------------------------------------------------
	
	/**
	 * Applies this filter to the given {@link PixelPack} instance, which is modified.
	 * @param source the image to be filtered
	 */
	public void applyTo(PixelPack source) {
		this.source = source;
		this.target = new PixelPack(source);	// empty copy of same dimensions as source
		runFilter(source, target);
	}
	
	private void runFilter(PixelPack source, PixelPack target) {	// do we always want to copy back??
		initFilter(source, target);
		pass = 0;
		try {
			while (pass < passesRequired()) {	// TODO: check return value of passesRequired()!
				//IJ.log("****** starting pass " + pass);
				initPass(source, target);
				runPass(source, target);
				target.copyTo(this.source); // copy target back to sources
				pass++;
			}
		} catch (AbortFilterException e) {};
		// the filter's result is to be found in 'source'
		closeFilter();
		this.source = null;
		this.target = null;
	}
	
	// -----------------------------------------------------------------------------------
	
	/**
	 * Applies this filter to the given {@link ImageProcessor}, which is modified.
	 * {@link PixelPack#DefaultOutOfBoundsStrategy} is used to handle out-of-bounds coordinates.
	 * @param ip the image to be filtered
	 */
	public void applyTo(ImageProcessor ip) {
		applyTo(ip, PixelPack.DefaultOutOfBoundsStrategy);
	}
	
	/**
	 * Applies this filter to the given {@link ImageProcessor}, which is modified;
	 * the specified {@link OutOfBoundsStrategy} is used to handle out-of-bounds coordinates.
	 * @param ip the image to be filtered
	 * @param obs the out-of-bounds strategy to be used
	 */
	public void applyTo(ImageProcessor ip, OutOfBoundsStrategy obs) {
		PixelPack pp = new PixelPack(ip, obs);
		applyTo(pp);
		pp.copyToImageProcessor(ip);	// copy data back to ip
	}
	
	// -----------------------------------------------------------------------------------
	
	// called once before the filter operations starts.
	// 
	// that depend on the image size
	/**
	 * This method is called once at the start of the filter execution.
	 * It does nothing by default.
	 * Concrete filter classes should override this method, 
	 * e.g., for setting up temporary data structures.
	 * 
	 * @param source the image source data
	 * @param target the image target data
	 */
	protected void initFilter(PixelPack source, PixelPack target) {
		// does nothing by default
	}
	
	/**
	 * This method is called once at the start of each filter pass.
	 * It does nothing by default.
	 * Concrete filter classes should override this method if needed.
	 * @param source the image source data
	 * @param target the image target data
	 */
	protected void initPass(PixelPack source, PixelPack target) {
		// does nothing by default
	}
	
	// concrete sub-classes should override to purge 
	// specific data structures if needed
	/**
	 * This method is called once when the filter terminates.
	 * It does nothing by default.
	 * Concrete filter classes should override this method if needed.
	 */
	protected void closeFilter() {
	}
	
	// limits the necessary number of passes, which may not be known at initialization.
	// multi-pass filters must override this method.
	/**
	 * Returns the necessary number of passes, which may change during execution 
	 * of the filter. The value 1 is returned by default.
	 * Multi-pass filters must override this method.
	 * The filter terminates as soon as the requested number of passes is reached.
	 * This this method can be used to terminate filter execution once some desired
	 * state has been reached (e.g., convergence).
	 * See also {@link #getPass()}.
	 * 
	 * @return the required number of filter passes
	 */
	protected int passesRequired() {
		return 1;	// do exactly 1 pass
	}
	
	/**
	 * This method can be called to abort the filter execution prematurely,
	 * e.g. for debugging.
	 */
	protected final void abort() {
		throw new AbortFilterException();
	}

	/**
	 * This method performs one pass of the filter, it must be implemented by a sub-class.
	 * There is usually no need for a custom filter class to override this method.
	 * 
	 * @param source the image source data
	 * @param target the image target data
	 */
	protected abstract void runPass(PixelPack source, PixelPack target);
	
	// progress reporting ----------------------------------------------------------------

	@Override
	public final double getProgress() {
		double fp = reportProgress();
		return this.reportProgress(fp);
	}
	
	// 
	/**
	 * This method is called asynchonously and may be overridden by the terminal filter class 
	 * if it does extensive work that should be monitored. 
	 * In this case the method must return a value between 0 and 1, reflecting the degree of completion
	 * (only of the final class's subtask) at the time of invocation.
	 * Normally this is not necessary, since the intermediate classes
	 * return their own progress state (for the less granular tasks) anyways.
	 * The default implementations returns 0 (progress).
	 * See {@link FilterShowProgressExample} for an example.
	 * @return the degree of completion (0,...,1)
	 */
	protected double reportProgress() {
		return 0;
	}
	
	/** This method is used for internal progress reporting through the class hierarchy. It should
	 * not be overridden or modified.
	 * 
	 * @param subProgress the current progress reported by the immediate sub-task (0,...,1)
	 * @return the cumulative progress (0,...,1)
	 */
	protected double reportProgress(double subProgress) {
		int pass = Math.max(getPass(), 0);	// getPass() returns -1 if loop is not started 
		double localProgress = (pass + subProgress) / passesRequired();
		return localProgress; // this is the final value returned to the monitor
	}
}
