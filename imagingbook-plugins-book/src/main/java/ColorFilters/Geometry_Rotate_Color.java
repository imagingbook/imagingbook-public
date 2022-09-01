/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package ColorFilters;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.color.colorspace.ColorStack;
import imagingbook.common.color.colorspace.ColorStack.ColorStackType;
import imagingbook.common.geometry.mappings.linear.Rotation2D;
import imagingbook.common.image.ImageMapper;
import imagingbook.common.interpolation.InterpolationMethod;
import imagingbook.common.util.Enums;

/**
 * This plugin rotates the input image. This operation
 * is performed in a color space specified by the user.
 * The intent is to visualize the differences of linear interpolation
 * when applied in different color spaces. Bilinear interpolation is 
 * used to avoid negative component values.
 * 
 * @author W. Burger
 * @version 2013/05/30
 */
public class Geometry_Rotate_Color implements PlugInFilter {
	
	static double angle = 15; // rotation angle (in degrees)
	static ColorStackType csType = ColorStackType.sRGB;
	
	ImagePlus imp = null;
	
    public int setup(String arg, ImagePlus imp) {
    	this.imp = imp;
        return DOES_RGB + NO_CHANGES;
    }

    public void run(ImageProcessor ip) {
    	if (!getParameters()) 
    		return;
    	ImagePlus colStack = ColorStack.createFrom(imp);
    	switch (csType) {
	    	case Lab : 	ColorStack.srgbToLab(colStack); break;
			case Luv: 	ColorStack.srgbToLuv(colStack); break;
			case RGB: 	ColorStack.srgbToRgb(colStack); break;
			case sRGB: 	break;
		default:
			IJ.error("Color space " + csType.name() + " not implemented!"); 
			return;
    	}
    	
    	Rotation2D imap = new Rotation2D(-2 * Math.PI * angle / 360);	// inverse mapping (target to source)
    	FloatProcessor[] processors = ColorStack.getProcessors(colStack);
  
    	ImageMapper mapper = new ImageMapper(imap, null, InterpolationMethod.Bilinear);
   		for (FloatProcessor fp : processors) {
   			//imap.applyTo(fp, InterpolationMethod.Bilinear);
   			mapper.map(fp);
   		}
       	
       	ColorStack.toSrgb(colStack);
       	colStack.setTitle(imp.getShortTitle() + "-rotated-" + csType.name());
       	ImagePlus result = ColorStack.toColorImage(colStack);
       	result.show();
    }
    
    boolean getParameters() {
    	String[] colorChoices = Enums.getEnumNames(ColorStackType.class);
		GenericDialog gd = new GenericDialog("Gaussian Filter");
		gd.addChoice("Color space", colorChoices, csType.name());
	
		gd.addNumericField("rotation angle", angle, 0);
		gd.showDialog();
		if(gd.wasCanceled())
			return false;
		csType = ColorStackType.valueOf(gd.getNextChoice());
		angle = gd.getNextNumber();

		return true;
    }

}

