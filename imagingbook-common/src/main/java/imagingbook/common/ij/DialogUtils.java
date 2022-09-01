/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.ij;

import ij.gui.GenericDialog;

public class DialogUtils {
	
	/**
	 * Creates a HTML string by formatting the supplied strings
	 * as individual text lines separated by {@literal <br>}.
	 * The complete text is wrapped by {@literal <html>...</html>}.
	 * Mainly to be used with {@link GenericDialog#addHelp(String)}.
	 * 
	 * @param lines a sequence of strings interpreted as text lines
	 * @return a HTML string
	 */
	public static String makeHtmlString(CharSequence... lines) {
		return "<html>\n" + String.join("<br>\n", lines) + "\n</html>";
	}
	
	
	/**
	 * Creates a string by formatting the supplied strings
	 * as individual text lines separated by newline.
	 * Mainly to be used with {@link GenericDialog#addMessage(String)}.
	 * 
	 * @param lines a sequence of strings interpreted as text lines
	 * @return a newline-separated string
	 */
	public static String makeLineSeparatedString(CharSequence... lines) {
		return String.join("\n", lines);
	}
	
	// ----------------------------------------------------
	
	public static void main(String[] args) {
		String html = makeHtmlString(
	            "Get busy living or",
	            "get busy dying.",
	            "--Stephen King");
		System.out.println(html);
		
		System.out.println();
		
		String lines = makeLineSeparatedString(
	            "Get busy living or",
	            "get busy dying.",
	            "--Stephen King");
		System.out.println(lines);
	}

}
