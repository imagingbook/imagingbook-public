/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.util;

/**
 * @author WB
 * @version 2022/11/20
 *
 */
public abstract class ArrayUtils {
	
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

}
