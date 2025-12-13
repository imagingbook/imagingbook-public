/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;

/**
 * @author WB
 * @version 2022/11/20
 *
 */
public final class ArrayUtils {
	
	private ArrayUtils() {}
	
	/**
	 * Counts the number of non-null elements in the given (non-primitive) array.
	 * @param arr an array of non-primitive type
	 * @return the number of non-null elements
	 */
	public static int countNonNullElements(Object[] arr) {
		int cnt = 0;
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] != null) {
				cnt++;
			}
		}
		return cnt;
	}

	/**
	 * Returns an iterator for the specified (non-primitive) array. The resulting iterator does not implement
	 * {@link Iterator#remove()}.
	 *
	 * @param <T> the generic element type
	 * @param array a non-primitive array
	 * @return the associated iterator
	 */
	public static <T> Iterator<T> getIterator(T[] array) {
		return Arrays.stream(array).iterator();
	}

	// Primitive array creation: -----------------------------------------------
	// TODO: add tests!

	/**
	 * Substitute for {@code java.util.function.FloatSupplier} missing from
	 * standard JDK.
	 */
	@FunctionalInterface
	public interface FloatSupplier {
		float get();
	}

	/**
	 * Utility method to create a {@code float} array with the specified length,
	 * using a lambda expression to initialize each element. Usage example:
	 * <pre>
	 * float[] floats = newFloatArray(5, () -> (float) Math.random());
	 * </pre>
	 * @param length the length of the new array
	 * @param supplier a supplier (lambda) expression
	 * @return a new {@code float} array
	 */
	public static float[] newFloatArray(int length, FloatSupplier supplier) {
		float[] result = new float[length];
		for (int i = 0; i < length; i++) {
			result[i] = supplier.get();
		}
		return result;
	}

	/**
	 * Utility method to create a {@code int} array with the specified length,
	 * using a lambda expression to initialize each element. Usage example:
	 * <pre>
	 * Random rg = new Random();
	 * int[] integers = newIntArray(7,() ->  rg.nextInt(100) - 50);
	 * </pre>
	 * @param length the length of the new array
	 * @param supplier a supplier (lambda) expression
	 * @return a new {@code int} array
	 */
	public static int[] newIntArray(int length, IntSupplier supplier) {
		int[] result = new int[length];
		for (int i = 0; i < length; i++) {
			result[i] = supplier.getAsInt();
		}
		return result;
	}

	/**
	 * Utility method to create a {@code double} array with the specified length,
	 * using a lambda expression to initialize each element. Usage example:
	 * <pre>
	 * double[] doubles = newDoubleArray(3, Math::random);
	 * </pre>
	 * @param length the length of the new array
	 * @param supplier a supplier (lambda) expression
	 * @return a new {@code double} array
	 */
	public static double[] newDoubleArray(int length, DoubleSupplier supplier) {
		double[] result = new double[length];
		for (int i = 0; i < length; i++) {
			result[i] = supplier.getAsDouble();
		}
		return result;
	}

	// ----------------------------------------------

	public static void main(String[] args) {
		float[] floats = newFloatArray(5, () -> (float) Math.random());
		System.out.println(Arrays.toString(floats));

		double[] doubles = newDoubleArray(3, Math::random);
		System.out.println(Arrays.toString(doubles));

		Random rg = new Random();
		int[] integers = newIntArray(7,() ->  rg.nextInt(100) - 50);
		System.out.println(Arrays.toString(integers));

	}

}
