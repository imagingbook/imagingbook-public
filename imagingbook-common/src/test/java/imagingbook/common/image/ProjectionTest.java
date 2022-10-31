package imagingbook.common.image;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ShortProcessor;
import imagingbook.core.resource.ImageResource;
import imagingbook.sampleimages.GeneralSampleImage;

public class ProjectionTest {
	
	static double TOL = 1e-6;
	
	static ImageResource ir = GeneralSampleImage.Boats;
	static double pH1 = 57489;
	static double pH2 = 98093;
	static double pV1 = 7944;
	static double pV2 = 71637;

	@Test
	public void testByte() {
		ByteProcessor ip = (ByteProcessor) ir.getImage().getProcessor();
		Projection proj = new Projection(ip);
		
		double[] pHor = proj.getHorizontal();
		double[] pVer = proj.getVertical();
		assertNotNull(pHor);
		assertNotNull(pVer);
		
		assertEquals(pH1, pHor[0], TOL);
		assertEquals(pH2, pHor[ip.getHeight() / 2], TOL);
		
		assertEquals(pV1, pVer[0], TOL);
		assertEquals(pV2, pVer[ip.getWidth() / 2], TOL);
	}
	
	@Test
	public void testShort() {
		ShortProcessor ip = ir.getImage().getProcessor().convertToShortProcessor(false);
		Projection proj = new Projection(ip);
		
		double[] pHor = proj.getHorizontal();
		double[] pVer = proj.getVertical();
		assertNotNull(pHor);
		assertNotNull(pVer);
		
		assertEquals(pH1, pHor[0], TOL);
		assertEquals(pH2, pHor[ip.getHeight() / 2], TOL);
		
		assertEquals(pV1, pVer[0], TOL);
		assertEquals(pV2, pVer[ip.getWidth() / 2], TOL);
	}
	
	@Test
	public void testFloat() {
		FloatProcessor ip = ir.getImage().getProcessor().convertToFloatProcessor();
		Projection proj = new Projection(ip);
		
		double[] pHor = proj.getHorizontal();
		double[] pVer = proj.getVertical();
		assertNotNull(pHor);
		assertNotNull(pVer);
		
		assertEquals(pH1, pHor[0], TOL);
		assertEquals(pH2, pHor[ip.getHeight() / 2], TOL);
		
		assertEquals(pV1, pVer[0], TOL);
		assertEquals(pV2, pVer[ip.getWidth() / 2], TOL);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testColor() {
		ColorProcessor ip = new ColorProcessor(10, 10);
		@SuppressWarnings("unused")
		Projection proj = new Projection(ip);
	}
}
