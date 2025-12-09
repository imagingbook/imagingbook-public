/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.util.bits;

/**
 * This interface defines the behavior of bit vectors, i.e., fixed-sized vectors with single bit elements. This is
 * similar to the standard Java class {@link java.util.BitSet}, which implements variable-sized vectors and additional
 * functionality.
 *
 * @author WB
 */
public interface BitVector {

    /**
     * Returns a copy of this {@link BitVector} which shares no data but is
     * {@code equal} to the original.
     * @return a copy of this {@link BitVector}
     */
    public BitVector duplicate();

	/**
	 * Returns {@code true} is the specified bit-element is set (1), {@code false}
     * otherwise (0).
	 * @param i the bit index
	 * @return as described
	 */
	public boolean get(int i);

	/**
	 * Sets the specified bit-element to the given boolean value (1 for {@code true},
     * 0 for {@code false}).
	 * @param i the bit index
	 * @param val a boolean value
	 */
	public default void set(int i, boolean val) {
		if (val) 
			this.set(i);
		else
			this.unset(i);
    }
	
	/**
	 * Sets the specified bit to {@code true} (1).
	 * @param i the bit index
	 */
	public void set(int i);

    /**
     * Sets all bits of this {@link BitVector} to the contents of the supplied
     * 0/1. Throws {@link IllegalArgumentException} if the length of the string
     * differs from this {@link BitVector}'s length or if the string contains
     * any non-0/1 character.
     * @param str01 a string of 0/1 values
     */
    public default void set(String str01) {
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
	 * Unsets the specified element (to bit-value 0).
	 * @param i the element index
	 */
	public void unset(int i);
	
	/**
	 * Sets all element values to 1.
	 */
	public void setAll();
	
	/**
	 * Sets all element values to 0.
	 */
	public void unsetAll();
	
	/**
	 * Returns the length of this bit vector.
	 * @return the length of this bit vector
	 */
	public int length();

    // ------------------------------------------------------------------------

	/**
	 * Returns the contents of this bit vector as a {@code byte} array.
     * Bit-value false maps to byte value 0, true maps to 1.
	 * @return a {@code byte} array
	 */
	public default byte[] asByteArray() {
		byte[] bytes = new byte[this.length()];
		for (int i = 0; i < bytes.length; i++) {
			if (get(i)) {
				bytes[i] = 1;
			}
		}
		return bytes;
	}

	/**
	 * Returns the contents of this bit vector as a {@code boolean} array.
	 * @return a {@code boolean} array
	 */
	public default boolean[] asBooleanArray() {
		boolean[] bools = new boolean[this.length()];
		for (int i = 0; i < bools.length; i++) {
			bools[i] = get(i);
		}
		return bools;
	}

    /**
     * Returns the contents of this bit vector as a {@code String} of 0/1
     * characters. Bit-value false maps to '0', true maps to character '1'.
     * @return a {@code String} of 0/1 characters
     */
    public default String asString() {
        char[] chars = new char[this.length()];
        for (int i = 0; i < chars.length; i++) {
            chars[i] = get(i) ? '1' : '0';
        }
        return new String(chars);
    }


	// static factory methods -----------------------------------------------

	/**
	 * Creates and returns a new bitvector of type {@link BitVector64} from the
     * specified {@code byte} array. Each byte element b is interpreted as
     * 0/false if b == 0 and 1/true otherwise.
	 * @param bytes an array of byte values
	 * @return a new bit vector
	 */
	public static BitVector from(byte[] bytes) {
		return new BitVector64(bytes);
	}

	/**
	 * Creates and returns a new bitvector of type {@link BitVector64} from the
     * specified {@code boolean} array, setting elements to false (0) or true (1).
	 * @param bools an array of boolean values
	 * @return a new bit vector
	 */
	public static BitVector from(boolean[] bools) {
		return new BitVector64(bools);
	}

    /**
     * Creates and returns a new bitvector of type {@link BitVector64} from the
     * specified {@code boolean} array, setting elements to 0/false or 1/true.
     * @param str01 a string of 0/1 characters
     * @return a new bit vector
     */
    public static BitVector from(String str01) {
        BitVector bv = BitVector.create(str01.length());
        bv.set(str01);
        return bv;
    }

	/**
	 * Creates and returns a new bitvector of type {@link BitVector64} with the
     * specified length. Elements are initialized to 0/false.
	 * @param length the length of the bit vector
	 * @return a new bit vector
	 */
	public static BitVector create(int length) {
        return (length <= 32) ?
		    new BitVector32(length) : new BitVector64(length);
	}

	/**
	 * Binarizes the specified {@code byte[]} by replacing all non-zero values
     * by 1. Returns a new array, the original array is not modified.
	 * @param b a {@code byte[]}
	 * @return a new {@code byte[]} with values 0/1 only
	 */
    @Deprecated         // move elsewhere (if used anywhere)
	public static byte[] binarize(byte[] b) {
		byte[] b2 = b.clone();
		for (int i = 0; i < b2.length; i++) {
			if (b2[i] != 0) {
				b2[i] = 1;
			}
		}
		return b2;
	}

    // -------------------------------------------------------------------------
    // Bit operations
    // -------------------------------------------------------------------------

    /**
     * Calculates the 'cardinality' of this bit vectors, i.e., the number of
     * true/1 bits.
     * @return the number of true/1 bits
     */
    public default int cardinality() {
        int n = this.length();
        int card = 0;
        for (int i = 0; i < n; i++)  {
            if (this.get(i)) {
                card++;
            }
        }
        return card;
    }
}
