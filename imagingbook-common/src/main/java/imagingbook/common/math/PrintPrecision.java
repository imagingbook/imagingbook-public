/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.math;

/**
 * This class holds settings to control the precision when printing
 * floating-point numbers, used in particular by various {@code toString()}
 * methods for vectors and matrices defined in class {@link Matrix} (mainly for
 * debugging).
 * 
 * @author WB
 *
 */
public abstract class PrintPrecision {
	
	private PrintPrecision() {}
	
	/** The default precision (number of digits = 3). */
	public static final int DefaultPrecision = 3;
	
	private static int precision = DefaultPrecision;
	private static String formatString;
	
	static {
		reset();	// to properly set the formatString
	}
	
	/**
	 * Reset print precision to the default value
	 * ({@link #DefaultPrecision}).
	 */
	public static void reset() {
		set(DefaultPrecision);
	}
	
	/**
	 * Set print precision to the specified number of digits.
	 * For example, with {@code nDigits = 5} the resulting element format string
	 * is {@code "%.5f"}.
	 * If a value &le;0 is specified, the scientific float format 
	 * string {@code "%e"} is used.
	 * 
	 * @param nDigits the number of digits to be used
	 */
	public static void set(int nDigits) {
		precision = Math.max(nDigits, 0);
		if (nDigits > 0) {
			formatString = "%." + precision + "f"; // e.g. "%.5f"
		}
		else {
			formatString = "%e";	// use scientific format - OK?
		}
	}
	
	/**
	 * Returns the current print precision (number of digits).
	 * @return the current number of digits
	 */
	public static int get() {
		return precision;
	}

	/**
	 * Returns the floaing-point format string for the current print precision (to
	 * be used in {@link String#format(String, Object...)}, for example
	 * {@code "%.6f"} if print precision is set to 6, or {@code "%e"} when
	 * precision &le;0.
	 * 
	 * @return the format string
	 */
	public static String getFormatStringFloat() {
		return formatString;
	}
}
