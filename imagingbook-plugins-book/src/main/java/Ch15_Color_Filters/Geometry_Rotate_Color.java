/*
 *  This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge.
 * All rights reserved. Visit https://imagingbook.com for additional details.
 */
package Ch15_Color_Filters;

import java.awt.color.ColorSpace;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.color.colorspace.LabColorSpace;
import imagingbook.common.color.colorspace.LinearRgb65ColorSpace;
import imagingbook.common.color.colorspace.LuvColorSpace;
import imagingbook.common.geometry.mappings.linear.Rotation2D;
import imagingbook.common.image.ColorPack;
import imagingbook.common.image.ImageMapper;
import imagingbook.common.image.interpolation.InterpolationMethod;

/**
 * This plugin rotates the input image. This operation
 * is performed in a color space specified by the user.
 * The intent is to visualize the differences of linear interpolation
 * when applied in different color spaces. Bilinear interpolation is 
 * used to avoid negative component values.
 * 
 * @author WB
 * @version 2022/09/02
 */
public class Geometry_Rotate_Color implements PlugInFilter {
	
	enum ColorStackType {
		Lab, Luv, LinearRGB, sRGB;
	}
	
	static double angle = 15; 							// rotation angle (in degrees)
	static ColorStackType csType = ColorStackType.sRGB;	// color space to use
	
	ImagePlus im = null;
	
    public int setup(String arg, ImagePlus imp) {
    	this.im = imp;
        return DOES_RGB + NO_CHANGES;
    }

    public void run(ImageProcessor ip) {
    	if (!getParameters()) 
    		return;
    	
    	ColorPack colStack = new ColorPack((ColorProcessor) ip);
    	ColorSpace cs = null;
    	
    	switch (csType) {
	    	case Lab : 		cs = LabColorSpace.getInstance(); break;
			case Luv: 		cs = LuvColorSpace.getInstance(); break;
			case LinearRGB: cs = LinearRgb65ColorSpace.getInstance(); break;
			case sRGB: 		cs = null; break;
			default:
				IJ.error("Color space " + csType.name() + " not implemented!"); 
				return;
    	}
    	
    	if (cs != null) {
    		colStack.convertFromSrgbTo(cs);
    	}
    	
    	Rotation2D imap = new Rotation2D(-2 * Math.PI * angle / 360);	// inverse mapping (target to source)
    	FloatProcessor[] processors = colStack.getProcessors();
  
    	ImageMapper mapper = new ImageMapper(imap, null, InterpolationMethod.Bilinear);
   		for (FloatProcessor fp : processors) {
   			//imap.applyTo(fp, InterpolationMethod.Bilinear);
   			mapper.map(fp);
   		}
       	
   		colStack.convertToSrgb();
   		ColorProcessor cp = colStack.toColorProcessor();
       	String title = im.getShortTitle() + "-rotated-" + csType.name();
       	ImagePlus result = new ImagePlus(title, cp);
       	result.show();
    }
    
    boolean getParameters() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addEnumChoice("Color space", csType);
		gd.addNumericField("rotation angle", angle, 0);
		
		gd.showDialog();
		if(gd.wasCanceled())
			return false;
		
		csType = gd.getNextEnumChoice(ColorStackType.class);
		angle = gd.getNextNumber();
		return true;
    }

}

