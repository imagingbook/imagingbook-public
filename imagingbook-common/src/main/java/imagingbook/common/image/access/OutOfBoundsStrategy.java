/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.image.access;

/**
 * Enumeration type representing the available strategies
 * for accessing pixel locations outside the image bounds.
 * 
 * @author WB
 */
public enum OutOfBoundsStrategy {
	/** Insert zero values. */
	ZeroValues,
	/** Insert the value of the nearest border pixel. */
	NearestBorder,
	/** Replicate the image by mirroring at its borders. */
	MirrorImage,
	/** Throws an exception when out-of-boundary coordinates are accessed. */
	ThrowException;
}
