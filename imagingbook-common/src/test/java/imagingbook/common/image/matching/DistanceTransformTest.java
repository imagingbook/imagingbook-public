/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.image.matching;

import org.junit.Test;

import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.testutils.NumericTestUtils;

public class DistanceTransformTest {
	
	private static int W = 12;
	private static int H = 10;

	private static byte[] pixels = {
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
			0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 
			0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
			0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 
			0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
	};
	
	private static float[][] distL1 = {
			{5, 4, 3, 2, 3, 4, 5, 6, 7, 8}, 
			{4, 3, 2, 1, 2, 3, 4, 5, 6, 7}, 
			{3, 2, 1, 0, 1, 2, 3, 4, 5, 6}, 
			{3, 2, 1, 1, 2, 3, 4, 4, 5, 6}, 
			{2, 1, 0, 1, 2, 3, 3, 3, 4, 5}, 
			{3, 2, 1, 2, 3, 3, 2, 2, 3, 4}, 
			{4, 3, 2, 3, 3, 2, 1, 1, 2, 3}, 
			{5, 4, 3, 3, 2, 1, 0, 0, 1, 2}, 
			{6, 5, 4, 3, 2, 1, 0, 1, 2, 3}, 
			{7, 6, 5, 4, 3, 2, 1, 2, 3, 4}, 
			{8, 7, 6, 5, 4, 3, 2, 3, 4, 5}, 
			{9, 8, 7, 6, 5, 4, 3, 4, 5, 6}};

	private static float[][] distL2 = {
		{3.8284f, 2.8284f, 2.4142f, 2.0000f, 2.4142f, 2.8284f, 3.8284f, 4.8284f, 5.8284f, 6.8284f}, 
		{3.4142f, 2.4142f, 1.4142f, 1.0000f, 1.4142f, 2.4142f, 3.4142f, 4.4142f, 5.4142f, 6.4142f}, 
		{2.8284f, 2.0000f, 1.0000f, 0.0000f, 1.0000f, 2.0000f, 3.0000f, 4.0000f, 5.0000f, 5.8284f}, 
		{2.4142f, 1.4142f, 1.0000f, 1.0000f, 1.4142f, 2.4142f, 3.4142f, 4.0000f, 4.4142f, 4.8284f}, 
		{2.0000f, 1.0000f, 0.0000f, 1.0000f, 2.0000f, 2.8284f, 3.0000f, 3.0000f, 3.4142f, 3.8284f}, 
		{2.4142f, 1.4142f, 1.0000f, 1.4142f, 2.4142f, 2.4142f, 2.0000f, 2.0000f, 2.4142f, 2.8284f}, 
		{2.8284f, 2.4142f, 2.0000f, 2.4142f, 2.4142f, 1.4142f, 1.0000f, 1.0000f, 1.4142f, 2.4142f}, 
		{3.8284f, 3.4142f, 3.0000f, 3.0000f, 2.0000f, 1.0000f, 0.0000f, 0.0000f, 1.0000f, 2.0000f}, 
		{4.8284f, 4.4142f, 4.0000f, 3.0000f, 2.0000f, 1.0000f, 0.0000f, 1.0000f, 1.4142f, 2.4142f}, 
		{5.8284f, 5.4142f, 4.4142f, 3.4142f, 2.4142f, 1.4142f, 1.0000f, 1.4142f, 2.4142f, 2.8284f}, 
		{6.8284f, 5.8284f, 4.8284f, 3.8284f, 2.8284f, 2.4142f, 2.0000f, 2.4142f, 2.8284f, 3.8284f}, 
		{7.2426f, 6.2426f, 5.2426f, 4.2426f, 3.8284f, 3.4142f, 3.0000f, 3.4142f, 3.8284f, 4.2426f}};

	@Test
	public void testL1() {
		ImageProcessor ip = new ByteProcessor(W, H, pixels);
		DistanceTransform dt = new DistanceTransform(ip, DistanceTransform.DistanceType.L1);
		float[][] dmap = dt.getDistanceMap();
		NumericTestUtils.assert2dArrayEquals(distL1, dmap, 1e-6);
	}
	
	@Test
	public void testL2() {
		ImageProcessor ip = new ByteProcessor(W, H, pixels);
		DistanceTransform dt = new DistanceTransform(ip, DistanceTransform.DistanceType.L2);
		float[][] dmap = dt.getDistanceMap();
		NumericTestUtils.assert2dArrayEquals(distL2, dmap, 1e-3);
	}
	
}
