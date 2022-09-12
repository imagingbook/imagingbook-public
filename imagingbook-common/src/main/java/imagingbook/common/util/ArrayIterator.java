/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.util;

import java.util.Arrays;
import java.util.Iterator;

/**
 * This class defines static methods returning to array iterators.
 */
public abstract class ArrayIterator {
	
	private ArrayIterator() {}
	
	/**
	 * Returns an iterator for the specified (non-primitive) array.
	 * See // https://stackoverflow.com/questions/10335662/convert-java-array-to-iterable.
	 * 
	 * @param <T> the generic element type
	 * @param array a non-primitive array 
	 * @return the associated iterator
	 */
	public static <T> Iterator<T> from(T[] array) {
		return Arrays.stream(array).iterator();
	}

}
