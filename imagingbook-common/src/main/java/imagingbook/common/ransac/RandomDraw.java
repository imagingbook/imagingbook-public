/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.ransac;

import java.util.Arrays;
import java.util.Random;

/**
 * An instance of this class randomly draws a set of k unique, non-null elements from a given array of element type T,
 * which may contain null elements (see method {@link #drawFrom(Object[], int)}). The size of the draw (k) is fixed and
 * must be specified at construction. The resulting sets contain no duplicate elements.
 *
 * @param <T> element type
 * @author WB
 * @version 2022/11/19
 */
public class RandomDraw<T> {
	
	/**
	 * Default maximum number of tries before exception is thrown.
	 */
	public static final int DefaultMaxTries = 1000;
	
	private int maxTries = DefaultMaxTries;
	private final Random rand;
	
	// -------------------------------------------------------------

	/**
	 * Constructor accepting an existing random generator. This may be useful to achieve predictable (repetitive) random
	 * behavior.
	 *
	 * @param rand a random generator of type {@link Random}
	 */
	public RandomDraw(Random rand) {
		this.rand = (rand == null) ? new Random() : rand;
	}
	
	/**
	 * Constructor creating its own random generator.
	 */
	public RandomDraw() {
		this(new Random());
	}
	
	/**
	 * Set the maximum number of tries.
	 * @param maxTries new maximum number of tries
	 * @see #DefaultMaxTries
	 */
	public void setMaxTries(int maxTries) {
		this.maxTries = maxTries;
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
	 * Randomly draws a set of k unique, non-null elements from the specified array of items, ignoring possible null
	 * elements. An exception is thrown if the maximum number of tries is exceeded (see {@link #DefaultMaxTries}). The
	 * returned array contains no null elements and no duplicates. Example:
	 * <pre>
	 * Integer[] numbers = { null, 1, 2, null, 3, 4, 5, 6, 7, null, null, null, 8, 9, 10, null };
	 * RandomDraw&lt;Integer&gt; rd = new RandomDraw&lt;&gt;();
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
				if (j++ > maxTries) {
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
	
}
