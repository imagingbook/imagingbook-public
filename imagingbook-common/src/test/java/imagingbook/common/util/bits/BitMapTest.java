/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.util.bits;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.ij.IjUtils;
import imagingbook.core.resource.ImageResource;
import imagingbook.sampleimages.GeneralSampleImage;
import imagingbook.testimages.BinaryTestImage;

public class BitMapTest {

	@Test
	public void test1() {
		int W = 97, H = 31;
		
		byte[] ba = new byte[W * H];
		
		BitMap bm = new BitMap(W, H, ba);
		assertEquals(W, bm.getWidth());
		assertEquals(H, bm.getHeight());
		
		assertArrayEquals(ba, bm.toByteArray());
		
		Arrays.fill(ba, (byte) (0xFF & 1));
		bm = new BitMap(W, H, ba);
		assertArrayEquals(ba, bm.toByteArray());
		
		// set/unset single elements
		Arrays.fill(ba, (byte) 0);
		bm.unsetAll();
		int i = 0;
		for (int v = 0; v < H; v++) {
			for (int u = 0; u < W; u++) {
				ba[i] = (byte) 1;
				bm.set(u, v);
				assertArrayEquals(ba, bm.toByteArray());
				ba[i] = (byte) 0;
				bm.unset(u, v);
				i = i + 1;
			}
		}
	}
	
	@Test
	public void test2() {
		for (int w : Arrays.asList(1, 23, 79, 127, 128)) {
			for (int h : Arrays.asList(1, 27, 79, 127, 256)) {
				byte[] ba = makerandomBits(w * h);
				BitMap bm = new BitMap(w, h, ba);
				assertEquals(w, bm.getWidth());
				assertEquals(h, bm.getHeight());
				assertArrayEquals(ba, bm.toByteArray());
			}
		}
	}
	
	@Test
	public void test3() {
		for (ImageResource ir : Arrays.asList(BinaryTestImage.Cat, GeneralSampleImage.MonasterySmall)) {
			ImageProcessor ip = ir.getImagePlus().getProcessor();
			assertTrue(ip instanceof ByteProcessor);
			
			BitMap bm = IjUtils.convertToBitMap((ByteProcessor) ip);
			assertNotNull(bm);
			assertEquals(ip.getWidth(), bm.getWidth());
			assertEquals(ip.getHeight(), bm.getHeight());
			
			byte[] ba = BitVector.binarize((byte[]) ip.getPixels());
			assertArrayEquals("problem with " + ir, ba, bm.toByteArray());
		}
	}
	
	
	// ----------------------------------------------------------------
	
	private byte[] makerandomBits(int n) {
		byte[] ba = new byte[n];
		Random rg = new Random(17);
		for (int i = 0; i < n; i++) {
			ba[i] = (byte) ((rg.nextBoolean()) ? 1 : 0);
		}
		return ba;
	}
	

}
