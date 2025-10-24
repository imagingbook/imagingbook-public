/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.common.image;

/**
 * Enumeration type representing the available strategies for accessing pixel locations outside the image bounds.
 *
 * @author WB
 * @see GridIndexer2D
 */
public enum OutOfBoundsStrategy {
	/** Replace out-of-bounds pixels by some default value. */
	DefaultValue,
	/** Replace out-of-bounds pixels by the nearest border pixel. */
	NearestBorder,
	/** Treat the image as being infinitely mirrored at its borders. */
	MirrorImage,
	/** Treat the image as repeating ininitely in x/y directions. */
	PeriodicImage,
	/** Throw an exception when out-of-boundary coordinates are accessed. */
	ThrowException;
}
