/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.ransac;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

public class RandomDrawTest {
	
	/**
	 * Checks for duplicate ("==") elements in the result (simple, for testing only).
	 * @param <Q> the generic element type
	 * @param items array of objects
	 * @return true if any object is null or contained more than once
	 */
	private static <Q> boolean hasDuplicates(Q[] items) {
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

	@Test
	public void test1() {
		Integer[] numbers = { null, 1, 2, null, 3, 4, 5, 6, 7, null, null, null, 8, 9, 10 , null};
		int K = 2;
		int N = 1000000;
		
		Random rg = new Random(17);
		RandomDraw<Integer> rd = new RandomDraw<>(rg);
		
		for (int i = 0; i < N; i++) {
			Integer[] draw = rd.drawFrom(numbers, K);
			assertEquals(K, draw.length);
			assertFalse("duplicates found in " + Arrays.toString(draw), hasDuplicates(draw));
		}
	}
	
	@Test
	public void test2() {
		int K = 5;
		String[] numbers = { null, "a", "b", null, "c", "d", "e", "f", "g", null, "0", null, "h", "i", "j", null};
		int N = 1000000;
		
		RandomDraw<String> rd = new RandomDraw<>();
		
		for (int i = 0; i < N; i++) {
			String[] draw = rd.drawFrom(numbers, K);
			assertEquals(K, draw.length);
			assertFalse("duplicates found in " + Arrays.toString(draw), hasDuplicates(draw));
		}
	}
	
	@Test
	public void test3() {
		int K = 3;
		String[] numbers = { "a", "b", "c"};
		int N = 1000000;
		
		Random rg = new Random(17);
		RandomDraw<String> rd = new RandomDraw<>(rg);
		
		for (int i = 0; i < N; i++) {
			String[] draw = rd.drawFrom(numbers, K);
			assertEquals(K, draw.length);
			assertFalse("duplicates found in " + Arrays.toString(draw), hasDuplicates(draw));
		}
	}

}
