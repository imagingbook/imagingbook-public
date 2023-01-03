/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.common.noise.hashing;

import java.util.concurrent.ThreadLocalRandom;

/**
 * <p>
 * Interface to all hash functions defined in this package. Typical usage example:
 * </p>
 * <pre>
 * HashFun hf = new Hash32Ward(seed);
 * double g = hf.hash(u); // g is in [-1,+1]
 * double g = hf.hash(u, v);
 * double[] g = hf.hash(u, v, w);
 * </pre>
 * <p>
 * Omit seed in the constructor call or use seed = 0 to get a random seed hash function of the specified type.
 * </p>
 *
 * @author WB
 * @version 2022/11/24
 */
public interface HashFunction {
	
	static int getRandomSeed(int seed) {
		int s = (seed == 0) ? ThreadLocalRandom.current().nextInt() : seed;
		return 0x000fffff & s;
	}

	/**
	 * 1D hash function: maps a single {@code int} key to a {@code double} hash value in [0,1].
	 *
	 * @param u the key
	 * @return the hash value
	 */
	public double hash(int u);					// 1D hash function

	/**
	 * 2D hash function: maps a pair of {@code int} keys to a pair of {@code double} hash values in [0,1].
	 *
	 * @param u the 1st key
	 * @param v the 2nd key
	 * @return the hash values
	 */
	public double[] hash(int u, int v);		// 2D hash function

	/**
	 * 3D hash function: maps a triplet of {@code int} keys to a triplet of {@code double} hash values in [0,1].
	 *
	 * @param u the 1st key
	 * @param v the 2nd key
	 * @param w the 3rd key
	 * @return the hash values
	 */
	public double[] hash(int u, int v, int w);

	/**
	 * N-dimensional hash function: maps a N-vector of {@code int} keys to N-vector of {@code double} hash values in
	 * [0,1].
	 *
	 * @param p a N-vector of keys
	 * @return a N-vector of hash values
	 */
	public double[] hash(int[] p);
	
}


