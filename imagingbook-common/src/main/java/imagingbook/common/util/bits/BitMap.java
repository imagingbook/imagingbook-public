package imagingbook.common.util.bits;

import imagingbook.common.geometry.basic.Pnt2d.PntInt;

/**
 * This class implements a true 2D bitmap container, i.e., each 0/1 element
 * occupies only a single bit (unlike {@code boolean} arrays, which require at 
 * least 8 bits per element).
 * 
 * @author WB
 * @version 2021/01/30
 */
public class BitMap {
	
	/** the width of this bitmap */
	private final int width;
	/** the height of this bitmap */
	private final int height;
	
	private final BitVector bitvec;
	
	/**
	 * Constructor.
	 * @param width the width of the new bitmap
	 * @param height the height of the new bitmap
	 */
	public BitMap(int width, int height) {
		this(width, height, null);
	}
	
	/**
	 * Constructor.
	 * @param width width the width of the new bitmap
	 * @param height height the height of the new bitmap
	 * @param bytes a byte array, 0 maps to false, anything else to true
	 */
	public BitMap(int width, int height, byte[] bytes) {
		this.width = width;
		this.height = height;
		this.bitvec = (bytes != null) ? BitVector.from(bytes) : BitVector.create(width * height);
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	/**
	 * Returns {@code true} is the specified element is set (1),
	 * {@code false} otherwise (0).
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @return as described
	 */
	public boolean get(int x, int y) {
		return bitvec.get(y * width + x);
	}
	
	/**
	 * Returns {@code true} is the specified element is set (1),
	 * {@code false} otherwise (0).
	 * @param p the x/y-coordinate (point)
	 * @return as described
	 */
	public boolean get(PntInt p) {
		return bitvec.get(p.y * width + p.x);
	}
	
	/**
	 * Sets the specified bit-element to the given boolean value
	 * (1 for {@code true}, 0 for {@code false}).
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @param val a boolean value
	 */
	public void set(int x, int y, boolean val) {
		if (val) {
			this.set(x, y);
		}
		else {
			this.unset(x, y);
		}
	}
	
	/**
	 * Sets the specified bit-element to the given boolean value
	 * (1 for {@code true}, 0 for {@code false}).
	 * @param p the x/y-coordinate (point)
	 * @param val a boolean value
	 */
	public void set(PntInt p, boolean val) {
		set(p.x, p.y, val);
	}
	
	/**
	 * Sets the specified element (to bit-value 1).
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 */
	public void set(int x, int y) {
		bitvec.set(y * width + x);
	}
	
	/**
	 * Sets the specified element (to bit-value 1).
	 * @param p the x/y-coordinate (point)
	 */
	public void set(PntInt p) {
		bitvec.set(p.y * width + p.x);
	}
	
	/**
	 * Unsets the specified element (to bit-value 0).
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 */
	public void unset(int x, int y) {
		bitvec.unset(y * width + x);
	}
	
	/**
	 * Unsets the specified element (to bit-value 0).
	 * @param p the x/y-coordinate (point)
	 */
	public void unset(PntInt p) {
		bitvec.unset(p.y * width + p.x);
	}
	
	/**
	 * Sets all elements to 1.
	 */
	public void setAll() {
		bitvec.setAll();
	}
	
	/**
	 * Sets all elements to 0.
	 */
	public void unsetAll() {
		bitvec.unsetAll();
	}
	
	/**
	 * Returns the underlying 1D {@link BitVector}.
	 * @return the bit vector
	 */
	public BitVector getBitVector() {
		return this.bitvec;
	}
	
	// static methods --------------------------------------------------
	
	/**
	 * Creates a new 2D bitmap of the specified size.
	 * @param width the width of the new bitmap
	 * @param height the height of the new bitmap
	 * @return the new bitmap
	 */
	public static BitMap create(int width, int height) {
		return new BitMap(width, height);
	}
	
}
