/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.util.bits;

import imagingbook.testutils.DeterministicRandom;
import org.junit.Test;

import java.util.random.RandomGenerator;

import static imagingbook.common.util.bits.BitVector.getLongAsString;
import static imagingbook.common.util.bits.BitVector.makeRandom;
import static org.junit.Assert.*;

public class BitVectorTest {

    @Test
    public void getLongAsStringTest() {
        assertEquals("0000000000000000000000000000000000000000000000000000000000000001",
                getLongAsString(1));
        assertEquals("1111111111111111111111111111111111111111111111111111111111111111",
                getLongAsString(-1));
        assertEquals("1111111111111111111111111111111111111010011111110011100111010010",
                getLongAsString(-92325422));
    }

    @Test
    public void getBitMaskTest() {
        int n;
        BitVector bv;

        n = 1; bv = new BitVector(n);
        assertEquals("0000000000000000000000000000000000000000000000000000000000000001",
                getLongAsString(bv.getBitMask()));

        n = 5; bv = new BitVector(n);
        assertEquals("0000000000000000000000000000000000000000000000000000000000011111",
                getLongAsString(bv.getBitMask()));

        n = 63; bv = new BitVector(n);
        assertEquals("0111111111111111111111111111111111111111111111111111111111111111",
                getLongAsString(bv.getBitMask()));

        n = 64; bv = new BitVector(n);
        assertEquals("1111111111111111111111111111111111111111111111111111111111111111",
                getLongAsString(bv.getBitMask()));

        n = 65;  bv = new BitVector(n);
        assertEquals("0000000000000000000000000000000000000000000000000000000000000001",
                getLongAsString(bv.getBitMask()));

        n = 3917; bv = new BitVector(n);
        assertEquals("0000000000000000000000000000000000000000000000000001111111111111",
                getLongAsString(bv.getBitMask()));
    }

    @Test
    public void constructorTest1A() {
        for (int n : new int[]{1, 33, 64, 65, 3017, 71925}) {
            BitVector bv = new BitVector(n);
            assertEquals(n, bv.length());
            assertEquals(0, bv.cardinality());
        }
    }

    @Test
    public void constructorTest1B() {
        for (int n : new int[]{1, 33, 64, 65, 3017, 71925}) {
            BitVector bv = new BitVector(n, false);
            assertEquals(n, bv.length());
            assertEquals(0, bv.cardinality());
        }
    }

    @Test
    public void constructorTest2() {
        for (int n : new int[]{1, 33, 64, 65, 3017, 71925}) {
            BitVector bv = new BitVector(n, true);
            assertEquals(n, bv.length());
            assertEquals(n, bv.cardinality());
        }
    }

    @Test
    public void setBitFromAllTest() {
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
    public void flipBitTest() {
        RandomGenerator rand = new DeterministicRandom(99);
        for (int n : new int[]{1, 33, 64, 3017, 71925}) {
            BitVector bv1 = BitVector.makeRandom(n, rand);
            BitVector bv2 = bv1.duplicate();
            for (int i = 0; i < bv1.length(); i++) {
                bv2.flipBit(i);
                assertNotEquals(bv1, bv2);
                assertEquals(1, bv1.hammingDistance(bv2));
                bv2.flipBit(i);
                assertEquals(bv1, bv2);
            }
        }
    }

    @Test
    public void testHashCode() {
        String str = "1010101111100100101001010100110101010";
        BitVector bv1 = new BitVector(str.length());
        bv1.setFrom("1010101111100100101001010100110101010");

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
    public void equalsTest1() {
        RandomGenerator rand = new DeterministicRandom(99);
        for (int n : new int[]{1, 33, 64, 3017, 71925}) {
            BitVector bv1 = makeRandom(n, rand);
            BitVector bv2 = bv1.duplicate();
            assertEquals(bv1, bv1);
            assertEquals(bv1, bv2);
            assertEquals(bv2, bv1);
        }
    }

    @Test
    public void equalsTest2() {
        RandomGenerator rand = new DeterministicRandom(99);
        for (int n : new int[]{1, 33, 64, 3017, 71925}) {
            BitVector bv1 = makeRandom(n, rand);
            BitVector bv2 = bv1.duplicate();
            int i = n/2;
            bv2.setBit(i, !bv2.getBit(i));    // flip 1 bit
            assertNotEquals(bv1, bv2);
        }
    }

    @Test
    public void bitwiseANDTest() {
        RandomGenerator rand = new DeterministicRandom(73);
        for (int n : new int[]{1, 33, 64, 3017, 71925}) {
            BitVector bv1 = makeRandom(n, rand);
            BitVector bv2 = new BitVector(bv1.length()); bv2.setAll(); // all 1s

            assertEquals(bv1, bv1.bitwiseAND(bv1));    // and with itself
            assertEquals(bv1, bv1.bitwiseAND(bv2));    // and with all 1s
            assertEquals(bv1, bv2.bitwiseAND(bv1));
        }
    }

    @Test
    public void bitwiseOrTest() {
        RandomGenerator rand = new DeterministicRandom(311);
        for (int n : new int[]{1, 33, 64, 3017, 71925}) {
            BitVector bv1 = makeRandom(n, rand);
            BitVector bv2 = new BitVector(bv1.length()); // all 0s
            BitVector bv3 = bv2.bitwiseNOT(); // all 1s

            assertEquals(bv1, bv1.bitwiseOR(bv1));    // or with itself
            assertEquals(bv1, bv1.bitwiseOR(bv2));    // or with all 0s
            assertEquals(bv1, bv2.bitwiseOR(bv1));
            assertEquals(bv3, bv1.bitwiseOR(bv1.bitwiseNOT()));
        }
    }

    @Test
    public void bitwiseXORTest1() {
        BitVector bv1 = BitVector.from("0011");
        BitVector bv2 = BitVector.from("0101");
        BitVector bv3 = bv1.bitwiseXOR(bv2);
        assertEquals(BitVector.from("0110"), bv3);
        assertEquals(bv1.hammingDistance(bv2), bv3.cardinality());
    }

    @Test
    public void bitwiseXORTest2() {
        RandomGenerator rand = new DeterministicRandom(17);
        for (int n : new int[]{1, 33, 64, 3017, 71925}) {
            BitVector bv1 = makeRandom(n, rand);
            BitVector bv2 = makeRandom(n, rand);
            BitVector bv3 = bv1.bitwiseXOR(bv2);
            assertEquals(bv1.hammingDistance(bv2), bv3.cardinality());
        }
    }

    @Test
    public void bitwiseNOTTest() {
        RandomGenerator rand = new DeterministicRandom(111);
        for (int n : new int[]{1, 33, 64, 3017, 71925}) {
            BitVector bv1 = makeRandom(n, rand);
            BitVector bv2 = bv1.bitwiseNOT();  // bv1 inverted
            BitVector bv3 = bv2.bitwiseNOT();  // bv2 inverted back to bv1

            assertNotEquals(bv1, bv2);
            assertEquals(bv1, bv3);

            for (int i = 0; i < n; i++) {
                assertNotEquals(bv1.getBit(i), bv2.getBit(i));
                assertEquals(bv1.getBit(i), bv3.getBit(i));
            }
        }
    }

    @Test
    public void hammingDistanceTest() {
        RandomGenerator rand = new DeterministicRandom(111);
        for (int n : new int[]{1, 33, 64, 3017, 71925}) {
            BitVector bv1 = makeRandom(n, rand);
            BitVector bv2 = bv1.bitwiseNOT();  // bv1 inverted
            BitVector bv3 = new BitVector(n);   // all 0s

            assertEquals(0, bv1.hammingDistance(bv1));  // 0 distance to itself
            assertEquals(n, bv1.hammingDistance(bv2));  // n different bits
            assertEquals(bv1.cardinality(), bv1.hammingDistance(bv3));  // number of 1s
        }
    }

    @Test
    public void asStringTest() {
        RandomGenerator rand = new DeterministicRandom(191);
        for (int n : new int[]{1, 33, 64, 3017, 71925}) {
            BitVector bv1 = makeRandom(n, rand);
            String str = bv1.asString();
            BitVector bv2 = BitVector.from(str);
            assertEquals(bv1, bv2);
        }
    }

    @Test
    public void asByteArrayTest() {
        RandomGenerator rand = new DeterministicRandom(413);
        for (int n : new int[]{1, 33, 64, 3017, 71925}) {
            BitVector bv1 = makeRandom(n, rand);
            byte[] bytes = bv1.asByteArray();
            BitVector bv2 = BitVector.from(bytes);
            assertEquals(bv1, bv2);
        }
    }

    @Test
    public void asBooleanArrayTest() {
        RandomGenerator rand = new DeterministicRandom(1333);
        for (int n : new int[]{1, 33, 64, 3017, 71925}) {
            BitVector bv1 = makeRandom(n, rand);
            boolean[] bools = bv1.asBooleanArray();
            BitVector bv2 = BitVector.from(bools);
            assertEquals(bv1, bv2);
        }
    }

    @Test
    public void setBitTest() {
        int n = 10000;
        BitVector bv = new BitVector(n);
        for (int i = 0; i < n; i++) {
            assertFalse(bv.getBit(i));
            bv.setBit(i);
            assertTrue(bv.getBit(i));
            assertEquals(i + 1, bv.cardinality());
        }
    }

    @Test
    public void unsetBitTest() {
        int n = 10000;
        BitVector bv = new BitVector(n);
        bv.setAll();
        for (int i = 0; i < n; i++) {
            assertTrue(bv.getBit(i));
            bv.unsetBit(i);
            assertFalse(bv.getBit(i));
            assertEquals(n - i - 1, bv.cardinality());
        }
    }

    @Test
    public void SetBitTest2() {
        int n = 10000;
        BitVector bv = new BitVector(n);
        for (int i = 0; i < n; i++) {
            assertFalse(bv.getBit(i));
            bv.setBit(i, true);
            assertTrue(bv.getBit(i));
            assertEquals(i + 1, bv.cardinality());
        }
    }

    @Test
    public void setBitTest3() {
        int n = 10000;
        BitVector bv = new BitVector(n);
        bv.setAll();
        for (int i = 0; i < n; i++) {
            assertTrue(bv.getBit(i));
            bv.setBit(i, false);
            assertFalse(bv.getBit(i));
            assertEquals(n - i - 1, bv.cardinality());
        }
    }


}
