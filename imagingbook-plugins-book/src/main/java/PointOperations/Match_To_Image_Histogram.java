/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
package PointOperations;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.common.histogram.HistogramMatcher;
import imagingbook.common.histogram.HistogramPlot;
import imagingbook.common.histogram.HistogramUtils;

/**
 * Adapts image intensities to match the histogram of another image.
 * 
 * @author WB
 * 
 * @see HistogramMatcher
 * @see HistogramPlot
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
		
		HistogramMatcher m = new HistogramMatcher();
		int[] F = m.matchHistograms(hA, hB);
		
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
		// TODO: use IjUtils.getOpenImages (as in Linear_Blending)
		int[] windowList = WindowManager.getIDList();
		if(windowList==null){
			IJ.noImage();
			return false;
		}
		// get image titles
		String[] windowTitles = new String[windowList.length];
		for (int i = 0; i < windowList.length; i++) {
			ImagePlus imp = WindowManager.getImage(windowList[i]);
			if (imp != null)
				windowTitles[i] = imp.getShortTitle();
			else
				windowTitles[i] = "untitled";
		}
		// create dialog and show
		GenericDialog gd = new GenericDialog("Select Reference Image");
		gd.addChoice("Reference Image:", windowTitles, windowTitles[0]);
		
		gd.showDialog(); 
		if (gd.wasCanceled()) 
			return false;
		
		int img2Index = gd.getNextChoiceIndex();
		imB = WindowManager.getImage(windowList[img2Index]);
		return true;
	}

}

