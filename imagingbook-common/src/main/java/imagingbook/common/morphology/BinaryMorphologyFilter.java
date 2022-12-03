/*
 *  This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge.
 * All rights reserved. Visit https://imagingbook.com for additional details.
 */
package imagingbook.common.morphology;

import static imagingbook.common.morphology.StructuringElements.makeBoxKernel3x3;

/**
 * This is the (abstract) super-class for binary morphological filters.
 * 
 * @author WB
 * @version 2022/01/24 
 * @version 2022/09/18 revised visibility, moved static methods
 */
public abstract class BinaryMorphologyFilter implements BinaryMorphologyOperator {
	
	final byte[][] H; // structuring element (used by implementing classes)
	
	// ---- constructors --------------------------------------------------------

	BinaryMorphologyFilter() {
		this(makeBoxKernel3x3());
	}
	
	BinaryMorphologyFilter(byte[][] H) {
		this.H = H;
	}
	
	// --------------------------------------------------------------------------
	
	/**
	 * Returns the structuring element (kernel H) for this morphological filter.
	 * Note that the structuring element is a 2D {@code byte} array structured in
	 * the form {@code H[y][x]}. A reference to the internal array is returned,
	 * i.e., no copy is made.
	 * 
	 * @return the structuring element
	 */
	public byte[][] getStructuringElement() {
		return this.H;
	}
	
	// ----------------------------------------------------------------


	// -------------------------------------------------------------
	
//	private static int[][] iA = {
//			{0, 0, 0, 3, 0, 0, 3, 3, 0, 0, 0, 0, 3, 0, 3, 3}, 
//			{0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 3, 0, 3, 1}, 
//			{0, 0, 0, 0, 0, 0, 0, 0, 0}, 
//			{3, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 3, 0, 3, 1}, 
//			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 
//			{}, 
//			{3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 
//			{3, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 1, 0, 1, 0}, 
//		};
//	
//	public static void main(String[] args) {
//		byte[][] bA = toByteArray(iA);
//		for (int i = 0; i < bA.length; i++) {
//			System.out.println(Arrays.toString(bA[i]));
//		}
//	}
}

