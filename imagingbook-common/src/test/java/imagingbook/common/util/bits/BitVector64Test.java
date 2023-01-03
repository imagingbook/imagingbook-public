/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.util.bits;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

public class BitVector64Test {

	@Test
	public void test1() {
		int K = 317;
		
		byte[] ba = new byte[K];
		
		BitVector bv = new BitVector64(ba);
		assertEquals(ba.length, bv.getLength());
		assertArrayEquals(ba, bv.toByteArray());
		
		Arrays.fill(ba, (byte) (0xFF & 1));
		bv = new BitVector64(ba);
		assertArrayEquals(ba, bv.toByteArray());
		
		// set/unset single elements
		Arrays.fill(ba, (byte) 0);
		bv.unsetAll();
		for (int i = 0; i < bv.getLength(); i++) {
			ba[i] = (byte) 1;
			bv.set(i);
			assertArrayEquals(ba, bv.toByteArray());
			ba[i] = (byte) 0;
			bv.unset(i);
		}
	}
	
	@Test
	public void test2() {
		for (int K : new int[] {1, 23, 79, 127, 128, 251, 255, 256, 6703}) {
			byte[] ba = makerandomBits(K);
			BitVector bv = new BitVector64(ba);
			assertArrayEquals(ba, bv.toByteArray());
		}
		
	}
	
	private byte[] makerandomBits(int n) {
		byte[] ba = new byte[n];
		Random rg = new Random(17);
		for (int i = 0; i < n; i++) {
			ba[i] = (byte) ((rg.nextBoolean()) ? 1 : 0);
		}
		return ba;
	}

}
