/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.common.filter.mask;

/**
 * Defines a circular mask with the specified radius. Mask values are 1 inside the radius, 0 outside. Mask dimensions
 * are square and odd, the reference point is as the center.
 *
 * @author WB
 */
public class CircularMask extends BinaryMask {
	
	/**
	 * Constructor.
	 * @param radius the mask radius
	 */
	public CircularMask(double radius) {
		super(makeMask(radius));
	}
	
	private static byte[][] makeMask(double radius) {
		int center = Math.max((int) Math.ceil(radius), 1);
		int width = 2 * center + 1;		// width/height of mask array
		byte[][] mask = new byte[width][width];	// initialized to zero
		double r2 = radius * radius + 1; 	// add 1 to get mask shape similar to ImageJ
		for (int i = 0; i < width; i++) {
			int x = i - center;
			for (int j = 0; j < width; j++) {
				int y = j - center;
				if (x*x + y*y <= r2) {
					mask[i][j] = 1;
				}
			}
		}
		return mask;
	}

}
