/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.image.interpolation;

/**
 * <p>
 * Enumeration type listing the most common interpolation methods. See Ch. 22 of [1] for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
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