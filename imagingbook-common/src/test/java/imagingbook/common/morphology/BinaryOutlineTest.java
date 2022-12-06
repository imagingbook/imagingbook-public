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
