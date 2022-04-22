/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
package MorphologicalFilters;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.ij.GuiTools;
import imagingbook.common.ij.IjUtils;
import imagingbook.common.morphology.BinaryClosing;
import imagingbook.common.morphology.BinaryDilation;
import imagingbook.common.morphology.BinaryErosion;
import imagingbook.common.morphology.BinaryMorphologyFilter;
import imagingbook.common.morphology.BinaryOpening;
import imagingbook.common.util.Enums;

/**
 * This plugin implements a binary morphology filter using an arbitrary
 * structuring element that can be interactively specified by the user.
 * 
 * @author WB
 * @version 2022/01/24
 */
public class Bin_Morphology_Free implements PlugInFilter {
	
	private static enum OpType {
		Dilate, Erode, Open, Close;
	}

	static final int W = 9;	// width and height of the structuring element
	static boolean[] freeStructure = new boolean[W * W];
	static boolean showElement = false;
	static OpType op = OpType.Dilate;

	static { // initially set the center element
		freeStructure[(W * W) / 2] = true;
	}

	@Override
	public int setup(String arg, ImagePlus imp) {
		return DOES_8G;
	}

	@Override
	public void run(ImageProcessor orig) {
		if (!showDialog()) {
			return;
		}
		
		byte[][] H = makeStructureElement(freeStructure);
		ByteProcessor bp = (ByteProcessor) orig;
		
		BinaryMorphologyFilter filter = null;
		switch(op) {
		case Close:	
			filter = new BinaryClosing(H); break;
		case Dilate:
			filter = new BinaryDilation(H); break;
		case Erode:
			filter = new BinaryErosion(H); break;
		case Open:
			filter = new BinaryOpening(H); break;
		}
		
		filter.applyTo(bp);
		
		if (showElement) {
			ByteProcessor pH = IjUtils.toByteProcessor(H);
			pH.invertLut();
			pH.setMinAndMax(0, 1);
			ImagePlus iH = new ImagePlus("H", pH);
			iH.show();
			GuiTools.zoomExact(iH, 10);
		}
	}

	private boolean showDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		String[] labels = makeFilterLabels();

		gd.addCheckboxGroup(W, W, labels, freeStructure);
		String[] ops = Enums.getEnumNames(OpType.class);
		gd.addChoice("Operation", ops, op.name());
		gd.addCheckbox("Show structuring element", showElement);
		gd.showDialog();
		if (gd.wasCanceled())
			return false;

		for (int i = 0; i < W * W; i++) {
			freeStructure[i] = gd.getNextBoolean();
		}
		
		showElement = gd.getNextBoolean();
		op = OpType.valueOf(gd.getNextChoice());
		return true;
	}

	private byte[][] makeStructureElement(boolean[] structure) {
		byte[][] kernel = new byte[W][W];
		int i = 0;
		for (int v = 0; v < W; v++) {
			for (int u = 0; u < W; u++) {
				if (structure[i])
					kernel[v][u] = 1;
				else
					kernel[v][u] = 0;
				i++;
			}
		}
		return kernel;
	}

	private String[] makeFilterLabels() {
		String[] labels = new String[W * W];
		for (int i = 0; i < labels.length; i++) {
			labels[i] = " ";
		}
		labels[(W * W) / 2] = "x";
		return labels;
	}

}
