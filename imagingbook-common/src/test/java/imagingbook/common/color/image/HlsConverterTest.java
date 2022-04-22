/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.color.image;

import static org.junit.Assert.assertArrayEquals;

import java.util.Random;

import org.junit.Test;

import imagingbook.common.color.colorspace.HlsConverter;

public class HlsConverterTest {

	@Test
	public void testFromRGBtoRGB() {  // tests all 16 mio RGB colors
		Random rg = new Random(17);
		HlsConverter hlsC = new HlsConverter();
		int[] rgb = new int[3];
		for (int i = 0; i < 1000; i++) {
			rgb[0] = rg.nextInt(256);
			rgb[1] = rg.nextInt(256);
			rgb[2] = rg.nextInt(256);
			float[] hls = hlsC.fromRGB(rgb);
			assertArrayEquals(rgb, hlsC.toRGB(hls));
		}
	}
}
