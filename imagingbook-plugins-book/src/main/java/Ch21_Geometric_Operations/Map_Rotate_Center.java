/*
 *  This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge.
 * All rights reserved. Visit https://imagingbook.com for additional details.
 */
package Ch21_Geometric_Operations;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.common.geometry.mappings.linear.AffineMapping2D;
import imagingbook.common.geometry.mappings.linear.Rotation2D;
import imagingbook.common.geometry.mappings.linear.Translation2D;
import imagingbook.common.ij.DialogUtils;
import imagingbook.common.ij.IjUtils;
import imagingbook.common.image.ImageMapper;
import imagingbook.common.image.OutOfBoundsStrategy;
import imagingbook.common.image.interpolation.InterpolationMethod;
import imagingbook.sampleimages.GeneralSampleImage;

/**
 * <p>
 * ImageJ plugin, rotates the current image by a specified angle around its center. See Sec. 21.1.1 of [1] for details.
 * Optionally opens a sample image if no image is currently open.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2022/11/28
 * @see ImageMapper
 * @see Rotation2D
 */
public class Map_Rotate_Center implements PlugInFilter {
	
	private static double alphaDeg = 15.0; 	// rotation angle (in degrees)
	
	private static OutOfBoundsStrategy OBS = OutOfBoundsStrategy.ZeroValues;
	private static InterpolationMethod IPM = InterpolationMethod.Bicubic;
	
	/**
	 * Constructor, asks to open a predefined sample image if no other image
	 * is currently open.
	 */
	public Map_Rotate_Center() {
		if (IjUtils.noCurrentImage()) {
			DialogUtils.askForSampleImage(GeneralSampleImage.Clown);
		}
	}

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
		new ImageMapper(iA, OBS, IPM).map(ip);
    }
	
	// --------------------------------------------
	
	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		
		gd.addNumericField("Rotation angle (deg)", alphaDeg, 1);
		gd.addEnumChoice("Image out-of-bounds strategy", OBS);
		gd.addEnumChoice("Pixel interpolation method", IPM);
		
		gd.showDialog();
		if (gd.wasCanceled())
			return false;
		
		alphaDeg = gd.getNextNumber();
		OBS = gd.getNextEnumChoice(OutOfBoundsStrategy.class);
		IPM = gd.getNextEnumChoice(InterpolationMethod.class);
		
		return true;
	}
}
