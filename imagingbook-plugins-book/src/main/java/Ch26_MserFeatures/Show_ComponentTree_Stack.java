/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package Ch26_MserFeatures;

import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.mser.MserData;
import imagingbook.common.mser.components.Component;
import imagingbook.common.mser.components.ComponentTree;
import imagingbook.common.mser.components.ComponentTree.Method;
import imagingbook.common.mser.components.PixelMap.Pixel;

/**
 * Creates the component tree of the current image (choose from 2 different methods)
 * and reconstructs the associated threshold stack.
 * This must be the same as the one produced by plugin 'Show_Threshold_Stack'.
 * 
 * @author WB (2021)
 *
 */
public class Show_ComponentTree_Stack implements PlugInFilter {
	
	static Method method = Method.LinearTime;

	@Override
	public int setup(String arg0, ImagePlus im) {
		return DOES_8G + NO_CHANGES;
	}
	
	@Override
	public void run(ImageProcessor ip) {
		if (!getUserInput()) {
			return;
		}
		
		ComponentTree<MserData> compTree = ComponentTree.from((ByteProcessor) ip, method);
		
		ImageStack compStack = makeComponentStack(compTree, ip.getWidth(), ip.getHeight());
		ImagePlus stackIm = new ImagePlus("Component Tree Stack - " + method.name(), compStack);
		stackIm.show();
//		GuiTools.zoomExact(stackIm, 16);
	}
	
	private boolean getUserInput() {
		GenericDialog gd = new GenericDialog("Settings");
		gd.addEnumChoice("component tree method", method);

		gd.showDialog();
		if (gd.wasCanceled())
			return false;

		method = gd.getNextEnumChoice(Method.class);
		return true;
	}
	
	// -------------------------------------------------------------------
	
	private ImageStack makeComponentStack(ComponentTree<?> rt, int width, int height) {
		//IJ.log("makeComponentStack...");
		ImageStack stack = new ImageStack(width, height);
		for (int level = 0; level < 256; level++) {
			ByteProcessor bp = new ByteProcessor(width, height);
			fillComponentImage(rt, bp, level);
			stack.addSlice("Level " + level, bp);
		}
		//IJ.log("makeComponentStack done.");
		return stack;
	}
	
	/**
	 * Creates a thresholded image with positions set
	 * if pixelvalue <= level.
	 * @param level
	 * @return
	 */
	private void fillComponentImage(ComponentTree<?> rt, ByteProcessor bp, int level) {
		//IJ.log("    makeComponentImage level="+level);
		//ByteProcessor bp = new ByteProcessor(width, height);
		bp.setValue(255); 
		bp.fill();
		for (Component<?> c : rt.getComponents()) {
			if (c.getLevel() <= level) {
				//IJ.log("    level="+level + "  painting " + c.toString());
				for (Pixel p : c.getLocalPixels()) {
					bp.set(p.x, p.y, 0);
				}
			}
		}
	}
	
}
