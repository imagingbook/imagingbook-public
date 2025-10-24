/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.morphology;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ij.process.ByteProcessor;
import imagingbook.common.geometry.basic.NeighborhoodType2D;
import imagingbook.testimages.BinaryTestImage;
import imagingbook.testutils.ImageTestUtils;

public class BinaryOutlineTest {

	@Test
	public void test1A() {	// 4-neighborhood
		ByteProcessor bp = (ByteProcessor) BinaryTestImage.BinaryTest.getImagePlus().getProcessor();
		BinaryOutline op = new BinaryOutline(NeighborhoodType2D.N4);
		op.applyTo(bp);
		
//		String tmpdir = FileUtils.getTempDirectory();
//		System.out.println(IjUtils.save(bp, tmpdir + "BinaryTestOutlineNH4.png"));
		
		ByteProcessor bp2 = (ByteProcessor) BinaryTestImage.BinaryTestOutlineNH4.getImagePlus().getProcessor();
		assertTrue(ImageTestUtils.match(bp2, bp)); 
	}
	
	@Test
	public void test1B() {	// 4-neighborhood
		ByteProcessor bp = (ByteProcessor) BinaryTestImage.BinaryTest.getImagePlus().getProcessor();
		BinaryOutline op = new BinaryOutline(NeighborhoodType2D.N8);
		op.applyTo(bp);
		
//		String tmpdir = FileUtils.getTempDirectory();
//		System.out.println(IjUtils.save(bp, tmpdir + "BinaryTestOutlineNH8.png"));
		
		ByteProcessor bp2 = (ByteProcessor) BinaryTestImage.BinaryTestOutlineNH8.getImagePlus().getProcessor();
		assertTrue(ImageTestUtils.match(bp2, bp)); 
	}
	
	@Test
	public void test2A() {	// 4-neighborhood
		ByteProcessor bp = (ByteProcessor) BinaryTestImage.Cat.getImagePlus().getProcessor();
		BinaryOutline op = new BinaryOutline(NeighborhoodType2D.N4);
		op.applyTo(bp);
		
//		String tmpdir = FileUtils.getTempDirectory();
//		System.out.println(IjUtils.save(bp, tmpdir + "CatOutlineNH4.png"));
		
		ByteProcessor bp2 = (ByteProcessor) BinaryTestImage.CatOutlineNH4.getImagePlus().getProcessor();
		assertTrue(ImageTestUtils.match(bp2, bp)); 
	}
	
	@Test
	public void test2B() {	// 8-neighborhood
		ByteProcessor bp = (ByteProcessor) BinaryTestImage.Cat.getImagePlus().getProcessor();
		BinaryOutline op = new BinaryOutline(NeighborhoodType2D.N8);
		op.applyTo(bp);
		
//		String tmpdir = FileUtils.getTempDirectory();
//		System.out.println(IjUtils.save(bp, tmpdir + "CatOutlineNH8.png"));
		
		ByteProcessor bp2 = (ByteProcessor) BinaryTestImage.CatOutlineNH8.getImagePlus().getProcessor();
		assertTrue(ImageTestUtils.match(bp2, bp)); 
	}

}
