/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package Ch03_PointOperations;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.common.histogram.HistogramPlot;
import imagingbook.common.histogram.HistogramUtils;
import imagingbook.common.ij.IjUtils;

/**
 * <p>
 * ImageJ plugin, adapts image intensities to match the histogram of another image
 * selected by the user.
 * See Sec. 3.6.4 (Fig. 3.16) of [1] for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>,
 * 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * 
 * @see HistogramPlot
 * @see HistogramUtils
 *
 */
public class Match_To_Image_Histogram implements PlugInFilter { 
	
	ImagePlus imB;	// reference image (selected interactively)
	
	@Override
	public int setup(String arg, ImagePlus imA) {
		return DOES_8G;
	}
	
	@Override
	public void run(ImageProcessor ipA) {
		if (!runDialog() || imB == null) // select the reference image
			return;
		
		ImageProcessor ipB = imB.getProcessor();
		
		// get histograms of both images
		int[] hA = ipA.getHistogram();
		int[] hB = ipB.getHistogram();
		(new HistogramPlot(hA, "Histogram A")).show();
		(new HistogramPlot(hB, "Histogram B")).show();
		(new HistogramPlot(HistogramUtils.cdf(hA), "Cumulative Histogram A")).show();
		(new HistogramPlot(HistogramUtils.cdf(hB), "Cumulative Histogram B")).show();
		
		int[] F = HistogramUtils.matchHistograms(hA, hB);
		
//		for (int i = 0; i < F.length; i++) {
//			IJ.log(i + " -> " + F[i]);
//		}
		
		ipA.applyTable(F);
		int[] hAm = ipA.getHistogram();
		(new HistogramPlot(hAm, "Histogram A (mod)")).show();
		(new HistogramPlot(HistogramUtils.cdf(hAm), "Cumulative Histogram A (mod)")).show();
	}

	boolean runDialog() {
		// get list of open images
		
		ImagePlus[] images = IjUtils.getOpenImages(true);
		String[] titles = new String[images.length];
		for (int i = 0; i < images.length; i++) {
			titles[i] = images[i].getShortTitle();
		}
		
		// create dialog and show
		GenericDialog gd = new GenericDialog("Select Reference Image");
		gd.addChoice("Reference Image:", titles, titles[0]);
		
		gd.showDialog(); 
		if (gd.wasCanceled()) 
			return false;
		
		imB = images[gd.getNextChoiceIndex()];
		return true;
	}

}

