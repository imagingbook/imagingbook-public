/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/

package Ch07_Morphological_Filters;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.geometry.basic.NeighborhoodType2D;
import imagingbook.common.ij.DialogUtils;
import imagingbook.common.morphology.BinaryMorphologyOperator;
import imagingbook.common.morphology.BinaryOutline;
import imagingbook.core.jdoc.JavaDocHelp;
import imagingbook.sampleimages.GeneralSampleImage;

import static imagingbook.common.ij.IjUtils.noCurrentImage;

/**
 * <p>
 * ImageJ plugin implementing a binary 'outline' operation using either a 4- or 8-neighborhood. See Sec. 7.2.7 of [1]
 * for additional details. This plugin works on 8-bit grayscale images only, the original image is modified. Zero-value
 * pixels are considered background, all other pixels are foreground. Different to ImageJ's built-in morphological
 * operators, this implementation does not incorporate the current display lookup-table (LUT).
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2022/01/24
 */
public class Outline_Demo implements PlugInFilter, JavaDocHelp {
	
	public static NeighborhoodType2D Neigborhood = NeighborhoodType2D.N4;

	private ImagePlus im;

	/** Constructor, asks to open a predefined sample image if no other image is currently open. */
	public Outline_Demo() {
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
		if (!showDialog()) {
			return;
		}
		
		BinaryMorphologyOperator outline = new BinaryOutline(Neigborhood);
		outline.applyTo((ByteProcessor) ip);
	}
	
	private boolean showDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addHelp(getJavaDocUrl());
		if (im.isInvertedLut()) {
			gd.setInsets(0, 0, 0);
			gd.addMessage("NOTE: Image has inverted LUT (0 = white)!");
		}
		gd.addEnumChoice("Neighborhood type", Neigborhood);

		gd.showDialog();
		if (gd.wasCanceled())
			return false;
		
		Neigborhood = gd.getNextEnumChoice(NeighborhoodType2D.class);
		return true;
	}
	
}

