/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.regions;

import static imagingbook.common.geometry.basic.NeighborhoodType2D.N4;
import static imagingbook.common.geometry.basic.NeighborhoodType2D.N8;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import ij.process.ByteProcessor;
import imagingbook.core.resource.ImageResource;
import imagingbook.testimages.BinaryTestImage;

/**
 * Segmentation test on a very small image that can be handled by recursive method too.
 * 
 * @author WB
 */
public class BinaryRegionSegmentation4_Test {

	private static int RegionCount_N4 = 6;
	private static int RegionCount_N8 = 3;
	
	private ImageResource ir = BinaryTestImage.SimpleTestGridImg;
	private ByteProcessor bp = (ByteProcessor) ir.getImagePlus().getProcessor();

	@Test
	public void testSegmentationBreadthFirst() {
		run(new BreadthFirstSegmentation(bp, N4), RegionCount_N4);
		run(new BreadthFirstSegmentation(bp, N8), RegionCount_N8);
	}
	
	@Test
	public void testSegmentationDepthFirst() {
		run(new DepthFirstSegmentation(bp, N4), RegionCount_N4);
		run(new DepthFirstSegmentation(bp, N8), RegionCount_N8);
	}
	
	@Test
	public void testSegmentationSequential() {
		run(new SequentialSegmentation(bp, N4), RegionCount_N4);
		run(new SequentialSegmentation(bp, N8), RegionCount_N8);
	}
	
	@Test
	public void testSegmentationRegionContour() {
		run(new RegionContourSegmentation(bp, N4), RegionCount_N4);
		run(new RegionContourSegmentation(bp, N8), RegionCount_N8);
	}
	
	@Test
	public void testSegmentationRecursive() {
		run(new RecursiveSegmentation(bp, N4), RegionCount_N4);
		run(new RecursiveSegmentation(bp, N8), RegionCount_N8);
	}
	
	// ---------------------------------------------------------------
	
	private void run(BinaryRegionSegmentation labeling, int rc) {
		List<BinaryRegion> regions = labeling.getRegions(true);
		Assert.assertNotNull(regions);
		Assert.assertEquals(rc, regions.size());
	}

}
