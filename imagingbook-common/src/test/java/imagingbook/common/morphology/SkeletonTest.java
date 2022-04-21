package imagingbook.common.morphology;

import static imagingbook.common.ij.IjUtils.match;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.DATA.GeneralTestImage;
import imagingbook.core.resource.ImageResource;

public class SkeletonTest {
	

	@Test
	public void test1() {
	
		ImageResource origRes = GeneralTestImage.Cat;
		ImageResource resultRes = GeneralTestImage.CatSkeleton;
		
		ImageProcessor origIp = origRes.getImage().getProcessor();
		ImageProcessor resultIp = resultRes.getImage().getProcessor();
		
		BinaryThinning thinning = new BinaryThinning();
		thinning.applyTo((ByteProcessor)origIp);
		
		int k = thinning.getIterations();
		assertEquals("thinning iterations expected", 12, k);
		assertTrue("results must match", match(origIp, resultIp));
	}

}
