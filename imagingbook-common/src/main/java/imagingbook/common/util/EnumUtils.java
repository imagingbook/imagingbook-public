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
 * This class defines static methods related to enum types.
 * @author WB
 *
 */
public abstract class EnumUtils {

	private EnumUtils() {}
	
	/**
	 * This static method returns an array of all constant names (strings) 
	 * for a given enumeration class. 
	 * Names are arranged in the same order as the enum constant definitions.
	 * Assume the enum definition: 
	 * <pre>enum MyEnum {A, B, C};</pre>
	 * Usage: 
	 * <pre>String[] names = getEnumNames(MyEnum.class);</pre>
	 * This returns the array 
	 * <pre>{"A", "B", "C"}</pre>
	 * 
	 * @param <E> the generic enum type
	 * @param enumclass enumeration class
	 * @return array (possibly empty) of names defined for the specified enumeration class
	 */
	public static <E extends Enum<E>> String[] getEnumNames(Class<E> enumclass) {
		E[] eConstants = enumclass.getEnumConstants();
		String[] eNames = new String[eConstants.length];
		for (int i = 0; i < eConstants.length; i++) {
			eNames[i] = eConstants[i].name();
		}
		return eNames;
	}
	
}
