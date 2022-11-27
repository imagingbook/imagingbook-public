/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package Ch21_GeometricOperations;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.common.geometry.mappings.linear.AffineMapping2D;
import imagingbook.common.geometry.mappings.linear.Rotation2D;
import imagingbook.common.geometry.mappings.linear.Translation2D;
import imagingbook.common.image.ImageMapper;
import imagingbook.common.image.OutOfBoundsStrategy;
import imagingbook.common.image.interpolation.InterpolationMethod;


public class Rotate_About_Center implements PlugInFilter {
	
	private static double alphaDeg = 15.0; 	// rotation angle (in degrees)
	private static OutOfBoundsStrategy obs = OutOfBoundsStrategy.ZeroValues;
	private static InterpolationMethod ipm = InterpolationMethod.Bicubic;

	@Override
    public int setup(String arg, ImagePlus im) {
        return DOES_ALL;
    }

	@Override
    public void run(ImageProcessor ip) {
		
		if (!runDialog()) {
			return;
		}
		
		double alpha =  Math.toRadians(alphaDeg);
		double xc = 0.5 * ip.getWidth();
		double yc = 0.5 * ip.getHeight();
		
		AffineMapping2D T1 = new Translation2D(-xc, -yc);
		AffineMapping2D R  = new Rotation2D(alpha);
		AffineMapping2D T2 = new Translation2D(xc, yc);
		AffineMapping2D A  = T1.concat(R).concat(T2);
		
		AffineMapping2D iA = A.getInverse(); 	// inverse mapping (target to source)
		new ImageMapper(iA, obs, ipm).map(ip);
    }
	
	// --------------------------------------------
	
	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		
		gd.addNumericField("Rotation angle (deg)", alphaDeg, 1);
		gd.addEnumChoice("Image out-of-bounds strategy", obs);
		gd.addEnumChoice("Üixel interpolation method", ipm);
		
		gd.showDialog();
		if (gd.wasCanceled())
			return false;
		
		alphaDeg = gd.getNextNumber();
		obs = gd.getNextEnumChoice(OutOfBoundsStrategy.class);
		ipm = gd.getNextEnumChoice(InterpolationMethod.class);
		
		return true;
	}
}
