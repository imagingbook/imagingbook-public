package imagingbook.spectral.dft;

import imagingbook.common.math.Matrix;
import imagingbook.spectral.TestUtils;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertArrayEquals;

public class Dft1dTest {

	
	@Test	// compares 2D DFT obtained width direct and fast methods (float)
	public void testDirectFastFloat() {
		Random rg = new Random(17);
		float TOL = 1E-6f;
		int n = 100;
		int width  = 1;
		for (int i = 0; i < n; i++) {
//			System.out.println("w = " + width);
			float[] reOrig = TestUtils.makeRandomVectorFloat(width, rg);
			float[] imOrig = TestUtils.makeRandomVectorFloat(width, rg);
			
			// direct DFT:
			float[] reD = Matrix.duplicate(reOrig);
			float[] imD = Matrix.duplicate(imOrig);
			Dft1d.Float dftD = new Dft1dDirect.Float(width);
			dftD.forward(reD, imD);
			
			// fast DFT (FFT):
			float[] reF = Matrix.duplicate(reOrig);
			float[] imF = Matrix.duplicate(imOrig);
			Dft1d.Float dftF = new Dft1dFast.Float(width);
			dftF.forward(reF, imF);

			assertArrayEquals(reD, reF, TOL);
			assertArrayEquals(imD, imF, TOL);
			
			width++;
		}
	}
	
	@Test	// compares 2D DFT obtained width direct and fast methods (double)
	public void testDirectFastDouble() {
		Random rg = new Random(17);
		double TOL = 1E-12;
		int n = 100;
		int width  = 1;
		for (int i = 0; i < n; i++) {
//			System.out.println("w = " + width);
			double[] reOrig = TestUtils.makeRandomVectorDouble(width, rg);
			double[] imOrig = TestUtils.makeRandomVectorDouble(width, rg);
			
			// direct DFT:
			double[] reD = Matrix.duplicate(reOrig);
			double[] imD = Matrix.duplicate(imOrig);
			Dft1d.Double dftD = new Dft1dDirect.Double(width);
			dftD.forward(reD, imD);
			
			// fast DFT (FFT):
			double[] reF = Matrix.duplicate(reOrig);
			double[] imF = Matrix.duplicate(imOrig);
			Dft1d.Double dftF = new Dft1dFast.Double(width);
			dftF.forward(reF, imF);

			assertArrayEquals(reD, reF, TOL);
			assertArrayEquals(imD, imF, TOL);
			
			width++;
		}
	}

}
