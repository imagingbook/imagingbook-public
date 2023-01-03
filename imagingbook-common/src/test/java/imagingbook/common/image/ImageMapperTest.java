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
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import imagingbook.common.geometry.mappings.Mapping2D;
import imagingbook.common.geometry.mappings.linear.AffineMapping2D;
import imagingbook.common.geometry.mappings.linear.Rotation2D;
import imagingbook.common.geometry.mappings.linear.Translation2D;
import imagingbook.common.image.interpolation.InterpolationMethod;
import imagingbook.sampleimages.GeneralSampleImage;
import imagingbook.testimages.MappingTestImage;
import imagingbook.testutils.ImageTestUtils;

public class ImageMapperTest {
	
	static float TOL = 1e-3f;
	static ByteProcessor ip1 = (ByteProcessor) GeneralSampleImage.MonasterySmall.getImagePlus().getProcessor();
	static ColorProcessor ip2 = (ColorProcessor) GeneralSampleImage.Clown.getImagePlus().getProcessor();
	
	static OutOfBoundsStrategy OBS = OutOfBoundsStrategy.ZeroValues;
	static InterpolationMethod IPM = InterpolationMethod.Bilinear;

	@Test
	public void testTranslateA() {	// perform integer shifts and check pixel values
		//int dx = 5, dy = 7;	
		for (int dx : Arrays.asList(5, 11, 30)) {
			for (int dy : Arrays.asList(0, 17, 21)) {
				Mapping2D mapping = new Translation2D(dx, dy);	// shift the image to the left and up
				ByteProcessor source = ip1;
				ByteProcessor target = (ByteProcessor) source.duplicate();
				
				ImageMapper mapper = new ImageMapper(mapping, OBS, IPM);
				mapper.map(source, target);
				
				assertEquals(source.getPixel(dx, dy), target.getPixel(0, 0));
			}
		}
	}
	
	@Test
	public void testTranslateB() {	// perform integer shifts and check pixel values
		//int dx = 5, dy = 7;	
		for (int dx : Arrays.asList(5, 11, 30)) {
			for (int dy : Arrays.asList(0, 17, 21)) {
				AffineMapping2D mapping = new Translation2D(-dx, -dy);	// shift the image to the right and down
				
				ByteProcessor source = ip1;
				ByteProcessor target = (ByteProcessor) source.duplicate();
				
				ImageMapper mapper = new ImageMapper(mapping, OBS, IPM);
				mapper.map(source, target);
				
				assertEquals(source.getPixel(0, 0), target.getPixel(dx, dy));
			}
		}
	}
	
	@Test
	public void testAffineGray() {
		AffineMapping2D mapping = new Rotation2D(Math.toRadians(15));
		
		ByteProcessor source = ip1;
		ByteProcessor target = (ByteProcessor) source.duplicate();
		
		ImageMapper mapper = new ImageMapper(mapping, OBS, IPM);
		mapper.map(source, target);
		
//		IjUtils.save(target, "D:/tmp/MonasterySmallRot15.png");
		ByteProcessor ip2 = (ByteProcessor) MappingTestImage.MonasterySmallRot15.getImagePlus().getProcessor();
		assertTrue(ImageTestUtils.match(ip2, target, TOL));
	}
	
	@Test
	public void testAffineColor() {
		AffineMapping2D mapping = new Rotation2D(Math.toRadians(15));
		
		ColorProcessor source = ip2;
		ColorProcessor target = (ColorProcessor) source.duplicate();
		
		ImageMapper mapper = new ImageMapper(mapping, OBS, IPM);
		mapper.map(source, target);
		
//		IjUtils.save(target, "D:/tmp/ClownRot15.png");
		ColorProcessor ip2 = (ColorProcessor) MappingTestImage.ClownRot15.getImagePlus().getProcessor();
		assertTrue(ImageTestUtils.match(ip2, target, TOL));
	}

}
