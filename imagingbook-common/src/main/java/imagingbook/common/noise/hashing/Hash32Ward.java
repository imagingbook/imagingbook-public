/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.common.noise.hashing;

/**
 * <p>
 * Hash function proposed by G Ward in [1]. See [2] for details.
 * </p>
 * <p>
 * [1] G. Ward, "A recursive implementation of the Perlin noise function", Graphics Gems II (1991).<br> [2] W. Burger,
 * M.J. Burge, <em>Principles of Digital Image Processing &ndash; Advanced Methods</em> (Vol. 3), Supplementary Chapter
 * 8: "Synthetic Gradient Noise", Springer (2013). <a href=
 * "https://dx.doi.org/10.13140/RG.2.1.3427.7284">https://dx.doi.org/10.13140/RG.2.1.3427.7284</a>
 * </p>
 *
 * @author WB
 * @version 2022/11/24
 */
public class Hash32Ward extends Hash32 {
	
	/**
	 * Constructor, creates a hash function with a random seed value.
	 */
	public Hash32Ward() {
		this(0);
	}
	
	/**
	 * Constructor creating a hash function with the specified seed value.
	 * @param seed the random seed value (set to 0 use a random seed value).
	 */
	public Hash32Ward(int seed) {
		super(seed);
	}
	
	@Override
	int hashInt(int key) {
		key = key + seed;	// WB added
		// lower 16 bits are highly repetitive and "perfectly" uniform!
		key = (key << 13) ^ key; // ^ denotes bitwise XOR operation
		key = (key * (key * key * 15731 + 789221) + 1376312589);
		return key;
	}
	
	public static void main(String[] args) {
		Hash32Ward hf = new Hash32Ward();
		for (int k = 0; k < 256; k++) {
			System.out.format("%d : %10f\n", k, hf.hash(k));
			
		}
	}
}
