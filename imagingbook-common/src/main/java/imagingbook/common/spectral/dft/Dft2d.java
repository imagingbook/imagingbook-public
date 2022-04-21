package imagingbook.common.spectral.dft;

import java.util.Arrays;

import ij.process.ImageProcessor;
import imagingbook.common.math.Matrix;

/**
 * Two-dimensional DFT implementation.
 * @author WB
 *
 */
public abstract class Dft2d {
	
	final ScalingMode sm;
	boolean useFastMode = true;
	
	public void useFastMode(boolean yesOrNo) {
		this.useFastMode = yesOrNo;
	}
	
	private Dft2d() {
		this(ScalingMode.DEFAULT);
	}
	
	private Dft2d(ScalingMode sm) {
		this.sm = sm;
	}
	
	// -----------------------------------------------------------------------
	
	public static class Float extends Dft2d {
		
		/**
		 * Performs an "in-place" 2D DFT forward transformation on the supplied data.
		 * The input signal is replaced by the associated DFT spectrum.
		 * @param gRe real part of the signal (modified)
		 * @param gIm imaginary part of the signal (modified)
		 */
		public void forward(float[][] gRe, float[][] gIm) {
			checkSize(gRe, gIm);
			transform(gRe, gIm, true);
		}
		
		/**
		 * Performs an "in-place" 2D DFT inverse transformation on the supplied spectrum.
		 * The input spectrum is replaced by the associated signal.
		 * @param GRe real part of the spectrum (modified)
		 * @param GIm imaginary part of the spectrum (modified)
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
		void transform(float[][] inRe, float[][] inIm, boolean forward) {
			final int width = inRe.length;
			final int height = inRe[0].length;

			// transform each row (in place):
			final float[] rowRe = new float[width];
			final float[] rowIm = new float[width];
			Dft1d.Float dftRow = 
					useFastMode ? new Dft1dFast.Float(width, sm) : new Dft1dDirect.Float(width, sm);
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
			Dft1d.Float dftCol = 
					useFastMode ? new Dft1dFast.Float(height, sm) : new Dft1dDirect.Float(height, sm);
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
					mag[u][v] = (float) Math.sqrt(a*a + b*b);
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
	
	public static class Double extends Dft2d {
		

		public void forward(double[][] gRe, double[][] gIm) {
			checkSize(gRe, gIm);
			transform(gRe, gIm, true);
		}
		
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
		void transform(double[][] gRe, double[][] gIm, boolean forward) {
			final int width = gRe.length;
			final int height = gRe[0].length;

			// transform each row (in place):
			final double[] rowRe = new double[width];
			final double[] rowIm = new double[width];
			Dft1d.Double dftRow = 
					useFastMode ? new Dft1dFast.Double(width, sm) : new Dft1dDirect.Double(width, sm);
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
			Dft1d.Double dftCol = 
					useFastMode ? new Dft1dFast.Double(height, sm) : new Dft1dDirect.Double(height, sm);
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
					mag[u][v] = Math.sqrt(a*a + b*b);
				}
			}
			return mag;
		}
		
		private void checkSize(double[][] re, double[][] im) {
			if (!Matrix.sameSize(re, im))
				throw new IllegalArgumentException("arrays for real/imagingary parts must be of same size");
		}
		
	}
	
	/**
	 * Static utility method for centering a 2D DFT spectrum.
	 * Modifies the given image by moving the origin of the image to its center
	 * (circularly).
	 * TODO: Check for possible bug when applied to a {@link FloatProcessor}!
	 * 
	 * @param ip an {@link ImageProcessor} instance
	 */
	public static void swapQuadrants(ImageProcessor ip) {
		// swap quadrants Q1 <-> Q3, Q2 <-> Q4
		// Q2 Q1
		// Q3 Q4
		ImageProcessor t1, t2;
		int w = ip.getWidth();
		int h = ip.getHeight();
		int wc = w / 2;
		int hc = h / 2;

		ip.setRoi(wc, 0, w - wc, hc); // Q1
		t1 = ip.crop();
		ip.setRoi(0, hc, wc, h - hc); // Q3
		t2 = ip.crop();

		ip.insert(t1, 0, hc); // swap Q1 <-> Q3
		ip.insert(t2, wc, 0);

		ip.setRoi(0, 0, wc, hc); // Q2
		t1 = ip.crop();
		ip.setRoi(wc, hc, w - wc, h - hc); // Q4
		t2 = ip.crop();

		ip.insert(t1, wc, hc);
		ip.insert(t2, 0, 0);
	}

}
