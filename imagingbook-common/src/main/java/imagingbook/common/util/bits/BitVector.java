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
 * This class implements a fixed-sized vector with single-bit elements. This is
 * similar to the standard Java class {@link java.util.BitSet}, which implements
 * variable-sized (extendable) bit vectors. Bit vectors allow efficient storage,
 * logical operations and comparison of bit data. Bit vectors are not immutable,
 * but their 0/1 elements can be modified. Operations between pairs of bit
 * vectors are only implemented for vectors of the same length, similar to
 * ordinary array operations. Bit vectors can be initialized from a variety
 * of data sources, such as 0/1 strings, boolean arrays or byte arrays.
 *
 * Bits are internally stored as a sequence (array) of 64-bit {@code long}
 * values. All excess bits are always maintained at 0 values.
 * Bit vectors must have at least 1 element. Zero-length bit vectors are not
 * allowed.
 *
 * @author WB
 */
public class BitVector {
	private static final int WL = 64;

    private final int length;
	private final long[] data;

    /**
     * The one and only public constructor.
     * @param length the number of bits to hold
     */
	public BitVector(int length) {
		if (length <= 0) {
			throw new IllegalArgumentException("bit vector length must be at least 1");
		}
		this.length = length;
        int n = (length + WL - 1) / WL; // number of long values required
		this.data = new long[n];
    }

    private BitVector(BitVector bv) {
        this.data = bv.data.clone();
        this.length = bv.length;
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

    /**
     * Creates and returns a new {@link BitVector} from the
     * specified {@code boolean} array, setting elements to 0=false or 1=true.
     * @param str01 a string of 0/1 characters
     * @return a new bit vector
     */
    public static BitVector from(String str01) {
        BitVector bv = new BitVector(str01.length());
        bv.set(str01);
        return bv;
    }

    /**
     * Creates and returns a new {@link BitVector} from the
     * specified {@code byte} array. Each byte element b is interpreted as
     * 0/false if b == 0 and 1/true otherwise.
     * @param bytes an array of byte values
     * @return a new bit vector
     */
    public static BitVector from(byte[] bytes) {
        BitVector bv = new BitVector(bytes.length);
        bv.set(bytes);
        return bv;
    }

    /**
     * Sets all bits of this {@link BitVector} to the contents of the supplied
     * array. Each byte element b is interpreted as
     * 0/false if b == 0 and 1/true otherwise.
     * Throws {@link IllegalArgumentException} if the length of the array
     * differs from this {@link BitVector}'s length.
     * @param bytes an array of {@code byte} values
     */
    public void set(byte[] bytes) {
        if (this.length() != bytes.length) {
            throw new IllegalArgumentException("wrong argument length: " + bytes.length);
        }
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] != 0) {
                this.set(i);
            }
        }
    }

    /**
     * Creates and returns a new {@link BitVector} from the
     * specified {@code boolean} array, setting elements to false (0) or true (1).
     * @param bools an array of boolean values
     * @return a new bit vector
     */
    public BitVector from(boolean[] bools) {
        BitVector bv = new BitVector(bools.length);
        bv.set(bools);
        return bv;
    }

    /**
     * Sets all bits of this {@link BitVector} to the contents of the supplied
     * array. Throws {@link IllegalArgumentException} if the length of the array
     * differs from this {@link BitVector}'s length.
     * @param bools an array of boolean values
     */
	public void set(boolean[] bools) {
        if (this.length() != bools.length) {
            throw new IllegalArgumentException("wrong argument length: " + bools.length);
        }
		for (int i = 0; i < bools.length; i++) {
			this.set(i, bools[i]);
		}
	}

    // ---------------------------------------------------------------------

    /**
     * Returns the length of this bit vector.
     * @return the length of this bit vector
     */
	public int length() {
		return this.length;
	}

    /**
     * Returns {@code true} is the specified bit-element is set (1), {@code false}
     * otherwise (0).
     * @param i the bit index
     * @return as described
     */
	public boolean get(int i) {
		if (i < 0 || i >= length) {
			throw new IndexOutOfBoundsException("illegal index " + i);
		}
		final long mask = 1L << (i % WL);
		return (data[i / WL] & mask) != 0L;
	}

    /**
     * Sets the specified bit to {@code true} (1).
     * @param i the bit index
     */
	public void set(int i) {
		if (i < 0 || i >= length) {
			throw new IndexOutOfBoundsException("illegal index " + i);
		}
		final int j = i / WL;	// word index
		final long mask = 1L << (i % WL);
		data[j] =  data[j] | mask;
	}

    /**
     * Unsets the specified element (to bit-value 0).
     * @param i the element index
     */
	public void unset(int i) {
		if (i < 0 || i >= length) {
			throw new IndexOutOfBoundsException("illegal index " + i);
		}
		final int j = i / WL;	// word index
		long mask = 1L << (i % WL);
		data[j] =  data[j] & ~mask;
	}

    /**
     * Sets all element values to 1.
     */
	public void set() {
        Arrays.fill(data, ~0L);
        this.applyBitMask();
	}

    /**
     * Sets all element values to 0.
     */
	public void unset() {
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
        byte[] bytes = new byte[this.length];
        for (int i = 0; i < this.length; i++) {
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

    /**
     * Returns a copy of this {@link BitVector} which shares no data but is
     * {@code equal} to the original.
     * @return a copy of this {@link BitVector}
     */
    public BitVector duplicate() {
        return new BitVector(this);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(data);
    }

    // ---------------------------------------------------------------------

    /**
     * Calculates and returns the cardinality (number of 1-bits) of this
     * bit vector.
     * @return the cardinality of this bit vector
     */
    public int cardinality() {
        int card = 0;
        for (int k = 0; k < data.length; k++) {
            card += Long.bitCount(data[k]);
        }
        return card;
    }

    /**
     * Performs a bitwise AND operation between this bit vector (a) and
     * the supplied bit vector B. Both must have the same length.
     * @param b the other bit vector
     * @return the bit vector (a AND b)
     */
    public BitVector and(BitVector b) {
        BitVector a = this;
        checkSameLength(a, b);
        BitVector c = a.duplicate();
        for (int k = 0; k < a.data.length; k++) {
            c.data[k] &= b.data[k];
        }
        return c;
    }

    /**
     * Performs a bitwise NOT operation on this bit vector
     * and returns the result as a new bit vector.
     * @return the negated bit vector
     */
    public BitVector not() {
        BitVector b = this.duplicate();
        for (int k = 0; k < b.data.length; k++) {
            b.data[k] = ~b.data[k];
        }
        b.applyBitMask();
        return b;
    }

    private void applyBitMask() {
        if (length % WL != 0) {    // not all bits are used
            data[data.length - 1] &= getBitMask();
        }
    }

    /**
     * Performs a bitwise OR operation between this bit vector (a) and
     * the supplied bit vector B. Both must have the same length.
     * @param b the other bit vector
     * @return the bit vector (a OR b)
     */
    public BitVector or(BitVector b) {
        BitVector a = this;
        checkSameLength(a, b);
        BitVector c = a.duplicate();
        for (int k = 0; k < a.data.length; k++) {
            c.data[k] &= b.data[k];
        }
        return c;
    }

    public int hammingDistance(BitVector b) {
        BitVector a = this;
        checkSameLength(a, b);
        int dist = 0;
        for (int k = 0; k < a.data.length; k++) {
            long c = a.data[k] ^ b.data[k];
            dist += Long.bitCount(c);
        }
        return dist;
    }

    // ---------------------------------------------------------------------

    private static void checkSameLength(BitVector a, BitVector b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("BitVectors a, b have different lengths");
        }
    }

}
