/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.math;

/**
 * This class holds various settings for the imagingbook library.
 * @author wilbur
 *
 */
public abstract class PrintPrecision {
	
	private static int defaultPrecision = 3;
	private static int precision = defaultPrecision;
	private static String formatStringFloat;
	
	static {
		 reset();
	}
	
	public static void reset() {
		set(defaultPrecision);
	}
	
	public static void set(int nDigits) {
		precision = Math.max(nDigits, 0);
		if (nDigits > 0) {
			formatStringFloat = "%." + precision + "f"; // e.g. "%.5f"
		}
		else {
			formatStringFloat = "%e";	// use scientific format - OK?
		}
	}
	
	public static String getFormatStringFloat() {
		return formatStringFloat;
	}
	
	public static int get() {
		return precision;
	}

}
