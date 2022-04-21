/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.spectral.dct;

/**
 * This class provides the functionality for calculating the DCT in 2D.
 * TODO: use generic types?
 * 
 * @author W. Burger
 * @version 2019-12-26
 */
public abstract class Dct2d {
	
	boolean useFastMode = true;
	
	public void useFastMode(boolean yesOrNo) {
		this.useFastMode = yesOrNo;
	}

	private Dct2d() {
	}
	
	// ---------------------------------------------------------------------------------------

	public static class Float extends Dct2d {
		
		public Float() {
		}

		/**
		 * Performs an "in-place" 2D DCT forward transformation on the supplied data.
		 * The input signal is replaced by the associated DCT spectrum.
		 * @param g the signal to be transformed (modified)
		 */
		public void forward(float[][] g) {
			transform(g, true);
		}

		/**
		 * Performs an "in-place" 2D DCT inverse transformation on the supplied spectrum.
		 * The input spectrum is replaced by the associated signal.
		 * @param G the spectrum to be transformed (modified)
		 */
		public void inverse(float[][] G) {
			transform(G, false);
		}

		// in-place 2D DCT
		private void transform(final float[][] data, boolean forward) {
			final int width = data.length;
			final int height = data[0].length;

			// do the rows:
			float[] row = new float[width];
			Dct1d.Float dct1R = 
					useFastMode ? new Dct1dFast.Float(width) : new Dct1dDirect.Float(width);
			for (int v = 0; v < height; v++) {
				//IJ.showProgress(v, height);
				extractRow(data, v, row);
				if (forward)
					dct1R.forward(row);
				else
					dct1R.inverse(row);
				insertRow(data, v, row);
			}

			// do the columns:
			float[] col = new float[height];
			Dct1d.Float dct1C = 
					useFastMode ? new Dct1dFast.Float(height) : new Dct1dDirect.Float(height);
			for (int u = 0; u < width; u++) {
				//IJ.showProgress(u, width);
				extractCol(data, u, col);
				if (forward)
					dct1C.forward(col);
				else
					dct1C.inverse(col);
				insertCol(data, u, col);
			}
		}

		private void extractRow(float[][] data, int v, float[] row) {
			final int width = data.length;
			for (int u = 0; u < width; u++) {
				row[u] = data[u][v];
			}
		}

		private void insertRow(float[][] data, int v, float[] row) {
			final int width = data.length;
			for (int u = 0; u < width; u++) {
				data[u][v] = (float) row[u];
			}
		}

		private void extractCol(float[][] data, int u, float[] column) {
			final int height = data[0].length;
			for (int v = 0; v < height; v++) {
				column[v] = data[u][v];
			}
		}

		private void insertCol(float[][] data, int u, float[] column) {
			final int height = data[0].length;
			for (int v = 0; v < height; v++) {
				data[u][v] = (float) column[v];
			}
		}
	}

}


