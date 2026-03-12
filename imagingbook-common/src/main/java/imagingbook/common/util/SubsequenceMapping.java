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

import java.util.Arrays;

/**
 * <p>
 * Stores the association between an original sequence (array) and a derived subsequence.
 * It provides information on (a) where to find an original element in the subsequence and
 * (b) where a subsequence element originated in the original sequence.
 * This is useful for extracting and manipulating subsequences while keeping their elements
 * linked to the elements in the original sequence.
 * Note that this class stores no data but only provides indexes to map between the two sequences.
 * Also note that these are sequences and not sets, i.e., ordering is maintained and duplicate
 * values are allowed.
 * </p>
 * <p>
 * Usage example:
 * </p>
 * <pre>{@code
 *  SubsequenceMapping map = new SubsequenceMapping("1110011110");
 *  // specifies original length = 10, subsequence length = 7
 *  int[] original = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};   // may be longer
 *  // extract a subsequence from an original:
 *  int[] subsequ = map.getSubSequence(original); // = {1, 2, 3, 6, 7, 8, 9}
 *  // modify the subsequence:
 *  for (int i = 0; i < subsequ.length; i++) {
 *      subsequ[i] = -subsequ[i];
 *  }
 *  // merge the modified subsequence back to the original:
 *  int[] merged = map.merge(subsequ, original); // {-1, -2, -3, 4, 5, -6,- 7, -8, -9, 10}
 * }</pre>
 */
public class SubsequenceMapping {

    private final int[] origToSubSeqIndex;
    private final int[] subToOrigSeqIndex;

    /**
     * Constructor. The supplied {@link BitVector} specifies the length of the original
     * sequence and flags the members of the subsequence.
     * @param subset a {@link BitVector} which flags members of the subsequence
     */
    public SubsequenceMapping(BitVector subset) {
        this.origToSubSeqIndex = new int[subset.length()];
        this.subToOrigSeqIndex = new int[subset.cardinality()];
        for (int i = 0, j = 0; i < origToSubSeqIndex.length; i++) {
            if (subset.getBit(i)) {         // keep this element in reduced vector
                origToSubSeqIndex[i] = j;        // i -> j (position reducedIndex)
                subToOrigSeqIndex[j] = i;         // j -> i (position in fullIndex)
                j++;
            }
            else {
                origToSubSeqIndex[i] = -1;      // mark skipped items in fullIndex -1
            }
        }
    }

    /**
     * Constructor. The supplied 0/1 string specifies the length of the original
     * sequence and flags the members of the subsequence (1 = include).
     * @param subsetString a string which flags members of the subsequence
     */
    public SubsequenceMapping(String subsetString) {
        this(BitVector.from(subsetString));
    }

    // ------------------------------------------------------------------------------------------

    /**
     * Returns the subsequence position of the item located at {@code origPos} in the original
     * sequence.
     * @param origPos position in original sequence
     * @return position in the subsequence if contained, -1 otherwise
     */
    public int getSubPosition(int origPos) {
        return origToSubSeqIndex[origPos];
    }

    /**
     * Returns the original position of the item located at  {@code subPos} in the subsequence.
     * @param subPos position in the subsequence
     * @return position in original sequence
     */
    public int getOrigPosition(int subPos) {
        return subToOrigSeqIndex[subPos];
    }

    /**
     * Returns the original sequence length this {@link SubsequenceMapping} is set up for.
     * @return the length of original vectors
     */
    public int getOrigSequenceLength() {
        return origToSubSeqIndex.length;
    }

    /**
     * Returns the subsequence length this {@link SubsequenceMapping} is set up for.
     * @return the subsequence length
     */
    public int getSubSequenceLength() {
        return subToOrigSeqIndex.length;
    }

    // --------------------------------------------------------------------------

    /**
     * Returns the internal index mapping from original positions i to subsequence positions j.
     * Index values are -1 for elements not contained in the subsequence.
     * @return a copy of the internal index from original to subsequence positions
     */
    public int[] getOrigToSubSeqIndex() {
        return origToSubSeqIndex.clone();
    }

    /**
     * Returns the internal index mapping from subsequence positions j to original positions i.
     * @return a copy of the internal index from subsequence to original positions
     */
    public int[] getSubToOrigSeqIndex() {
        return subToOrigSeqIndex.clone();
    }

    // --------------------------------------------------------------------------

    private void checkOrigDimensions(int n) {
        if (n < origToSubSeqIndex.length) {
            throw new IllegalArgumentException("length of input array must be at least " + origToSubSeqIndex.length);
        }
    }

    /**
     * Returns the subsequence of the input array as specified by this {@link SubsequenceMapping}
     * instance.
     * @param original the input array
     * @return the subsequence as an array of the same element type as the input array
     */
    public int[] getSubSequence(int[] original) {
        checkOrigDimensions(original.length);
        int[] sub = new int[subToOrigSeqIndex.length];
        for (int j = 0; j < sub.length; j++) {
            sub[j] = original[subToOrigSeqIndex[j]];
        }
        return sub;
    }

    /**
     * Returns the subsequence of the input array as specified by this {@link SubsequenceMapping}
     * instance.
     * @param original the input array
     * @return the subsequence as an array of the same element type as the input array
     */
    public float[] getSubSequence(float[] original) {
        checkOrigDimensions(original.length);
        float[] sub = new float[subToOrigSeqIndex.length];
        for (int j = 0; j < sub.length; j++) {
            sub[j] = original[subToOrigSeqIndex[j]];
        }
        return sub;
    }

    /**
     * Returns the subsequence of the input array as specified by this {@link SubsequenceMapping}
     * instance.
     * @param original the input array
     * @return the subsequence as an array of the same element type as the input array
     */
    public double[] getSubSequence(double[] original) {
        checkOrigDimensions(original.length);
        double[] sub = new double[subToOrigSeqIndex.length];
        for (int j = 0; j < sub.length; j++) {
            sub[j] = original[subToOrigSeqIndex[j]];
        }
        return sub;
    }

    /**
     * Returns the subsequence of the input array as specified by this {@link SubsequenceMapping}
     * instance.
     * @param original the input array
     * @return the subsequence as an array of the same element type as the input array
     */
    public <T> T[] getSubSequence(T[] original) {
        checkOrigDimensions(original.length);
        T[] sub = Arrays.copyOf(original, subToOrigSeqIndex.length);
        for (int j = 0; j < sub.length; j++) {
            sub[j] = original[subToOrigSeqIndex[j]];
        }
        return sub;
    }

    // --------------------------------------------------------------------------------------

    private void checkSubOrigDimensions(int m, int n) {
        if (m != subToOrigSeqIndex.length) {
            throw new IllegalArgumentException("length of subsequence must be " + subToOrigSeqIndex.length);
        }
        if (n < origToSubSeqIndex.length) {
            throw new IllegalArgumentException("length of original sequence must be at least " + origToSubSeqIndex.length);
        }
    }

    /**
     * Merges the specified subsequence into the original sequence by replacing only the
     * elements contained in the subsequence. Both sequences must match the lengths
     * defined by this {@link SubsequenceMapping} instance.
     * A copy of the original sequence is returned, none of the arguments is modified.
     * @param subSeq the subsequence
     * @param origSeq the original sequence
     * @return a copy of the original sequence with the elements of the subsequence merged into
     */
    public int[] merge(int[] subSeq, int[] origSeq) {
        checkSubOrigDimensions(subSeq.length, origSeq.length);
        int[] merged = Arrays.copyOf(origSeq, origSeq.length);
        // insert from subsequence:
        for (int j = 0; j < subSeq.length; j++) {
            merged[subToOrigSeqIndex[j]] = subSeq[j];
        }
        return merged;
    }

    /**
     * Merges the specified subsequence into the original sequence by replacing only the
     * elements contained in the subsequence. Both sequences must match the lengths
     * defined by this {@link SubsequenceMapping} instance.
     * A copy of the original sequence is returned, none of the arguments is modified.
     * @param subSeq the subsequence
     * @param origSeq the original sequence
     * @return a copy of the original sequence with the elements of the subsequence merged into
     */
    public double[] merge(double[] subSeq, double[] origSeq) {
        checkSubOrigDimensions(subSeq.length, origSeq.length);
        double[] merged = Arrays.copyOf(origSeq, origSeq.length);
        // insert from subsequence:
        for (int j = 0; j < subSeq.length; j++) {
            merged[subToOrigSeqIndex[j]] = subSeq[j];
        }
        return merged;
    }

    /**
     * Merges the specified subsequence into the original sequence by replacing only the
     * elements contained in the subsequence. Both sequences must match the lengths
     * defined by this {@link SubsequenceMapping} instance.
     * A copy of the original sequence is returned, none of the arguments is modified.
     * @param subSeq the subsequence
     * @param origSeq the original sequence
     * @return a copy of the original sequence with the elements of the subsequence merged into
     */
    public float[] merge(float[] subSeq, float[] origSeq) {
        checkSubOrigDimensions(subSeq.length, origSeq.length);
        float[] merged = Arrays.copyOf(origSeq, origSeq.length);
        // insert from subsequence:
        for (int j = 0; j < subSeq.length; j++) {
            merged[subToOrigSeqIndex[j]] = subSeq[j];
        }
        return merged;
    }

    /**
     * Merges the specified subsequence into the original sequence by replacing only the
     * elements contained in the subsequence. Both sequences must match the lengths
     * defined by this {@link SubsequenceMapping} instance.
     * A copy of the original sequence is returned, none of the arguments is modified.
     * @param subSeq the subsequence
     * @param origSeq the original sequence
     * @return a copy of the original sequence with the elements of the subsequence merged into
     */
    public <T> T[]  merge(T[] subSeq, T[] origSeq) {
        checkSubOrigDimensions(subSeq.length, origSeq.length);
        T[] merged = Arrays.copyOf(origSeq, origSeq.length);
        // insert from subsequence:
        for (int j = 0; j < subSeq.length; j++) {
            merged[subToOrigSeqIndex[j]] = subSeq[j];
        }
        return merged;
    }

}
