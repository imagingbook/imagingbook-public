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
 * This class ...
 *
 * @author WB
 */
public class BitVector {

	private static final int WL = 64;

    private final int length;
	private final long[] data;
    // private final long[] mask;   // mask[0] is the bitmask used for the final word

    private BitVector(BitVector bv) {
        this.data = bv.data.clone();
        this.length = bv.length;
    }

    /**
     * Main constructor.
     * @param length the number of bits to hold
     */
	public BitVector(int length) {
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

    /**
     * Helper method. Returns the bit pattern of a long value as a 0/1 string
     * (in MSB-first order). All 64 bits (i.e., leading zeros) are included
     * in the string.
     * @param longVal a long value
     * @return the corresponding 0/1 string
     */
    static String getLongAsString(long longVal) {
        return String.format("%64s", Long.toBinaryString(longVal)).replace(' ', '0');
    }

    // -----------------------------------------------------------------------

    public static BitVector from(byte[] bytes) {
        BitVector bv = new BitVector(bytes.length);
        bv.set(bytes);
        return bv;
    }

    public void set(byte[] bytes) {
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] != 0) {
                this.set(i);
            }
        }
    }

    public BitVector from(boolean[] bools) {
        BitVector bv = new BitVector(bools.length);
        bv.set(bools);
        return bv;
    }

	public void set(boolean[] bools) {
		for (int i = 0; i < bools.length; i++) {
			this.set(i, bools[i]);
		}
	}

    // ---------------------------------------------------------------------

	public int length() {
		return this.length;
	}

	public boolean get(int i) {
		if (i < 0 || i >= length) {
			throw new IndexOutOfBoundsException("illegal index " + i);
		}
		final long mask = 1L << (i % WL);
		return (data[i / WL] & mask) != 0L;
	}

	public void set(int i) {
		if (i < 0 || i >= length) {
			throw new IndexOutOfBoundsException("illegal index " + i);
		}
		final int j = i / WL;	// word index
		final long mask = 1L << (i % WL);
		data[j] =  data[j] | mask;
	}

	public void unset(int i) {
		if (i < 0 || i >= length) {
			throw new IndexOutOfBoundsException("illegal index " + i);
		}
		final int j = i / WL;	// word index
		long mask = 1L << (i % WL);
		data[j] =  data[j] & ~mask;
	}

	public void setAll() {
        Arrays.fill(data, ~0L);
        if (this.length % WL != 0) {    // not all bits are used
            data[data.length - 1] &= this.getBitMask();
        }
	}

	public void unsetAll() {
        Arrays.fill(data, 0L);
	}

    /**
     * Sets the specified bit-element to the given boolean value (1 for {@code true},
     * 0 for {@code false}).
     * @param i the bit index
     * @param val a boolean value
     */
    public void set(int i, boolean val) {
        if (val)
            this.set(i);
        else
            this.unset(i);
    }

    /**
     * Sets all bits of this {@link BitVector} to the contents of the supplied
     * 0/1. Throws {@link IllegalArgumentException} if the length of the string
     * differs from this {@link BitVector}'s length or if the string contains
     * any non-0/1 character.
     * @param str01 a string of 0/1 values
     */
    public void set(String str01) {
        if (this.length() != str01.length()) {
            throw new IllegalArgumentException("wrong argument length: " + str01.length());
        }
        char[] chars01 = str01.toCharArray();
        for (int i = 0; i < chars01.length; i++) {
            switch(chars01[i]) {
                case '0' ->  this.unset(i);
                case '1' -> this.set(i);
                default ->  throw new IllegalArgumentException("illegal character in 0/1 string: " + chars01[i]);
            }
        }
    }

    /**
     * Returns the contents of this bit vector as a {@code byte} array.
     * Bit-value false maps to byte value 0, true maps to 1.
     * @return a {@code byte} array
     */
    public byte[] asByteArray() {
        byte[] bytes = new byte[this.length()];
        for (int i = 0; i < bytes.length; i++) {
            if (get(i)) {
                bytes[i] = 1;
            }
        }
        return bytes;
    }

    /**
     * Returns the contents of this bit vector as a {@code String} of 0/1
     * characters. Bit-value false maps to '0', true maps to character '1'.
     * @return a {@code String} of 0/1 characters
     */
    public String asString() {
        char[] chars = new char[this.length()];
        for (int i = 0; i < chars.length; i++) {
            chars[i] = get(i) ? '1' : '0';
        }
        return new String(chars);
    }

    /**
     * Returns the contents of this bit vector as a {@code boolean} array.
     * @return a {@code boolean} array
     */
    public boolean[] asBooleanArray() {
        boolean[] bools = new boolean[this.length()];
        for (int i = 0; i < bools.length; i++) {
            bools[i] = get(i);
        }
        return bools;
    }
		
	
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append(BitVector.class.getSimpleName() + "[");
		for (int i = 0; i < length; i++) {
			buf.append(this.get(i) ? "1" : "0");
		}
		buf.append("]");
		return buf.toString();
	}

    // ---------------------------------------------------------------------

    public BitVector duplicate() {
        return new BitVector(this);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(data);
    }

    // ---------------------------------------------------------------------

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
