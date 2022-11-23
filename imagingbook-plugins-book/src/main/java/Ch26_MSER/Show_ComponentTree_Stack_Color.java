/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package Ch26_MSER;

import java.awt.Color;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.color.sets.CssColor;
import imagingbook.common.mser.MserData;
import imagingbook.common.mser.components.Component;
import imagingbook.common.mser.components.ComponentTree;
import imagingbook.common.mser.components.ComponentTree.Method;
import imagingbook.common.mser.components.PixelMap.Pixel;

/**
 * Creates the component tree of the current image (choose from 2 different
 * methods) and reconstructs the associated threshold stack. This must be the
 * same as the one produced by plugin {@link Show_Threshold_Stack}.
 * 
 * @author WB
 * @version 2022/11/23
 * @see Show_Threshold_Stack
 */
public class Show_ComponentTree_Stack_Color implements PlugInFilter {
	
	private static Method method = Method.LinearTime;
	
	private static Color[] COLORS = CssColor.SelectColors;// MserColor.LevelColors;

	@Override
	public int setup(String arg0, ImagePlus im) {
		return DOES_8G + NO_CHANGES;
	}
	
	@Override
	public void run(ImageProcessor ip) {
		if (!runDialog()) {
			return;
		}
		
		ComponentTree<MserData> compTree = ComponentTree.from((ByteProcessor) ip, method);
		
		ImageStack compStack = makeComponentStack(compTree, ip.getWidth(), ip.getHeight());
		ImagePlus stackIm = new ImagePlus("Component Tree Stack - " + method.name(), compStack);
		stackIm.show();
//		GuiTools.zoomExact(stackIm, 16);
	}
	
	// -------------------------------------------------------------------
	
	private ImageStack makeComponentStack(ComponentTree<?> rt, int width, int height) {
		//IJ.log("makeComponentStack...");
		ImageStack stack = new ImageStack(width, height);
		for (int level = 0; level < 256; level++) {
			ColorProcessor bp = new ColorProcessor(width, height);
			fillComponentImage(rt, bp, level);
			stack.addSlice("Level " + level, bp);
		}
		//IJ.log("makeComponentStack done.");
		return stack;
	}
	
	/**
	 * Creates a thresholded image with positions set if pixelvalue <= level.
	 */
	private void fillComponentImage(ComponentTree<?> rt, ColorProcessor cp, int level) {
		IJ.log("    makeComponentImage level="+level);
		//ByteProcessor bp = new ByteProcessor(width, height);
//		cp.setValue(255); 
		cp.setColor(Color.white);
		cp.fill();
		for (Component<?> c : rt.getComponents()) {
			if (c.getLevel() <= level) {
				//IJ.log("    level="+level + "  painting " + c.toString());
				
				Color col = COLORS[c.ID % COLORS.length];
				cp.setColor(col);
//				cp.setValue(c.ID);
				for (Pixel p : c.getAllPixels()) {
					cp.drawDot(p.x, p.y);
					//cp.set(p.x, p.y, 0);
				}
			}
		}
	}
	
	// -------------------------------------------------------------------
	
	private boolean runDialog() {
		GenericDialog gd = new GenericDialog("Settings");
		gd.addEnumChoice("component tree method", method);

		gd.showDialog();
		if (gd.wasCanceled())
			return false;

		method = gd.getNextEnumChoice(Method.class);
		return true;
	}
	
	
}
