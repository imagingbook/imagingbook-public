/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.spectral.dct;

/**
 * <p>
 * Common interface for all 2D DCT implementations. Based on associated
 * one-dimensional DCT methods (see {@link Dct1d}). Data arrays are indexed as
 * {@code data[x][y]}, with 0 &le; x &lt; width and 0 &le; y &lt; height. See Ch.
 * 20 of [1] for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic
 * Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2022/10/21
 * @see Dct1d
 */
public interface Dct2d {
	
	/**
	 * Returns the 'width' of the 2D data array (length of dimension 0).
	 * Data arrays are indexed as {@code data[x][y]}, with 
	 * 0 &le; x &lt; width and 0 &le; y &lt; height.
	 * @return the width of the 2D data array
	 */
	public int getWidth();
	
	/**
	 * Returns the 'height' of the 2D data array (length of dimension 1).
	 * Data arrays are indexed as {@code data[x][y]}, with 
	 * 0 &le; x &lt; width and 0 &le; y &lt; height.
	 * @return the height of the 2D data array
	 */
	public int getHeight();
	
	// ---------------------------------------------------------------------------------------

	/**
	 * Sub-interface for 2D DCT implementations operating on {@code float} data.
	 */
	public interface Float extends Dct2d {
		
		/**
		 * Returns a suitable 1D DCT of the specified size ({@code float}).
		 * @param size the size of the DCT
		 * @return a {@link Dct1d.Float} instance
		 */
		public Dct1d.Float get1dDct(int size);
		
		/**
		 * Performs an "in-place" 2D DCT forward transformation on the supplied data.
		 * The input signal is replaced by the associated DCT spectrum.
		 * @param g the signal to be transformed (modified)
		 */
		public default void forward(float[][] g) {
			transform(g, true);
		}

		/**
		 * Performs an "in-place" 2D DCT inverse transformation on the supplied spectrum.
		 * The input spectrum is replaced by the associated signal.
		 * @param G the spectrum to be transformed (modified)
		 */
		public default void inverse(float[][] G) {
			transform(G, false);
		}

		/**
		 * Transforms the given 2D array 'in-place'.
		 * 
		 * @param data the input signal or spectrum (modified)
		 * @param forward forward transformation if {@code true}, inverse transformation if {@code false}
		 */
		default void transform(final float[][] data, boolean forward) {
			checkSize(data);
			final int width = data.length;
			final int height = data[0].length;

			// do the rows:
			float[] row = new float[width];
			Dct1d.Float dct1R = get1dDct(width);
			for (int v = 0; v < height; v++) {
				extractRow(data, v, row);
				if (forward)
					dct1R.forward(row);
				else
					dct1R.inverse(row);
				insertRow(data, v, row);
			}

			// do the columns:
			float[] col = new float[height];
			Dct1d.Float dct1C = get1dDct(height);
			for (int u = 0; u < width; u++) {
				extractCol(data, u, col);
				if (forward)
					dct1C.forward(col);
				else
					dct1C.inverse(col);
				insertCol(data, u, col);
			}
		}

		default void extractRow(float[][] data, int v, float[] row) {
			final int width = data.length;
			for (int u = 0; u < width; u++) {
				row[u] = data[u][v];
			}
		}

		default void insertRow(float[][] data, int v, float[] row) {
			final int width = data.length;
			for (int u = 0; u < width; u++) {
				data[u][v] = row[u];
			}
		}

		default void extractCol(float[][] data, int u, float[] column) {
			final int height = data[0].length;
			for (int v = 0; v < height; v++) {
				column[v] = data[u][v];
			}
		}

		default void insertCol(float[][] data, int u, float[] column) {
			final int height = data[0].length;
			for (int v = 0; v < height; v++) {
				data[u][v] = column[v];
			}
		}
		
		public default void checkSize(float[][] A) {
			if (A.length != this.getWidth()) 
				throw new IllegalArgumentException(
						String.format("wrong 2D array width %d (expected %d)", A.length, this.getWidth()));
			if (A[0].length != this.getHeight()) 
				throw new IllegalArgumentException(
						String.format("wrong 2D array height %d (expected %d)", A[0].length, this.getHeight()));
		}
		
	}
	
	/**
	 * Sub-interface for 2D DCT implementations operating on {@code double} data.
	 */
	public interface Double extends Dct2d {
		
		/**
		 * Returns a suitable 1D DCT of the specified size ({@code double}).
		 * @param size the size of the DCT
		 * @return a {@link Dct1d.Double} instance
		 */
		public Dct1d.Double get1dDct(int size);
		
		/**
		 * Performs an "in-place" 2D DCT forward transformation on the supplied data.
		 * The input signal is replaced by the associated DCT spectrum.
		 * @param g the signal to be transformed (modified)
		 */
		public default void forward(double[][] g) {
			transform(g, true);
		}

		/**
		 * Performs an "in-place" 2D DCT inverse transformation on the supplied spectrum.
		 * The input spectrum is replaced by the associated signal.
		 * @param G the spectrum to be transformed (modified)
		 */
		public default void inverse(double[][] G) {
			transform(G, false);
		}
		
		/**
		 * Transforms the given 2D array 'in-place'.
		 * 
		 * @param data the input signal or spectrum (modified)
		 * @param forward forward transformation if {@code true}, inverse transformation if {@code false}
		 */
		default void transform(final double[][] data, boolean forward) {
			checkSize(data);
			final int width = data.length;
			final int height = data[0].length;

			// do the rows:
			double[] row = new double[width];
			Dct1d.Double dct1R = get1dDct(width);
			for (int v = 0; v < height; v++) {
				extractRow(data, v, row);
				if (forward)
					dct1R.forward(row);
				else
					dct1R.inverse(row);
				insertRow(data, v, row);
			}

			// do the columns:
			double[] col = new double[height];
			Dct1d.Double dct1C = get1dDct(height);
			for (int u = 0; u < width; u++) {
				extractCol(data, u, col);
				if (forward)
					dct1C.forward(col);
				else
					dct1C.inverse(col);
				insertCol(data, u, col);
			}
		}
		
		default void extractRow(double[][] data, int v, double[] row) {
			final int width = data.length;
			for (int u = 0; u < width; u++) {
				row[u] = data[u][v];
			}
		}

		default void insertRow(double[][] data, int v, double[] row) {
			final int width = data.length;
			for (int u = 0; u < width; u++) {
				data[u][v] = row[u];
			}
		}

		default void extractCol(double[][] data, int u, double[] column) {
			final int height = data[0].length;
			for (int v = 0; v < height; v++) {
				column[v] = data[u][v];
			}
		}

		default void insertCol(double[][] data, int u, double[] column) {
			final int height = data[0].length;
			for (int v = 0; v < height; v++) {
				data[u][v] = column[v];
			}
		}
		
		public default void checkSize(double[][] A) {
			if (A.length != this.getWidth()) 
				throw new IllegalArgumentException(
						String.format("wrong 2D array width %d (expected %d)", A.length, this.getWidth()));
			if (A[0].length != this.getHeight()) 
				throw new IllegalArgumentException(
						String.format("wrong 2D array height %d (expected %d)", A[0].length, this.getHeight()));
		}
		
	}

}