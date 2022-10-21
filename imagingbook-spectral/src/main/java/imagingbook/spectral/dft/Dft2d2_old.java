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
public abstract class Dft2d2_old {
	
	ScalingMode scalingMode = ScalingMode.DEFAULT;
	boolean fastMode = true;
	
	private Dft2d2_old() {
	}
	
	/**
	 * Sets the scaling mode to be used by this DFT/FFT implementation.
	 * @param scalingMode the scaling mode
	 */
	public void setScalingMode(ScalingMode scalingMode) {
		this.scalingMode = scalingMode;	
	}
	
	/**
	 * Sets a boolean flag to use the FFT (true) or a direct DFT implementation (false).
	 * @param fastMode set true to use the FFT
	 */
	public void setFastMode(boolean fastMode) {
		this.fastMode = fastMode;
	}
	
	// -----------------------------------------------------------------------
	
	/**
	 * Implementation of the 2D DFT/FFT using {@code float} data.
	 */
	public static class Float extends Dft2d2_old {
		
		/**
		 * Constructor.
		 */
		public Float() {
			super();
		}
		
		/**
		 * Performs an "in-place" forward DFT or FFT on the supplied 2D data.
		 * The input signal is replaced by the associated DFT spectrum.
		 * 
		 * @param gRe real part of the signal (modified)
		 * @param gIm imaginary part of the signal (modified)
		 * @see #transform(float[][], float[][], boolean)
		 */
		public void forward(float[][] gRe, float[][] gIm) {
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
		public void inverse(float[][] GRe, float[][] GIm) {
			checkSize(GRe, GIm);
			transform(GRe, GIm, false);
		}
		
		/**
		 * Transforms the given 2D arrays 'in-place'. Separate arrays of identical size
		 * must be supplied for the real and imaginary parts of the signal (forward)
		 * or spectrum (inverse), neither of which may be null.
		 * 
		 * @param inRe real part of the input signal or spectrum (modified)
		 * @param inIm imaginary part of the input signal or spectrum (modified)
		 * @param forward forward transformation if {@code true}, inverse transformation if {@code false}
		 */
		public void transform(float[][] inRe, float[][] inIm, boolean forward) {
			requireNonNull(inRe);
			requireNonNull(inIm);
			final int width = inRe.length;
			final int height = inRe[0].length;

			// transform each row (in place):
			final float[] rowRe = new float[width];
			final float[] rowIm = new float[width];
			Dft1d.Float dftRow = fastMode ? 
					new Dft1dFast.Float(width, scalingMode) : 
					new Dft1dDirect.Float(width, scalingMode);
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
			Dft1d.Float dftCol = fastMode ? 
					new Dft1dFast.Float(height, scalingMode) : 
					new Dft1dDirect.Float(height, scalingMode);
			for (int u = 0; u < width; u++) {
				extractCol(inRe, u, colRe);
				extractCol(inIm, u, colIm);
				dftCol.transform(colRe, colIm, forward);
				insertCol(inRe, u, colRe);
				insertCol(inIm, u, colIm);
			}
		}
		
		// extract the values of row 'v' of 'g' into 'row'
		private void extractRow(float[][] g, int v, float[] row) {
			if (g == null) {			// TODO: check if needed
				Arrays.fill(row, 0);
			}
			else {
				for (int u = 0; u < row.length; u++) {
					row[u] = g[u][v];
				}
			}
		}

		// insert 'row' into row 'v' of 'g'
		private void insertRow(float[][] g, int v, float[] row) {
			for (int u = 0; u < row.length; u++) {
				g[u][v] = row[u];
			}
		}

		// extract the values of column 'u' of 'g' into 'cols'
		private void extractCol(float[][] g, int u, float[] col) {
			if (g == null) {			// TODO: check if needed
				Arrays.fill(col, 0);
			}
			else {
				for (int v = 0; v < col.length; v++) {
					col[v] = g[u][v];
				}
			}
		}

		// insert 'col' into column 'u' of 'g'
		private void insertCol(float[][] g, final int u, float[] col) {
			for (int v = 0; v < col.length; v++) {
				g[u][v] = col[v];
			}
		}
		
		/**
		 * Calculates and returns the magnitude of the supplied complex-valued 2D data.
		 * @param re the real part of the data
		 * @param im the imaginary part of the data
		 * @return a 2D array of magnitude values
		 */
		public float[][] getMagnitude(float[][] re, float[][] im) {
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
		
		private void checkSize(float[][] re, float[][] im) {
			if (!Matrix.sameSize(re, im))
				throw new IllegalArgumentException("arrays for real/imagingary parts must be of same size");
		}
		
	}
	
	// -----------------------------------------------------------------------
	
	/**
	 * Implementation of the 2D DFT/FFT using {@code double} data.
	 */
	public static class Double extends Dft2d2_old {
		
		/**
		 * Constructor.
		 */
		public Double() {
			super();
		}

		/**
		 * Performs an "in-place" forward DFT or FFT on the supplied 2D data.
		 * The input signal is replaced by the associated DFT spectrum.
		 * 
		 * @param gRe real part of the signal (modified)
		 * @param gIm imaginary part of the signal (modified)
		 * @see #transform(double[][], double[][], boolean)
		 */
		public void forward(double[][] gRe, double[][] gIm) {
			checkSize(gRe, gIm);
			transform(gRe, gIm, true);
		}
		
		/**
		 * Performs an "in-place" inverse DFT or FFT on the supplied 2D spectrum.
		 * The input spectrum is replaced by the associated signal.
		 * 
		 * @param GRe real part of the spectrum (modified)
		 * @param GIm imaginary part of the spectrum (modified)
		 * @see #transform(double[][], double[][], boolean)
		 */
		public void inverse(double[][] GRe, double[][] GIm) {
			checkSize(GRe, GIm);
			transform(GRe, GIm, false);
		}
		
		/**
		 * Transforms the given 2D arrays 'in-place', i.e., real and imaginary
		 * arrays of identical size must be supplied, neither may be null.
		 * 
		 * @param gRe
		 * @param gIm
		 * @param forward
		 */
		public void transform(double[][] gRe, double[][] gIm, boolean forward) {
			requireNonNull(gRe);
			requireNonNull(gIm);
			final int width = gRe.length;
			final int height = gRe[0].length;

			// transform each row (in place):
			final double[] rowRe = new double[width];
			final double[] rowIm = new double[width];
			Dft1d.Double dftRow = fastMode ? 
					new Dft1dFast.Double(width, scalingMode) : 
					new Dft1dDirect.Double(width, scalingMode);
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
			Dft1d.Double dftCol = fastMode ? 
					new Dft1dFast.Double(height, scalingMode) : 
					new Dft1dDirect.Double(height, scalingMode);
			for (int u = 0; u < width; u++) {
				extractCol(gRe, u, colRe);
				extractCol(gIm, u, colIm);
				dftCol.transform(colRe, colIm, forward);
				insertCol(gRe, u, colRe);
				insertCol(gIm, u, colIm);
			}
		}
		
		// extract the values of row 'v' of 'g' into 'row'
		private void extractRow(double[][] g, int v, double[] row) {
			if (g == null) {			// TODO: check if needed
				Arrays.fill(row, 0);
			}
			else {
				for (int u = 0; u < row.length; u++) {
					row[u] = g[u][v];
				}
			}
		}

		// insert 'row' into row 'v' of 'g'
		private void insertRow(double[][] g, int v, double[] row) {
			for (int u = 0; u < row.length; u++) {
				g[u][v] = row[u];
			}
		}

		// extract the values of column 'u' of 'g' into 'cols'
		private void extractCol(double[][] g, int u, double[] col) {
			if (g == null) {			// TODO: check if needed
				Arrays.fill(col, 0);
			}
			else {
				for (int v = 0; v < col.length; v++) {
					col[v] = g[u][v];
				}
			}
		}

		// insert 'col' into column 'u' of 'g'
		private void insertCol(double[][] g, final int u, double[] col) {
			for (int v = 0; v < col.length; v++) {
				g[u][v] = col[v];
			}
		}
		
		/**
		 * Calculates and returns the magnitude of the supplied complex-valued 2D data.
		 * 
		 * @param re the real part of the data
		 * @param im the imaginary part of the data
		 * @return a 2D array of magnitude values
		 */
		public double[][] getMagnitude(double[][] re, double[][] im) {
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
		
		private void checkSize(double[][] re, double[][] im) {
			requireNonNull(re);
			requireNonNull(im);
			if (!Matrix.sameSize(re, im))
				throw new IllegalArgumentException("arrays for real/imagingary parts must be of same size");
		}		
	}
	
}
