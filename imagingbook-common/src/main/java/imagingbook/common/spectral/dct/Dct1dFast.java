/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.spectral.dct;


import org.jtransforms.dct.DoubleDCT_1D;
import org.jtransforms.dct.FloatDCT_1D;

import imagingbook.common.math.Matrix;

/**
 * Fast implementation of the DCT, based on the JTransforms package
 * by Piotr Wendykier (see <a href="https://github.com/wendykierp/JTransforms">
 * https://github.com/wendykierp/JTransforms</a>).
 */
public abstract class Dct1dFast {


	final int M;				// size of the input vector
	final double s; 			// common scale factor

	private Dct1dFast(int M) {
		this.M = M;
		this.s = Math.sqrt(2.0 / M); 
	}
	
	// ------------------------------------------------------------------------------
	
	public static class Float extends Dct1dFast implements Dct1d.Float {
		
		private final FloatDCT_1D fct;
		
		public Float(int M) {
			super(M);
			this.fct = new FloatDCT_1D(M);
		}

		@Override
		public void forward(float[] g) {
			checkLength(g, M);
			fct.forward(g, true);
		}

		@Override
		public void inverse(float[] G) {
			checkLength(G, M);
			fct.inverse(G, true);
		} 
		
	}

	// ------------------------------------------------------------------------------
	
	public static class Double extends Dct1dFast implements Dct1d.Double {
		
		private final DoubleDCT_1D fct;
		
		public Double(int M) {
			super(M);
			this.fct = new DoubleDCT_1D(M);
		}

		@Override
		public void forward(double[] g) {
			checkLength(g, M);
			fct.forward(g, true);
		}

		@Override
		public void inverse(double[] G) {
			checkLength(G, M);
			fct.inverse(G, true);
		}
	}

	// -----------------------------------------------------------------------------

	// test example
	public static void main(String[] args) {
		{
			System.out.println("FLOAT (fast DCT) test:");
			float[] data = {1,2,3,4,5,3,0};
			System.out.println("Original data:      " + Matrix.toString(data));
	
			Dct1d.Float dct = new Dct1dFast.Float(data.length);
			dct.forward(data);
			System.out.println("DCT spectrum:       " + Matrix.toString(data));
	
			dct.inverse(data);
			System.out.println("Reconstructed data: " + Matrix.toString(data));
			System.out.println();
		}
		{
			System.out.println("DOUBLE (fast DCT) test:");
			double[] data = {1,2,3,4,5,3,0};
			System.out.println("Original data:      " + Matrix.toString(data));
	
			Dct1d.Double dct = new Dct1dFast.Double(data.length);
			dct.forward(data);
			System.out.println("DCT spectrum:       " + Matrix.toString(data));
	
			dct.inverse(data);
			System.out.println("Reconstructed data: " + Matrix.toString(data));
			System.out.println();
		}
	}

	//	Original data:      {1.000, 2.000, 3.000, 4.000, 5.000, 3.000, 0.000}
	//	DCT of data:        {6.803, -0.361, -3.728, 1.692, -0.888, -0.083, 0.167}
	//	Reconstructed data: {1.000, 2.000, 3.000, 4.000, 5.000, 3.000, -0.000}


}
