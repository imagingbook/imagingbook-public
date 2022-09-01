/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.noise.perlin;

import imagingbook.common.noise.hashing.Hash32Shift;
import imagingbook.common.noise.hashing.HashFun;

/**
 * <p>
 * Gradient (Perlin) noise implementation (see [1] for a detailed description).
 * This is the super-class for all other Perlin noise
 * generators (1D, 2D, N-dimensional).
 * </p>
 * <p>
 * [1] "Synthetic Gradient Noise", supplementary book chapter (8) to W. Burger and M.J. Burge, 
 * "Principles of Digital Image Processing - Advanced Methods (Vol. 3)", 
 * Undergraduate Topics in Computer Science, Springer-Verlag, London (2013).
 * <a href="http://dx.doi.org/10.13140/RG.2.1.3427.7284">http://dx.doi.org/10.13140/RG.2.1.3427.7284</a>
 * </p>
 */

public abstract class PerlinNoiseGen {

	final double f_min;
	final double f_max;
	final double persistence; // persistence (phi)
	double[] F; 		// frequencies f_i
	double[] A; 		// amplitudes a_i

	HashFun hashFun;
	public boolean VERBOSE = false; // for debugging only

	PerlinNoiseGen(double f_min, double f_max, double persistence, HashFun hash) {
		this.f_min = f_min;
		this.f_max = f_max;
		this.persistence = persistence;
		this.hashFun = (hash != null) ? hash : new Hash32Shift();	// default
		this.makeFrequencies(f_min, f_max, persistence);
	}

	/**
	 * Perlin's improved "fading" function s(x)
	 * @param x interpolation position in [0,1]
	 * @return s(x) = 10 x^3 - 15 x^4 + 6 x^5 
	 */
	double s(double x) {
		return x * x * x * (x * (x * 6 - 15) + 10); // s(x) = 10 x^3 - 15 x^4 + 6 x^5
	}

	void makeFrequencies(double fmin, double fmax, double persistence) {
		int oct = getFrequencySteps(f_min, f_max);
		if (oct < 1) {
			throw new IllegalArgumentException("f_max is smaller than f_min");
		}
		F = new double[oct];
		A = new double[oct];
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

	// ----------------- UTILITY METHODS ----------------------------

	/**
	 * The number of frequency steps, given fmin and fmax.
	 * 
	 * @param fmin min. frequency
	 * @param fmax max. frequency
	 * @return number of frequency steps
	 */
	public int getFrequencySteps(double fmin, double fmax) {	// TODO: should not be public!
		int i = 0;
		double f = fmin;
		while (f <= fmax) {
			i = i + 1;
			f = 2 * f;
		}
		return i;
	}

	/**
	 * @return An array with the frequencies f_i used for generating 
	 * the noise function.
	 */
	public double[] getFrequencies() {
		return F;
	}

	/**
	 * @return An array with the amplitudes a_i used for generating 
	 * the noise function.
	 */
	public double[] getAmplitudes() {
		return A;
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
	
//	public static void main(String[] args) {	// TODO: delete
//		double f_min = 0.01;
//		double f_max = 0.015;
//		int octaves = getFrequencySteps(f_min, f_max);
//		System.out.format("fmin=%f fmax=%f octaves= %d", f_min, f_max, octaves);
//	}

}
