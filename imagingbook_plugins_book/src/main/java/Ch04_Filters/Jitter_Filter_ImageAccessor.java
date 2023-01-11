/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package Ch04_Filters;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.common.ij.DialogUtils;
import imagingbook.common.image.OutOfBoundsStrategy;
import imagingbook.common.image.access.ImageAccessor;
import imagingbook.core.jdoc.JavaDocHelp;
import imagingbook.sampleimages.GeneralSampleImage;

import java.util.Random;

import static imagingbook.common.ij.IjUtils.noCurrentImage;

/**
 * <p>
 * ImageJ plugin -- Jitter filter implemented with {@link ImageAccessor}, allowing uniform access to all image types.
 * Works for all image types, using nearest-border-pixel strategy. The input image is destructively modified. See Sec.
 * 4.7 (Exercise 4.14) of [1] for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2022/09/23 added out-of-bounds strategy
 * @see ImageAccessor
 * @see OutOfBoundsStrategy
 */
public class Jitter_Filter_ImageAccessor implements PlugInFilter, JavaDocHelp {
	
	/** The filter radius. */
	public static int R = 3;
	/** The out-of-bounds strategy to be used (see {@link OutOfBoundsStrategy}). */
	public static OutOfBoundsStrategy OBS = OutOfBoundsStrategy.NearestBorder;

	/** Constructor, asks to open a predefined sample image if no other image is currently open. */
	public Jitter_Filter_ImageAccessor() {
		if (noCurrentImage()) {
			DialogUtils.askForSampleImage(GeneralSampleImage.Flower);
		}
	}

	@Override
	public int setup(String arg, ImagePlus im) {
		return DOES_ALL;
	}

	@Override
	public void run(ImageProcessor ip1) {
		if (!runDialog()) {
			return;
		}

		final int w = ip1.getWidth();
		final int h = ip1.getHeight();
		final int d = 2 * R + 1;	// width/height of the "kernel"

		ImageProcessor ip2 = ip1.duplicate();
		ImageAccessor ia1 = ImageAccessor.create(ip1);
		ImageAccessor ia2 = ImageAccessor.create(ip2, OBS, null);

		Random rnd = new Random();
		
		for (int u = 0; u < w; u++) {
			for (int v = 0; v < h; v++) {
				int rx = rnd.nextInt(d) - R;
				int ry = rnd.nextInt(d) - R;
				// pick a random position inside the current support region
				float[] p = ia2.getPix(u + rx, v + ry);
				// replace the current center pixel in ip1
				ia1.setPix(u, v, p);
			}
		}
	}

	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addHelp(getJavaDocUrl());
		gd.addMessage("Filter kernel size is (2r+1) x (2r+1):");
		gd.addNumericField("Kernel radius (r > 0)", R, 0);
		gd.addEnumChoice("Out-of-bounds strategy", OBS);

		gd.showDialog();
		if (gd.wasCanceled())
			return false;

		R = (int) gd.getNextNumber();
		OBS = gd.getNextEnumChoice(OutOfBoundsStrategy.class);
		return true;
	}
	
}
