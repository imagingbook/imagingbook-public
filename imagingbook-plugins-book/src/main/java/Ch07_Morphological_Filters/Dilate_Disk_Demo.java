/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package Ch07_Morphological_Filters;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.ij.DialogUtils;
import imagingbook.common.morphology.BinaryDilation;
import imagingbook.common.morphology.BinaryMorphologyFilter;
import imagingbook.common.morphology.StructuringElements;
import imagingbook.sampleimages.GeneralSampleImage;

import static imagingbook.common.ij.IjUtils.noCurrentImage;

/**
 * <p>
 * ImageJ plugin implementing a binary dilation by a disk-shaped structuring element with a fixed radius. See Sec. 7.2
 * of [1] for additional details. This plugin works on 8-bit grayscale images only, the original image is modified.
 * Zero-value pixels are considered background, all other pixels are foreground. Different to ImageJ's built-in
 * morphological operators, this implementation does not incorporate the current display lookup-table (LUT).
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2022/12/07
 */
public class Dilate_Disk_Demo implements PlugInFilter {

	private static double Radius = 6.5;

	private ImagePlus im;

	/** Constructor, asks to open a predefined sample image if no other image is currently open. */
	public Dilate_Disk_Demo() {
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

		byte[][] H = StructuringElements.makeDiskKernel(Radius);
		BinaryMorphologyFilter filter = new BinaryDilation(H);
		filter.applyTo((ByteProcessor) ip);
	}

	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		if (im.isInvertedLut()) {
			gd.setInsets(0, 0, 0);
			gd.addMessage("NOTE: Image has inverted LUT (0 = white)!");
		}
		gd.addNumericField("Radius (filters only)", Radius, 1, 5, "pixels");

		gd.showDialog();
		if (gd.wasCanceled())
			return false;

		Radius = gd.getNextNumber();
		return true;
	}
}
