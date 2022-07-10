/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.ransac;

import java.util.Arrays;
//import java.util.HashSet;
import java.util.Random;

/**
 * Randomly draws a set of k unique, non-null elements from a given array
 * with elements of type T, which may contain null elements.
 * The size of the draw (k) is fixed and must be specified at construction.
 * The resulting sets contain no duplicate elements.
 * 
 * @author WB
 *
 * @param <T> element type
 */
public class RandomDraw<T> {
	
	private static final int MaxTries = 1000;	// maximum number of tries before exception is thrown
	private final Random rand;
	
	// -------------------------------------------------------------
	
	public RandomDraw(Random rand) {
		this.rand = (rand == null) ? new Random() : rand;
	}
	
	public RandomDraw() {
		this(new Random());
	}
	
	// -------------------------------------------------------------
	
	// alternative version using a HashSet (slower)
//	public T[] drawFrom(T[] items, int k) {
//		if (k < 1) throw new IllegalArgumentException("k must be greater or equal 1");
//		HashSet<T> hs = new HashSet<>(k);
//		int i = 0;
//		while (i < k) {
//			int j = rand.nextInt(items.length);	// next random index
//			T item = items[j];
//			if (item != null && hs.add(item)) {
//				i++;
//			}
//		}
//		return hs.toArray(Arrays.copyOf(items, k));
//	}
	
	/**
	 * Randomly draws a set of k unique, non-null elements from the specified
	 * array of items, ignoring possible null elements.
	 * An exception is thrown if the maximum number of tries is exceeded.
	 * The returned array contains no null elements and no duplicates.
	 * Example:
	 * <pre>
	 * Integer[] numbers = {null, 1, 2, null, 3, 4, 5, 6, 7, null, null, null, 8, 9, 10 , null};
	 * RandomDraw<Integer> rd = new RandomDraw<>();
	 * Integer[] draw = rd.drawFrom(numbers, 2);
	 * </pre>
	 * 
	 * @param items an array of elements of type T, null elements being allowed
	 * @param k the number of items to draw
	 * @return an array of k randomly drawn (non-null) items
	 */
	public T[] drawFrom(T[] items, int k) {
		if (k < 1) throw new IllegalArgumentException("k must be greater or equal 1");
		int[] idx = drawRandomIndexes(items, k);
		T[] draw = Arrays.copyOf(items, k);	// trick to create an array of generic type T
		for (int i = 0; i < k; i++) {
			draw[i] = items[idx[i]];
		}
		return draw;
	}
	
	// draw k unique integers (all must be different):
	private int[] drawRandomIndexes(T[] items, int k) {
		int[] indexes = new int[k];
		for (int d = 0; d < k; d++) {
			int j = 0;							// count tries
			int i = rand.nextInt(items.length);
			while (items[i] == null || wasPickedBefore(indexes, d, i)) {
				i = rand.nextInt(items.length);
				if (j++ > MaxTries) {
					throw new RuntimeException("max. tries exceeded: " + j);
				}
			}
			indexes[d] = i;
		}
		return indexes;
	}

	// Checks if idx[0],...,idx[d-1] contains i.
	private boolean wasPickedBefore(int[] indexes, int d, int i) {
		for (int j = 0; j < d; j++) {
			if (i == indexes[j]) {
				return true;
			}
		}	
		return false;
	}
	
	/**
	 * Checks for duplicate ("==") elements in the result (simple, for testing only).
	 * @param <Q> the generic element type
	 * @param items array of objects
	 * @return true if any object is null or contained more than once
	 */
	protected static <Q> boolean hasDuplicates(Q[] items) {
		for (int i = 0; i < items.length; i++) {
			Q x = items[i];
			if (x == null)
				return true;
			for (int j = 0; j < i; j++) {
				if (x == items[j]) {
					return true;
				}
			}
		}
		return false;
	}
	
}
