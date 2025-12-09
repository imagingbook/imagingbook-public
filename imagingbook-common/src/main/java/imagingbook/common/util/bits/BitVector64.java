/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.util.bits;

import java.util.Arrays;

/**
 * This class implements {@link BitVector} with internal 64-bit {@code long} data.
 *
 * @author WB
 */
public class BitVector64 implements BitVector {

	private static final int WL = 64;

    private final int length;
	private final long[] data;
    // private final long[] mask;   // mask[0] is the bitmask used for the final word

    private BitVector64(BitVector64 bv) {
        this.data = bv.data.clone();
        this.length = bv.length;
    }

    /**
     * Main constructor.
     * @param length the number of bits to hold
     */
	public BitVector64(int length) {
		if (length <= 0) {
			throw new IllegalArgumentException("bit vector length must be at least 1");
		}
		this.length = length;
        int n = (length + WL - 1) / WL;
		//int n = (length % WL == 0) ? length / WL : length / WL + 1;	// word count
		this.data = new long[n];
    }

    // package private, for debugging/testing only
    long[] getData() {
        return this.data;
    }

    /**
     * Calculates and returns the bitmask for the last 64-bit word.
     * For example, for length = 5 (whenever length % 64 == 5), the
     * resulting mask is
     * 1111100000000000000000000000000000000000000000000000000000000000"
     * @return the bitmask as a long value
     */
    long getBitMask() {
        int n =  this.length % 64;      // n = number of leading 1's
        long mask =  (n == 0) ?
            -1L :                 // all 64 bits = 1
            -1L << (64 - n);     // shift in 64-n zeros from the right
        return mask;
    }

    static String getLongAsString(long longVal) {
        return String.format("%64s", Long.toBinaryString(longVal)).replace(' ', '0');
    }

    // -----------------------------------------------------------------------

    @Deprecated
	public BitVector64(byte[] bytes) {
		this(bytes.length);
		for (int i = 0; i < bytes.length; i++) {
			if (bytes[i] != 0) {
				this.set(i);
			}
		}
	}

    @Deprecated
	public BitVector64(boolean[] bools) {
		this(bools.length);
		for (int i = 0; i < bools.length; i++) {
			this.set(i, bools[i]);
		}
	}
	
	// ---------------------------------------------------------------------



    // ---------------------------------------------------------------------

	@Override
	public int length() {
		return this.length;
	}
	
	@Override
	public boolean get(int i) {
		if (i < 0 || i >= length) {
			throw new IndexOutOfBoundsException("illegal index " + i);
		}
		final long mask = 1L << (i % WL);
		return (data[i / WL] & mask) != 0L;
	}
	
	@Override
	public void set(int i) {
		if (i < 0 || i >= length) {
			throw new IndexOutOfBoundsException("illegal index " + i);
		}
		final int j = i / WL;	// word index
		final long mask = 1L << (i % WL);
		data[j] =  data[j] | mask;
	}
	
	@Override
	public void unset(int i) {
		if (i < 0 || i >= length) {
			throw new IndexOutOfBoundsException("illegal index " + i);
		}
		final int j = i / WL;	// word index
		long mask = 1L << (i % WL);
		data[j] =  data[j] & ~mask;
	}
	
	@Override
	public void setAll() {
        Arrays.fill(data, ~0L);
        if (this.length % WL != 0) {    // not all bits are used
            data[data.length - 1] &= this.getBitMask();
        }
	}

	@Override
	public void unsetAll() {
        Arrays.fill(data, 0L);
	}
		
	
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append(BitVector64.class.getSimpleName() + "[");
		for (int i = 0; i < length; i++) {
			buf.append(this.get(i) ? "1" : "0");
		}
		buf.append("]");
		return buf.toString();
	}

    // ---------------------------------------------------------------------

    @Override
    public BitVector64 duplicate() {
        return new BitVector64(this);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(data);
    }

    // ---------------------------------------------------------------------

    @Override
    public int cardinality() {
        int card = 0;
        for (int k = 0; k < data.length; k++) {
            card += Long.bitCount(data[k]);
        }
        return card;
    }


    public static void main(String[] args) {
        for (int k = 0; k <= 128; k++) {
            System.out.println(k + " -> " + (64 - k % 64));
        }
    }
}
