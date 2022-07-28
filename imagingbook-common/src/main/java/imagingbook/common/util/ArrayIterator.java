/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.util;


import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This class implements a simple iterator for arbitrary arrays.
 * Does not care about array elements being null.
 * 
 * TODO: skip null elements
 */
public class ArrayIterator<T> implements Iterator<T> {

	private final T[] data;
	private int next;

	/**
	 * Constructor.
	 * @param data the array to be iterated
	 */
	public ArrayIterator(T[] data) {
		this.data = data;
		next = 0;
	}

	@Override
	public boolean hasNext() {
		return next < data.length;
	}

	@Override
	public T next() {
		if (hasNext()) {
			return data[next++];
		}
		else {
			throw new NoSuchElementException();
		}
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
