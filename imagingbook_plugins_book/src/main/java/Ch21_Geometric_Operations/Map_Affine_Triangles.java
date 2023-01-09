/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package Ch21_Geometric_Operations;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.Pnt2d.PntInt;
import imagingbook.common.geometry.mappings.linear.AffineMapping2D;
import imagingbook.common.ij.DialogUtils;
import imagingbook.common.ij.IjUtils;
import imagingbook.common.image.ImageMapper;
import imagingbook.common.image.OutOfBoundsStrategy;
import imagingbook.common.image.interpolation.InterpolationMethod;
import imagingbook.common.math.Matrix;
import imagingbook.sampleimages.GeneralSampleImage;

/**
 * <p>
 * ImageJ plugin, applies an affine transformation derived from a pair of triangles P, Q (for the source and target
 * image, respectively) to the current image. See Sec. 21.1.3 of [1] for details. Optionally opens a sample image if no
 * image is currently open.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2022/11/28
 * @see ImageMapper
 * @see AffineMapping2D
 */
public class Map_Affine_Triangles implements PlugInFilter {
	
   	private static Pnt2d[] P = {			// source triangle
			PntInt.from(0, 0),
			PntInt.from(400, 0),
			PntInt.from(400, 400)
    	};

   	private static Pnt2d[] Q = {			// target triangle
			PntInt.from(0, 60),
			PntInt.from(400, 20),
			PntInt.from(300, 400)
    	};
    		
	/**
	 * Constructor, asks to open a predefined sample image if no other image
	 * is currently open.
	 */
	public Map_Affine_Triangles() {
		if (IjUtils.noCurrentImage()) {
			DialogUtils.askForSampleImage(GeneralSampleImage.Kepler);
		}
	}

    @Override
	public int setup(String arg, ImagePlus imp) {
        return DOES_ALL;
    }

    @Override
	public void run(ImageProcessor ip) {
		// inverse mapping (target to source):
    	AffineMapping2D m = AffineMapping2D.fromPoints(P, Q);	// forward mapping P -> Q
		AffineMapping2D mi = m.getInverse();					// inverse mapping Q -> P
		new ImageMapper(mi, OutOfBoundsStrategy.ZeroValues, InterpolationMethod.Bicubic).map(ip);
		
		IJ.log("A = \n" + Matrix.toString(m.getTransformationMatrix()));
    }
}
