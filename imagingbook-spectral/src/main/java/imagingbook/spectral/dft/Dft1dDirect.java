/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.spectral.dft;

/**
 * <p>
 * Direct (slow) implementation of the 1-dimensional DFT using tabulated sine
 * and cosine values. Note that this class has no public constructor -
 * instantiate sub-class {@link Dft1dDirect.Float} or {@link Dft1dDirect.Double}
 * instead, as shown below. See Sec. 18.4.1 of [1] for additional details.
 * </p>
 * <p>
 * Usage example (for {@code float} data):
 * </p>
 * <pre>
 * float[] re = {1, 2, 3, 4, 5, 3, 0};
 * float[] im = {0, 0, 0, 0, 0, 0, 0};
 * Dft1d.Float dft = new Dft1dDirect.Float(re.length);
 * dct.forward(re, im);  // re/im now is the DFT spectrum
 * dct.inverse(re, im);  // re/im now is the original signal 
 * ...
 * </pre>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic
 * Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2022/10/23
 */
public abstract class Dft1dDirect extends Dft1dImp {
	
	final double[] WC;	// table of cosine values, WC(k) = cos(2 pi k / M)
	final double[] WS;	// table of sine values,   WC(k) = sin(2 pi k / M)
	
	private Dft1dDirect(int size, ScalingMode sm) {
		super(size, sm);	// sets M
		this.WC = makeCosTable();
		this.WS = makeSinTable();
	}
	
	private double[] makeCosTable() {
		final double[] cosTable = new double[M];
		for (int k = 0; k < M; k++) {
			cosTable[k] = Math.cos(2 * Math.PI * k / M);
		}
		return cosTable;
	}

	private double[] makeSinTable() {
		final double[] sinTable = new double[M];
		for (int k = 0; k < M; k++) {
			sinTable[k] = Math.sin(2 * Math.PI * k / M);
		}
		return sinTable;
	}
	
	// ----------------------------------------------------------------------
	
	/**
	 * One-dimensional DFT implementation using {@code float} data. 
	 */
	public static class Float extends Dft1dDirect implements Dft1d.Float {
		
		private final float[] outRe;
		private final float[] outIm;
		
		/**
		 * Constructor using a specific scaling mode.
		 * 
		 * @param M the size of the data vectors
		 * @param sm the scaling mode
		 */
		public Float(int M, ScalingMode sm) {
			super(M, sm);
			this.outRe = new float[M];
			this.outIm = new float[M];
		}
		
		/**
		 * Constructor using the default scaling mode.
		 * 
		 * @param M the size of the data vectors
		 */
		public Float(int M) {
			this(M, ScalingMode.DEFAULT);
		}
		
		@Override
		public void forward(float[] gRe, float[] gIm) {
			transform(gRe, gIm, true);
		}
		
		@Override
		public void inverse(float[] GRe, float[] GIm) {
			transform(GRe, GIm, false);
		}
		
		@Override
		public void transform(float[] inRe, float[] inIm, boolean forward) {
			checkSize(inRe, inIm);
			final double scale = sm.getScale(M, forward);
			for (int u = 0; u < M; u++) {
				double sumRe = 0;
				double sumIm = 0;
				for (int m = 0; m < M; m++) {
					final double re = inRe[m];
					final double im = inIm[m];
					final int k = (u * m) % M;
					final double cosPhi = WC[k];
					final double sinPhi = (forward) ? -WS[k] : WS[k];
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
	 * One-dimensional DFT implementation using {@code double} data. 
	 */
	public static class Double extends Dft1dDirect implements Dft1d.Double {
		
		private final double[] outRe;
		private final double[] outIm;
		
		/**
		 * Constructor using a specific scaling mode.
		 * 
		 * @param M the size of the data vectors
		 * @param sm the scaling mode
		 */
		public Double(int M, ScalingMode sm) {
			super(M, sm);
			this.outRe = new double[M];
			this.outIm = new double[M];
		}
		
		/**
		 * Constructor using the default scaling mode.
		 * 
		 * @param M the size of the data vectors
		 */
		public Double(int M) {
			this(M, ScalingMode.DEFAULT);
		}
		
		@Override
		public void forward(double[] gRe, double[] gIm) {
			transform(gRe, gIm, true);
		}
		
		@Override
		public void inverse(double[] GRe, double[] GIm) {
			transform(GRe, GIm, false);
		}
		
		@Override
		public void transform(double[] inRe, double[] inIm, boolean forward) {
			checkSize(inRe, inIm);
			final double scale = sm.getScale(M, forward);
			for (int u = 0; u < M; u++) {
				double sumRe = 0;
				double sumIm = 0;
				for (int m = 0; m < M; m++) {
					final double re = inRe[m];
					final double im = inIm[m];
					final int k = (u * m) % M;
					final double cosPhi = WC[k];
					final double sinPhi = (forward) ? -WS[k] : WS[k];
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

//	//test example
//	public static void main(String[] args) {
//
//		System.out.println("******************** Float test (DFT) ********************");
//		{
//			float[] re = { 1, 2, 3, 4, 5, 6, 7, 8 };
//			float[] im = new float[re.length];
//
//			System.out.println("original signal:");
//			System.out.println("gRe = " + Matrix.toString(re));
//			System.out.println("gIm = " + Matrix.toString(im));
//
//			Dft1d.Float dft = new Dft1dDirect.Float(re.length);
//			dft.forward(re, im);
////			float[] GRe = dft.getRe();
////			float[] GIm = dft.getIm();
//
//			System.out.println("DFT spectrum:");
//			System.out.println("GRe = " + Matrix.toString(re));
//			System.out.println("GIm = " + Matrix.toString(im));
//
//			dft.inverse(re, im);
//
//			System.out.println("reconstructed signal:");
//			System.out.println("gRe' = " + Matrix.toString(re));
//			System.out.println("gIm' = " + Matrix.toString(im));
//		}
//		
//		System.out.println();
//		System.out.println("******************** Double test (DFT) ********************");
//
//		{
//			double[] re = { 1, 2, 3, 4, 5, 6, 7, 8 };
//			double[] im = new double[re.length];
//
//			System.out.println("original signal:");
//			System.out.println("gRe = " + Matrix.toString(re));
//			System.out.println("gIm = " + Matrix.toString(im));
//
//			Dft1d.Double dft = new Dft1dDirect.Double(re.length);
//			dft.forward(re, im);
//
//			System.out.println("DFT spectrum:");
//			System.out.println("GRe = " + Matrix.toString(re));
//			System.out.println("GIm = " + Matrix.toString(im));
//
//			dft.inverse(re, im);
//
//			System.out.println("reconstructed signal:");
//			System.out.println("gRe' = " + Matrix.toString(re));
//			System.out.println("gIm' = " + Matrix.toString(im));
//		}
//	}

}
