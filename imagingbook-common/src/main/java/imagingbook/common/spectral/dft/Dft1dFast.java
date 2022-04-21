package imagingbook.common.spectral.dft;

import org.jtransforms.fft.DoubleFFT_1D;
import org.jtransforms.fft.FloatFFT_1D;

import imagingbook.common.math.Matrix;

/**
 * FFT (fast) implementation of the DFT, based on the JTransforms package
 * by Piotr Wendykier (see <a href="https://github.com/wendykierp/JTransforms">
 * https://github.com/wendykierp/JTransforms</a>).
 */
public abstract class Dft1dFast {
	
	final int M;			// size (length) of the signal or spectrum
	final ScalingMode sm;	
	
	private Dft1dFast(int M, ScalingMode sm) {
		this.M = M;
		this.sm = sm;
	}
	
	// ----------------------------------------------------------------------
	
	/**
	 * One-dimensional FFT implementation using single precision (float) data. 
	 */
	public static class Float extends Dft1dFast implements Dft1d.Float {
	
		private final float[] A;		// temporary array for FFT composed of re/im values
		private final FloatFFT_1D fft;
		
		public Float(int M, ScalingMode sm) {
			super(M, sm);
			this.A = new float[2 * M];
			this.fft = new FloatFFT_1D(M);
		}
		
		public Float(int M) {
			this(M, ScalingMode.DEFAULT);
		}
		
		@Override
		public void forward(float[] gRe, float[] gIm) {
			checkSize(gRe, gIm, M);
//			float scale = (float) sm.getScale(M, true);
//			setupA(gRe, gIm, A);	
//			fft.complexForward(A);
//			decompA(A, gRe, gIm, scale);
			transform(gRe, gIm, true);
		}
		
		@Override
		public void inverse(float[] GRe, float[] GIm) {
			checkSize(GRe, GIm, M);
//			float scale = (float) sm.getScale(M, false);
//			setupA(GRe, GIm, A);	
//			fft.complexInverse(A, false);
//			decompA(A, GRe, GIm, scale);
			transform(GRe, GIm, false);
		}
		
		public void transform(float[] inRe, float[] inIm, boolean forward) {
			checkSize(inRe, inIm, M);
			final float scale = (float) sm.getScale(M, forward);
			setupA(inRe, inIm, A);	
			if (forward)
				fft.complexForward(A);
			else
				fft.complexInverse(A, false);
			decompA(A, inRe, inIm, scale);
		}
		
		// (re, im) -> A
		private void setupA(float[] re, float[] im, float[] A) {
			for (int i = 0; i < M; i++) {
				A[2*i] = re[i];
				A[2*i + 1] = im[i];
			}
		}
		
		// A -> (re, im)
		private void decompA(float[] A, float[] re, float[] im, float scale) {
			for (int i = 0; i < M; i++) {
				re[i] = A[2*i] * scale;
				im[i] = A[2*i + 1] * scale;
			}
		}
	}
	
	// ----------------------------------------------------------------------
	
	/**
	 * One-dimensional FFT implementation using double precision data. 
	 */
	public static class Double extends Dft1dFast implements Dft1d.Double{
	
		private final double[] A;		// temporary array for FFT composed of re/im values
		private final DoubleFFT_1D fft;
		
		public Double(int M, ScalingMode sm) {
			super(M, sm);
			this.A = new double[2 * M];
			this.fft = new DoubleFFT_1D(M);
		}
		
		public Double(int M) {
			this(M, ScalingMode.DEFAULT);
		}
		
		@Override
		public void forward(double[] gRe, double[] gIm) {
			checkSize(gRe, gIm, M);
//			double scale = sm.getScale(M, true);
//			setupA(gRe, gIm, A);	
//			fft.complexForward(A);
//			decompA(A, gRe, gIm, scale);
			transform(gRe, gIm, true);
		}
		
		@Override
		public void inverse(double[] GRe, double[] GIm) {
			checkSize(GRe, GIm, M);
//			double scale = sm.getScale(M, false);
//			setupA(GRe, GIm, A);	
//			fft.complexInverse(A, false);
//			decompA(A, GRe, GIm, scale);
			transform(GRe, GIm, false);
		}
		
		@Override
		public void transform(double[] inRe, double[] inIm, boolean forward) {
			checkSize(inRe, inIm, M);
			final double scale = (double) sm.getScale(M, forward);
			setupA(inRe, inIm, A);	
			if (forward)
				fft.complexForward(A);
			else
				fft.complexInverse(A, false);
			decompA(A, inRe, inIm, scale);
		}
		
		// (re, im) -> A
		private void setupA(double[] re, double[] im, double[] A) {
			for (int i = 0; i < M; i++) {
				A[2*i] = re[i];
				A[2*i + 1] = im[i];
			}
		}
		
		// A -> (re, im)
		private void decompA(double[] A, double[] re, double[] im, double scale) {
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
	public static void main(String[] args) {

		System.out.println("******************** Float test (FFT) ********************");
		{
			float[] re = { 1, 2, 3, 4, 5, 6, 7, 8 };
			float[] im = new float[re.length];

			System.out.println("original signal:");
			System.out.println("gRe = " + Matrix.toString(re));
			System.out.println("gIm = " + Matrix.toString(im));

			Dft1d.Float dft = new Dft1dFast.Float(re.length);
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
		System.out.println("******************** Double test (FFT) ********************");

		{
			double[] re = { 1, 2, 3, 4, 5, 6, 7, 8 };
			double[] im = new double[re.length];

			System.out.println("original signal:");
			System.out.println("gRe = " + Matrix.toString(re));
			System.out.println("gIm = " + Matrix.toString(im));

			Dft1d.Double dft = new Dft1dFast.Double(re.length);
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
