/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.regions;

import static imagingbook.common.geometry.basic.NeighborhoodType2D.N4;
import static imagingbook.common.geometry.basic.NeighborhoodType2D.N8;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import ij.process.ByteProcessor;
import imagingbook.common.regions.segment.BinaryRegionSegmentation;
import imagingbook.common.regions.segment.BreadthFirstSegmentation;
import imagingbook.common.regions.segment.DepthFirstSegmentation;
import imagingbook.common.regions.segment.RegionContourSegmentation;
import imagingbook.common.regions.segment.SequentialSegmentation;
import imagingbook.core.resource.ImageResource;
import imagingbook.sampleimages.GeneralTestImage;

/**
 * Segmentation test on a medium-sized image which cannot be handled by 
 * the recursive method.
 * 
 * @author WB
 */
public class BinaryRegionSegmentation3_Test {
	
	private static int RegionCount_N4 = 88;
	private static int RegionCount_N8 = 9;
	
	private ImageResource path = GeneralTestImage.SegmentationMed;
	private ByteProcessor bp = (ByteProcessor) path.getImage().getProcessor();


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
	
//	@Test
//	public void testSegmentationRecursive() {
//		run(new SegmentationRecursive(bp, N4), RegionCount_N4);
//		run(new SegmentationRecursive(bp, N8), RegionCount_N8);
//	}
	
	// ---------------------------------------------------------------
	
	private void run(BinaryRegionSegmentation labeling, int rc) {
		List<BinaryRegion> regions = labeling.getRegions(true);
		Assert.assertNotNull(regions);
		Assert.assertEquals(rc, regions.size());
	}

}
