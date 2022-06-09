/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.spectral.dct;

/**
 * Interface specifying all one-dimensional DCT implementations.
 * The definition used is the one adopted by MATLAB
 * (see https://www.mathworks.com/help/signal/ref/dct.html), called 
 * "DCT-II" on Wikipedia  (https://en.wikipedia.org/wiki/Discrete_cosine_transform).
 * 
 */
public interface Dct1d {
	
	public interface Float extends Dct1d {
		
		/**
		 * Performs an "in-place" 1D DCT forward transformation on the supplied data.
		 * The input signal is replaced by the associated DFT spectrum.
		 * @param g the signal to be transformed (modified)
		 */
		void forward(float[] g);
		
		/**
		 * Performs an "in-place" 1D DCT inverse transformation on the supplied spectrum.
		 * The input spectrum is replaced by the associated signal.
		 * @param G the spectrum to be transformed (modified)
		 */
		void inverse(float[] G);
		
		default void checkLength(float[] a, int n) {
			if (a.length != n)
				throw new IllegalArgumentException("data array must be of length " + n);
		}
		
	}
	
	public interface Double extends Dct1d {
		
		/**
		 * Performs an "in-place" 1D DCT forward transformation on the supplied data.
		 * The input signal is replaced by the associated DFT spectrum.
		 * @param g the signal to be transformed (modified)
		 */
		void forward(double[] g);
		
		/**
		 * Performs an "in-place" 1D DCT inverse transformation on the supplied spectrum.
		 * The input spectrum is replaced by the associated signal.
		 * @param G the spectrum to be transformed (modified)
		 */
		void inverse(double[] G);
		
		default void checkLength(double[] a, int n) {
			if (a.length != n)
				throw new IllegalArgumentException("data array must be of length " + n);
		}
		
	}

}
