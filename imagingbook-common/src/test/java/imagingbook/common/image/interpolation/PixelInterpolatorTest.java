package imagingbook.common.image.interpolation;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import imagingbook.common.image.OutOfBoundsStrategy;
import imagingbook.common.image.access.RgbAccessor;
import imagingbook.common.image.access.ScalarAccessor;
import imagingbook.common.image.interpolation.PixelInterpolator.InterpolationMethod;
import imagingbook.sampleimages.GeneralSampleImage;
import imagingbook.testutils.ImageTestUtils;

public class PixelInterpolatorTest {
	
	static float TOL = 1e-6f;
	static OutOfBoundsStrategy obs = OutOfBoundsStrategy.NearestBorder;
	
	static final ByteProcessor ip1 = (ByteProcessor) GeneralSampleImage.MonasterySmall.getImage().getProcessor();
	static final ColorProcessor ip2 = (ColorProcessor) GeneralSampleImage.Clown.getImage().getProcessor();

	@Test
	public void testScalarA() {
		ByteProcessor bp = ImageTestUtils.makeRandomByteProcessor(37, 19, 17);
		checkIntegerCoordinatesScalar(bp, InterpolationMethod.NearestNeighbor);
		checkIntegerCoordinatesScalar(bp, InterpolationMethod.Bicubic);
		checkIntegerCoordinatesScalar(bp, InterpolationMethod.Bilinear);
		checkIntegerCoordinatesScalar(bp, InterpolationMethod.CatmullRom);
		checkIntegerCoordinatesScalar(bp, InterpolationMethod.Lanzcos2);
		checkIntegerCoordinatesScalar(bp, InterpolationMethod.Lanzcos3);
//		checkIntegerCoordinatesScalar(bp, InterpolationMethod.CubicBSpline);		// this does not work for approxiators!
//		checkIntegerCoordinatesScalar(bp, InterpolationMethod.MitchellNetravali);	// this does not work for approxiators!
	}
	
	@Test
	public void testVectorA() {
		ColorProcessor cp = ImageTestUtils.makeRandomColorProcessor(37, 19, 17);
		checkIntegerCoordinatesRgb(cp, InterpolationMethod.NearestNeighbor);
		checkIntegerCoordinatesRgb(cp, InterpolationMethod.Bicubic);
		checkIntegerCoordinatesRgb(cp, InterpolationMethod.Bilinear);
		checkIntegerCoordinatesRgb(cp, InterpolationMethod.CatmullRom);
		checkIntegerCoordinatesRgb(cp, InterpolationMethod.Lanzcos2);
		checkIntegerCoordinatesRgb(cp, InterpolationMethod.Lanzcos3);
//		checkIntegerCoordinatesRgb(cp, InterpolationMethod.CubicBSpline);			// this does not work for approxiators!
//		checkIntegerCoordinatesRgb(cp, InterpolationMethod.MitchellNetravali);		// this does not work for approxiators!
	}
	
	@Test
	public void testScalarB() {
		ScalarAccessor ia;
		double x = 3.7;
		double y = 7.1;
		
		ia = ScalarAccessor.create(ip1, obs, InterpolationMethod.Bicubic);
		assertEquals(223.98138f, ia.getVal(x, y), TOL);
		
		ia = ScalarAccessor.create(ip1, obs, InterpolationMethod.BicubicSharp);
		assertEquals(224.00769f, ia.getVal(x, y), TOL);
		
		ia = ScalarAccessor.create(ip1, obs, InterpolationMethod.BicubicSmooth);
		assertEquals(223.97272f, ia.getVal(x, y), TOL);
		
		ia = ScalarAccessor.create(ip1, obs, InterpolationMethod.Bilinear);
		assertEquals(224.5500030517578f, ia.getVal(x, y), TOL);
		
		ia = ScalarAccessor.create(ip1, obs, InterpolationMethod.CatmullRom);
		assertEquals(223.97365f, ia.getVal(x, y), TOL);
		
		ia = ScalarAccessor.create(ip1, obs, InterpolationMethod.CubicBSpline);
		assertEquals(225.39142f, ia.getVal(x, y), TOL);
		
		ia = ScalarAccessor.create(ip1, obs, InterpolationMethod.Lanzcos2);
		assertEquals(227.37834f, ia.getVal(x, y), TOL);
		
		ia = ScalarAccessor.create(ip1, obs, InterpolationMethod.Lanzcos3);
		assertEquals(222.8695f, ia.getVal(x, y), TOL);
		
		ia = ScalarAccessor.create(ip1, obs, InterpolationMethod.Lanzcos4);
		assertEquals(224.20848f, ia.getVal(x, y), TOL);
		
		ia = ScalarAccessor.create(ip1, obs, InterpolationMethod.MitchellNetravali);
		assertEquals(224.50372f, ia.getVal(x, y), TOL);
		
		ia = ScalarAccessor.create(ip1, obs, InterpolationMethod.NearestNeighbor);
		assertEquals(223.0f, ia.getVal(x, y), TOL);
		
	}
	



	
	// --------------------------------------------------------------------
	
	// check if interpolated pixel values at discrete grid points are the same as the original pixels
	private void checkIntegerCoordinatesScalar(ByteProcessor ip, InterpolationMethod ipm) {
		ScalarAccessor ia = ScalarAccessor.create(ip, obs, ipm);
		int w = ia.getWidth();
		int h = ia.getHeight();
		for (int u = 1; u < w - 1; u++) {
			for (int v = 1; v < h - 1; v++) {
				assertEquals(ip.get(u, v), ia.getVal((double)u, (double)v), TOL);
			}
		}
	}
	
	// check if interpolated pixel values at discrete grid points are the same as the original pixels
	private void checkIntegerCoordinatesRgb(ColorProcessor ip, InterpolationMethod ipm) {
		RgbAccessor ia = RgbAccessor.create((ColorProcessor)ip, obs, ipm);
		int w = ia.getWidth();
		int h = ia.getHeight();
		for (int u = 1; u < w - 1; u++) {
			for (int v = 1; v < h - 1; v++) {
				int[] rgb = ip.getPixel(u, v, null);
				for (int k = 0; k < 3; k++) {
					assertEquals(rgb[k], ia.getVal((double)u, (double)v, k), TOL);
				}
			}
		}
	}

}
