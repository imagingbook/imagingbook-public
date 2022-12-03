/*
 *  This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge.
 * All rights reserved. Visit https://imagingbook.com for additional details.
 */

package Ch07_MorphologicalFilters;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.morphology.BinaryThinning;

/**
 * <p>This ImageJ plugin demonstrates morphological thinning
 * on binary images. See Sec. 7.2 of [1] for additional details. 
 * This plugin works on 8-bit grayscale images only, the original
 * image is modified. The maximum number of thinning iterations
 * can be specified.
 * Zero-value pixels are considered background, all other pixels
 * are foreground. Different to ImageJ's built-in morphological
 * operators, this implementation does not incorporate the current display 
 * lookup-table (LUT).
 * </p> 
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic
 * Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2022/01/24
 *
 */
public class Thinning_Demo implements PlugInFilter {
	
	private int maxIterations;
	
	@Override
	public int setup(String arg, ImagePlus imp) {
		return DOES_8G;
	}

	@Override
	public void run(ImageProcessor ip) {
		maxIterations = Math.max(ip.getWidth(), ip.getHeight());
		
		if (!showDialog()) {
			return;
		}
		
		BinaryThinning thin = new BinaryThinning(maxIterations);
		thin.applyTo((ByteProcessor) ip);
		IJ.log("Iterations performed: " + thin.getIterations());
	}
	
	private boolean showDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addNumericField("max. iterations", maxIterations, 0);

		gd.showDialog();
		if (gd.wasCanceled())
			return false;
		
		maxIterations = (int) gd.getNextNumber();
		return true;
	}
	
}

