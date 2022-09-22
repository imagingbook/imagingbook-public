/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package Ch03_PointOperations;

import static imagingbook.common.math.Arithmetic.sqr;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.common.histogram.HistogramMatcher;
import imagingbook.common.histogram.HistogramPlot;
import imagingbook.common.histogram.HistogramUtils;

/**
 * <p>
 * ImageJ plugin, adapts image intensities to match a Gaussian distribution
 * with specified parameters &mu;, &sigma; ({@link #Mean}, {@link #StdDev}).
 * See Sec. 3.6.4 (Fig. 3.15) of [1] for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing - An Algorithmic Approach</em>,
 * 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * 
 * @see HistogramUtils
 * @see HistogramPlot
 *
 */
public class Match_To_Gaussian_Histogram implements PlugInFilter {
	
	public static double Mean = 128;
	public static double StdDev = 128;
	
	@Override
	public int setup(String arg0, ImagePlus im) {
		return DOES_8G;
	}
	
	@Override
	public void run(ImageProcessor ip) {
		
		// get histograms
		int[] hi = ip.getHistogram();
		int[] hG = makeGaussianHistogram(Mean, StdDev);
				
		(new HistogramPlot(hi, "Image Histogram")).show();
		(new HistogramPlot(hG, "Gaussian Histogram")).show();
		
		double[] nhG = HistogramUtils.normalizeMax(hG);
		(new HistogramPlot(nhG, "Gaussian Hist. normalized")).show();
		
		double[] chG = HistogramUtils.cdf(hG);
    	(new HistogramPlot(chG, "Gaussian Hist. cumulative")).show();
		
    	int[] F = HistogramUtils.matchHistograms(hi, hG);
//		HistogramMatcher m = new HistogramMatcher();	
//		int[] F = m.matchHistograms(hi, hG);
		
//		for (int i = 0; i < F.length; i++) {
//			IJ.log(i + " -> " + F[i]);
//		}
		
		ip.applyTable(F);
		int[] hAm = ip.getHistogram();
		(new HistogramPlot(hAm, "Histogram A (mod)")).show();
		(new HistogramPlot(HistogramUtils.cdf(hAm), "Cumulative Histogram A (mod)")).show();
	}
	
	private int[] makeGaussianHistogram (double mean, double sigma) {
		int[] h = new int[256];
		double sigma2 = 2 * sqr(sigma);
		for (int i = 0; i < h.length; i++) {
			double x = mean - i;
			double g = Math.exp(-sqr(x) / sigma2) / sigma;
			h[i] = (int) Math.round(10000 * g);
		}
		return h;
	}

}

