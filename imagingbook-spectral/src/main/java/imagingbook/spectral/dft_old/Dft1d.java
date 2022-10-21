/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.spectral.dft_old;

import java.util.Objects;

/**
 * <p>
 * Interface to be implemented by all one-dimensional DFT/FFT implementations.
 * See Ch. 18 of [1] for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing - An Algorithmic Approach</em>,
 * 3rd ed, Springer (2022).
 * </p>
 * @see Dft1dDirect
 * @see Dft1dFast
 */
public interface Dft1d {
	
	public interface Float extends Dft1d {
		/**
		 * Performs an "in-place" DFT forward transformation on the supplied 1D {@code float} data.
		 * The input signal is replaced by the associated DFT spectrum.
		 * @param gRe real part of the signal (modified)
		 * @param gIm imaginary part of the signal (modified)
		 */
		public void forward(float[] gRe, float[] gIm);
		
		/**
		 * Performs an "in-place" DFT inverse transformation on the supplied 1D {@code float} spectrum.
		 * The input spectrum is replaced by the associated signal.
		 * @param GRe real part of the spectrum (modified)
		 * @param GIm imaginary part of the spectrum (modified)
		 */
		public void inverse(float[] GRe, float[] GIm);
		
		/**
		 * Transforms the given 1D arrays 'in-place'. Separate arrays of identical size
		 * must be supplied for the real and imaginary parts of the signal (forward) 
		 * or spectrum (inverse), neither of which may be {@code null}.
		 * 
		 * @param inRe real part of the input signal or spectrum (modified)
		 * @param inIm imaginary part of the input signal or spectrum (modified)
		 * @param forward forward transformation if {@code true}, inverse transformation if {@code false}
		 */
		public void transform(float[] inRe, float[] inIm, boolean forward);
		
		default void checkSize(float[] re, float[] im, int n) {
			Objects.requireNonNull(re);
			Objects.requireNonNull(im);
			if (re.length != n || im.length != n)
				throw new IllegalArgumentException("arrays for real/imagingary parts must be of size " + n);
		}
		
	}
	
	// -------------------------------------------------------------------
	
	public interface Double extends Dft1d {
		
		/**
		 * Performs an "in-place" 1D DFT forward transformation on the supplied {@code double} data.
		 * The input signal is replaced by the associated DFT spectrum.
		 * 
		 * @param gRe real part of the signal (modified)
		 * @param gIm imaginary part of the signal (modified)
		 */
		public void forward(double[] gRe, double[] gIm);
		
		/**
		 * Performs an "in-place" 1D DFT inverse transformation on the supplied {@code double} spectrum.
		 * The input spectrum is replaced by the associated signal.
		 * 
		 * @param GRe real part of the spectrum (modified)
		 * @param GIm imaginary part of the spectrum (modified)
		 */
		public void inverse(double[] GRe, double[] GIm);
		
		/**
		 * Transforms the given 1D arrays 'in-place'. Separate arrays of identical size
		 * must be supplied for the real and imaginary parts of the signal (forward) 
		 * or spectrum (inverse), neither of which may be {@code null}.
		 * 
		 * @param inRe real part of the input signal or spectrum (modified)
		 * @param inIm imaginary part of the input signal or spectrum (modified)
		 * @param forward forward transformation if {@code true}, inverse transformation if {@code false}
		 */
		public void transform(double[] inRe, double[] inIm, boolean forward);
		
		default void checkSize(double[] re, double[] im, int n) {
			Objects.requireNonNull(re);
			Objects.requireNonNull(im);
			if (re.length != n || im.length != n)
				throw new IllegalArgumentException("arrays for real/imagingary parts must be of size " + n);
		}
		
	}
	
}
