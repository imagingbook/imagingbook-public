/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2026 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.util;

import imagingbook.common.util.bits.BitVector;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

public class SubsequenceMappingTest {

    @Test
    public void ArrayIndexmapTest1() {
        // BitVector subset = BitVector.from("1110011110");
        imagingbook.common.util.SubsequenceMapping map = new imagingbook.common.util.SubsequenceMapping("1110011110");
        // System.out.println(Arrays.toString(map.getOrigToSubSeqIndex()));
        // System.out.println(Arrays.toString(map.getSubToOrigSeqIndex()));
        assertArrayEquals(new int[] {0, 1, 2, -1, -1, 3, 4, 5, 6, -1}, map.getOrigToSubSeqIndex());
        assertArrayEquals(new int[] {0, 1, 2, 5, 6, 7, 8}, map.getSubToOrigSeqIndex());

        assertEquals(-1, map.getSubPosition(3));
        assertEquals(5, map.getSubPosition(7));
        assertEquals(8, map.getOrigPosition(6));

        for (int p = 0; p < map.getOrigSequenceLength(); p++) {
            if (map.getSubPosition(p) > 0) {
                assertEquals(p, map.getOrigPosition(map.getSubPosition(p)));
            }
        }
    }

    @Test
    public void getSubsequenceIntTest() {
        BitVector subset = BitVector.from("1110011110");
        imagingbook.common.util.SubsequenceMapping map = new imagingbook.common.util.SubsequenceMapping(subset);
        int[] original = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        int[] subsequ = map.getSubSequence(original);
        // System.out.println(Arrays.toString(subsequ));
        assertArrayEquals(new int[] {1, 2, 3, 6, 7, 8, 9}, subsequ);
    }

    @Test
    public void getSubsequenceFloatTest() {
        BitVector subset = BitVector.from("1110011110");
        imagingbook.common.util.SubsequenceMapping map = new imagingbook.common.util.SubsequenceMapping(subset);
        float[] original = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        float[] subsequ = map.getSubSequence(original);
        // System.out.println(Arrays.toString(subsequ));
        assertArrayEquals(new float[] {1, 2, 3, 6, 7, 8, 9}, subsequ, 1e-6f);
    }

    @Test
    public void getSubsequenceDoubleTest() {
        BitVector subset = BitVector.from("1110011110");
        imagingbook.common.util.SubsequenceMapping map = new imagingbook.common.util.SubsequenceMapping(subset);
        double[] original = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        double[] subsequ = map.getSubSequence(original);
        // System.out.println(Arrays.toString(subsequ));
        // assertArrayEquals(new double[] {1, 2, 3, 6, 7, 8, 9}, subsequ, 1e-6);
    }

    @Test
    public void getSubsequenceObjectTest() {
        BitVector subset = BitVector.from("1110011110");
        imagingbook.common.util.SubsequenceMapping map = new imagingbook.common.util.SubsequenceMapping(subset);
        String[] original = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
        String[] subsequ = map.getSubSequence(original);
        // System.out.println(Arrays.toString(subsequ));
        assertArrayEquals(new String[] {"1", "2", "3", "6", "7", "8", "9"}, subsequ);
    }

    // ----------------------------------------

    @Test
    public void mergeIntTest0() {
        BitVector subset = BitVector.from("1110011110");
        imagingbook.common.util.SubsequenceMapping map = new imagingbook.common.util.SubsequenceMapping(subset);
        int[] original = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        int[] subsequ = map.getSubSequence(original);
        // System.out.println(Arrays.toString(subsequ));
        for (int j = 0; j < subsequ.length; j++) {
            subsequ[j] = 11 + j;
        }
        int[] merged = map.merge(subsequ, original);
        // System.out.println(Arrays.toString(merged));
        assertArrayEquals(new int[] {11, 12, 13, 4, 5, 14, 15, 16, 17, 10}, merged);
        assertNotSame(original, merged);
    }

    @Test
    public void mergeIntTest() {
        BitVector subset = BitVector.from("1110011110");
        imagingbook.common.util.SubsequenceMapping map = new imagingbook.common.util.SubsequenceMapping(subset);
        int[] original = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        int[] subsequ = map.getSubSequence(original);
        // System.out.println(Arrays.toString(subsequ));
        int[] merged = map.merge(subsequ, original);
        assertArrayEquals(original, merged);
        assertNotSame(original, merged);
    }

    @Test
    public void mergeDoubleTest() {
        BitVector subset = BitVector.from("1110011110");
        imagingbook.common.util.SubsequenceMapping map = new imagingbook.common.util.SubsequenceMapping(subset);
        double[] original = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        double[] subsequ = map.getSubSequence(original);
        // System.out.println(Arrays.toString(subsequ));
        double[] merged = map.merge(subsequ, original);
        assertArrayEquals(original, merged, 1e-6);
        assertNotSame(original, merged);
    }

    @Test
    public void mergeFloatTest() {
        BitVector subset = BitVector.from("1110011110");
        imagingbook.common.util.SubsequenceMapping map = new imagingbook.common.util.SubsequenceMapping(subset);
        float[] original = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        float[] subsequ = map.getSubSequence(original);
        // System.out.println(Arrays.toString(subsequ));
        float[] merged = map.merge(subsequ, original);
        assertArrayEquals(original, merged, 1e-6f);
        assertNotSame(original, merged);
    }

    @Test
    public void mergeObjectTest0() {
        BitVector subset = BitVector.from("1110011110");
        imagingbook.common.util.SubsequenceMapping map = new imagingbook.common.util.SubsequenceMapping(subset);
        String[] original = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
        String[] subsequ = map.getSubSequence(original);
        // System.out.println(Arrays.toString(subsequ));
        for (int j = 0; j < subsequ.length; j++) {
            subsequ[j] = "x" + subsequ[j];
        }
        String[] merged = map.merge(subsequ, original);
        // System.out.println(Arrays.toString(merged));
        assertArrayEquals(new String[] {"x1", "x2", "x3", "4", "5", "x6", "x7", "x8", "x9", "10"}, merged);
        assertNotSame(original, merged);
    }

    @Test
    public void mergeObjectTest() {
        BitVector subset = BitVector.from("1110011110");
        imagingbook.common.util.SubsequenceMapping map = new imagingbook.common.util.SubsequenceMapping(subset);
        String[] original = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
        String[] subsequ = map.getSubSequence(original);
        // System.out.println(Arrays.toString(subsequ));
        String[] merged = map.merge(subsequ, original);
        assertArrayEquals(original, merged);
        assertNotSame(original, merged);
    }
}