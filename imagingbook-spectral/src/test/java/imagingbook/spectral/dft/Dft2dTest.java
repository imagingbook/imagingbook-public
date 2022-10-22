package imagingbook.spectral.dft;

import java.util.Random;

import org.junit.Test;

import imagingbook.common.math.Matrix;
import imagingbook.testutils.NumericTestUtils;

public class Dft2dTest {
	
	@Test	// compares 2D DFT obtained width direct and fast methods (float)
	public void testDirectFastFloat() {
		Random rg = new Random(17);
		float TOL = 1E-6f;
		int n = 50;
		int width  = 1;
		int height = n;
		for (int i = 0; i < n; i++) {
//			System.out.println("w = " + width + " h = " + height);
			float[][] reOrig = makeRandomArrayFloat(width, height, rg);
			float[][] imOrig = makeRandomArrayFloat(width, height, rg);
			
			// direct DFT:
			float[][] reD = Matrix.duplicate(reOrig);
			float[][] imD = Matrix.duplicate(imOrig);
			Dft2d.Float dftD = new Dft2dDirect.Float();
			dftD.forward(reD, imD);
			
			// fast DFT (FFT):
			float[][] reF = Matrix.duplicate(reOrig);
			float[][] imF = Matrix.duplicate(imOrig);
			Dft2d.Float dftF = new Dft2dFast.Float();
			dftF.forward(reF, imF);

			NumericTestUtils.assertArrayEquals(reD, reF, TOL);
			NumericTestUtils.assertArrayEquals(imD, imF, TOL);
			
			width++;
			height--;
		}
	}
	
	@Test	// compares 2D DFT obtained width direct and fast methods (double)
	public void testDirectFastDouble() {
		Random rg = new Random(17);
		double TOL = 1E-12;
		int n = 50;
		int width  = 1;
		int height = n;
		for (int i = 0; i < n; i++) {
//			System.out.println("w = " + width + " h = " + height);
			double[][] reOrig = makeRandomArrayDouble(width, height, rg);
			double[][] imOrig = makeRandomArrayDouble(width, height, rg);
			
			// direct DFT:
			double[][] reD = Matrix.duplicate(reOrig);
			double[][] imD = Matrix.duplicate(imOrig);
			Dft2d.Double dftD = new Dft2dDirect.Double();
			dftD.forward(reD, imD);
			
			// fast DFT (FFT):
			double[][] reF = Matrix.duplicate(reOrig);
			double[][] imF = Matrix.duplicate(imOrig);
			Dft2d.Double dftF = new Dft2dFast.Double();
			dftF.forward(reF, imF);

			NumericTestUtils.assertArrayEquals(reD, reF, TOL);
			NumericTestUtils.assertArrayEquals(imD, imF, TOL);
			
			width++;
			height--;
		}
	}
	
	// ------------------------------------------------------------------

	static float[][] makeRandomArrayFloat(int n0, int n1, Random rg) {
		float[][] a = new float[n0][n1];
		for (int i = 0; i < n0; i++) {
			for (int j = 0; j < n1; j++) {
				a[i][j] = 2 * rg.nextFloat() - 1;
			}
		}
		return a;
	}
	
	static double[][] makeRandomArrayDouble(int n0, int n1, Random rg) {
		double[][] a = new double[n0][n1];
		for (int i = 0; i < n0; i++) {
			for (int j = 0; j < n1; j++) {
				a[i][j] = 2 * rg.nextDouble() - 1;
			}
		}
		return a;
	}

}
