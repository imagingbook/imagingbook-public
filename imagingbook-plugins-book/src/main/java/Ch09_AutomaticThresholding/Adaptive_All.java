/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package Ch09_AutomaticThresholding;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.common.ij.IjUtils;

/**
 * <p>
 * ImageJ demo plugin making available a selection of adaptive thresholders. See
 * Sec. 9.2 of [1] for additional details. This plugin works on 8-bit grayscale
 * images only. The original image is modified to a binary image.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing - An Algorithmic
 * Approach</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2022/04/01
 * 
 * @see Adaptive_Bernsen
 * @see Adaptive_Interpolating
 * @see Adaptive_Niblack
 * @see Adaptive_Sauvola
 */
public class Adaptive_All implements PlugInFilter {
	
	enum Algorithm {
		Bernsen(Adaptive_Bernsen.class), 
		Interpolating(Adaptive_Interpolating.class),
		Niblack(Adaptive_Niblack.class),
		Sauvola(Adaptive_Sauvola.class);
		
		final Class<? extends PlugInFilter> pluginClass;
		
		Algorithm(Class<? extends PlugInFilter> cls) {	//constructor
			this.pluginClass = cls;
		}
	}
	
	private static Algorithm algo = Algorithm.Bernsen;
	private ImagePlus im = null;
	
	@Override
	public int setup(String arg, ImagePlus im) {
		this.im = im;
		return DOES_8G;
	}

	@Override
	public void run(ImageProcessor ip) {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addEnumChoice("Algorithm", algo);
		
		gd.showDialog();
		if (gd.wasCanceled())
			return;
		
		im.unlock();
		
		algo = gd.getNextEnumChoice(Algorithm.class);
		IjUtils.runPlugInFilter(algo.pluginClass);
		
//		IJ.runPlugIn(imp, algo.pluginClass.getCanonicalName(), null);
	}

}
