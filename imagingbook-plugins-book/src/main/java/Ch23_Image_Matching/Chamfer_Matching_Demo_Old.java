/*
 *  This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge.
 * All rights reserved. Visit https://imagingbook.com for additional details.
 */
package Ch23_Image_Matching;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.ij.IjUtils;
import imagingbook.common.image.matching.ChamferMatcher;
import imagingbook.common.image.matching.DistanceNorm;


/**
 * This ImageJ plugin demonstrates the use of the {@link ChamferMatcher} class. The active (search) image is assumed to
 * be binary (not checked). The reference (template) image is selected interactively by the user.
 *
 * @author WB
 * @version 2022/12/13
 */
public class Chamfer_Matching_Demo_Old implements PlugInFilter {
	
	private ImagePlus imgI;		// the search image
	private ImagePlus imgR;		// the reference image (smaller)

	@Override
    public int setup(String arg, ImagePlus imp) {
    	this.imgI = imp;
        return DOES_8G + NO_CHANGES;
    }

	@Override
    public void run(ImageProcessor ipI) {
		if (!runDialog() || imgR == null) {
			return;
		}
		
		ByteProcessor I = ipI.convertToByteProcessor();						// search image I
    	ByteProcessor R = imgR.getProcessor().convertToByteProcessor(); 	// reference image R
    	
    	// TODO: better initialize matcher with reference image R?
    	ChamferMatcher matcher = new ChamferMatcher(I, DistanceNorm.L2);
    	float[][] Qa = matcher.getMatch(R);
    	
    	FloatProcessor Q = new FloatProcessor(Qa);
		(new ImagePlus("Match of " + imgI.getTitle(), Q)).show();
    }
 
    private boolean runDialog() {
    	ImagePlus[] openImages = IjUtils.getOpenImages(true, imgI);
		if (openImages.length == 0) {
			IJ.error("No other images are open.");
			return false;
		}
		
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		String[] titles = IjUtils.getImageShortTitles(openImages);
		gd.addChoice("Reference image:", titles, titles[0]); // TODO: consider gd.addImageChoice()
		
		gd.showDialog();
		if (gd.wasCanceled())
			return false;
		
		imgR = openImages[gd.getNextChoiceIndex()];
		return true;
    }
		
}