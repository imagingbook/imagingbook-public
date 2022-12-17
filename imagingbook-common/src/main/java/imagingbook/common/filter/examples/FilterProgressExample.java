/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.filter.examples;

import imagingbook.common.filter.generic.GenericFilter;
import imagingbook.common.filter.generic.GenericFilterVector;
import imagingbook.common.image.PixelPack;
import imagingbook.common.util.progress.ConsoleProgressMonitor;
import imagingbook.common.util.progress.ProgressMonitor;
import imagingbook.common.util.progress.ProgressMonitorExample;

/**
 *<p>
 * This class defines a custom filter implementing {@link GenericFilterVector} 
 * that reports its progress, which is queried by a {@link ProgressMonitor}
 * in the {@link #main(String[])} method.
 * </p>
 * <p>
 * In this example the work required for processing each SINGLE pixel is
 * assumed to be substantial and progress reporting thus goes all the way
 * down to the single pixel level.
 * Method {@link #getProgress()} in this case reports how much of the work for
 * the current pixel is already completed. 
 * Note that this is for demonstration only and such fine granularity is usually
 * not needed.
 * Typically method {@link #getProgress()} needs not to be overridden or
 * may be defined to return always 1.
 * </p>
 * <p>
 * The OVERALL filter progress (which depends on the image size and the 
 * number of required filter passes) is calculated automatically by the
 * associated super classes.
 * </p>
 * @author WB
 *
 * @see ProgressMonitor
 * @see ProgressMonitorExample
 * @see ConsoleProgressMonitor
 */
public class FilterProgressExample extends GenericFilterVector {

	// variables for progress reporting
	private int maxCnt = 5000;
	private int cnt;
	
	@Override
	protected void initFilter(PixelPack source, PixelPack target) {	
	}
	
	@Override
	protected void initPass(PixelPack source, PixelPack target) {
	}
	
	@Override
	protected int passesRequired() {
		return 3;	// needs 3 passes
	}
	
	@Override
	protected float[] doPixel(PixelPack source, int u, int v) {
		float[] dummy = new float[3];
		float sum = 0;
		cnt = 0;
		for (int i = 0; i < maxCnt; i++) {
			this.cnt = i;
			sum += (float) Math.sqrt(i); // some dummy work
		}
		cnt = 0;
		dummy[0] = sum;
		return dummy;
	}
	
	// -------------------------------------------------------------
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Note: This method returns the filter's progress (degree of completion) for 
	 * the current pixel (see {@link #doPixel(PixelPack, int, int)}).
	 * Typically this fine granularity is not relevant and
	 * the method should either return 1 or not be overridden at all (removed).
	 * </p>
	 */
	@Override
	protected double reportProgress() {
		return (double) this.cnt / this.maxCnt;
	}
	
	// -------------------------------------------------------------
	
	/**
	 * Main method for demonstration only.
	 * @param args ignored
	 */
	public static void main(String[] args) {
		PixelPack pp = new PixelPack(400, 300, 3, null);		// dummy data for the filter to process
		GenericFilter filter = new FilterProgressExample();
		try (ProgressMonitor monitor = new ConsoleProgressMonitor(filter)) { // starts monitoring immediately
			monitor.setWaitTime(500);	
			filter.applyTo(pp);
		}	// closed automatically
	}

}
