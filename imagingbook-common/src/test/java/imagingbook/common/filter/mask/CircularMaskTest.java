/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.filter.mask;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import ij.process.ByteProcessor;

public class CircularMaskTest {

	@Test
	public void test1() {
		BinaryMask mask = new CircularMask(4.5);
		
		assertEquals(11, mask.getWidth());
		assertEquals(11, mask.getHeight());
		
		assertEquals(5, mask.getCenterX());
		assertEquals(5, mask.getCenterY());
		
		assertEquals(69, mask.getElementCount());

		byte[][] B = mask.getByteArray();
		assertNotNull(B);
		
		ByteProcessor bp = mask.getByteProcessor();
		assertNotNull(bp);
		assertEquals(mask.getWidth(), bp.getWidth());
		assertEquals(mask.getHeight(), bp.getHeight());
		
		for (int u = 0; u < mask.getWidth(); u++) {
			for (int v = 0; v < mask.getHeight(); v++) {
				assertEquals(255 * B[u][v], bp.get(u, v));
			}
		}
	}

}
