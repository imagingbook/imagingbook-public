/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package Ch15_Color_Filters;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.common.filter.nonlinear.VectorMedianFilter;
import imagingbook.common.filter.nonlinear.VectorMedianFilter.Parameters;
import imagingbook.common.ij.DialogUtils;
import imagingbook.common.math.VectorNorm.NormType;
import imagingbook.core.jdoc.JavaDocHelp;
import imagingbook.sampleimages.GeneralSampleImage;

import static imagingbook.common.ij.IjUtils.noCurrentImage;

/**
 * <p>
 * This plugin applies a vector median filter to a RGB color image. See Sec. 15.2.2 of [1] for details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2022/12/10
 */
public class MedianFilter_Vector implements PlugInFilter, JavaDocHelp {

	private static final Parameters params = new VectorMedianFilter.Parameters();
	static {
		params.radius = 3;
		params.distanceNorm = NormType.L1;
	}

	private ImagePlus im;

	/**
	 * Constructor, asks to open a predefined sample image if no other image is currently open.
	 */
	public MedianFilter_Vector() {
		if (noCurrentImage()) {
			DialogUtils.askForSampleImage(GeneralSampleImage.ColorTest3);
		}
	}

    @Override
	public int setup(String arg, ImagePlus im) {
		this.im = im;
         return DOES_RGB;
    }

    @Override
	public void run(ImageProcessor ip) {
    	if (!runDialog())
			return;

    	VectorMedianFilter filter = new VectorMedianFilter(params);
    	filter.applyTo(ip);
    }
    
    private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addHelp(getJavaDocUrl());
		gd.addNumericField("Filter radius", params.radius, 1);
		gd.addEnumChoice("Distance norm", params.distanceNorm);
//		gd.addCheckbox("Mark modified pixels", params.markModifiedPixels);
//		gd.addCheckbox("Show mask", params.showMask);
		
		gd.showDialog();
		if(gd.wasCanceled()) return false;
		
		params.radius = Math.max(gd.getNextNumber(),0.5);
		params.distanceNorm = gd.getNextEnumChoice(NormType.class);
//		params.markModifiedPixels = gd.getNextBoolean();
//		params.showMask = gd.getNextBoolean();
		return true;
    }
}

