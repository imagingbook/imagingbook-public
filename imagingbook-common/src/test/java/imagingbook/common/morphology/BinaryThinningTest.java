/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.morphology;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ij.process.ByteProcessor;
import imagingbook.testimages.BinaryTestImage;
import imagingbook.testutils.ImageTestUtils;

public class BinaryThinningTest {
	
	@Test
	public void test1() {
		ByteProcessor bp = (ByteProcessor) BinaryTestImage.Cat.getImage().getProcessor();
		
		BinaryThinning thinning = new BinaryThinning();
		thinning.applyTo(bp);
		
		assertTrue(thinning.isComplete());
		assertEquals(12, thinning.getIterations());
		
		ByteProcessor bp2 = (ByteProcessor) BinaryTestImage.CatThinning.getImage().getProcessor();
		assertTrue(ImageTestUtils.match(bp2, bp));
	}
	
	@Test
	public void test2() {
		ByteProcessor bp = (ByteProcessor) BinaryTestImage.BinaryTest.getImage().getProcessor();

		BinaryThinning thinning = new BinaryThinning();
		thinning.applyTo(bp);
		
//		String tmpdir = FileUtils.getTempDirectory();
//		System.out.println(IjUtils.save(bp, tmpdir + "BinaryTestThinning.png"));
		
		ByteProcessor bp2 = (ByteProcessor) BinaryTestImage.BinaryTestThinning.getImage().getProcessor();
		assertTrue(ImageTestUtils.match(bp2, bp));
	}


}
