/*
 *  This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge.
 * All rights reserved. Visit https://imagingbook.com for additional details.
 */
package imagingbook.common.util;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

/** 
 * Clipboard-related methods from
 * http://examples.javacodegeeks.com/desktop-java/awt/datatransfer/getting-and-setting-text-on-the-system-clipboard/
 * 
 * @author WB
 * @version 2022/11/20
 */

public abstract class Clipboard {
	
	private Clipboard() {}
	
	/**
	 * Writes a string to the system clipboard.
	 * @param str some string
	 */
	public static void copyStringToClipboard(String str) {
		StringSelection stringSelection = new StringSelection(str);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);  
	} 

	/**
	 * If a string is on the system clipboard, this method returns it; otherwise it returns null.
	 * @return the clipboard string or null
	 */
	public static String getStringFromClipboard() { 
		String str = null;
		Transferable trf = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null); 
		if (trf != null && trf.isDataFlavorSupported(DataFlavor.stringFlavor)) {
			try {
				str = (String) trf.getTransferData(DataFlavor.stringFlavor);
			}  catch (Exception e) {};
		}
		return str; 
	}

}
