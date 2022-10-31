/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.image.interpolation;

import imagingbook.common.image.access.ScalarAccessor;


/**
 * <p>
 * This interface defines the behavior of 2D pixel interpolators
 * for scalar-valued images. See Ch. 22 of [1] for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>,
 * 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 */
public interface PixelInterpolator {
	
	/**
	 * Returns a {@link PixelInterpolator} instance for the specified
	 * {@link InterpolationMethod}.
	 * 
	 * @param method the interpolation method
	 * @return an instance of {@link PixelInterpolator}
	 */
	public static PixelInterpolator create(InterpolationMethod method) {
		switch (method) {
		case NearestNeighbor : 	return new NearestNeighborInterpolator();
		case Bilinear : 		return new BilinearInterpolator();
		case Bicubic : 			return new BicubicInterpolator(1.00);
		case BicubicSmooth : 	return new BicubicInterpolator(0.25);
		case BicubicSharp : 	return new BicubicInterpolator(1.75);
		case CatmullRom: 		return new CatmullRomInterpolator();
		case CubicBSpline: 		return new CubicBSplineInterpolator();
		case MitchellNetravali: return new MitchellNetravaliInterpolator();
		case Lanzcos2 : 		return new LanczosInterpolator(2);
		case Lanzcos3 : 		return new LanczosInterpolator(3);
		case Lanzcos4 : 		return new LanczosInterpolator(4);
		//default : throw new IllegalArgumentException("unhandled interpolation method: " + method);
		}
		return null;
	}
	
	/**
	 * Returns the interpolated pixel value for the specified position. 
	 * 
	 * @param ia a {@link ScalarAccessor} to the interpolated image
	 * @param x continuous interpolation position (horiz.)  
	 * @param y continuous interpolation position (vert.)  
	 * @return The interpolated pixel value at position (x,y).
	 */
	public float getInterpolatedValue(ScalarAccessor ia, double x, double y);
	
	/**
	 * Returns the value of the one-dimensional weight function w(x), that is
	 * the weight given to some pixel at distance x from the current interpolation
	 * point. This method is primarily used for testing.
	 * 
	 * @param x the position relative to the interpolation point
	 * @return the weight for this position
	 */
	public double getWeight(double x);
	
	/**
	 * Returns the value of the two-dimensional weight function W(x,y), that is
	 * the weight given to some pixel at distance (x,y) relative to the current interpolation
	 * point. This method is primarily used for testing.
	 * 
	 * @param x the x-position relative to the interpolation point
	 * @param y the y-position relative to the interpolation point
	 * @return the weight for this position
	 */
	public default double getWeight(double x, double y) {
		return getWeight(x) * getWeight(y);
	}
	
	
	
}
