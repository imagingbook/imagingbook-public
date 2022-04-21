package imagingbook.common.util;

public abstract class ArrayUtils {
	
	/**
	 * Counts the number of non-null elements in the given (non-primitive) array.
	 * @param arr an array of non-primitive type
	 * @return the number of non-null elements
	 */
	public static int countElements(Object[] arr) {
		int cnt = 0;
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] != null) {
				cnt++;
			}
		}
		return cnt;
	}

}
