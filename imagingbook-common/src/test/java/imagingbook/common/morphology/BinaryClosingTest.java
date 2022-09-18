package imagingbook.common.morphology;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ij.process.ByteProcessor;
import imagingbook.testimages.BinaryTestImage;
import imagingbook.testutils.ImageTestUtils;

public class BinaryClosingTest {

	@Test
	public void test1() {
		ByteProcessor bp = (ByteProcessor) BinaryTestImage.BinaryTest.getImage().getProcessor();
		BinaryClosing op = new BinaryClosing();
		op.applyTo(bp);
		
//		String tmpdir = FileUtils.getTempDirectory();
//		System.out.println(IjUtils.save(bp, tmpdir + "binary-test-closing1.png"));
		
		ByteProcessor bp2 = (ByteProcessor) BinaryTestImage.BinaryTestClosing1.getImage().getProcessor();
		assertTrue(ImageTestUtils.match(bp2, bp)); 
	}
	
	@Test
	public void test2() {
		ByteProcessor bp = (ByteProcessor) BinaryTestImage.BinaryTest.getImage().getProcessor();
		BinaryClosing op = new BinaryClosing(TestKernels.H2);
		op.applyTo(bp);
		
//		String tmpdir = FileUtils.getTempDirectory();
//		System.out.println(IjUtils.save(bp, tmpdir + "BinaryTestClosing2.png"));
		
		ByteProcessor bp2 = (ByteProcessor) BinaryTestImage.BinaryTestClosing2.getImage().getProcessor();
		assertTrue(ImageTestUtils.match(bp2, bp)); 
	}
	
	@Test
	public void test3() {
		ByteProcessor bp = (ByteProcessor) BinaryTestImage.Cat.getImage().getProcessor();
		BinaryClosing op = new BinaryClosing(StructuringElements.makeDiskKernel(3));
		op.applyTo(bp);
		
//		String tmpdir = FileUtils.getTempDirectory();
//		System.out.println(IjUtils.save(bp, tmpdir + "CatClosing3.png"));
		
		ByteProcessor bp2 = (ByteProcessor) BinaryTestImage.CatClosing3.getImage().getProcessor();
		assertTrue(ImageTestUtils.match(bp2, bp)); 
	}

}
