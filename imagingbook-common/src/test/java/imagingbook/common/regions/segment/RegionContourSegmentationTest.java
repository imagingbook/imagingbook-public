package imagingbook.common.regions.segment;

import static imagingbook.common.geometry.basic.NeighborhoodType2D.N4;
import static imagingbook.common.geometry.basic.NeighborhoodType2D.N8;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import ij.process.ByteProcessor;
import imagingbook.DATA.GeneralTestImage;
import imagingbook.common.geometry.basic.NeighborhoodType2D;
import imagingbook.common.regions.BinaryRegion;
import imagingbook.common.regions.Contour;
import imagingbook.core.resource.ImageResource;

public class RegionContourSegmentationTest {
	
	@Test
	public void testSimpleN4() {
		TestCase tc = new TestCase();
		// -----------------------------------------------------------------
		tc.imgResource =  GeneralTestImage.SimpleTestGridImg;
		tc.NHT = N4;
		tc.RegionCount = 6;
		tc.LargestRegionSize = 64;
		tc.OuterContourLength = 100;
		tc.FirstRegionWithHole = -1;		// ????
		tc.FirstInnerContourLength = 1;	// ????
		// -----------------------------------------------------------------	
		tc.run();
	}
	
	@Test
	public void testSimpleN8() {
		TestCase tc = new TestCase();
		// -----------------------------------------------------------------
		tc.imgResource =  GeneralTestImage.SimpleTestGridImg;
		tc.NHT = N8;
		tc.RegionCount = 3;
		tc.LargestRegionSize = 75;
		tc.OuterContourLength = 50;
		tc.FirstRegionWithHole = 0;
		tc.FirstInnerContourLength = 24;
		// -----------------------------------------------------------------
		tc.run();
	}
	
	// ************************************************************************
	
	@Test
	public void testSmallN4() {
		TestCase tc = new TestCase();
		// -----------------------------------------------------------------
		tc.imgResource =  GeneralTestImage.SegmentationSmall;
		tc.NHT = N4;
		tc.RegionCount = 26;
		tc.LargestRegionSize = 1000;
		tc.OuterContourLength = 168;
		tc.FirstRegionWithHole = 0;
		tc.FirstInnerContourLength = 108;
		// -----------------------------------------------------------------
		tc.run();
	}
	
	@Test
	public void testSmallN8() {
		TestCase tc = new TestCase();
		// -----------------------------------------------------------------
		tc.imgResource =  GeneralTestImage.SegmentationSmall;
		tc.NHT = N8;
		tc.RegionCount = 9;
		tc.LargestRegionSize = 1000;
		tc.OuterContourLength = 159;
		tc.FirstRegionWithHole = 0;
		tc.FirstInnerContourLength = 99;
		// -----------------------------------------------------------------
		tc.run();
	}
	
	// ************************************************************************
	
	@Test
	public void testMedN4() {
		TestCase tc = new TestCase();
		// -----------------------------------------------------------------
		tc.imgResource =  GeneralTestImage.SegmentationMed;
		tc.NHT = N4;
		tc.RegionCount = 88;
		tc.LargestRegionSize = 16352;
		tc.OuterContourLength = 686;
		tc.FirstRegionWithHole = 0;
		tc.FirstInnerContourLength = 434;
		// -----------------------------------------------------------------
		tc.run();
	}
	
	@Test
	public void testMedN8() {
		TestCase tc = new TestCase();
		// -----------------------------------------------------------------
		tc.imgResource =  GeneralTestImage.SegmentationMed;
		tc.NHT = N8;
		tc.RegionCount = 9;
		tc.LargestRegionSize = 16352;
		tc.OuterContourLength = 650;
		tc.FirstRegionWithHole = 0;
		tc.FirstInnerContourLength = 395;
		// -----------------------------------------------------------------
		tc.run();
	}
	
	// ************************************************************************
	
	@Test
	public void testBigN4() {
		TestCase tc = new TestCase();
		// -----------------------------------------------------------------
		tc.imgResource =  GeneralTestImage.RhinoBigCrop;
		tc.NHT = N4;
		tc.RegionCount = 10254;
		tc.LargestRegionSize = 562365;
		tc.OuterContourLength = 11548;
		tc.FirstRegionWithHole = 0;
		tc.FirstInnerContourLength = 168;
		// -----------------------------------------------------------------
		tc.run();
	}
	
	@Test
	public void testBigN8() {
		TestCase tc = new TestCase();
		// -----------------------------------------------------------------
		tc.imgResource =  GeneralTestImage.RhinoBigCrop;
		tc.NHT = N8;
		tc.RegionCount = 9382;
		tc.LargestRegionSize = 564659;
		tc.OuterContourLength = 9974;
		tc.FirstRegionWithHole = 0;
		tc.FirstInnerContourLength = 125;
		// -----------------------------------------------------------------
		tc.run();
	}
	
	
	// --------------------------------------------------------------------------------------------
	// --------------------------------------------------------------------------------------------
	
	private static class TestCase {
		ImageResource imgResource;
		NeighborhoodType2D NHT;
		int RegionCount;
		int LargestRegionSize;
		int OuterContourLength;
		int FirstRegionWithHole;
		int FirstInnerContourLength;
		
		void run() {
			ByteProcessor bp = (ByteProcessor)imgResource.getImage().getProcessor();
			RegionContourSegmentation segmentation = new RegionContourSegmentation(bp, NHT);
			
			// check region count:
			List<BinaryRegion> regions = segmentation.getRegions(true);
			Assert.assertNotNull(regions);
			Assert.assertFalse(regions.isEmpty());
			Assert.assertEquals(RegionCount, regions.size());
			
			// check largest region's size:
			BinaryRegion r0 = regions.get(0);	// the largest region
			Assert.assertEquals(LargestRegionSize, r0.getSize());
			
			// check largest regions outer contour length:
			Contour outerContour = r0.getOuterContour();
			Assert.assertNotNull(outerContour);
			Assert.assertEquals(OuterContourLength, outerContour.getLength());
			Assert.assertEquals(0, outerContour.countDuplicatePoints());
			Assert.assertTrue(outerContour.isClosed(NHT));
			
			// check inner regions and contours
			int k = findFirstRegionWithHole(regions);
			Assert.assertEquals(FirstRegionWithHole, k);
			if (k >= 0) {
				List<Contour> ics = regions.get(k).getInnerContours();
				Assert.assertFalse(ics.isEmpty());
				Assert.assertEquals(FirstInnerContourLength, ics.get(0).getLength());
				Assert.assertEquals(0, ics.get(0).countDuplicatePoints());
				Assert.assertTrue(ics.get(0).isClosed(NHT));
			}
//			try {
//				Thread.sleep(2000);
//			} catch (InterruptedException e) {}
		}
		
	}
	
	private static int findFirstRegionWithHole(List<BinaryRegion> regions) {
		int i = 0;
		for (BinaryRegion r : regions) {
			List<Contour> ics = r.getInnerContours();
			if (ics != null && !ics.isEmpty()) {
				return i;
			}
			i = i + 1;
		}		
		return -1;
	}

}
