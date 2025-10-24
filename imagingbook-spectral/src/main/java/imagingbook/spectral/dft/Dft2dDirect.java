/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.spectral.dft;

/**
 * <p>
 * Direct (slow) implementation of the 2-dimensional DFT using tabulated sine
 * and cosine values. Note that this class has no public constructor -
 * instantiate sub-class {@link Dft2dDirect.Float} or {@link Dft2dDirect.Double}
 * instead, as shown below. See Ch. 19 of [1] for additional details.
 * </p>
 * <p>
 * Usage example (for {@code float} data):
 * </p>
 * <pre>
 * float[][] re = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}, {-2, -1, 0}};
 * float[][] im = {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}};
 * int w = re.length;       // w = 4
 * int h = re[0].length;    // h = 3
 * Dft2d.Float dft = new Dft2dDirect.Float(w, h);
 * dct.forward(re, im);  // re/im now is the 2D DFT spectrum
 * dct.inverse(re, im);  // re/im now is the original 2D signal 
 * ...</pre>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic
 * Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @see Dft1dDirect
 */
public abstract class Dft2dDirect extends Dft2dImp {
	
	private Dft2dDirect(int width, int height, ScalingMode sm) {
		super(width, height, sm);
	}
	
	// -------------------------------------------------------------------------
	
	/**
	 * Two-dimensional DFT implementation using {@code float} data. 
	 */
	public static class Float extends Dft2dDirect implements Dft2d.Float {
		
		/**
		 * Constructor using a specific scaling mode.
		 * 
		 * @param sm the scaling mode
		 * @param width width of the data array (dimension 0)
		 * @param height height of the data array (dimension 1)
		 */
		public Float(int width, int height, ScalingMode sm) {
			super(width, height, sm);
		}
		
		/**
		 * Constructor using the default scaling mode.
		 * 
		 * @param width width of the data array (dimension 0)
		 * @param height height of the data array (dimension 1)
		 */
		public Float(int width, int height) {
			this(width, height, ScalingMode.DEFAULT);
		}
		
		// -------------------

		@Override
		public Dft1d.Float get1dDft(int size) {
			return new Dft1dDirect.Float(size, this.sm);
		}

	}
	
	// -------------------------------------------------------------------------

	/**
	 * Two-dimensional DFT implementation using {@code double} data. 
	 */
	public static class Double extends Dft2dDirect implements Dft2d.Double {
		
		/**
		 * Constructor using a specific scaling mode.
		 * 
		 * @param width width of the data array (dimension 0)
		 * @param height height of the data array (dimension 1)
		 * @param sm the scaling mode
		 */
		public Double(int width, int height, ScalingMode sm) {
			super(width, height, sm);
		}
		
		/**
		 * Constructor using the default scaling mode.
		 * 
		 * @param width width of the data array (dimension 0)
		 * @param height height of the data array (dimension 1)
		 */
		public Double(int width, int height) {
			this(width, height, ScalingMode.DEFAULT);
		}
		
		// -------------------

		@Override
		public Dft1d.Double get1dDft(int size) {
			return new Dft1dDirect.Double(size, this.sm);
		}
	
	}
}
