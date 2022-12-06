package imagingbook.common.morphology;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ij.process.ByteProcessor;
import imagingbook.testimages.BinaryTestImage;
import imagingbook.testutils.ImageTestUtils;

public class BinaryDilationTest {

	@Test
	public void test1() {
		ByteProcessor bp = (ByteProcessor) BinaryTestImage.BinaryTest.getImagePlus().getProcessor();
		BinaryDilation op = new BinaryDilation();
		op.applyTo(bp);
		
//		String tmpdir = FileUtils.getTempDirectory();
//		System.out.println(IjUtils.save(bp, tmpdir + "binary-test-dilation1.png"));
		
		ByteProcessor bp2 = (ByteProcessor) BinaryTestImage.BinaryTestDilation1.getImagePlus().getProcessor();
		assertTrue(ImageTestUtils.match(bp2, bp)); 
	}
	
	@Test
	public void test2() {
		ByteProcessor bp = (ByteProcessor) BinaryTestImage.BinaryTest.getImagePlus().getProcessor();
		BinaryDilation op = new BinaryDilation(TestKernels.H2);
		op.applyTo(bp);
		
//		String tmpdir = FileUtils.getTempDirectory();
//		System.out.println(IjUtils.save(bp, tmpdir + "BinaryTestDilation2.png"));
		
		ByteProcessor bp2 = (ByteProcessor) BinaryTestImage.BinaryTestDilation2.getImagePlus().getProcessor();
		assertTrue(ImageTestUtils.match(bp2, bp)); 
	}
	
	@Test
	public void test3() {
		ByteProcessor bp = (ByteProcessor) BinaryTestImage.Cat.getImagePlus().getProcessor();
		BinaryDilation op = new BinaryDilation(StructuringElements.makeDiskKernel(3));
		op.applyTo(bp);
		
//		String tmpdir = FileUtils.getTempDirectory();
//		System.out.println(IjUtils.save(bp, tmpdir + "CatDilation3.png"));
		
		ByteProcessor bp2 = (ByteProcessor) BinaryTestImage.CatDilation3.getImagePlus().getProcessor();
		assertTrue(ImageTestUtils.match(bp2, bp)); 
	}

}
