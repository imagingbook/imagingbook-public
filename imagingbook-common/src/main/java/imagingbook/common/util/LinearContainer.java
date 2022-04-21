/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.util;

import java.util.Iterator;

//import java.util.Iterator;

/**
 * This class implements a 1D map for arbitrary objects
 * with flexible bottom and top index,
 * similar to an array in Pascal. Containers are immutable.
 */
public class LinearContainer<T> implements Iterable<T> {
	
	private final int botIndex, topIndex;
	private final T[] data;
	
	/**
	 * Creates a LinearContainer with the index range
	 * [0, n - 1], like an ordinary array.
	 * @param n size of the container.
	 */
	public LinearContainer(int n) {
		this(0, n - 1);
	}
	
	/**
	 * Creates a LinearContainer with the index range
	 * [botIndex, topIndex].
	 * @param botIndex bottom (smallest) index.
	 * @param topIndex top (largest) index.
	 */
	@SuppressWarnings("unchecked")
	public LinearContainer(int botIndex, int topIndex) {
		if (botIndex > topIndex) {
			throw new IllegalArgumentException("LinearContainer: botIndex > topIndex");
		}
		this.botIndex = botIndex;
		this.topIndex = topIndex;
		int n = topIndex - botIndex + 1;
		data = (T[]) new Object[n];
	}
	
	public T getElement(int k) {
		return data[k - botIndex];
	}
	
	public void setElement(int k, T elem) {
		// TODO: check for out-of-bounds index k
		data[k - botIndex] = elem;
	}

	public int getBotIndex() {
		return botIndex;
	}
	
	public int getTopIndex() {
		return topIndex;
	}

	public Iterator<T> iterator() {
		return new ArrayIterator<T>(data);
	}

}
