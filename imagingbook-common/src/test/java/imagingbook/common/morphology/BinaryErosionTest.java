package imagingbook.common.morphology;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ij.process.ByteProcessor;
import imagingbook.testimages.BinaryTestImage;
import imagingbook.testutils.ImageTestUtils;

public class BinaryErosionTest {

	@Test
	public void test1() {
		ByteProcessor bp = (ByteProcessor) BinaryTestImage.BinaryTest.getImage().getProcessor();
		BinaryErosion op = new BinaryErosion();
		op.applyTo(bp);
		
//		String tmpdir = FileUtils.getTempDirectory();
//		System.out.println(IjUtils.save(bp, tmpdir + "binary-test-erosion1.png"));
		
		ByteProcessor bp2 = (ByteProcessor) BinaryTestImage.BinaryTestErosion1.getImage().getProcessor();
		assertTrue(ImageTestUtils.match(bp2, bp)); 
	}

	@Test
	public void test2() {
		ByteProcessor bp = (ByteProcessor) BinaryTestImage.BinaryTest.getImage().getProcessor();
		BinaryErosion op = new BinaryErosion(TestKernels.H2);
		op.applyTo(bp);
		
//		String tmpdir = FileUtils.getTempDirectory();
//		System.out.println(IjUtils.save(bp, tmpdir + "BinaryTestErosion2.png"));
		
		ByteProcessor bp2 = (ByteProcessor) BinaryTestImage.BinaryTestErosion2.getImage().getProcessor();
		assertTrue(ImageTestUtils.match(bp2, bp)); 
	}
	
	@Test
	public void test3() {
		ByteProcessor bp = (ByteProcessor) BinaryTestImage.Cat.getImage().getProcessor();
		BinaryErosion op = new BinaryErosion(StructuringElements.makeDiskKernel(3));
		op.applyTo(bp);
		
//		String tmpdir = FileUtils.getTempDirectory();
//		System.out.println(IjUtils.save(bp, tmpdir + "CatErosion3.png"));
		
		ByteProcessor bp2 = (ByteProcessor) BinaryTestImage.CatErosion3.getImage().getProcessor();
		assertTrue(ImageTestUtils.match(bp2, bp)); 
	}
}
