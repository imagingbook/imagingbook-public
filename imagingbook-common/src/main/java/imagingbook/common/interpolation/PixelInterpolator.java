/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.interpolation;

import imagingbook.common.image.access.ScalarAccessor;


/**
 * This is the common (abstract) superclass to all pixel interpolators
 * for scalar-valued images.
 * @author WB
 *
 */
public interface PixelInterpolator {
	
	public static PixelInterpolator create(ScalarAccessor ia) {
		return create(ia.getInterpolationMethod());
	}
	
	public static PixelInterpolator create(InterpolationMethod method) {
		switch (method) {
		case NearestNeighbor : 	return new NearestNeighborInterpolator(); 
		case Bilinear : 		return new BilinearInterpolator();
		case Bicubic : 			return new BicubicInterpolator(1.00);
		case BicubicSmooth : 	return new BicubicInterpolator(0.25);
		case BicubicSharp : 	return new BicubicInterpolator(1.75);
		case CatmullRom: 		return new SplineInterpolator(0.5, 0.0);
		case CubicBSpline: 		return new SplineInterpolator(0.0, 1.0);
		case MitchellNetravali: return new SplineInterpolator(1.0/3, 1.0/3);
		case Lanzcos2 : 		return new LanczosInterpolator(2);
		case Lanzcos3 : 		return new LanczosInterpolator(3);
		case Lanzcos4 : 		return new LanczosInterpolator(4);
		default : throw new IllegalArgumentException("unhandled interpolator method: " + method);
		}
	}
	
	/**
	 * All interpolator classes must implement this method. 
	 * @param ia Accessor to the interpolated image.
	 * @param x Continuous interpolation position (horiz.)  
	 * @param y Continuous interpolation position (vert.)  
	 * @return The interpolated pixel value at position (x,y).
	 */
	public abstract float getInterpolatedValue(ScalarAccessor ia, double x, double y);
	
}
