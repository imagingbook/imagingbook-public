package imagingbook.common.interpolation;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import imagingbook.common.image.OutOfBoundsStrategy;
import imagingbook.common.image.access.RgbAccessor;
import imagingbook.common.image.access.ScalarAccessor;
import imagingbook.common.interpolation.PixelInterpolator.InterpolationMethod;
import imagingbook.sampleimages.GeneralSampleImage;

public class PixelInterpolatorTest {
	
	static float TOL = 1e-6f;
	static OutOfBoundsStrategy obs = OutOfBoundsStrategy.NearestBorder;
	
	static final ByteProcessor ip1 = (ByteProcessor) GeneralSampleImage.MonasterySmall.getImage().getProcessor();
	static final ColorProcessor ip2 = (ColorProcessor) GeneralSampleImage.Clown.getImage().getProcessor();

	@Test
	public void testScalarA() {
		checkIntegerCoordinatesScalar(ip1, InterpolationMethod.NearestNeighbor);
		checkIntegerCoordinatesScalar(ip1, InterpolationMethod.Bicubic);
		checkIntegerCoordinatesScalar(ip1, InterpolationMethod.Bilinear);
		checkIntegerCoordinatesScalar(ip1, InterpolationMethod.CatmullRom);
		checkIntegerCoordinatesScalar(ip1, InterpolationMethod.CubicBSpline);
		checkIntegerCoordinatesScalar(ip1, InterpolationMethod.MitchellNetravali);
		checkIntegerCoordinatesScalar(ip1, InterpolationMethod.Lanzcos2);
		checkIntegerCoordinatesScalar(ip1, InterpolationMethod.Lanzcos3);
	}
	
	@Test
	public void testVectorA() {
		checkIntegerCoordinatesRgb(ip2, InterpolationMethod.NearestNeighbor);
		checkIntegerCoordinatesRgb(ip2, InterpolationMethod.Bicubic);
		checkIntegerCoordinatesRgb(ip2, InterpolationMethod.Bilinear);
		checkIntegerCoordinatesRgb(ip2, InterpolationMethod.CatmullRom);
		checkIntegerCoordinatesRgb(ip2, InterpolationMethod.CubicBSpline);
		checkIntegerCoordinatesRgb(ip2, InterpolationMethod.MitchellNetravali);
		checkIntegerCoordinatesRgb(ip2, InterpolationMethod.Lanzcos2);
		checkIntegerCoordinatesRgb(ip2, InterpolationMethod.Lanzcos3);
	}
	
	@Test
	public void testScalarB() {
		ScalarAccessor ia;
		
		ia = ScalarAccessor.create(ip1, obs, InterpolationMethod.Bicubic);
		assertEquals(223.98138f, ia.getVal(3.7, 7.1), TOL);
		
		ia = ScalarAccessor.create(ip1, obs, InterpolationMethod.Bilinear);
		assertEquals(224.5500030517578f, ia.getVal(3.7, 7.1), TOL);
		
		ia = ScalarAccessor.create(ip1, obs, InterpolationMethod.CatmullRom);
		assertEquals(223.97365f, ia.getVal(3.7, 7.1), TOL);
		
		// TODO: complete
	}
	
	
	// --------------------------------------------------------------------
	
	private void checkIntegerCoordinatesScalar(ByteProcessor ip, InterpolationMethod ipm) {
		ScalarAccessor ia = ScalarAccessor.create(ip, obs, ipm);
		int w = ia.getWidth();
		int h = ia.getHeight();
		for (int u = 1; u < w - 1; u++) {
			for (int v = 1; v < h - 1; v++) {
				assertEquals(ip.get(u, v), ia.getVal(u, v), TOL);
			}
		}
	}
	
	private void checkIntegerCoordinatesRgb(ColorProcessor ip, InterpolationMethod ipm) {
		RgbAccessor ia = RgbAccessor.create((ColorProcessor)ip, obs, ipm);
		int w = ia.getWidth();
		int h = ia.getHeight();
		for (int u = 1; u < w - 1; u++) {
			for (int v = 1; v < h - 1; v++) {
				int[] rgb = ip.getPixel(u, v, null);
				for (int k = 0; k < 3; k++) {
					assertEquals(rgb[k], ia.getVal(u, v, k), TOL);
				}
			}
		}
	}

}
