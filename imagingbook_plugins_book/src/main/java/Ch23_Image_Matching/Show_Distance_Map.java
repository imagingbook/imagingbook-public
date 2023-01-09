/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package Ch23_Image_Matching;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.ij.DialogUtils;
import imagingbook.common.ij.IjUtils;
import imagingbook.common.image.matching.DistanceTransform;
import imagingbook.core.plugin.JavaDocHelp;
import imagingbook.sampleimages.GeneralSampleImage;

/**
 * <p>
 * Demonstrates the use of the {@link DistanceTransform} class. See Sec. 23.2.3 of [1] for details. Optionally opens a
 * sample image if no image is currently open. The active image is assumed to be binary (not checked).
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2022/12/13
 * @see DistanceTransform
 */
public class Show_Distance_Map implements PlugInFilter, JavaDocHelp {
	
	private static DistanceTransform.DistanceType distanceNorm = DistanceTransform.DistanceType.L1;
	
	private ImagePlus im;

	/**
	 * Constructor, asks to open a predefined sample image if no other image is currently open.
	 */
	public Show_Distance_Map() {
		if (IjUtils.noCurrentImage()) {
			DialogUtils.askForSampleImage(GeneralSampleImage.RhinoSmallInv);
		}
	}

	@Override
    public int setup(String arg, ImagePlus im) {
    	this.im = im;
        return DOES_8G + NO_CHANGES;
    }

	@Override
    public void run(ImageProcessor ip) {
		IJ.log("run");
    	if (!runDialog()) {
			return;
    	}
    	
    	DistanceTransform dt = new DistanceTransform(ip, distanceNorm);
		FloatProcessor dtIp = new FloatProcessor(dt.getDistanceMap());
		(new ImagePlus("Distance Transform of " + im.getShortTitle(), dtIp)).show();
    }
    
	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addHelp(getJavaDocUrl());
		if (im.isInvertedLut()) {
			gd.setInsets(0, 0, 0);
			gd.addMessage("NOTE: Image has inverted LUT (0 = white)!");
		}
		gd.addEnumChoice("Distance norm", distanceNorm);

		gd.showDialog();
		if (gd.wasCanceled())
			return false;
		
		distanceNorm = gd.getNextEnumChoice(DistanceTransform.DistanceType.class);
		return true;
	}
		
}
