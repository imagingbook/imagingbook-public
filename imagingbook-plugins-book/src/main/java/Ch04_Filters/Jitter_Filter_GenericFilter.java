/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package Ch04_Filters;
import java.util.Random;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.common.filter.generic.GenericFilter;
import imagingbook.common.filter.generic.GenericFilterVector;
import imagingbook.common.ij.DialogUtils;
import imagingbook.common.image.OutOfBoundsStrategy;
import imagingbook.common.image.PixelPack;
import imagingbook.sampleimages.GeneralSampleImage;

import static imagingbook.common.ij.IjUtils.noCurrentImage;

/**
 * <p> ImageJ plugin -
 * implementation of the "Jitter filter" based on {@link GenericFilter}. Works for all image types, using
 * nearest-border-pixel strategy. The input image is destructively modified.
 * </p>
 * <p>
 * Note that only the operation for a single pixel needs to be specified (method {@code GenericFilterVector.doPixel()}
 * in the inner class {@code JitterFilter}), everything else (i.e., looping over all pixels, special treatment of border
 * pixels and multiple color components) are automatically handled by {@link GenericFilter}. See Sec. 4.7 (Exercise
 * 4.14) of [1] for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2022/09/23
 *
 * @see GenericFilter
 * @see OutOfBoundsStrategy
 */
public class Jitter_Filter_GenericFilter implements PlugInFilter {
	
	/** The filter radius. */
	public static int R = 3;
	/** The out-of-bounds strategy to be used (see {@link OutOfBoundsStrategy}). */
	public static OutOfBoundsStrategy OBS = OutOfBoundsStrategy.NearestBorder;

	/** Constructor, asks to open a predefined sample image if no other image is currently open. */
	public Jitter_Filter_GenericFilter() {
		if (noCurrentImage()) {
			DialogUtils.askForSampleImage(GeneralSampleImage.Flower);
		}
	}
		
	@Override
	public int setup(String arg, ImagePlus im) {
		return DOES_ALL;
	}

	@Override
	public void run(ImageProcessor ip) {
		if (!runDialog()) {
			return;
		}

		GenericFilter filter = new JitterFilter();	// see below
		filter.applyTo(ip, OBS);	// that's all!
	}
	
	// --------------------------------------------------------------

	/**
	 * This inner class actually implements the Jitter filter, based on the functionality provided by
	 * {@link GenericFilterVector}, which can process images with 1 or more color channels. All we need to do is to
	 * specify what is to be done for a single pixel.
	 */
	private static class JitterFilter extends GenericFilterVector {
		private final int dist = 2 * R + 1;	// width/height of the "kernel"
		private final Random rnd = new Random();
		
		@Override
		protected float[] doPixel(PixelPack source, int u, int v) {
			int rx = rnd.nextInt(dist) - R;
			int ry = rnd.nextInt(dist) - R;
			return source.getPix(u + rx, v + ry);
		}
	}

	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
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
