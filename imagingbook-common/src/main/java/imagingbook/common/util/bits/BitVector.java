package imagingbook.common.util.bits;

/**
 * This interface defines the behavior of bit vectors, i.e.,
 * fixed-sized vectors with single bit elements.
 * This is similar to the standard Java class {@link java.util.BitSet}, which
 * implements variable-sized vectors and additional functionality.
 * 
 * @author WB
 */
public interface BitVector {
	
	/**
	 * Returns {@code true} is the specified bit-element is set (1),
	 * {@code false} otherwise (0).
	 * @param i the element index
	 * @return as described
	 */
	public boolean get(int i);
	
	/**
	 * Sets the specified bit-element to the given boolean value
	 * (1 for {@code true}, 0 for {@code false}).
	 * @param i the element index
	 * @param val a boolean value
	 */
	public default void set(int i, boolean val) {
		if (val) 
			this.set(i);
		else
			this.unset(i);
	}
	
	/**
	 * Sets the specified element (to bit-value 1).
	 * @param i the element index
	 */
	public void set(int i);
	
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
	public int getLength();
	
	/**
	 * Returns the contents of this bit vector as a {@code byte} array.
	 * Bit-value 0 maps to byte value 0x00, value 1 maps to 0xFF.
	 * @return a {@code byte} array
	 */
	public default byte[] toByteArray() {
		byte[] bytes = new byte[this.getLength()];
		for (int i = 0; i < bytes.length; i++) {
			if (get(i)) {
				bytes[i] = (byte) 0xFF;
			}
		}
		return bytes;
	}
	
	/**
	 * Returns the contents of this bit vector as a {@code boolean} array.
	 * Bit-value 0 maps to false, value 1 maps to true.
	 * @return a {@code boolean} array
	 */
	public default boolean[] toBooleanArray() {
		boolean[] bools = new boolean[this.getLength()];
		for (int i = 0; i < bools.length; i++) {
			bools[i] = get(i);
		}
		return bools;
	}
	
	// static factory methods -----------------------------------------------
	
	/**
	 * Creates and returns a new bitvector of type
	 * {@link BitVector64} from the specified {@code byte}
	 * array. Each byte element b is interpreted as 0/false
	 * if b = 0 and 1/true otherwise.
	 * 
	 * @param bytes an array of byte values
	 * @return a new bit vector
	 */
	public static BitVector from(byte[] bytes) {
		return new BitVector64(bytes);
	}
	
	/**
	 * Creates and returns a new bitvector of type
	 * {@link BitVector64} from the specified {@code boolean}
	 * array, setting elements to 0/false or 1/true.
	 * 
	 * @param bools an array of boolean values
	 * @return a new bit vector
	 */
	public static BitVector from(boolean[] bools) {
		return new BitVector64(bools);
	}
	
	/**
	 * Creates and returns a new bitvector of type
	 * {@link BitVector64} with the specified length.
	 * Elements are initialized to 0/false.
	 * 
	 * @param length the length of the bit vector
	 * @return a new bit vector
	 */
	public static BitVector create(int length) {
		return new BitVector64(length);
	}

}
