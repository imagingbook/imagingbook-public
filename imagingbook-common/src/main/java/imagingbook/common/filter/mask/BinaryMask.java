/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.filter.mask;

import ij.process.ByteProcessor;
import imagingbook.common.ij.IjUtils;

import java.util.Objects;

/**
 * This class defines a binary mask, as used in filters, for example.
 * 
 * @author WB
 * @version 2022/09/10
 */
public class BinaryMask {
	// TODO: check against structuring elements for binary morphology
	
	private final int width;			// mask center position (x)
	private final int height;			// mask center position (y)
	private final int centerX;			// mask center position (x)
	private final int centerY;			// mask center position (y)
	private final int count;			// number of nonzero mask elements
	private final byte[][] mask;		// mask[x][y] specifies the support region
	
	public BinaryMask(byte[][] mask, int centerX, int centerY) {
		Objects.requireNonNull(mask);
		this.mask = mask;
		this.width = mask.length;
		this.height = mask[0].length;
		this.centerX = centerX;
		this.centerY = centerY;
		this.count = countElements(mask);
	}
	
	public BinaryMask(byte[][] mask) {
		this(mask, mask.length/2, mask[0].length/2);
	}

	private int countElements(byte[][] maskArray) {
		int cnt = 0;
		for (int x = 0; x < width; x++) {
			if (maskArray[x].length != height) {
				throw new IllegalArgumentException("mask array is not rectangular");
			}
			for (int y = 0; y < height; y++) {
				if (maskArray[x][y] != 0)
					cnt = cnt + 1;
			}
		}
		return cnt;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	/**
	 * Returns the x-coordinate of the mask's reference point.
	 * @return the x-coordinate of the mask's reference point
	 */
	public int getCenterX() {
		return centerX;
	}
	
	/**
	 * Returns the y-coordinate of the mask's reference point.
	 * @return the y-coordinate of the mask's reference point
	 */
	public int getCenterY() {
		return centerY;
	}
	
	/**
	 * Returns the number of inside-mask (1) elements.
	 * @return the number of inside-mask elements
	 */
	public int getElementCount() {
		return count;
	}

	/**
	 * Returns a {@code byte[x][y]} with outside mask element values zero, inside mask elements non-zero. The first
	 * array index corresponds to the horizontal coordinate.
	 *
	 * @return the mask as a {@code byte[][]}
	 */
	public byte[][] getByteArray() {
		return mask;
	}

	/**
	 * Returns a {@code byte[x][y]} with outside mask element values zero, inside mask elements non-zero. The first
	 * array index corresponds to the horizontal coordinate.
	 *
	 * @return the mask as a {@code byte[][]}
	 */
	public ByteProcessor getByteProcessor() {
		ByteProcessor bp = IjUtils.toByteProcessor(mask);
		bp.threshold(0);
		return bp;
	}

}
