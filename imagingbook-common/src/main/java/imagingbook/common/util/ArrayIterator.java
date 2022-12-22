/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.common.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.PrimitiveIterator;

/**
 * This class defines static methods for creating array iterators.
 * 
 * @author WB
 * @version 2022/11/20
 */
public abstract class ArrayIterator {
	// TODO: make class generic, consider primitive arrays!
	private ArrayIterator() {}
	
	// https://stackoverflow.com/questions/10335662/convert-java-array-to-iterable.
	/**
	 * Returns an iterator for the specified (non-primitive) array. The resulting iterator does not implement
	 * {@link Iterator#remove()}.
	 *
	 * @param <T> the generic element type
	 * @param array a non-primitive array
	 * @return the associated iterator
	 */
	public static <T> Iterator<T> from(T[] array) {
		return Arrays.stream(array).iterator();
	}
	
	// public static void main(String[] args) {
	// 	Integer[] a = {1, 2, 3};
	// 	Iterator<Integer> iterA = ArrayIterator.from(a);
	//
	// 	while(iterA.hasNext()) {
	// 		System.out.println(iterA.next());
	// 	}
	//
	// 	int[] b = {5, 6, 7};
	// 	PrimitiveIterator.OfInt iterB = Arrays.stream(b).iterator();
	// 	while(iterB.hasNext()) {
	// 		System.out.println(iterB.next());
	// 	}
	// }

}
