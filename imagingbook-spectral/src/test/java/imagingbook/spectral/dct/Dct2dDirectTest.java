package imagingbook.spectral.dct;

import imagingbook.common.math.Matrix;
import imagingbook.spectral.TestUtils;
import imagingbook.testutils.NumericTestUtils;
import org.junit.Test;

import java.util.Random;

public class Dct2dDirectTest {

	@Test
	public void testFloat1() {
		float TOL = 1E-6f;
		float[][] re =  { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 }, { -2, -1, 0 } };
		runTestFloat(re, TOL);
	}
	
	@Test
	public void testDouble1() {
		double TOL = 1E-12;
		double[][] re =  { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 }, { -2, -1, 0 } };
		runTestDouble(re, TOL);
	}
	
	// ---------------------------------------------------------
	
	@Test
	public void testFloat2() {
		Random rg = new Random(17);
		float TOL = 1E-6f;
		int n = 50;
		int width  = 1;
		int height = n;
		for (int i = 0; i < n; i++) {
			float[][] re = TestUtils.makeRandomArrayFloat(width, height, rg);
			runTestFloat(re, TOL);
			width++;
			height--;
		}
	}
	
	@Test
	public void testDouble2() {
		Random rg = new Random(17);
		double TOL = 1E-12;
		int n = 50;
		int width  = 1;
		int height = n;
		for (int i = 0; i < n; i++) {
			double[][] re = TestUtils.makeRandomArrayDouble(width, height, rg);
			runTestDouble(re, TOL);
			width++;
			height--;
		}
	}
	
	// --------------------------------------------------------------
	
	private void runTestFloat(float[][] reOrig, float TOL) {
		int w = reOrig.length;
		int h = reOrig[0].length;
		float[][] re = Matrix.duplicate(reOrig);
		Dct2d.Float dct = new Dct2dDirect.Float(w, h);
		dct.forward(re);
		dct.inverse(re);		
		NumericTestUtils.assert2dArrayEquals(reOrig, re, TOL);
	}
	
	private void runTestDouble(double[][] reOrig, double TOL) {
		int w = reOrig.length;
		int h = reOrig[0].length;
		double[][] re = Matrix.duplicate(reOrig);
		Dct2d.Double dct = new Dct2dDirect.Double(w, h);
		dct.forward(re);
		dct.inverse(re);		
		NumericTestUtils.assert2dArrayEquals(reOrig, re, TOL);
	}

}
