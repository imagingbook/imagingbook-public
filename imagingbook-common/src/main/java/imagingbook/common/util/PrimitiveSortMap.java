/*
 *  This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge.
 * All rights reserved. Visit https://imagingbook.com for additional details.
 */
package imagingbook.common.util;

import java.util.stream.IntStream;

/**
 * <p>
 * Determines the 'permutation' of a sequence of numbers and keeps it as an array ({@code perm}) of position indexes.
 * These indexes indicate how the original input array may be re-ordered to become sorted (see
 * {@link #getPermutation()}). Implemented only for arrays of type {@code int}, {@code float} and {@code double}.
 * </p>
 * <p>
 * Usage example - get the second-smallest element of a {@code double} array:
 * </p>
 * <pre>
 * 	double[] a = ... 	// some array
 * 	int k = PrimitiveSortMap.getNthSmallestIndex(a, 1);	// index of 2nd-smallest element
 * 	double x = a[k];
 * </pre>
 * <p>
 * Alternatively, the 2nd-smallest <em>value</em> can be obtained directly by
 * </p>
 * <pre>
 * double x = PrimitiveSortMap.getNthSmallestValue(a, 1);
 * </pre>
 *
 * @author WB
 * @version 2022/09/15
 */
public class PrimitiveSortMap {
	
	private final int[] perm;
	
	/**
	 * Constructor for {@code double[]}. The input array is not modified.
	 * @param a the input array
	 */
	public PrimitiveSortMap(double[] a) {
		this.perm = IntStream.range(0, a.length).boxed()
		        .sorted((i, j) -> Double.compare(a[i], a[j]))
		        .mapToInt(x->x.intValue()).toArray();
	}
	
	/**
	 * Constructor for {@code float[]}. The input array is not modified.
	 * @param a the input array
	 */
	public PrimitiveSortMap(float[] a) {
		this.perm = IntStream.range(0, a.length).boxed()
		        .sorted((i, j) -> Float.compare(a[i], a[j]))
		        .mapToInt(x->x.intValue()).toArray();
	}
	
	/**
	 * Constructor for {@code int[]}. The input array is not modified.
	 * @param a the input array
	 */
	public PrimitiveSortMap(int[] a) {
		this.perm = IntStream.range(0, a.length).boxed()
		        .sorted((i, j) -> Integer.compare(a[i], a[j]))
		        .mapToInt(x->x.intValue()).toArray();
	}
	
	// --------------------------------------------------

	/**
	 * Returns the permutation (position indexes) for the underlying number sequence as a {@code int} array. That is,
	 * the first value in the returned array is the index of the smallest element in {@code numbers}, the second element
	 * points to the second-smallest element, etc. For example, if the original number sequence (passed to the
	 * constructor) is
	 * <pre>
	 *     double[] a = {50.0, 20.0, 100.0, 120.0, 40.0, -10.0};
	 * </pre>
	 * then the index array produced by
	 * <pre>
	 *     int[] perm = new PrimitiveSortMap(a).getPermutation();
	 * </pre> contains
	 * {@code (5, 1, 4, 0, 2, 3)}. This means that
	 * <pre>
	 *     a[perm[i]] &le; a[perm[i+1]]
	 * </pre>
	 * and thus the sequence
	 * <pre>
	 *     {a[perm[0]], A[perm[1]],..., A[perm[N-1]]}
	 * </pre>
	 * is sorted.
	 *
	 * @return the sorting map (permutation)
	 */
	public int[] getPermutation() {
		return this.perm;
	}
	
	/**
	 * Returns the index of the n-th smallest element of the original number sequence
	 * (passed to the constructor),
	 * starting with {@code n = 0}, which returns the index of the smallest element.
	 * 
	 * @param n the index
	 * @return the index of the n-th smallest element in the original number sequence
	 */
	public int getIndex(int n) {
		if (n < 0 || n >= perm.length) {
			throw new IllegalArgumentException("position index n out of range: " + n);
		}
		return perm[n];
	}
	
	// --------------------------------------------------
	
	/**
	 * Returns the nth smallest element of the specified array,
	 * starting with n = 0, which returns the smallest 
	 * element of the array.
	 * 
	 * @param numbers sequence of unsorted values
	 * @param n the index
	 * @return the n-th smallest element
	 */
	public static double getNthSmallestValue(double[] numbers, int n) {
		return numbers[getNthSmallestIndex(numbers, n)];
	}
	
	/**
	 * Returns the index of the nth smallest element of the specified array,
	 * starting with n = 0, which returns the smallest element of 
	 * the array.
	 * 
	 * @param numbers sequence of unsorted values
	 * @param n the index
	 * @return the index of the n-th smallest element
	 */
	public static int getNthSmallestIndex(double[] numbers, int n) {
		return new PrimitiveSortMap(numbers).getIndex(n);
	}
	
	/**
	 * Returns the largest element of the specified array.
	 * 
	 * @param numbers sequence of unsorted values
	 * @return the largest element
	 */
	public static double getLargestValue(double[] numbers) {
		return numbers[getLargestIndex(numbers)];
	}
	
	/**
	 * Returns the index of the largest element of the specified array.
	 * 
	 * @param numbers sequence of unsorted values
	 * @return the index of the largest element
	 */
	public static int getLargestIndex(double[] numbers) {
		return getNthSmallestIndex(numbers, numbers.length - 1);
	}
	
	/**
	 * Returns the nth smallest element of the specified array,
	 * starting with n = 0, which returns the smallest element of 
	 * the array.
	 * 
	 * @param numbers sequence of unsorted values
	 * @param n the index
	 * @return the n-th smallest element
	 */
	public static float getNthSmallestValue(float[] numbers, int n) {
		return numbers[getNthSmallestIndex(numbers, n)];
	}
	
	/**
	 * Returns the index of the nth smallest element of the specified array,
	 * starting with n = 0, which returns the smallest element of 
	 * the array.
	 * 
	 * @param numbers sequence of unsorted values
	 * @param n the index
	 * @return the index of the n-th smallest element
	 */
	public static int getNthSmallestIndex(float[] numbers, int n) {
		return new PrimitiveSortMap(numbers).getIndex(n);
	}
	
	/**
	 * Returns the largest element of the specified array.
	 * 
	 * @param numbers sequence of unsorted values
	 * @return the largest element
	 */
	public static float getLargestValue(float[] numbers) {
		return numbers[getLargestIndex(numbers)];
	}
	
	/**
	 * Returns the index of the largest element of the specified array.
	 * 
	 * @param numbers sequence of unsorted values
	 * @return the index of the largest element
	 */
	public static int getLargestIndex(float[] numbers) {
		return getNthSmallestIndex(numbers, numbers.length - 1);
	}
	
	/**
	 * Returns the nth smallest element of the specified {@code int[]},
	 * starting with {@code n = 0}, which returns the smallest element of {@code numbers}.
	 * 
	 * @param numbers sequence of unsorted values
	 * @param n the index
	 * @return the n-th smallest element of {@code numbers}
	 */
	public static int getNthSmallestValue(int[] numbers, int n) {
		return numbers[getNthSmallestIndex(numbers, n)];
	}
	
	/**
	 * Returns the index of the nth smallest element of the specified array,
	 * starting with n = 0, which returns the smallest element of 
	 * the array.
	 * 
	 * @param numbers sequence of unsorted values
	 * @param n the index
	 * @return the index of the n-th smallest element
	 */
	public static int getNthSmallestIndex(int[] numbers, int n) {
		return new PrimitiveSortMap(numbers).getIndex(n);
	}
	
	/**
	 * Returns the largest element of the specified array.
	 * 
	 * @param numbers sequence of unsorted values
	 * @return the largest element
	 */
	public static float getLargestValue(int[] numbers) {
		return numbers[getLargestIndex(numbers)];
	}
	
	/**
	 * Returns the index of the largest element of the specified array.
	 * 
	 * @param numbers sequence of unsorted values
	 * @return the index of the largest element
	 */
	public static int getLargestIndex(int[] numbers) {
		return getNthSmallestIndex(numbers, numbers.length - 1);
	}
	
}
