/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package Tools_;

import ij.IJ;
import ij.WindowManager;
import ij.plugin.PlugIn;
import ij.text.TextWindow;

import java.awt.Font;
import java.awt.Window;

/**
 * Sets the font of the IJ.log window to MONOSPACE.
 * @author WB
 */
public class Set_Log_Font_Monospace implements PlugIn {
	private static String FontType = Font.MONOSPACED;
	private static int FontStyle = Font.PLAIN;
	private static int FontSize = 16;
	private static boolean Antialiased = true;

	@Override
	public void run(String arg0) {
		Window win = WindowManager.getWindow("Log");
		if (win == null) {
			IJ.log("");            // set up the log window if not available yet
		}
		win = WindowManager.getWindow("Log");
		TextWindow tw = (TextWindow) win;
		tw.getTextPanel().setFont(new Font(FontType, FontStyle, FontSize), Antialiased);
		// IJ.log("This should now be monospaced!");
	}
}
