/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.noise.hashing;

/**
 * <p>
 * Integer hash function proposed by Thomas Wang in [1]. See [2] for details.
 * </p>
 * <p>
 * [1] Thomas Wang, "Integer Hash Function",
 * http://web.archive.org/web/20071223173210/http://www.concentric.net/~Ttwang/tech/inthash.htm
 * (Jan. 2007).<br>
 * [2] W. Burger, M.J. Burge, <em>Principles of Digital Image Processing &ndash;
 * Advanced Methods</em> (Vol. 3), Supplementary Chapter 8: "Synthetic Gradient
 * Noise", Springer (2013).
 * </p>
 * 
 * @author WB
 * @version 2022/11/24
 */
public class Hash32Shift extends Hash32 {

	/**
	 * Constructor, creates a hash function with a random seed value.
	 */
	public Hash32Shift() {
		this(0);
	}
	
	/**
	 * Constructor creating a hash function with the specified seed value.
	 * @param seed the random seed value (set to 0 use a random seed value).
	 */
	public Hash32Shift(int seed) {
		super(seed);
	}
	
	@Override
	int hashInt(int key) {
		key = key + seed;	// WB added
		key = ~key + (key << 15); // key = (key << 15) - key - 1;
		key = key ^ (key >>> 12);
		key = key + (key << 2);
		key = key ^ (key >>> 4);
		key = key * 2057; // key = (key + (key << 3)) + (key << 11);
		key = key ^ (key >>> 16);
		return key;
	}

}
