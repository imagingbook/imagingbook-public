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
		ByteProcessor bp = (ByteProcessor) BinaryTestImage.Cat.getImagePlus().getProcessor();
		
		BinaryThinning thin = new BinaryThinning();
		thin.applyTo(bp);
		
		assertTrue(thin.isComplete());
		assertEquals(12, thin.getIterations());
		
		ByteProcessor bp2 = (ByteProcessor) BinaryTestImage.CatThinning.getImagePlus().getProcessor();
		assertTrue(ImageTestUtils.match(bp2, bp));
	}
	
	@Test
	public void test2() {
		ByteProcessor bp = (ByteProcessor) BinaryTestImage.BinaryTest.getImagePlus().getProcessor();

		BinaryThinning thin = new BinaryThinning();
		thin.applyTo(bp);
		
//		String tmpdir = FileUtils.getTempDirectory();
//		System.out.println(IjUtils.save(bp, tmpdir + "BinaryTestThinning.png"));
		
		ByteProcessor bp2 = (ByteProcessor) BinaryTestImage.BinaryTestThinning.getImagePlus().getProcessor();
		assertTrue(ImageTestUtils.match(bp2, bp));
	}


}
