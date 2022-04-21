/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package ImageMatching;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.matching.CorrCoeffMatcher;


/** 
 * Template matching plugin based on the local correlation coefficient.
 * TODO: Slow because it uses getPixelValue() for pixel access.
 * 
 * @author WB
 */
public class CorrelCoeff_Matching_Demo implements PlugInFilter {

	private ImagePlus imgR = null;

    public int setup(String arg, ImagePlus im) {
        return DOES_8G + DOES_32 + NO_CHANGES;
    }
    
    //--------------------------------------------------------------------

    public void run(ImageProcessor ipI) {
		if (!showDialog() || imgR == null) {
			return;
		}
		
    	FloatProcessor I = (FloatProcessor) ipI.convertToFloatProcessor();	// search image
		CorrCoeffMatcher matcher = new CorrCoeffMatcher(I);
		
		ImageProcessor ipR = imgR.getProcessor();
		FloatProcessor R = (FloatProcessor) ipR.convertToFloatProcessor();	// reference image
		
		float[][] C = matcher.getMatch(R);
		FloatProcessor Cp = new FloatProcessor(C);
		(new ImagePlus("Correlation Coefficient", Cp)).show();
    }
 
    //--------------------------------------------------------------------

    private boolean showDialog() {
		int[] wList = WindowManager.getIDList();
		if (wList==null) {
			IJ.error("No windows are open.");
			return false;
		}

		String[] titles = new String[wList.length];
		for (int i = 0; i < wList.length; i++) {
			ImagePlus imp = WindowManager.getImage(wList[i]);
			if (imp!=null)
				titles[i] = imp.getTitle();
			else
				titles[i] = "";
		}
		GenericDialog gd = new GenericDialog("Select Reference Image");
		String title2;
		if (imgR == null)
			title2 = titles[0];
		else
			title2 = imgR.getTitle();
		gd.addChoice("Template:", titles, title2);
		gd.showDialog();
		if (gd.wasCanceled())
			return false;
		else {
			int index2 = gd.getNextChoiceIndex();
			title2 = titles[index2];
			imgR = WindowManager.getImage(wList[index2]);
			return true;
		}
    }
}

