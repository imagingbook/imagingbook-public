/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.morphology;

import static imagingbook.common.ij.IjUtils.match;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.core.resource.ImageResource;
import imagingbook.sampleimages.GeneralSampleImage;
import imagingbook.testimages.BinaryTestImage;

public class SkeletonTest {
	

	@Test
	public void test1() {
	
		ImageResource origRes = GeneralSampleImage.Cat;
		ImageResource resultRes = BinaryTestImage.CatSkeleton;
		
		ImageProcessor origIp = origRes.getImage().getProcessor();
		ImageProcessor resultIp = resultRes.getImage().getProcessor();
		
		BinaryThinning thinning = new BinaryThinning();
		thinning.applyTo((ByteProcessor)origIp);
		
		int k = thinning.getIterations();
		assertEquals("thinning iterations expected", 12, k);
		assertTrue("results must match", match(origIp, resultIp));
	}

}
