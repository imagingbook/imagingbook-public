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
import imagingbook.common.ij.DialogUtils;
import imagingbook.common.ij.GuiTools;
import imagingbook.common.ij.IjUtils;
import imagingbook.common.morphology.BinaryClosing;
import imagingbook.common.morphology.BinaryDilation;
import imagingbook.common.morphology.BinaryErosion;
import imagingbook.common.morphology.BinaryMorphologyFilter;
import imagingbook.common.morphology.BinaryOpening;
import imagingbook.common.morphology.StructuringElements;
import imagingbook.sampleimages.GeneralSampleImage;

import static imagingbook.common.ij.IjUtils.noCurrentImage;

/**
 * <p>
 * This plugin implements a binary morphology filter (dilation, erosion, opening, or closing) using a disk-shaped
 * structuring element whose radius can be specified. See Sec. 7.2 of [1] for additional details. This plugin works on
 * 8-bit grayscale images only. Zero-value pixels are considered background, all other pixels are foreground. Different
 * to ImageJ's built-in morphological operators, this implementation does not incorporate the current display
 * lookup-table (LUT).
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2022/01/24
 */
public class Bin_Morphology_Disk implements PlugInFilter {
	
	public enum OpType {
		Dilate, Erode, Open, Close;
	}

	/** Operation type (dilation, erosion, opening, closing). */
	public static OpType Operation = OpType.Dilate;
	/** Radius of the structuring element. */
	public static double Radius = 2.5;
	/** Display the structuring element as a binary image. */
	public static boolean ShowStructuringElement = false;

	private ImagePlus im;

	/** Constructor, asks to open a predefined sample image if no other image is currently open. */
	public Bin_Morphology_Disk() {
		if (noCurrentImage()) {
			DialogUtils.askForSampleImage(GeneralSampleImage.RhinoSmallInv);
		}
	}

	@Override
	public int setup(String arg, ImagePlus im) {
		this.im = im;
		return DOES_8G;
	}

	@Override
	public void run(ImageProcessor ip) {
		if (!runDialog()) {
			return;
		}
		
		ByteProcessor bp = (ByteProcessor) ip;
		byte[][] H = StructuringElements.makeDiskKernel(Radius);
		
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

	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		if (im.isInvertedLut()) {
			gd.setInsets(0, 0, 0);
			gd.addMessage("NOTE: Image has inverted LUT (0 = white)!");
		}
		gd.addNumericField("Radius (filters only)", Radius, 1, 5, "pixels");
		gd.addEnumChoice("Operation", Operation);
		gd.addCheckbox("Show structuring element", ShowStructuringElement);

		gd.showDialog();
		if (gd.wasCanceled())
			return false;
		
		Radius = gd.getNextNumber();
		Operation = gd.getNextEnumChoice(OpType.class);
		ShowStructuringElement = gd.getNextBoolean();
		return true;
	}
	
}
