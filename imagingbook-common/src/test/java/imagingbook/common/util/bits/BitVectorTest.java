/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.util.bits;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Random;

import static imagingbook.common.util.bits.BitVector.getLongAsString;
import static org.junit.Assert.assertEquals;

public class BitVectorTest {

	@Test
	public void test1() {
		int K = 317;
		
		byte[] ba = new byte[K];

        BitVector bv = BitVector.from(ba);
		assertEquals(ba.length, bv.length());
		Assert.assertArrayEquals(ba, bv.asByteArray());
		
		Arrays.fill(ba, (byte) (0xFF & 1));
		bv = BitVector.from(ba);
		Assert.assertArrayEquals(ba, bv.asByteArray());
		
		// set/unset single elements
		Arrays.fill(ba, (byte) 0);
		bv.unsetAll();
		for (int i = 0; i < bv.length(); i++) {
			ba[i] = (byte) 1;
			bv.set(i);
			Assert.assertArrayEquals(ba, bv.asByteArray());
			ba[i] = (byte) 0;
			bv.unset(i);
		}
	}
	
	@Test
	public void test2() {
		for (int K : new int[] {1, 23, 79, 127, 128, 251, 255, 256, 6703}) {
			byte[] ba = makerandomBits(K);
            BitVector bv = BitVector.from(ba);
			Assert.assertArrayEquals(ba, bv.asByteArray());
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

    // replace tests above ----------------------------------------------------

    @Test
    public void constructorTest1() {
        for (int n : new int[]{1, 33, 64, 3017, 71925}) {
            BitVector bv = new BitVector(n);
            assertEquals(n, bv.length());
        }
    }


    @Test
    public void setAllTest() {
        for (int n : new int[]{1, 33, 64, 3017, 71925}) {
            BitVector bv = new BitVector(n);
            assertEquals(0, bv.cardinality());
            bv.setAll();
            assertEquals(n, bv.cardinality());
            bv.unsetAll();
            assertEquals(0, bv.cardinality());
        }
    }

    @Test
    public void testHashCode() {
        String str = "1010101111100100101001010100110101010";
        BitVector bv1 = new BitVector(str.length());
        bv1.set("1010101111100100101001010100110101010");

        BitVector bv2 = bv1.duplicate();
        assertEquals(bv1.hashCode(), bv2.hashCode());
    }

    @Test
    public void cardinalityTest0() {
        int n = 23; BitVector bv = new BitVector(n);
        int c = bv.cardinality();
        assertEquals(0, c);

        bv.setAll();
        c = bv.cardinality();
        assertEquals(n, c);
    }

    @Test
    public void getBitMaskTest() {
        int n;
        BitVector bv;

        n = 1; bv = new BitVector(n);
        assertEquals("1000000000000000000000000000000000000000000000000000000000000000", getLongAsString(bv.getBitMask()));

        n = 5; bv = new BitVector(n);
        assertEquals("1111100000000000000000000000000000000000000000000000000000000000", getLongAsString(bv.getBitMask()));

        n = 63; bv = new BitVector(n);
        assertEquals("1111111111111111111111111111111111111111111111111111111111111110", getLongAsString(bv.getBitMask()));

        n = 64; bv = new BitVector(n);
        assertEquals("1111111111111111111111111111111111111111111111111111111111111111", getLongAsString(bv.getBitMask()));

        n = 65;  bv = new BitVector(n);
        assertEquals("1000000000000000000000000000000000000000000000000000000000000000", getLongAsString(bv.getBitMask()));

        n = 3917; bv = new BitVector(n);
        assertEquals("1111111111111000000000000000000000000000000000000000000000000000", getLongAsString(bv.getBitMask()));
    }

    @Test
    public void getLongAsStringTest() {
        assertEquals("0000000000000000000000000000000000000000000000000000000000000001", getLongAsString(1));
        assertEquals("1111111111111111111111111111111111111111111111111111111111111111", getLongAsString(-1));
        assertEquals("1111111111111111111111111111111111111010011111110011100111010010", getLongAsString(-92325422));
    }


}
