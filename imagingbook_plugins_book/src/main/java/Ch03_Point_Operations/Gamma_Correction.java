/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package Ch03_Point_Operations;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.common.ij.DialogUtils;
import imagingbook.core.plugin.JavaDocHelp;
import imagingbook.sampleimages.GeneralSampleImage;

import static imagingbook.common.ij.IjUtils.noCurrentImage;

/**
 * <p>
 * This ImageJ performs simple gamma correction (with fixed &gamma; = 2.8) on
 * a 8-bit grayscale image, which is modified.
 * See Sec. 3.7 (Prog. 3.4) of [1] for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>,
 * 3rd ed, Springer (2022).
 * </p>
 * @author WB
 *
 */
public class Gamma_Correction implements PlugInFilter, JavaDocHelp {
	
	private static double GAMMA = 2.8;

	/** Constructor, asks to open a predefined sample image if no other image is currently open. */
	public Gamma_Correction() {
		if (noCurrentImage()) {
			DialogUtils.askForSampleImage(GeneralSampleImage.IrishManor);
		}
	}

	@Override
	public int setup(String arg, ImagePlus im) {
		return DOES_8G;
	}
    
	@Override
	public void run(ImageProcessor ip) {
		if (!runDialog()) {
			return;
		}

		// works for 8-bit images only 
	    int K = 256;
	    int aMax = K - 1;
	
	    // create and fill the lookup table:
	    int[] Fgc = new int[K];                
	
	    for (int a = 0; a < K; a++) {
	        double aa = (double) a / aMax;		// scale to [0,1]
	        double bb = Math.pow(aa, GAMMA);	// power function
	        // scale back to [0,255]
	        int b = (int) Math.round(bb * aMax); 
	        Fgc[a] = b;  
	    }
	    
	    ip.applyTable(Fgc);  // modify the image
	}

	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());

		gd.addNumericField("gamma (\u03B3 > 0)", GAMMA, 2);

		gd.showDialog();
		if (gd.wasCanceled())
			return false;

		GAMMA = gd.getNextNumber();
		return true;
	}
}
