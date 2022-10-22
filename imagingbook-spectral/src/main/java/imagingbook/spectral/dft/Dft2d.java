/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.spectral.dft;

import static java.util.Objects.requireNonNull;

import java.util.Arrays;

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
		public default void transform(float[][] inRe, float[][] inIm, boolean forward) {
			requireNonNull(inRe);
			requireNonNull(inIm);
			final int width = inRe.length;
			final int height = inRe[0].length;

			// transform each row (in place):
			final float[] rowRe = new float[width];
			final float[] rowIm = new float[width];
			Dft1d.Float dftRow = get1dDft(width);
			for (int v = 0; v < height; v++) {
				extractRow(inRe, v, rowRe);
				extractRow(inIm, v, rowIm);
				dftRow.transform(rowRe, rowIm, forward);
				insertRow(inRe, v, rowRe);
				insertRow(inIm, v, rowIm);
			}

			// transform each column (in place):
			final float[] colRe = new float[height];
			final float[] colIm = new float[height];
			Dft1d.Float dftCol = get1dDft(height);
			for (int u = 0; u < width; u++) {
				extractCol(inRe, u, colRe);
				extractCol(inIm, u, colIm);
				dftCol.transform(colRe, colIm, forward);
				insertCol(inRe, u, colRe);
				insertCol(inIm, u, colIm);
			}
		}
		
		public Dft1d.Float get1dDft(int size);
		
		public default void extractRow(float[][] g, int v, float[] row) {
			for (int u = 0; u < row.length; u++) {
				row[u] = g[u][v];
			}
		}
		
		public default void insertRow(float[][] g, int v, float[] row) {
			for (int u = 0; u < row.length; u++) {
				g[u][v] = row[u];
			}
		}
			
		public default void extractCol(float[][] g, int u, float[] col) {
			for (int v = 0; v < col.length; v++) {
				col[v] = g[u][v];
			}
		}
		
		public default void insertCol(float[][] g, final int u, float[] col) {
			for (int v = 0; v < col.length; v++) {
				g[u][v] = col[v];
			}
		}
		
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
		
		/**
		 * Calculates and returns the magnitude of the supplied complex-valued 2D data.
		 * @param re the real part of the data
		 * @param im the imaginary part of the data
		 * @return a 2D array of magnitude values
		 */
		public default  float[][] getMagnitude(float[][] re, float[][] im) {
			checkSize(re, im);
			final int width = re.length;
			final int height = re[0].length;
			float[][] mag = new float[width][height];
			for (int u = 0; u < width; u++) {
				for (int v = 0; v < height; v++) {
					float a = re[u][v];
					float b = im[u][v];
					mag[u][v] = (float) Math.hypot(a, b);
				}
			}
			return mag;
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
		public default void transform(double[][] gRe, double[][] gIm, boolean forward) {
			requireNonNull(gRe);
			requireNonNull(gIm);
			final int width = gRe.length;
			final int height = gRe[0].length;

			// transform each row (in place):
			final double[] rowRe = new double[width];
			final double[] rowIm = new double[width];
			Dft1d.Double dftRow = get1dDft(width);
			for (int v = 0; v < height; v++) {
				extractRow(gRe, v, rowRe);
				extractRow(gIm, v, rowIm);
				dftRow.transform(rowRe, rowIm, forward);
				insertRow(gRe, v, rowRe);
				insertRow(gIm, v, rowIm);
			}

			// transform each column (in place):
			final double[] colRe = new double[height];
			final double[] colIm = new double[height];
			Dft1d.Double dftCol = get1dDft(height);
			for (int u = 0; u < width; u++) {
				extractCol(gRe, u, colRe);
				extractCol(gIm, u, colIm);
				dftCol.transform(colRe, colIm, forward);
				insertCol(gRe, u, colRe);
				insertCol(gIm, u, colIm);
			}
		}
		
		public Dft1d.Double get1dDft(int size);
		
		public default void extractRow(double[][] g, int v, double[] row) {
			if (g == null) {			// TODO: check if needed
				Arrays.fill(row, 0);
			}
			else {
				for (int u = 0; u < row.length; u++) {
					row[u] = g[u][v];
				}
			}
		}
		
		public default void insertRow(double[][] g, int v, double[] row) {
			for (int u = 0; u < row.length; u++) {
				g[u][v] = row[u];
			}
		}
		
		public default void extractCol(double[][] g, int u, double[] col) {
			if (g == null) {			// TODO: check if needed
				Arrays.fill(col, 0);
			}
			else {
				for (int v = 0; v < col.length; v++) {
					col[v] = g[u][v];
				}
			}
		}
		
		public default void insertCol(double[][] g, final int u, double[] col) {
			for (int v = 0; v < col.length; v++) {
				g[u][v] = col[v];
			}
		}
		
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
		
		/**
		 * Calculates and returns the magnitude of the supplied complex-valued 2D data.
		 * 
		 * @param re the real part of the data
		 * @param im the imaginary part of the data
		 * @return a 2D array of magnitude values
		 */
		public default double[][] getMagnitude(double[][] re, double[][] im) {
			checkSize(re, im);
			final int width = re.length;
			final int height = re[0].length;
			double[][] mag = new double[width][height];
			for (int u = 0; u < width; u++) {
				for (int v = 0; v < height; v++) {
					double a = re[u][v];
					double b = im[u][v];
					mag[u][v] = Math.hypot(a, b);
				}
			}
			return mag;
		}
		
		public default void checkSize(double[][] re, double[][] im) {
			if (!Matrix.sameSize(re, im))
				throw new IllegalArgumentException("arrays for real/imagingary parts must be of same size");
		}
	}
	
}
