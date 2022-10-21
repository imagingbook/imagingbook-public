/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.spectral.dft_old;

import imagingbook.common.math.Matrix;

/**
 * <p>
 * Abstract super-class of all two-dimensional DFT/FFT implementations.
 * Based on associated one-dimensional DFT/FFT methods (see {@link Dft1d}).
 * See Ch. 19 of [1] for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing - An Algorithmic Approach</em>,
 * 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2022/10/21
 * @see Dft1d
 */
public interface Dft2d {
	
	// -------------------------------------------------------------
	
	public interface Float extends Dft2d {
		
		/**
		 * Transforms the given 2D arrays 'in-place'. Separate arrays of identical size
		 * must be supplied for the real and imaginary parts of the signal (forward)
		 * or spectrum (inverse), neither of which may be null.
		 * 
		 * @param inRe real part of the input signal or spectrum (modified)
		 * @param inIm imaginary part of the input signal or spectrum (modified)
		 * @param forward forward transformation if {@code true}, inverse transformation if {@code false}
		 */
		public void transform(float[][] inRe, float[][] inIm, boolean forward);
		
		/**
		 * Performs an "in-place" forward DFT or FFT on the supplied 2D data.
		 * The input signal is replaced by the associated DFT spectrum.
		 * 
		 * @param gRe real part of the signal (modified)
		 * @param gIm imaginary part of the signal (modified)
		 * @see #transform(float[][], float[][], boolean)
		 */
		public default void forward(float[][] gRe, float[][] gIm) {
			checkSize(gRe, gIm);
			transform(gRe, gIm, true);
		}
		
		/**
		 * Performs an "in-place" inverse DFT or FFT on the supplied 2D spectrum.
		 * The input spectrum is replaced by the associated signal.
		 * 
		 * @param GRe real part of the spectrum (modified)
		 * @param GIm imaginary part of the spectrum (modified)
		 * @see #transform(float[][], float[][], boolean)
		 */
		public default void inverse(float[][] GRe, float[][] GIm) {
			checkSize(GRe, GIm);
			transform(GRe, GIm, false);
		}
		
		public default void checkSize(float[][] re, float[][] im) {
			if (!Matrix.sameSize(re, im))
				throw new IllegalArgumentException("arrays for real/imagingary parts must be of same size");
		}
	}
	
	// -------------------------------------------------------------
	
	public interface Double extends Dft2d {
		
		/**
		 * Transforms the given 2D arrays 'in-place'. Separate arrays of identical size
		 * must be supplied for the real and imaginary parts of the signal (forward)
		 * or spectrum (inverse), neither of which may be null.
		 * 
		 * @param inRe real part of the input signal or spectrum (modified)
		 * @param inIm imaginary part of the input signal or spectrum (modified)
		 * @param forward forward transformation if {@code true}, inverse transformation if {@code false}
		 */
		public void transform(double[][] inRe, double[][] inIm, boolean forward);
		
		/**
		 * Performs an "in-place" forward DFT or FFT on the supplied 2D data.
		 * The input signal is replaced by the associated DFT spectrum.
		 * 
		 * @param gRe real part of the signal (modified)
		 * @param gIm imaginary part of the signal (modified)
		 * @see #transform(float[][], float[][], boolean)
		 */
		public default void forward(double[][] gRe, double[][] gIm) {
			checkSize(gRe, gIm);
			transform(gRe, gIm, true);
		}
		
		/**
		 * Performs an "in-place" inverse DFT or FFT on the supplied 2D spectrum.
		 * The input spectrum is replaced by the associated signal.
		 * 
		 * @param GRe real part of the spectrum (modified)
		 * @param GIm imaginary part of the spectrum (modified)
		 * @see #transform(float[][], float[][], boolean)
		 */
		public default void inverse(double[][] GRe, double[][] GIm) {
			checkSize(GRe, GIm);
			transform(GRe, GIm, false);
		}
		
		public default void checkSize(double[][] re, double[][] im) {
			if (!Matrix.sameSize(re, im))
				throw new IllegalArgumentException("arrays for real/imagingary parts must be of same size");
		}
	}
	
}
