/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;

public class PrimitiveSortMapTest {

	@Test
	public void test1() {
		double[] numbers = { 50, 20, 100, 120, 40, -10 };
		int[] permExpected = {5, 1, 4, 0, 2, 3};
		int n = numbers.length;
		
		PrimitiveSortMap sm = new PrimitiveSortMap(numbers);
		
		int[] perm = sm.getPermutation();
		assertArrayEquals(permExpected, perm);
		
		for (int k = 0; k < n; k++) {
			int smallestIdx = PrimitiveSortMap.getNthSmallestIndex(numbers, k);
			assertEquals(numbers[perm[k]], numbers[smallestIdx], 0);
			assertEquals(numbers[perm[k]], PrimitiveSortMap.getNthSmallestValue(numbers, k), 0);
		}
		
		int largestIdx = PrimitiveSortMap.getLargestIndex(numbers);
		assertEquals(numbers[perm[n - 1]], numbers[largestIdx], 0);
	}
	

	
	@Test		// check if permutation is "sorted"
	public void test2Double() {
		Random rg = new Random(17);
		int n = 100;
		for (int k = 0; k < 10; k++) {
			double[] numbers = makeRandomDoubleArr(n, rg);
			int[] perm = new PrimitiveSortMap(numbers).getPermutation();
			for (int i = 1; i < numbers.length; i++) {
				assertTrue(numbers[perm[i - 1]] <= numbers[perm[i]]);
			}
			assertEquals(numbers[perm[0]], PrimitiveSortMap.getNthSmallestValue(numbers, 0), 0);
			assertEquals(numbers[perm[n - 1]], PrimitiveSortMap.getNthSmallestValue(numbers, n - 1), 0);
		}
	}
	
	@Test		// check if permutation is "sorted"
	public void test2Float() {
		Random rg = new Random(17);
		int n = 100;
		for (int k = 0; k < 10; k++) {
			float[] numbers = makeRandomFloatArr(n, rg);
			int[] perm = new PrimitiveSortMap(numbers).getPermutation();
			for (int i = 1; i < numbers.length; i++) {
				assertTrue(numbers[perm[i - 1]] <= numbers[perm[i]]);
			}
			assertEquals(numbers[perm[0]], PrimitiveSortMap.getNthSmallestValue(numbers, 0), 0);
			assertEquals(numbers[perm[n - 1]], PrimitiveSortMap.getNthSmallestValue(numbers, n - 1), 0);
		}
	}
	
	@Test		// check if permutation is "sorted"
	public void test2Int() {
		Random rg = new Random(17);
		int n = 100;
		for (int k = 0; k < 10; k++) {
			int[] numbers = makeRandomIntArr(n, rg);
			int[] perm = new PrimitiveSortMap(numbers).getPermutation();
			for (int i = 1; i < numbers.length; i++) {
				assertTrue(numbers[perm[i - 1]] <= numbers[perm[i]]);
			}
			assertEquals(numbers[perm[0]], PrimitiveSortMap.getNthSmallestValue(numbers, 0), 0);
			assertEquals(numbers[perm[n - 1]], PrimitiveSortMap.getNthSmallestValue(numbers, n - 1), 0);
		}
	}
	
	// -----------------------------------------

	private static double[] makeRandomDoubleArr(int n, Random rg) {
		double[] vals = new double[n];
		for (int i = 0; i < n; i++) {
			vals[i] = rg.nextInt(1000);
		}
		return vals;
	}
	
	private static float[] makeRandomFloatArr(int n, Random rg) {
		float[] vals = new float[n];
		for (int i = 0; i < n; i++) {
			vals[i] = rg.nextInt(1000);
		}
		return vals;
	}
	
	private static int[] makeRandomIntArr(int n, Random rg) {
		int[] vals = new int[n];
		for (int i = 0; i < n; i++) {
			vals[i] = rg.nextInt(1000);
		}
		return vals;
	}
}
