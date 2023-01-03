/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.common.noise.perlin;

import imagingbook.common.noise.hashing.Hash32Shift;
import imagingbook.common.noise.hashing.HashFunction;

/**
 * <p>
 * Gradient (Perlin) noise implementation (see [1] for a detailed description). This is the super-class for all other
 * Perlin noise generators (1D, 2D, N-dimensional).
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Principles of Digital Image Processing &ndash; Advanced Methods</em> (Vol. 3),
 * Supplementary Chapter 8: "Synthetic Gradient Noise", Springer (2013). <a href=
 * "https://dx.doi.org/10.13140/RG.2.1.3427.7284">https://dx.doi.org/10.13140/RG.2.1.3427.7284</a>
 * </p>
 *
 * @author WB
 * @version 2022/11/24
 */
public abstract class PerlinNoiseGenerator {

	private final double f_min;
	private final double f_max;
	private final int oct;
	private final double persistence; // persistence (phi)
	
	final double[] F; 		// frequencies f_i
	final double[] A; 		// amplitudes a_i
	final HashFunction hashFun;

	/**
	 * Constructor (non-public).
	 */
	PerlinNoiseGenerator(double f_min, double f_max, double persistence, HashFunction hash) {
		if (f_max < f_min) {
			throw new IllegalArgumentException("f_max must not be less than f_min");
		}
		this.f_min = f_min;
		this.f_max = f_max;
		this.oct = getFrequencySteps(f_min, f_max);
		this.F = new double[oct];
		this.A = new double[oct];
		this.persistence = persistence;
		this.hashFun = (hash != null) ? hash : new Hash32Shift();	// default
		this.makeFrequencies(f_min, f_max);
	}

	/**
	 * Perlin's improved "fading" function s(x)
	 *
	 * @param x interpolation position in [0,1]
	 * @return s(x) = 10 x^3 - 15 x^4 + 6 x^5
	 */
	double s(double x) {
		return x * x * x * (x * (x * 6 - 15) + 10);
	}

	// ----------------- helper methods: ----------------------------
	
	private void makeFrequencies(double fmin, double fmax) {
		int n = 0;
		double f = f_min;
		double a = 1;
		while (f <= f_max && n < oct) {
			F[n] = f;
			A[n] = a;
			n = n + 1;
			f = 2 * f;
			a = persistence * a;
		}
	}

	private int getFrequencySteps(double fmin, double fmax) {	// TODO: should not be public!
		int i = 0;
		double f = fmin;
		while (f <= fmax) {
			i = i + 1;
			f = 2 * f;
		}
		return i;
	}
	
	/**
	 * Fast floor method (by Gustavson?)
	 * @param x argument
	 * @return floor(x), works for pos. and neg. x.
	 */
	int ffloor(double x) {
		int xint = (int) x;
		return (xint < x) ? xint : xint - 1;
	}

	// -----------------------------------------------

	/**
	 * Returns an array with the frequencies f_i used for generating the noise function
	 * @return an array of frequencies
	 */
	public double[] getFrequencies() {
		return F;
	}

	/**
	 * Returns an array with the amplitudes a_i used for generating * the noise function.
	 *
	 * @return an array of amplitudes
	 */
	public double[] getAmplitudes() {
		return A;
	}

}
