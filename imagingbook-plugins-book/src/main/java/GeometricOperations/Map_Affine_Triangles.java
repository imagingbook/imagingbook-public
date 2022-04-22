/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
package GeometricOperations;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.Pnt2d.PntInt;
import imagingbook.common.geometry.mappings.linear.AffineMapping2D;
import imagingbook.common.image.ImageMapper;
import imagingbook.common.image.access.OutOfBoundsStrategy;
import imagingbook.common.interpolation.InterpolationMethod;
import imagingbook.common.math.Matrix;

/**
 * Demo plugin showing how to specify an affine transformation from
 * a pair of triangles (point arrays).
 * Also illustrates the use of {@link ImageMapper}.
 * 
 * @author WB
 *
 * @see AffineMapping2D
 * @see ImageMapper
 */
public class Map_Affine_Triangles implements PlugInFilter {

    public int setup(String arg, ImagePlus imp) {
        return DOES_ALL;
    }

    public void run(ImageProcessor ip) {
    	Pnt2d[] P = {			// source triangle
			PntInt.from(0, 0),
			PntInt.from(400, 0),
			PntInt.from(400, 400)
    	};

    	Pnt2d[] Q = {			// target triangle
			PntInt.from(0, 60),
			PntInt.from(400, 20),
			PntInt.from(300, 400)
    	};

		// inverse mapping (target to source):
    	AffineMapping2D m = AffineMapping2D.fromPoints(P, Q);	// forward mapping P -> Q
		AffineMapping2D mi = m.getInverse();					// inverse mapping Q -> P
		new ImageMapper(mi, OutOfBoundsStrategy.ZeroValues, InterpolationMethod.Bicubic).map(ip);
		
		IJ.log("A = \n" + Matrix.toString(m.getTransformationMatrix()));
    }
}
