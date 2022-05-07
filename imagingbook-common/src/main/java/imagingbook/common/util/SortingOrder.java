package imagingbook.common.util;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * Determines the 'permutation' of a sequence of numbers and
 * keeps it as an array ({@link #perm}) of position indexes.
 * These indexes indicate how the original number sequence
 * may be re-ordered to become sorted
 * (see {@link #getPermutation()}).
 * 
 * @author WB
 *
 */
public class SortingOrder {
	
	private final int[] perm;
	
	public SortingOrder(double[] numbers) {
		this.perm = IntStream.range(0, numbers.length).boxed()
		        .sorted((i, j) -> Double.compare(numbers[i], numbers[j]))
		        .mapToInt(x->x.intValue()).toArray();
	}
	
	public SortingOrder(float[] numbers) {
		this.perm = IntStream.range(0, numbers.length).boxed()
		        .sorted((i, j) -> Float.compare(numbers[i], numbers[j]))
		        .mapToInt(x->x.intValue()).toArray();
	}
	
	public SortingOrder(int[] numbers) {
		this.perm = IntStream.range(0, numbers.length).boxed()
		        .sorted((i, j) -> Integer.compare(numbers[i], numbers[j]))
		        .mapToInt(x->x.intValue()).toArray();
	}
	
	// --------------------------------------------------
	
	/**
	 * Returns the permutation (sorted position indexes) for the underlying
	 * number sequence ({@code numbers}) as a {@code int} array.
	 * That is, the first value in the returned array is the index
	 * of the smallest element in {@code numbers}, the second element
	 * points to the second-smallest element, etc.
	 * For example, if the original number sequence (passed to the constructor) is
	 * <pre>numbers = (50.0, 20.0, 100.0, 120.0, 40.0, -10.0)</pre>
	 * then the permutation array returned by {@link #getPermutation()} is
	 * <pre>perm = (5, 1, 4, 0, 2, 3)</pre>
	 * This means that the sequence
	 * <pre>(numbers[perm[0]], numbers[perm[1]],..., numbers[perm[N-1]])</pre>
	 * is sorted, with 
	 * <pre>numbers[perm[i]] &leq; numbers[perm[i+1]]</pre>
	 * 
	 * @return the sorting order (permutation)
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
	 * @return the index of the n-th smallest element
	 */
	public int getIndex(int n) {
		if (n < 0 || n >= perm.length) {
			throw new IllegalArgumentException("position index n out of range: " + n);
		}
		return perm[n];
	}
	
	// --------------------------------------------------

	
	/**
	 * Returns the nth smallest element of the specified sequence of {@code double} values,
	 * starting with n = 0, which returns the smallest element.
	 * 
	 * @param numbers sequence of unsorted values
	 * @param n the index
	 * @return the n-th smallest element of {@code numbers}
	 */
	public static double getNthSmallest(double[] numbers, int n) {
		int idx = new SortingOrder(numbers).getIndex(n);
		return numbers[idx];
	}
	
	/**
	 * Returns the nth smallest element of the specified sequence of {@code float} values,
	 * starting with n = 0, which returns the smallest element.
	 * 
	 * @param numbers sequence of unsorted values
	 * @param n the index
	 * @return the n-th smallest element of {@code numbers}
	 */
	public static float getNthSmallest(float[] numbers, int n) {
		int idx = new SortingOrder(numbers).getIndex(n);
		return numbers[idx];
	}
	
	/**
	 * Returns the nth smallest element of the specified sequence of {@code int} values,
	 * starting with n = 0, which returns the smallest element.
	 * 
	 * @param numbers sequence of unsorted values
	 * @param n the index
	 * @return the n-th smallest element of {@code numbers}
	 */
	public static int getNthSmallest(int[] numbers, int n) {
		int idx = new SortingOrder(numbers).getIndex(n);
		return numbers[idx];
	}
	
	// ------------------------------------------------------------
	
	private static double[] makeRandomArr(int n) {
		double[] vals = new double[n];
		Random rg = new Random();
		for (int i = 0; i < n; i++) {
			vals[i] = rg.nextInt(1000);
		}
		return vals;
	}
	
	private static boolean isSorted(double[] arr) {
	    for (int i = 1; i < arr.length; i++) {
	        if (arr[i - 1] > arr[i]) {
	            return false;
	        };
	    }
	    return true;
	}
	
	// ------------------------------------------------------------
	
	public static void main(String[] args) {
//		double[] numbersToBeSorted = { 50, 20, 100, 120, 40, -10 };
		double[] numbersToBeSorted = makeRandomArr(100);
		System.out.println("numbersToBeSorted = " + Arrays.toString(numbersToBeSorted));
		System.out.println("is sorted = " + isSorted(numbersToBeSorted));
		int[] perm = new SortingOrder(numbersToBeSorted).getPermutation();
		System.out.println("permutation = " + Arrays.toString(perm));
		
		double[] numbersSorted = new double[numbersToBeSorted.length];
		for (int i = 0; i < numbersToBeSorted.length; i++) {
//			System.out.println(numbersToBeSorted[i]);
			numbersSorted[i] = numbersToBeSorted[perm[i]];
		}
		System.out.println("numbersSorted = " + Arrays.toString(numbersSorted));
		System.out.println("is sorted = " + isSorted(numbersSorted));
		
		System.out.println("element idx 3 = " + getNthSmallest(numbersToBeSorted, 3));
	}

}
