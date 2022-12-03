/*
 *  This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge.
 * All rights reserved. Visit https://imagingbook.com for additional details.
 */
package Ch07_MorphologicalFilters;

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

/**
 * <p>
 * ImageJ plugin implementing a binary morphology filter with an arbitrary
 * structuring element that can be interactively specified by the user.
 * See Sec. 7.2 of [1] for additional details.
 * This plugin works on 8-bit grayscale images only.
 * Zero-value pixels are considered background, all other pixels
 * are foreground. Different to ImageJ's built-in morphological
 * operators, this implementation does not incorporate the current display 
 * lookup-table (LUT).
 * </p> 
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic
 * Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * @author WB
 * @version 2022/01/24
 */
public class Bin_Morphology_Free implements PlugInFilter {
	
	public static enum OpType {
		Dilate, Erode, Open, Close;
	}

	/** Operation type (dilation, erosion, opening, closing). */
	public static OpType Operation = OpType.Dilate;
	/** Width and height of the structuring element. */
	public static final int Size = 4;
	/** Display the structuring element as a binary image. */
	public static boolean ShowStructuringElement = false;
	
	private static boolean[] freeStructure = new boolean[Size * Size];
	static { // initially set the center element
		freeStructure[(Size * Size) / 2] = true;
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
		switch(Operation) {
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
		
		if (ShowStructuringElement) {
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

		gd.addCheckboxGroup(Size, Size, labels, freeStructure);
		gd.addEnumChoice("Operation", Operation);
		gd.addCheckbox("Show structuring element", ShowStructuringElement);
		gd.showDialog();
		if (gd.wasCanceled())
			return false;

		for (int i = 0; i < Size * Size; i++) {
			freeStructure[i] = gd.getNextBoolean();
		}
		Operation = gd.getNextEnumChoice(OpType.class);
		ShowStructuringElement = gd.getNextBoolean();
		return true;
	}

	private byte[][] makeStructureElement(boolean[] structure) {
		byte[][] kernel = new byte[Size][Size];
		int i = 0;
		for (int v = 0; v < Size; v++) {
			for (int u = 0; u < Size; u++) {
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
		String[] labels = new String[Size * Size];
		for (int i = 0; i < labels.length; i++) {
			labels[i] = " ";
		}
		labels[(Size * Size) / 2] = "x";
		return labels;
	}

}
