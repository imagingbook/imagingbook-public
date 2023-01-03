/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
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
		ByteProcessor ip = (ByteProcessor) ir.getImagePlus().getProcessor();
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
		ShortProcessor ip = ir.getImagePlus().getProcessor().convertToShortProcessor(false);
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
		FloatProcessor ip = ir.getImagePlus().getProcessor().convertToFloatProcessor();
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
