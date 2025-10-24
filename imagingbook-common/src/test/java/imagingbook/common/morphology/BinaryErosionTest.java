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
import imagingbook.testimages.BinaryTestImage;
import imagingbook.testutils.ImageTestUtils;

public class BinaryErosionTest {

	@Test
	public void test1() {
		ByteProcessor bp = (ByteProcessor) BinaryTestImage.BinaryTest.getImagePlus().getProcessor();
		BinaryErosion op = new BinaryErosion();
		op.applyTo(bp);
		
//		String tmpdir = FileUtils.getTempDirectory();
//		System.out.println(IjUtils.save(bp, tmpdir + "binary-test-erosion1.png"));
		
		ByteProcessor bp2 = (ByteProcessor) BinaryTestImage.BinaryTestErosion1.getImagePlus().getProcessor();
		assertTrue(ImageTestUtils.match(bp2, bp)); 
	}

	@Test
	public void test2() {
		ByteProcessor bp = (ByteProcessor) BinaryTestImage.BinaryTest.getImagePlus().getProcessor();
		BinaryErosion op = new BinaryErosion(TestKernels.H2);
		op.applyTo(bp);
		
//		String tmpdir = FileUtils.getTempDirectory();
//		System.out.println(IjUtils.save(bp, tmpdir + "BinaryTestErosion2.png"));
		
		ByteProcessor bp2 = (ByteProcessor) BinaryTestImage.BinaryTestErosion2.getImagePlus().getProcessor();
		assertTrue(ImageTestUtils.match(bp2, bp)); 
	}
	
	@Test
	public void test3() {
		ByteProcessor bp = (ByteProcessor) BinaryTestImage.Cat.getImagePlus().getProcessor();
		BinaryErosion op = new BinaryErosion(StructuringElements.makeDiskKernel(3));
		op.applyTo(bp);
		
//		String tmpdir = FileUtils.getTempDirectory();
//		System.out.println(IjUtils.save(bp, tmpdir + "CatErosion3.png"));
		
		ByteProcessor bp2 = (ByteProcessor) BinaryTestImage.CatErosion3.getImagePlus().getProcessor();
		assertTrue(ImageTestUtils.match(bp2, bp)); 
	}
}
