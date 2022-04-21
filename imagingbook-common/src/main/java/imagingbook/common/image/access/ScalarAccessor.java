package imagingbook.common.image.access;

import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;
import imagingbook.common.interpolation.InterpolationMethod;
import imagingbook.common.interpolation.PixelInterpolator;

/**
 * The common (abstract) super-class for all image accessors to scalar-valued
 * images. It inherits all methods from {@link ImageAccessor} but adds the
 * methods {@link #getVal(int, int)}, {@link #getVal(double, double)} and
 * {@link #setVal(int, int, float)} for reading and writing scalar-valued pixel
 * data.
 */
public abstract class ScalarAccessor extends ImageAccessor {
	
	protected float defaultValue = 0f;

	/**
	 * Creates a new image accessor of general type {@link ScalarAccessor}. The conrete type
	 * of the returned instance depends on the specified image, i.e., {@link ByteAccessor}
	 * for {@link ByteProcessor}, {@link ShortAccessor} for {@link ShortProcessor},
	 * {@link FloatAccessor} for {@link FloatProcessor}.
	 * 
	 * @param ip  the image to be accessed
	 * @param obs the out-of-bounds strategy to be used (use {@code null} for
	 *            default settings)
	 * @param ipm the interpolation method to be used (use {@code null} for default
	 *            settings)
	 * @return a new image accessor
	 */
	public static ScalarAccessor create(ImageProcessor ip, OutOfBoundsStrategy obs, InterpolationMethod ipm) {
		if (ip instanceof ByteProcessor)
			return new ByteAccessor((ByteProcessor) ip, obs, ipm);
		if (ip instanceof ShortProcessor)
			return new ShortAccessor((ShortProcessor) ip, obs, ipm);
		if (ip instanceof FloatProcessor)
			return new FloatAccessor((FloatProcessor) ip, obs, ipm);
		throw new IllegalArgumentException(
				"cannot create " + ScalarAccessor.class.getSimpleName() + " for " + ip.getClass().getSimpleName());
	}
	
	@Override
	public int getDepth() {
		return 1;
	}
	
	@Override
	public ScalarAccessor getComponentAccessor(int k) {
		checkComponentIndex(k);
		return this;
	}

	/**
	 * Reads and returns the scalar pixel value for the given image position. The
	 * value returned for coordinates outside the image boundaries depends on the
	 * {@link OutOfBoundsStrategy} specified for this {@link ImageAccessor}.
	 * 
	 * @param u the x-coordinate
	 * @param v the y-coordinate
	 * @return the pixel value ({@code float})
	 */
	public abstract float getVal(int u, int v); // returns pixel value at integer position (u, v)
	
	@Override
	public float getVal(int u, int v, int k) {
		checkComponentIndex(k);
		return this.getVal(u, v);
	}
	
	@Override
	public float getVal(double x, double y, int k) {
		checkComponentIndex(k);
		return this.getVal(x, y);
	}

	/**
	 * Reads and returns the interpolated scalar pixel value for the given image
	 * position. The value returned for coordinates outside the image boundaries
	 * depends on the {@link OutOfBoundsStrategy} specified for this
	 * {@link ImageAccessor}.
	 * 
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @return the pixel value ({@code float})
	 */
	public float getVal(double x, double y) { // interpolating version
		return interpolator.getInterpolatedValue(this, x, y);
	}

	/**
	 * Writes a scalar pixel value to the given image position. What happens for
	 * coordinates outside the image boundaries depends on the
	 * {@link OutOfBoundsStrategy} specified for this {@link ImageAccessor}.
	 * 
	 * @param u   the x-coordinate
	 * @param v   the y-coordinate
	 * @param val the new pixel value ({@code float})
	 */
	public abstract void setVal(int u, int v, float val);
	
	public void setVal(int u, int v, int k, float val) {
		if (k == 0) {
			this.setVal(u, v, val);
		}
		else {
			throw new IllegalArgumentException("invalid component index " + k);
		}
	}

	protected final PixelInterpolator interpolator; // performs interpolation

	protected ScalarAccessor(ImageProcessor ip, OutOfBoundsStrategy obs, InterpolationMethod ipm) {
		super(ip, obs, ipm);
		this.interpolator = PixelInterpolator.create(interpolationMethod);
	}

	@Override
	public float[] getPix(int u, int v) {
		return new float[] { this.getVal(u, v) };
	}

	@Override
	public float[] getPix(double x, double y) {
		return new float[] { this.getVal(x, y) };
	}

	@Override
	public void setPix(int u, int v, float[] pix) {
		this.setVal(u, v, pix[0]);
	}
	
	// ---------------------------------------------------------------------
	
//	@Override
//	public void setDefaultValue(float val) {
//		this.defaultValue = val;
//	}
	
//	public void setDefaultValue(float[] vals) {
//		if (vals.length != 1) {
//			throw new IllegalArgumentException("default values must be of length " + 1);
//		}
//		this.setDefaultValue(vals[0]);
//	}
	
	@Override
	protected void checkComponentIndex(int k) {
		if (k != 0) {
			throw new IllegalArgumentException("invalid component index " + k);
		}
	}
}