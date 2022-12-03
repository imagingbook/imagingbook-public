/*
 *  This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge.
 * All rights reserved. Visit https://imagingbook.com for additional details.
 */
package Ch03_PointOperations;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.common.histogram.HistogramPlot;
import imagingbook.common.histogram.HistogramUtils;
import imagingbook.common.histogram.PiecewiseLinearCdf;

/**
 * <p>
 * Adapts image intensities to match a reference histogram that is piecewise-linear.
 * See Sec. 3.6.4 (Fig. 3.14) of [1] for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>,
 * 3rd ed, Springer (2022).
 * </p>
 * @author WB
 * 
 * @see HistogramUtils
 * @see HistogramPlot
 * @see PiecewiseLinearCdf
 */
public class Match_To_Piecewise_Linear_Histogram implements PlugInFilter { 
	
	static int[]    a = {28, 75, 150, 210};			// a_k (brightness values)
	static double[] P = {.05, .25, .75, .95};		// P_k (cum. probabilities)
	
	@Override
	public int setup(String arg0, ImagePlus im) {
		return DOES_8G;
	}
	
	@Override
	public void run(ImageProcessor ipA) {
		// get histogram of original image
		int[] hA = ipA.getHistogram();
		
		(new HistogramPlot(hA, "Histogram A")).show();
		(new HistogramPlot(HistogramUtils.cdf(hA), "Cumulative Histogram A")).show();
		
		// -------------------------
		PiecewiseLinearCdf cdf = new PiecewiseLinearCdf(256, a, P);
		// -------------------------
		
		double[] nhB = cdf.getPdf();
		nhB = HistogramUtils.normalizeMax(nhB);
		(new HistogramPlot(nhB, "Piecewise Linear")).show();
		(new HistogramPlot(cdf, "Piecewise Linear Cumulative")).show();
		
		int[] F = HistogramUtils.matchHistograms(hA, cdf);
		
//		for (int i = 0; i < F.length; i++) {
//			IJ.log(i + " -> " + F[i]);
//		}
		
		ipA.applyTable(F);
		
		int[] hAm = ipA.getHistogram();
		(new HistogramPlot(hAm, "Histogram A (mod)")).show();
		(new HistogramPlot(HistogramUtils.cdf(hAm), "Cumulative Histogram A (mod)")).show();
	}

}

