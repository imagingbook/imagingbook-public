package imagingbook.common.spectral.dft;

import imagingbook.common.math.Matrix;

/**
 * Direct (slow) implementation of the 1-dimensional DFT
 * using tabulated sine and cosine values.
 * @author WB
 */
public abstract class Dft1dDirect {
	
	final int M;			// size (length) of the signal or spectrum
	final ScalingMode sm;	
	final double[] cosTable;
	final double[] sinTable;
	
	private Dft1dDirect(int M, ScalingMode sm) {
		this.M = M;
		this.sm = sm;
		this.cosTable = makeCosTable();
		this.sinTable = makeSinTable();
	}
	
	private double[] makeCosTable() {
		double[] cosTable = new double[M];
		double theta = 2 * Math.PI / M;
		for (int i = 0; i < M; i++) {
			cosTable[i] = Math.cos(theta * i);
		}
		return cosTable;
	}

	private double[] makeSinTable() {
		double[] sinTable = new double[M];
		double theta = 2 * Math.PI / M;
		for (int i = 0; i < M; i++) {
			sinTable[i] = Math.sin(theta * i);
		}
		return sinTable;
	}
	
	// ----------------------------------------------------------------------
	
	/**
	 * One-dimensional DFT implementation using single precision (float) data. 
	 */
	public static class Float extends Dft1dDirect implements Dft1d.Float {
		
		private final float[] outRe;
		private final float[] outIm;
		
		public Float(int M, ScalingMode sm) {
			super(M, sm);
			this.outRe = new float[M];
			this.outIm = new float[M];
		}
		
		public Float(int M) {
			this(M, ScalingMode.DEFAULT);
		}
		
		@Override
		public void forward(float[] gRe, float[] gIm) {
			checkSize(gRe, gIm, M);
			transform(gRe, gIm, true);
		}
		
		@Override
		public void inverse(float[] GRe, float[] GIm) {
			checkSize(GRe, GIm, M);
			transform(GRe, GIm, false);
		}
		
		public void transform(float[] inRe, float[] inIm, boolean forward) {
			checkSize(inRe, inIm, M);
			final double scale = sm.getScale(M, forward);
			for (int u = 0; u < M; u++) {
				double sumRe = 0;
				double sumIm = 0;
				for (int m = 0; m < M; m++) {
					final double re = inRe[m];
					final double im = (inIm == null) ? 0 : inIm[m];
					final int k = (u * m) % M;
					final double cosPhi = cosTable[k];
					final double sinPhi = (forward) ? -sinTable[k] : sinTable[k];
					// complex multiplication: (gRe + i gIm) * (cosPhi + i sinPhi)
					sumRe += re * cosPhi - im * sinPhi;
					sumIm += re * sinPhi + im * cosPhi;
				}
				outRe[u] = (float) (scale * sumRe);	
				outIm[u] = (float) (scale * sumIm);
			}
			System.arraycopy(outRe, 0, inRe, 0, M);
			System.arraycopy(outIm, 0, inIm, 0, M);
		}
	}

	// ----------------------------------------------------------------------
	
	/**
	 * One-dimensional DFT implementation using double precision data. 
	 */
	public static class Double extends Dft1dDirect implements Dft1d.Double {
		
		private final double[] outRe;
		private final double[] outIm;
		
		public Double(int M, ScalingMode sm) {
			super(M, sm);
			this.outRe = new double[M];
			this.outIm = new double[M];
		}
		
		public Double(int M) {
			this(M, ScalingMode.DEFAULT);
		}
		
		@Override
		public void forward(double[] gRe, double[] gIm) {
			checkSize(gRe, gIm, M);
			transform(gRe, gIm, true);
		}
		
		@Override
		public void inverse(double[] GRe, double[] GIm) {
			checkSize(GRe, GIm, M);
			transform(GRe, GIm, false);
		}
		
		public void transform(double[] inRe, double[] inIm, boolean forward) {
			checkSize(inRe, inIm, M);
			final double scale = sm.getScale(M, forward);
			for (int u = 0; u < M; u++) {
				double sumRe = 0;
				double sumIm = 0;
				for (int m = 0; m < M; m++) {
					final double re = inRe[m];
					final double im = (inIm == null) ? 0 : inIm[m];
					final int k = (u * m) % M;
					final double cosPhi = cosTable[k];
					final double sinPhi = (forward) ? -sinTable[k] : sinTable[k];
					// complex multiplication: (gRe + i gIm) * (cosPhi + i sinPhi)
					sumRe += re * cosPhi - im * sinPhi;
					sumIm += re * sinPhi + im * cosPhi;
				}
				outRe[u] = scale * sumRe;	
				outIm[u] = scale * sumIm;
			}
			System.arraycopy(outRe, 0, inRe, 0, M);
			System.arraycopy(outIm, 0, inIm, 0, M);
		}
		
	}

	// ----------------------------------------------------------------------

	/*
	 * Direct implementation of the one-dimensional DFT for arbitrary signal lengths.
	 * This DFT uses the same definition as Mathematica. Example:
	 * Fourier[{1, 2, 3, 4, 5, 6, 7, 8}, FourierParameters -> {0, -1}]:
		{12.7279 + 0. i, 
		-1.41421 + 3.41421 i, 
		-1.41421 + 1.41421 i, 
		-1.41421 + 0.585786 i, 
		-1.41421 + 0. i, 
		-1.41421 - 0.585786 i, 
		-1.41421 - 1.41421 i, 
		-1.41421 - 3.41421 i}
	 */

	//test example
	public static void main(String[] args) {

		System.out.println("******************** Float test (DFT) ********************");
		{
			float[] re = { 1, 2, 3, 4, 5, 6, 7, 8 };
			float[] im = new float[re.length];

			System.out.println("original signal:");
			System.out.println("gRe = " + Matrix.toString(re));
			System.out.println("gIm = " + Matrix.toString(im));

			Dft1d.Float dft = new Dft1dDirect.Float(re.length);
			dft.forward(re, im);
//			float[] GRe = dft.getRe();
//			float[] GIm = dft.getIm();

			System.out.println("DFT spectrum:");
			System.out.println("GRe = " + Matrix.toString(re));
			System.out.println("GIm = " + Matrix.toString(im));

			dft.inverse(re, im);

			System.out.println("reconstructed signal:");
			System.out.println("gRe' = " + Matrix.toString(re));
			System.out.println("gIm' = " + Matrix.toString(im));
		}
		
		System.out.println();
		System.out.println("******************** Double test (DFT) ********************");

		{
			double[] re = { 1, 2, 3, 4, 5, 6, 7, 8 };
			double[] im = new double[re.length];

			System.out.println("original signal:");
			System.out.println("gRe = " + Matrix.toString(re));
			System.out.println("gIm = " + Matrix.toString(im));

			Dft1d.Double dft = new Dft1dDirect.Double(re.length);
			dft.forward(re, im);

			System.out.println("DFT spectrum:");
			System.out.println("GRe = " + Matrix.toString(re));
			System.out.println("GIm = " + Matrix.toString(im));

			dft.inverse(re, im);

			System.out.println("reconstructed signal:");
			System.out.println("gRe' = " + Matrix.toString(re));
			System.out.println("gIm' = " + Matrix.toString(im));
		}
	}

}
