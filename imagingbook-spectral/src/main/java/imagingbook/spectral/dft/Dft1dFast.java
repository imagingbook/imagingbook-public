/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.spectral.dft;

import org.jtransforms.fft.DoubleFFT_1D;
import org.jtransforms.fft.FloatFFT_1D;

/**
 * <p>
 * FFT (fast) implementation of the DFT, based on the JTransforms package
 * by Piotr Wendykier (see <a href="https://github.com/wendykierp/JTransforms">
 * https://github.com/wendykierp/JTransforms</a>).
 * See Sec. 18.4.2 of [1] for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing - An Algorithmic Approach</em>,
 * 3rd ed, Springer (2022).
 * </p>
 */
public abstract class Dft1dFast extends Dft1dImp {
	
	private Dft1dFast(int size, ScalingMode sm) {
		super(size, sm);
	}
	
	// ----------------------------------------------------------------------
	
	/**
	 * One-dimensional FFT implementation using {@code float} data. 
	 */
	public static class Float extends Dft1dFast implements Dft1d.Float {
	
		private final float[] A;		// temporary array for FFT composed of re/im values
		private final FloatFFT_1D fft;
		
		/**
		 * Constructor using a specific scaling mode.
		 * @param M the size of the data vectors
		 * @param sm the scaling mode
		 */
		public Float(int M, ScalingMode sm) {
			super(M, sm);
			this.A = new float[2 * M];
			this.fft = new FloatFFT_1D(M);
		}
		
		/**
		 * Constructor using the default scaling mode.
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
			final float scale = (float) sm.getScale(M, forward);
			composeA(inRe, inIm, A);	
			if (forward)
				fft.complexForward(A);
			else
				fft.complexInverse(A, false);
			decomposeA(A, inRe, inIm, scale);
		}
		
		// (re, im) -> A
		private void composeA(float[] re, float[] im, float[] A) {
			for (int i = 0; i < M; i++) {
				A[2*i] = re[i];
				A[2*i + 1] = im[i];
			}
		}
		
		// A -> (re, im)
		private void decomposeA(float[] A, float[] re, float[] im, float scale) {
			for (int i = 0; i < M; i++) {
				re[i] = A[2*i] * scale;
				im[i] = A[2*i + 1] * scale;
			}
		}
	}
	
	// ----------------------------------------------------------------------
	
	/**
	 * One-dimensional FFT implementation using {@code double} data. 
	 */
	public static class Double extends Dft1dFast implements Dft1d.Double {
	
		private final double[] A;		// temporary array for FFT composed of re/im values
		private final DoubleFFT_1D fft;
		
		/**
		 * Constructor using a specific scaling mode.
		 * @param M the size of the data vectors
		 * @param sm the scaling mode
		 */
		public Double(int M, ScalingMode sm) {
			super(M, sm);
			this.A = new double[2 * M];
			this.fft = new DoubleFFT_1D(M);
		}
		
		/**
		 * Constructor using the default scaling mode.
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
			composeA(inRe, inIm, A);	
			if (forward)
				fft.complexForward(A);
			else
				fft.complexInverse(A, false);
			decomposeA(A, inRe, inIm, scale);
		}
		
		// (re, im) -> A
		private void composeA(double[] re, double[] im, double[] A) {
			for (int i = 0; i < M; i++) {
				A[2*i] = re[i];
				A[2*i + 1] = im[i];
			}
		}
		
		// A -> (re, im)
		private void decomposeA(double[] A, double[] re, double[] im, double scale) {
			for (int i = 0; i < M; i++) {
				re[i] = A[2*i] * scale;
				im[i] = A[2*i + 1] * scale;
			}
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
//	public static void main(String[] args) {
//
//		System.out.println("******************** Float test (FFT) ********************");
//		{
//			float[] re = { 1, 2, 3, 4, 5, 6, 7, 8 };
//			float[] im = new float[re.length];
//
//			System.out.println("original signal:");
//			System.out.println("gRe = " + Matrix.toString(re));
//			System.out.println("gIm = " + Matrix.toString(im));
//
//			Dft1d.Float dft = new Dft1dFast.Float(re.length);
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
//		
//		System.out.println();
//		System.out.println("******************** Double test (FFT) ********************");
//
//		{
//			double[] re = { 1, 2, 3, 4, 5, 6, 7, 8 };
//			double[] im = new double[re.length];
//
//			System.out.println("original signal:");
//			System.out.println("gRe = " + Matrix.toString(re));
//			System.out.println("gIm = " + Matrix.toString(im));
//
//			Dft1d.Double dft = new Dft1dFast.Double(re.length);
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
