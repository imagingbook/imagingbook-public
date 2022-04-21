package imagingbook.common.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Classes which require a complex {@code toString()} method should
 * implement this interface, which requires the single method 
 * {@link #printToStream(PrintStream)}, e.g.,
 * <pre>
 * public void printToStream(PrintStream strm) {
 *     strm.format("...", ...);
 * }</pre>
 * Note that this interface cannot override the default {@link Object#toString()} 
 * method directly. If needed, this should be done in the implementing class by
 * using the pre-defined {@link #printToString()} method:
 * <pre>
 * public String toString() {
 *    return printToString();
 * }</pre>
 * @author WB
 * @version 2021/09/17
 */
public interface PrintsToStream {
	
	/**
	 * To be implemented by concrete implementations.
	 * This method is supposed to write a description of this object
	 * to the given {@link PrintStream}.
	 * @param strm the output stream to print to
	 */
	public void printToStream(PrintStream strm);
	
	/**
	 * Utility method to save some boilerplate code.
	 * Returns the description of this object produced
	 * by {@link #printToStream(PrintStream)}.
	 * @return the string representation of this object
	 */
	public default String printToString() {
		ByteArrayOutputStream bas = new ByteArrayOutputStream();
		try (PrintStream strm = new PrintStream(bas)) {
			printToStream(strm);
		}
		return bas.toString();
	}

}
