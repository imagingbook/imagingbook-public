/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.interpolation;

/**
 * Enumeration type listing the most common interpolation methods.
 * 
 * @author WB
 * @see PixelInterpolator
 * @see PixelInterpolator#create(InterpolationMethod)
 */
public enum InterpolationMethod {
	NearestNeighbor,
	Bilinear,
	Bicubic,
	BicubicSmooth,
	BicubicSharp,
	CatmullRom,
	CubicBSpline,
	MitchellNetravali,
	Lanzcos2,
	Lanzcos3,
	Lanzcos4;
}

