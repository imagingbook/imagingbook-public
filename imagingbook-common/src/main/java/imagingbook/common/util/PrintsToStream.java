/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * <p>
 * Classes which require a complex {@code toString()} method should implement
 * this interface, which requires the single method
 * {@link #printToStream(PrintStream)}, e.g.,
 * </p>
 * 
 * <pre>
 * public void printToStream(PrintStream strm) {
 *     strm.format("...", ...);
 * }
 * </pre>
 * <p>
 * Note that this interface cannot override the default
 * {@link Object#toString()} method directly. If needed, this should be done in
 * the implementing class by using the pre-defined {@link #printToString()}
 * method:
 * </p>
 * 
 * <pre>
 * public String toString() {
 * 	return printToString();
 * }
 * </pre>
 * 
 * @author WB
 * @version 2021/09/17
 */
public interface PrintsToStream {
	
	/**
	 * This method writes a description of this object to the specified
	 * {@link PrintStream}.
	 * 
	 * @param strm the output stream to print to
	 */
	public void printToStream(PrintStream strm);
	
	/**
	 * Convenience method to save some boilerplate code. Calls
	 * {@link #printToStream(PrintStream)} to produce a description string for this
	 * object. The {@link #printToString()} method can also be used by implementing
	 * classes to override the standard {@link Object#toString()} method, e.g.,
	 * <pre>
	 * &commat;Override
	 * public String toString() {
	 * 	return this.printToString();
	 * }
	 * </pre>
	 * 
	 * @return the string representation of this object as defined by {@link #printToStream(PrintStream)}
	 */
	public default String printToString() {
		ByteArrayOutputStream bas = new ByteArrayOutputStream();
		try (PrintStream strm = new PrintStream(bas)) {
			this.printToStream(strm);
		}
		return bas.toString();
	}

}
