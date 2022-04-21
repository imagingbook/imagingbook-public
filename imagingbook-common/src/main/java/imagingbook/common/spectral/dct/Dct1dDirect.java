/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.spectral.dct;

import imagingbook.common.math.Matrix;

/**
 * This class calculates the 1D DFT using tabulated cosine values.
 * This version is considerably faster than the naive version without tables.
 * Other optimizations are possible.
 * 
 * @author W. Burger
 * @version 2019-12-26
 */
public abstract class Dct1dDirect {

	final double CM0 = 1.0 / Math.sqrt(2);
	final int M;				// size of the input vector
	final int M4;				// = 4*M
	final double s; 			// common scale factor
	final double[] cosTable;	// pre-calculated table of cosines

	private Dct1dDirect(int M) {
		this.M = M;
		this.M4 = 4 * M;
		this.s = Math.sqrt(2.0 / M); 
		this.cosTable = makeCosineTable();
	}

	private double[] makeCosineTable() {
		double[] table = new double[M4]; 	// we need a table of size 4*M
		for (int j = 0; j < M4; j++) {		// j is equivalent to (m * (2 * u + 1)) % 4M
			//double phi = j * Math.PI / (2 * M);
			double phi = 2 * j * Math.PI / M4;
			table[j] = Math.cos(phi);
		}
		return table;
	}
	
	// ------------------------------------------------------------------------------
	
	public static class Float extends Dct1dDirect implements Dct1d.Float {
		
		private final float[] tmp;		// array to hold temporary data
		
		public Float(int M) {
			super(M);
			this.tmp = new float[M];
		}

		@Override
		public void forward(float[] g) {
			checkLength(g, M);
			if (g.length != M)
				throw new IllegalArgumentException();
			float[] G = tmp;
			for (int m = 0; m < M; m++) {
				double cm = (m == 0) ? CM0 : 1.0;
				double sum = 0;
				for (int u = 0; u < M; u++) {
					sum += g[u] * cm * cosTable[(m * (2 * u + 1)) % M4];
				}
				G[m] = (float) (s * sum);
			}
			System.arraycopy(G, 0, g, 0, M); // copy G -> g
		}

		@Override
		public void inverse(float[] G) {
			checkLength(G, M);
			if (G.length != M)
				throw new IllegalArgumentException();
			float[] g = tmp;
			for (int u = 0; u < M; u++) {
				double sum = 0;
				for (int m = 0; m < M; m++) {
					double cm = (m == 0) ? CM0 : 1.0;
					sum += cm * G[m] * cosTable[(m * (2 * u + 1)) % M4];
				}
				g[u] = (float) (s * sum);
			}
			System.arraycopy(g, 0, G, 0, M); // copy g -> G
		} 
		
	}

	// ------------------------------------------------------------------------------
	
	public static class Double extends Dct1dDirect implements Dct1d.Double {
		
		private final double[] tmp;		// array to hold temporary data
		
		public Double(int M) {
			super(M);
			this.tmp = new double[M];
		}

		@Override
		public void forward(double[] g) {
			checkLength(g, M);
			if (g.length != M)
				throw new IllegalArgumentException();
			double[] G = tmp;
			for (int m = 0; m < M; m++) {
				double cm = (m == 0) ? CM0 : 1.0;
				double sum = 0;
				for (int u = 0; u < M; u++) {
					sum += g[u] * cm * cosTable[(m * (2 * u + 1)) % M4];
				}
				G[m] = s * sum;
			}
			System.arraycopy(G, 0, g, 0, M); // copy G -> g
		}

		@Override
		public void inverse(double[] G) {
			checkLength(G, M);
			if (G.length != M)
				throw new IllegalArgumentException();
			double[] g = tmp;
			for (int u = 0; u < M; u++) {
				double sum = 0;
				for (int m = 0; m < M; m++) {
					double cm = (m == 0) ? CM0 : 1.0;
					sum += cm * G[m] * cosTable[(m * (2 * u + 1)) % M4];
				}
				g[u] = s * sum;
			}
			System.arraycopy(g, 0, G, 0, M); // copy g -> G
		}
	}

	// -----------------------------------------------------------------------------

	// test example
	public static void main(String[] args) {
		{
			System.out.println("FLOAT test:");
			float[] data = {1,2,3,4,5,3,0};
			System.out.println("Original data:      " + Matrix.toString(data));
	
			Dct1d.Float dct = new Dct1dDirect.Float(data.length);
			dct.forward(data);
			System.out.println("DCT spectrum:       " + Matrix.toString(data));
	
			dct.inverse(data);
			System.out.println("Reconstructed data: " + Matrix.toString(data));
			System.out.println();
		}
		{
			System.out.println("DOUBLE test:");
			double[] data = {1,2,3,4,5,3,0};
			System.out.println("Original data:      " + Matrix.toString(data));
	
			Dct1d.Double dct = new Dct1dDirect.Double(data.length);
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
