package imagingbook.spectral.dct;

import java.util.Random;

import org.junit.Test;

import imagingbook.common.math.Matrix;
import imagingbook.spectral.TestUtils;
import imagingbook.testutils.NumericTestUtils;

public class Dct2dTest {

	@Test	// compares 2D DFT obtained width direct and fast methods (float)
	public void testDirectFastFloat() {
		Random rg = new Random(17);
		float TOL = 1E-4f;		// reduced accuracy!
		int n = 50;
		int width  = 1;
		int height = n;
		for (int i = 0; i < n; i++) {
//			System.out.println("w = " + width + " h = " + height);
			float[][] reOrig = TestUtils.makeRandomArrayFloat(width, height, rg);
			
			// direct DCT:
			float[][] reD = Matrix.duplicate(reOrig);
			Dct2d.Float dctD = new Dct2dDirect.Float(width, height);
			dctD.forward(reD);
			
			// fast DFT (FFT):
			float[][] reF = Matrix.duplicate(reOrig);
			Dct2d.Float dctF = new Dct2dFast.Float(width, height);
			dctF.forward(reF);

			NumericTestUtils.assert2dArrayEquals(reD, reF, TOL);
			
			width++;
			height--;
		}
	}
	
	@Test	// compares 2D DCT obtained width direct and fast methods (double)
	public void testDirectFastDouble() {
		Random rg = new Random(17);
		double TOL = 1E-12;
		int n = 50;
		int width  = 1;
		int height = n;
		for (int i = 0; i < n; i++) {
//			System.out.println("w = " + width + " h = " + height);
			double[][] reOrig = TestUtils.makeRandomArrayDouble(width, height, rg);
			
			// direct DFT:
			double[][] reD = Matrix.duplicate(reOrig);
			Dct2d.Double dctD = new Dct2dDirect.Double(width, height);
			dctD.forward(reD);
			
			// fast DFT (FFT):
			double[][] reF = Matrix.duplicate(reOrig);
			Dct2d.Double dctF = new Dct2dFast.Double(width, height);
			dctF.forward(reF);

			NumericTestUtils.assert2dArrayEquals(reD, reF, TOL);
			
			width++;
			height--;
		}
	}
}
