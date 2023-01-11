/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package Ch26_MSER;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.color.sets.CssColor;
import imagingbook.common.ij.DialogUtils;
import imagingbook.common.mser.MserData;
import imagingbook.common.mser.components.Component;
import imagingbook.common.mser.components.ComponentTree;
import imagingbook.common.mser.components.ComponentTree.Method;
import imagingbook.common.mser.components.PixelMap.Pixel;
import imagingbook.core.jdoc.JavaDocHelp;
import imagingbook.core.resource.ImageResource;
import imagingbook.sampleimages.GeneralSampleImage;

import java.awt.Color;

import static imagingbook.common.ij.IjUtils.noCurrentImage;

/**
 * <p>
 * This ImageJ plugin creates the component tree of the given image and reconstructs the associated threshold stack by
 * coloring the individual components. The component tree is the basis of the MSER feature detection algorithm. The user
 * may choose from two different component tree algorithms:
 * </p>
 * <ol>
 * <li> "global immersion" (quasi-linear time) or </li>
 * <li>"local flooding" (linear time).</li>
 * </ol>
 * <p>
 * See Sec. 26.2 of [1] for details. If no image is currently open, the user is asked to load a predefined sample image.
 * Note: This implementation is quite inefficient since the image pixels contained in each tree component must be
 * collected recursively at each threshold level for visualization. Use on small images only!
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>,
 * 3rd ed, Springer (2022).
 * </p>
 *
 * @author WB
 * @version 2023/01/04
 */
public class Show_Component_Tree_Stack implements PlugInFilter, JavaDocHelp {

	private static ImageResource SampleImage = GeneralSampleImage.DotBlotSmall;
	private static ComponentTree.Method method = Method.LinearTime;
	private static Color[] COLORS = CssColor.SelectColors;

	/**
	 * Constructor, asks to open a predefined sample image if no other image is currently open.
	 */
	public Show_Component_Tree_Stack() {
		if (noCurrentImage()) {
			DialogUtils.askForSampleImage(SampleImage);
		}
	}

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
	}
	
	// -------------------------------------------------------------------
	
	private ImageStack makeComponentStack(ComponentTree<?> rt, int width, int height) {
		ImageStack stack = new ImageStack(width, height);
		for (int level = 0; level < 256; level++) {
			IJ.showProgress(level, 256);
			ColorProcessor cp = new ColorProcessor(width, height);
			fillComponentImage(rt, cp, level);
			stack.addSlice("Level " + level, cp);
		}
		IJ.showProgress(256, 256);
		return stack;
	}
	
	/**
	 * Creates a thresholded image with positions set if pixelvalue <= level.
	 */
	private void fillComponentImage(ComponentTree<?> rt, ColorProcessor cp, int level) {
		cp.setColor(Color.white);
		cp.fill();
		for (Component<?> c : rt.getComponents()) {
			if (c.getLevel() <= level) {
				int col = COLORS[c.ID % COLORS.length].getRGB();
				cp.setColor(col);
				for (Pixel p : c.getAllPixels()) {
					cp.set(p.x, p.y, col);
				}
			}
		}
	}
	
	// -------------------------------------------------------------------
	
	private boolean runDialog() {
		GenericDialog gd = new GenericDialog("Settings");
		gd.addHelp(getJavaDocUrl());
		gd.addEnumChoice("Component tree method", method);

		gd.showDialog();
		if (gd.wasCanceled())
			return false;

		method = gd.getNextEnumChoice(Method.class);
		return true;
	}
	
	
}
