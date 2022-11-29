/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package Tools_;

import java.awt.Color;
import java.awt.Font;

import ij.IJ;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import imagingbook.bookimages.BookImageCh01;
import imagingbook.bookimages.BookImageCh02;
import imagingbook.core.resource.ImageResource;

/**
 * Open one of the listed book images
 * (still experimental!).
 * 
 * @author WB
 */
public class Show_Book_Images implements PlugIn {
	
	enum BookChapter {
		Chapter_1,
		Chapter_2,
		;
	}
	
	BookChapter selectedChapter = null;
	ImageResource selectedImage = null;

	@Override
	public void run(String arg0) {
		
		if (!selectChapter()) {
			return;
		}
		
		IJ.log(selectedChapter.toString());
		
		// could not find a more elegant way for dispatching
		// (what generic variable type can be used to hold BookImageCh01.class, etc.?)
		switch(selectedChapter) {
		case Chapter_1: selectImage(BookImageCh01.class); break;
		case Chapter_2: selectImage(BookImageCh02.class); break;
		}
		
		if (selectedImage == null) {
			return;
		}
		
		IJ.log(selectedImage.toString());
		IJ.log("inside JAR: " + selectedImage.isInsideJar());
		
		selectedImage.getImage().show();
	}
	
	
	boolean selectChapter() {
		GenericDialog gd = new GenericDialog("Select book chapter");
		
//		gd.addHelp("<html>This is a help text.</html>");
//		gd.addHelp(DialogUtils.makeHtmlString(
//				"This is a help text",
//				"consisting of multiple lines",
//				"which are concatenated."
//				));
		gd.addHelp("https://imagingbook.com");
		
		Font font = new Font("SansSerif", Font.BOLD, 12);
		
		gd.addMessage("A message for the user that is longer\nwith multiple lines.", font, Color.blue);
//		gd.addMessage(DialogUtils.makeLineSeparatedString(
//				"A message for the user",
//				"with multiple lines."));
				
		gd.addEnumChoice("Select chapter", BookChapter.values()[0]);
		
		gd.showDialog();
		if (gd.wasCanceled()) {
			return false;
		}
		
		selectedChapter = gd.getNextEnumChoice(BookChapter.class);
		return true;
	}
	
	// ---------------------------------------------------

	<E extends Enum<E>> boolean selectImage(Class<E> enumClass) {
		GenericDialog gd = new GenericDialog("Select book images");
		
		gd.addEnumChoice("Select image", enumClass.getEnumConstants()[0]);
		
		gd.showDialog();
		if (gd.wasCanceled()) {
			selectedImage = null;
			return false;
		}
		
		selectedImage = (ImageResource) gd.getNextEnumChoice(enumClass);	
		return true;
	}

}
