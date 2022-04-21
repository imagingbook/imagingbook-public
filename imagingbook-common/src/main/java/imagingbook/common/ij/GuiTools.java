/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.ij;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.plugin.ScreenGrabber;

/**
 * 
 * @author WB
 * @version 2020/10/09
 *
 */
public class GuiTools {
	
	public static String DEFAULT_DIALOG_TITLE = "Choose image";
	
	/**
	 * Queries the user to select one of the currently open images.
	 *  
	 * @param title name of the dialog window (if null, the default title is used)
	 * @param excludeIm image to exclude from being selected (typically the current image)
	 * @return a reference to the chosen image (ImagePlus) or null, if the dialog was cancelled
	 */
    public static ImagePlus chooseOpenImage(String title, ImagePlus excludeIm) {
		if (title == null) {
			title = DEFAULT_DIALOG_TITLE;
		}
		int[] imgIdsAll = WindowManager.getIDList();
		if (imgIdsAll==null) {
			IJ.error("No images are open.");
			return null;
		}

		List<Integer> imgIdList   = new ArrayList<Integer>(imgIdsAll.length);	// use a Map instead?
		List<String>  imgNameList = new ArrayList<String>(imgIdsAll.length);
		
		for (int id : imgIdsAll) {
			ImagePlus img = WindowManager.getImage(id);
			if (img!=null && img != excludeIm && img.isProcessor()) {
				imgIdList.add(id);
				imgNameList.add(img.getShortTitle());
			}
		}
		
		if (imgIdList.size() < 1) {
			IJ.error("No other images found.");
			return null;
		}
		
		Integer[] imgIds   = imgIdList.toArray(new Integer[0]);
		String[]  imgNames = imgNameList.toArray(new String[0]);
		GenericDialog gd = new GenericDialog(title, null);
		gd.addChoice("Image:", imgNames, imgNames[0]);	
		gd.showDialog();
		if (gd.wasCanceled())
			return null;
		else {
			int idx = gd.getNextChoiceIndex();
			return WindowManager.getImage(imgIds[idx]);
		}
    }
    
	public static ImagePlus chooseOpenImage(String title) {
		return chooseOpenImage(title, null);
	}
	
    public static ImagePlus chooseOpenImage() {
    	return chooseOpenImage(null, null);
    }
    
    // ---------------------------------------------------------------------------------------------
    
	/**
	 * Modifies the view of the given {@link ImagePlus} image to the specified magnification
	 * (zoom) factor and the anchor position in the source image. 
	 * The size of the image window remains unchanged.
	 * The specified anchor point is the top-left corner of the source rectangle,
	 * both coordinates must be positive.
	 * The method fails (does nothing and returns {@code null}) if the resulting 
	 * source rectangle does not fit into the image.
	 * If successful, the view is modified and the resulting source rectangle is returned.
	 * Otherwise {@code null} is returned.
	 * 
	 * @param im the image, which must be currently open (displayed)
	 * @param magnification the new magnification factor (1.0 = 100%)
	 * @param xa the x-coordinate of the anchor point
	 * @param ya the y-coordinate of the anchor point
	 * @return the resulting source rectangle if successful, {@code null} otherwise 
	 */
	public static Rectangle setImageView(ImagePlus im, double magnification, int xa, int ya) {
		ImageCanvas ic = im.getCanvas();
        if (ic == null) {
        	IJ.showMessage("Image has no canvas.");
            return null;
        }
        
        Dimension d = ic.getPreferredSize();
        int dstWidth = d.width;
        int dstHeight = d.height;
        
        int imgWidth = im.getWidth();
        int imgHeight = im.getHeight();
        
        if (xa < 0 || ya < 0) {
        	throw new IllegalArgumentException("anchor coordinates may not be negative!");
        }
        
        if (magnification <= 0.001) {
            throw new IllegalArgumentException("magnification value must be positive!");
        }
        
        // calculate size of the new source rectangle
        int srcWidth  = (int) Math.ceil(dstWidth / magnification); // check!!
        int srcHeight = (int) Math.ceil(dstHeight / magnification);
        
        if (xa + srcWidth > imgWidth || ya + srcHeight > imgHeight) {
        	// source rectangle does not fully fit into source image
        	return null;
        }
        
        Rectangle srcRect = new Rectangle(xa, ya, srcWidth, srcHeight);
        ic.setSourceRect(srcRect);
        im.repaintWindow();
        
		return srcRect;
	}
	
	// ---------------------------------------------------------------
	
	private static final int DEFAULT_SCREEN_MARGIN_X = 10; // horizontal screen margin
	private static final int DEFAULT_SCREEN_MARGIN_Y = 30; // vertical screen margin
	
	/**
	 * Resizes the window of the given image to fit an arbitrary, user-specified
	 * magnification factor.
	 * The resulting window size is limited by the current screen size.
	 * The window size is reduced if too large but the given magnification factor
	 * remains always unchanged.
	 * <br>
	 * Adapted from https://albert.rierol.net/plugins/Zoom_Exact.java
	 * Copyright Albert Cardona @ 2006
	 * General Public License applies.
	 * <br>
	 * TODO: Check calculation of source rectangle, final magnification may not always be exact!
	 * 
	 * @param im the image, which must be currently open (displayed)
	 * @param magnification the new magnification factor (1.0 = 100%)
	 * @param marginX horizontal screen margin
	 * @param marginY vertical screen margin
	 * @return true if successful, false otherwise
	 */
	public static boolean zoomExact(ImagePlus im, double magnification, int marginX, int marginY) {
		ImageWindow win = im.getWindow();
		if (null == win)
			return false;
		ImageCanvas ic = win.getCanvas();
		if (null == ic)
			return false;
		
		if (magnification <= 0.001) {
			return false;
		}

		// fit to screen
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int maxWidth  = screen.width - marginX;
		int maxHeight = screen.height - marginY;
		double w = Math.min(magnification * im.getWidth(), maxWidth);
		double h = Math.min(magnification * im.getHeight(), maxHeight);
				
		Rectangle sourceRect = new Rectangle(0, 0, (int) (w / magnification), (int) (h / magnification));
		try {
			// by reflection:
			Field f_srcRect = ic.getClass().getDeclaredField("srcRect");
			f_srcRect.setAccessible(true);
			f_srcRect.set(ic, sourceRect);
			ic.setSize((int) w, (int) h);
			ic.setMagnification(magnification);
			win.pack();
			im.repaintWindow();
			return true;
		}
		catch (Exception e) { e.printStackTrace(); }
		return false;
	}
	
	/**
	 * Convenience method for {@link #zoomExact(ImagePlus, double, int, int)}
	 * using default screen margins.
	 * 
	 * @param im the image, which must be currently open (displayed)
	 * @param magnification the new magnification factor (1.0 = 100%)
	 * @return true if successful, false otherwise
	 */
	public static boolean zoomExact(ImagePlus im, double magnification) {
		return zoomExact(im, magnification, DEFAULT_SCREEN_MARGIN_X, DEFAULT_SCREEN_MARGIN_Y);
	}
	
	// -----------------------------------------------------------------------
	
	/**
	 * Retrieves the current magnification (zoom) factor for the given
	 * image window.
	 * 
	 * @param im the image, which must be currently open (displayed)
	 * @return the magnification factor
	 */
	public static double getMagnification(ImagePlus im) {
		return im.getWindow().getCanvas().getMagnification();
	}

	// -----------------------------------------------------------------------
	
	/**
	 * Captures the specified image window and returns it as a new {@link ImagePlus}
	 * instance. Uses ImageJ's built-in {@link ScreenGrabber} plugin.
	 * 
	 * @param im the image, which must be currently open (displayed)
	 * @return a new image with the grabbed contents
	 */
	public static ImagePlus captureImage(ImagePlus im) {
		return new ScreenGrabber().captureImage();
	}
	
}
