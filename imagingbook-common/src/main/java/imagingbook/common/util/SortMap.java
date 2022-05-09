package imagingbook.common.util;

import java.util.stream.IntStream;

/**
 * Determines the 'permutation' of a sequence of numbers and
 * keeps it as an array ({@link #perm}) of position indexes.
 * These indexes indicate how the original input array
 * may be re-ordered to become sorted
 * (see {@link #getPermutation()}).
 * 
 * @author WB
 *
 */
public class SortMap {
	
	private final int[] perm;
	
	/**
	 * Constructor for {@code double[]}. The input array is not modified.
	 * @param numbers the input array
	 */
	public SortMap(double[] numbers) {
		this.perm = IntStream.range(0, numbers.length).boxed()
		        .sorted((i, j) -> Double.compare(numbers[i], numbers[j]))
		        .mapToInt(x->x.intValue()).toArray();
	}
	
	/**
	 * Constructor for {@code float[]}. The input array is not modified.
	 * @param numbers the input array
	 */
	public SortMap(float[] numbers) {
		this.perm = IntStream.range(0, numbers.length).boxed()
		        .sorted((i, j) -> Float.compare(numbers[i], numbers[j]))
		        .mapToInt(x->x.intValue()).toArray();
	}
	
	/**
	 * Constructor for {@code int[]}. The input array is not modified.
	 * @param numbers the input array
	 */
	public SortMap(int[] numbers) {
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
	 * This means that 
	 * <pre>numbers[perm[i]] &le; numbers[perm[i+1]]</pre>
	 * and thus the sequence
	 * <pre>(numbers[perm[0]], numbers[perm[1]],..., numbers[perm[N-1]])</pre>
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
	 * Returns the nth smallest element of the specified  {@code double[]},
	 * starting with {@code n = 0}, which returns the smallest element of {@code numbers}.
	 * 
	 * @param numbers sequence of unsorted values
	 * @param n the index
	 * @return the n-th smallest element of {@code numbers}
	 */
	public static double getNthSmallest(double[] numbers, int n) {
		int idx = new SortMap(numbers).getIndex(n);
		return numbers[idx];
	}
	
	/**
	 * Returns the nth smallest element of the specified {@code float[]},
	 * starting with {@code n = 0}, which returns the smallest element of {@code numbers}.
	 * 
	 * @param numbers sequence of unsorted values
	 * @param n the index
	 * @return the n-th smallest element of {@code numbers}
	 */
	public static float getNthSmallest(float[] numbers, int n) {
		int idx = new SortMap(numbers).getIndex(n);
		return numbers[idx];
	}
	
	/**
	 * Returns the nth smallest element of the specified {@code int[]},
	 * starting with {@code n = 0}, which returns the smallest element of {@code numbers}.
	 * 
	 * @param numbers sequence of unsorted values
	 * @param n the index
	 * @return the n-th smallest element of {@code numbers}
	 */
	public static int getNthSmallest(int[] numbers, int n) {
		int idx = new SortMap(numbers).getIndex(n);
		return numbers[idx];
	}
	

}
