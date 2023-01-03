/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.util.bits;

import java.util.Arrays;

/**
 * This class implements {@link BitVector} with internal 32-bit {@code int} data.
 * @author WB
 */
public class BitVector32 implements BitVector {
	
	private static final int WL = 32;
	
	private final int[] data;
	private final int length;
	
	public BitVector32(int length) {
		if (length <= 0) {
			throw new IllegalArgumentException("bit vector length must be at least 1");
		}
		this.length = length;
		int n = (length % WL == 0) ? length / WL : length / WL + 1;	// word count
		this.data = new int[n];
	}
	
	public BitVector32(byte[] bytes) {
		this(bytes.length);
		for (int i = 0; i < bytes.length; i++) {
			if (bytes[i] != 0) {
				this.set(i);
			}
		}
	}
	
	public BitVector32(boolean[] bools) {
		this(bools.length);
		for (int i = 0; i < bools.length; i++) {
			this.set(i, bools[i]);
		}
	}
	
	// ---------------------------------------------------------------------

	@Override
	public int getLength() {
		return this.length;
	}
	
	@Override
	public boolean get(int i) {
		if (i < 0 || i >= length) {
			throw new IndexOutOfBoundsException("illegal index " + i);
		}
		final int mask = 1 << (i % WL);
		return (data[i / WL] & mask) != 0;
	}
	
	@Override
	public void set(int i) {
		if (i < 0 || i >= length) {
			throw new IndexOutOfBoundsException("illegal index " + i);
		}
		final int j = i / WL;	// word index
		final int mask = 1 << (i % WL);
		data[j] =  data[j] | mask;
	}
	
	@Override
	public void unset(int i) {
		if (i < 0 || i >= length) {
			throw new IndexOutOfBoundsException("illegal index " + i);
		}
		final int j = i / WL;	// word index
		final int mask = 0x01 << (i % WL);
		data[j] =  data[j] & ~mask;
	}
	
	@Override
	public void setAll() {
		Arrays.fill(data, ~0);
	}

	@Override
	public void unsetAll() {
		Arrays.fill(data, 0);
	}
	
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append(BitVector32.class.getSimpleName() + "[");
		for (int i = 0; i < length; i++) {
			buf.append(this.get(i) ? "1" : "0");
		}
		buf.append("]");
		return buf.toString();
	}

}
