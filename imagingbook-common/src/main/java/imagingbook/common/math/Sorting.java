package imagingbook.common.math;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

public abstract class Sorting {
	
	/**
	 * Returns the sorting order for the specified sequence of values
	 * as a {@code int} array.
	 * That is, the first value in the returned array is the index
	 * of the smallest element in {@code numbers}, the second element
	 * points to the second-smallest element, etc.
	 * For example,
	 * <pre>numbers = [50.0, 20.0, 100.0, 120.0, 40.0, -10.0]</pre>
	 * gives the result
	 * <pre>[5, 1, 4, 0, 2, 3]</pre>
	 * </pre>
	 * 
	 * @param numbers sequence of unsorted values
	 * @return the sorting order (permutation)
	 */
	public static int[] getSortingOrder(double[] numbers) {
		int[] permutation = IntStream.range(0, numbers.length).boxed()
		        .sorted((i, j) -> Double.compare(numbers[i], numbers[j]))
		        .mapToInt(x->x.intValue()).toArray();
		return permutation;
	}
	
	/**
	 * Returns the nth smallest element of the specified sequence of values,
	 * starting with n=0, which returns the smallest element.
	 * 
	 * @param numbers sequence of unsorted values
	 * @param n the index
	 * @return the n-th smallest element
	 */
	public static double getNthSmallestElement(double[] numbers, int n) {
		if (n < 0 || n >= numbers.length) {
			throw new IllegalArgumentException("index n out of range: " + n);
		}
		int[] permutation = getSortingOrder(numbers);
		return numbers[permutation[n]];
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
	
	public static void main(String[] args) {
//		double[] numbersToBeSorted = { 50, 20, 100, 120, 40, -10 };
		double[] numbersToBeSorted = makeRandomArr(100);
		System.out.println("numbersToBeSorted = " + Arrays.toString(numbersToBeSorted));
		System.out.println("is sorted = " + isSorted(numbersToBeSorted));
		int[] perm = getSortingOrder(numbersToBeSorted);
		System.out.println("permutation = " + Arrays.toString(perm));
		
		double[] numbersSorted = new double[numbersToBeSorted.length];
		for (int i = 0; i < numbersToBeSorted.length; i++) {
//			System.out.println(numbersToBeSorted[i]);
			numbersSorted[i] = numbersToBeSorted[perm[i]];
		}
		System.out.println("numbersSorted = " + Arrays.toString(numbersSorted));
		System.out.println("is sorted = " + isSorted(numbersSorted));
		
		System.out.println("element idx 3 = " + getNthSmallestElement(numbersToBeSorted, 3));
	}

}
