/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.math;

import java.io.Closeable;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Locale;

/**
 * This class holds settings to control the precision when printing floating-point numbers, used in particular by
 * various {@code toString()} methods for vectors and matrices defined in class {@link Matrix}.
 * Instances of this class are immutable. A static stack of {@link PrintPrecision} instances is
 * maintained, whose top element defines the current precision. Recommended use is within a
 * local try-with-resources block, for example:
 * <pre>
 *      try (var prec = new PrintPrecision(6, Locale.GERMAN)) {
 *            // perform some floating-point output here
 *      }</pre>
 * Precision is automatically reset to the previous setting at the end of the try-block.
 * Multiple try blocks may be nested.
 * Note that this simple mechanism is mostly intended for debugging and not thread-safe.
 * @author WB
 */
public final class PrintPrecision implements Closeable {

	/**
	 * The default precision (number of digits = 3).
	 */
	public static final int DefaultPrecision = 3;
	public static final Locale DefaultLocale = Locale.US;
	private static final Deque<PrintPrecision> precisionStack;

	private final int precision;
	private final Locale locale;
	private final String formatString;
	private boolean closed = false;

	static {
		precisionStack = new ArrayDeque<>();
		clearAll();
	}

	/**
	 * Sets the current print precision and locale to default values, without clearing the
	 * precision stack.
	 */
	public PrintPrecision() {
		this(DefaultPrecision, DefaultLocale);
	}

	/**
	 * Sets the current print precision to the specified number of digits. For example, with
	 * {@code nDigits = 5} the resulting element format string is {@code "%.5f"}.
	 * If a value &le;0 is specified, the scientific float format string {@code "%e"} is used.
	 * @param nDigits the number of digits to be used
	 * @param locale the {@link Locale} to be used (for commas)
	 */
	public PrintPrecision(int nDigits, Locale locale) {
		this.precision = Math.max(nDigits, 0);
		this.locale = locale;
		this.formatString = makeFormatString();
		precisionStack.push(this);
	}

	/**
	 * Sets the current print precision to the specified number of digits. For example, with
	 * {@code nDigits = 5} the resulting element format string is {@code "%.5f"}.
	 * If a value &le;0 is specified, the scientific float format string {@code "%e"} is used.
	 * @param nDigits the number of digits to be used
	 */
	public PrintPrecision(int nDigits) {
		this(nDigits, DefaultLocale);
	}

	private String makeFormatString() {
		return (precision > 0) ?
				"%." + precision + "f" :    // e.g. "%.5f"
				"%e";                        // use scientific format - OK?
	}

	// ---------------------------------------------------------------------------------------------

	public int getPrecision() {
		checkClosedState();
		return precision;
	}

	public Locale getLocale() {
		checkClosedState();
		return locale;
	}

	public String getFormatString() {
		checkClosedState();
		return formatString;
	}

	// ---------------------------------------------------------------------------------------------

	/**
	 * Returns the current {@link PrintPrecision} instance.
	 * @return the current print precision
	 */
	public static PrintPrecision current() {
		checkPrecisionStack();
		return precisionStack.peek();
	}

	/**
	 * Resets everything to default settings.
	 */
	public static void clearAll() {
		precisionStack.clear();
		precisionStack.push(new PrintPrecision(DefaultPrecision));
	}

	/**
	 * Set print precision to the specified number of digits. For example, with {@code nDigits = 5} the resulting
	 * element format string is {@code "%.5f"}. If a value &le;0 is specified, the scientific float format string
	 * {@code "%e"} is used.
	 * @deprecated use try-with-resources instead!
	 * @param nDigits the number of digits to be used
	 */
	@Deprecated
	public static PrintPrecision set(int nDigits) {
		return new PrintPrecision(nDigits);
	}

	// /**
	//  * Reset print precision to the previous value.
	//  * @deprecated use try-with-resources instead!
	//  */
	// @Deprecated
	// public static void reset() {
	// 	checkPrecisionStack();
	// 	precisionStack.pop();
	// }

	// /**
	//  * Returns the floating-point format string for the current print precision (to be used in
	//  * {@link String#format(String, Object...)}, for example {@code "%.6f"} if print precision is set to 6, or
	//  * {@code "%e"} when precision &le;0.
	//  * @deprecated use {@link PrintPrecision#current()} + {@link PrintPrecision#getFormatString()} instead.
	//  * @return the format string
	//  */
	// @Deprecated
	// public static String getFormatStringFloat() {
	// 	checkPrecisionStack();
	// 	return precisionStack.peek().formatString;
	// }
	// ---------------------------------------------------------------------------------------------

	private void checkClosedState() {
		if (closed) {
			throw new IllegalStateException("resource is closed and cannot be used anymore");
		}
	}

	private static void checkPrecisionStack() {
		if (precisionStack.isEmpty()) {
			throw new IllegalStateException("empty PrintPrecision stack - should never happen");
		}
	}

	@Override
	public void close() {
		// Removes this instance from the top of the PrintPrecision stack, if still present.
		// close() should not be called explicitly, only by try-with-resources block
		// pop off all items until either stack is empty or this is on top
		// while (!precisionStack.isEmpty() && precisionStack.peek() != this) {
		// 	precisionStack.pop();
		// }
		if (precisionStack.isEmpty()) {
			throw new IllegalStateException("empty PrintPrecision stack - should never happen");
		}
		if (closed) {    // already closed, not on stack any more
			return;
		}
		if (precisionStack.peek() != this) {
			throw new IllegalStateException("corrupted PrintPrecision stack");
		}
		precisionStack.pop();    // now remove self
		this.closed = true;
	}
}
