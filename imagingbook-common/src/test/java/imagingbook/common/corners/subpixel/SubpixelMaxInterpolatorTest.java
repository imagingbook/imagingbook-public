package imagingbook.common.corners.subpixel;

import org.junit.Assert;
import org.junit.Test;

public class SubpixelMaxInterpolatorTest {
	
	static float DELTA = 1E-6f;
	
	static float[] samples1 = {
			16,9,7,
			11,8,15,
			14,12,10}; // = s_0,...,s_8
	
	static float[] samples2 = {
			40229.785156f, 33941.535156f, 25963.150391f, 
			39558.175781f, 39078.843750f, 33857.863281f, 
			39861.664063f, 38746.250000f, 33652.839844f};  // = s_0,...,s_8

	// ------------------------------------------------------------------------
	
	@Test
	public void testQuadraticTaylor() {
		SubpixelMaxInterpolator interp = new SubpixelMaxInterpolator.QuadraticTaylor();
		float[] expected1 = {-0.38320211f, 0.08748906f, 16.59667587f};
		Assert.assertArrayEquals(expected1, interp.getMax(samples1), DELTA);
	}
	
	@Test
	public void testQuadraticLeastSquares() {
		SubpixelMaxInterpolator interp = new SubpixelMaxInterpolator.QuadraticLeastSquares();
		float[] expected1 = {-0.41613588f, 0.32979476f, 15.65628719f};
		Assert.assertArrayEquals(expected1, interp.getMax(samples1), DELTA);
	}
	
	@Test
	public void testQuartic() {
		SubpixelMaxInterpolator interp = new SubpixelMaxInterpolator.Quartic();
		float[] expected1 = {-0.40573445f, 0.11285823f, 16.62036324f};
		Assert.assertArrayEquals(expected1, interp.getMax(samples1), DELTA);
	}
	
}
