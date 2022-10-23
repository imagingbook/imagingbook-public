/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.spectral.dct;


import org.jtransforms.dct.DoubleDCT_1D;
import org.jtransforms.dct.FloatDCT_1D;

/**
 * <p>
 * This is fast implementation of the DCT, based on the JTransforms package by
 * Piotr Wendykier (<a href="https://github.com/wendykierp/JTransforms">
 * https://github.com/wendykierp/JTransforms</a>). See Sec. 20.1 of [1] for
 * additional details.
 * </p>
 * <p>
 * Usage example (for {@code float} data):
 * <pre>
 * float[] data = {1, 2, 3, 4, 5, 3, 0};
 * Dct1d.Float dct = new Dct1dFast.Float(data.length);
 * dct.forward(data);
 * dct.inverse(data);
 * ... </pre>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing - An Algorithmic
 * Approach</em>, 3rd ed, Springer (2022).
 * </p>
 */
public abstract class Dct1dFast extends Dct1dImp {

	final double s; 			// common scale factor

	private Dct1dFast(int M) {
		super(M);
		this.s = Math.sqrt(2.0 / M); 
	}
	
	// ------------------------------------------------------------------------------
	
	/**
	 * One-dimensional DCT implementation using {@code float} data. 
	 */
	public static class Float extends Dct1dFast implements Dct1d.Float {
		
		private final FloatDCT_1D fct;
		
		/**
		 * Constructor.
		 * @param M the data size
		 */
		public Float(int M) {
			super(M);
			this.fct = new FloatDCT_1D(M);
		}

		@Override
		public void forward(float[] g) {
			checkSize(g);
			fct.forward(g, true);
		}

		@Override
		public void inverse(float[] G) {
			checkSize(G);
			fct.inverse(G, true);
		} 
		
	}

	// ------------------------------------------------------------------------------
	
	/**
	 * One-dimensional DCT implementation using {@code double} data. 
	 */
	public static class Double extends Dct1dFast implements Dct1d.Double {
		
		private final DoubleDCT_1D fct;
		
		/**
		 * Constructor.
		 * @param M the data size
		 */
		public Double(int M) {
			super(M);
			this.fct = new DoubleDCT_1D(M);
		}

		@Override
		public void forward(double[] g) {
			checkSize(g);
			fct.forward(g, true);
		}

		@Override
		public void inverse(double[] G) {
			checkSize(G);
			fct.inverse(G, true);
		}
	}

}
