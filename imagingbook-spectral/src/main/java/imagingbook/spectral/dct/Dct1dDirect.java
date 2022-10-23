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
 * Calculates the 1D DFT using tabulated cosine values
 * for {@code float} or {@code double} data (see sub-classes
 * {@link Dct1dDirect.Float} and {@link Dct1dDirect.Double}, respectively).
 * This implementation is
 * considerably faster than the naive version without cosine tables (see
 * {@link Dct1dSlow}). Other optimizations are possible. See Sec. 20.1 of [1]
 * for additional details.
 * </p>
 * <p>
 * Usage example (for {@code float} data):
 * <pre>
 * float[] data = {1, 2, 3, 4, 5, 3, 0};
 * Dct1d.Float dct = new Dct1dDirect.Float(data.length);
 * dct.forward(data);
 * dct.inverse(data);
 * ... </pre>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing - An Algorithmic
 * Approach</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2022/10/23
 * @see Dct1dSlow
 * @see Dct1dFast
 */
public abstract class Dct1dDirect implements Dct1d {

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
	
	@Override
	public int getSize() {
		return this.M;
	}
	
	// ------------------------------------------------------------------------------
	
	/**
	 * One-dimensional DCT implementation using {@code float} data. 
	 */
	public static class Float extends Dct1dDirect implements Dct1d.Float {
		
		private final float[] tmp;		// array to hold temporary data
		
		/**
		 * Constructor.
		 * @param M the data size
		 */
		public Float(int M) {
			super(M);
			this.tmp = new float[M];
		}

		@Override
		public void forward(float[] g) {
			checkSize(g);
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
			checkSize(G);
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
	
	/**
	 * One-dimensional DCT implementation using {@code double} data. 
	 */
	public static class Double extends Dct1dDirect implements Dct1d.Double {
		
		private final double[] tmp;		// array to hold temporary data
		
		/**
		 * Constructor.
		 * @param M the data size
		 */
		public Double(int M) {
			super(M);
			this.tmp = new double[M];
		}

		@Override
		public void forward(double[] g) {
			checkSize(g);
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
			checkSize(G);
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

}
