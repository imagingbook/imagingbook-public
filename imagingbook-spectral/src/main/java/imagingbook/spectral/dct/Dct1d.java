/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.spectral.dct;

/**
 * <p>
 * Interface specifying all one-dimensional DCT implementations. The definition
 * used is the one adopted by MATLAB (<a href=
 * "https://www.mathworks.com/help/signal/ref/dct.html">https://www.mathworks.com/help/signal/ref/dct.html</a>),
 * called "DCT-II" on Wikipedia
 * (<a href="https://en.wikipedia.org/wiki/Discrete_cosine_transform">
 * https://en.wikipedia.org/wiki/Discrete_cosine_transform</a>). See Ch. 20 of
 * [1] for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic
 * Approach</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @see Dct1dDirect
 * @see Dct1dFast
 * 
 * @author WB
 * @version 2022/10/23
 */
public interface Dct1d {
	
	/**
	 * Returns the size of this DCT (length of data vectors).
	 * @return the size of this DCT
	 */
	public int getSize();
	
	/**
	 * Sub-interface for 1D DCT implementations operating on {@code float} data.
	 */
	public interface Float extends Dct1d {
		
		/**
		 * Performs an "in-place" 1D DCT forward transformation on the supplied data.
		 * The input signal is replaced by the associated DFT spectrum.
		 * 
		 * @param g the signal to be transformed (modified)
		 */
		void forward(float[] g);
		
		/**
		 * Performs an "in-place" 1D DCT inverse transformation on the supplied spectrum.
		 * The input spectrum is replaced by the associated signal.
		 * 
		 * @param G the spectrum to be transformed (modified)
		 */
		void inverse(float[] G);
		
		default void checkSize(float[] a) {
			if (a.length != getSize())
				throw new IllegalArgumentException(
					String.format("wrong 1D array size %d (expected %d)", a.length, getSize()));
		}
		
	}
	
	/**
	 * Sub-interface for 1D DCT implementations operating on {@code double} data.
	 */
	public interface Double extends Dct1d {
		
		/**
		 * Performs an "in-place" 1D DCT forward transformation on the supplied data.
		 * The input signal is replaced by the associated DFT spectrum.
		 * 
		 * @param g the signal to be transformed (modified)
		 */
		void forward(double[] g);
		
		/**
		 * Performs an "in-place" 1D DCT inverse transformation on the supplied spectrum.
		 * The input spectrum is replaced by the associated signal.
		 * 
		 * @param G the spectrum to be transformed (modified)
		 */
		void inverse(double[] G);
		
		default void checkSize(double[] a) {
			if (a.length != getSize())
				throw new IllegalArgumentException(
					String.format("wrong 1D array size %d (expected %d)", a.length, getSize()));
		}
		
	}

}
