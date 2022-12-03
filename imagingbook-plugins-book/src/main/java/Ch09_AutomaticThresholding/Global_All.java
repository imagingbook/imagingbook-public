/*
 *  This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge.
 * All rights reserved. Visit https://imagingbook.com for additional details.
 */
package Ch09_AutomaticThresholding;

import static imagingbook.common.ij.DialogUtils.askForSampleImage;
import static imagingbook.common.ij.IjUtils.noCurrentImage;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.common.ij.IjUtils;
import imagingbook.sampleimages.GeneralSampleImage;

/**
 * <p>
 * ImageJ plugin making available a selection of global thresholders.
 * See Sec. 9.1 of [1] for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic
 * Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2022/04/02
 * 
 * @see Global_Isodata
 * @see Global_MaxEntropy
 * @see Global_Mean
 * @see Global_Median
 * @see Global_MinError
 * @see Global_MinMax
 * @see Global_Otsu
 */
public class Global_All implements PlugInFilter {
	
	enum Algorithm {
		IsoData(Global_Isodata.class), 
		MaxEntropy(Global_MaxEntropy.class),
		Mean(Global_Mean.class),
		Median(Global_Median.class),
		MinError(Global_MinError.class),
		MinMax(Global_MinMax.class),
		Otsu(Global_Otsu.class);
		
		final Class<? extends PlugInFilter> pluginClass;
		
		Algorithm(Class<? extends PlugInFilter> cls) {
			this.pluginClass = cls;
		}
	}
	
	private static Algorithm algo = Algorithm.IsoData;
	private ImagePlus imp = null;
	
	/**
	 * Constructor, asks to open a predefined sample image if no other image
	 * is currently open.
	 */
	public Global_All() {
		if (noCurrentImage()) {
			askForSampleImage(GeneralSampleImage.Kepler);
		}
	}
	
	@Override
	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return DOES_8G;
	}

	@Override
	public void run(ImageProcessor ip) {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addEnumChoice("Algorithm", algo);
		
		gd.showDialog();
		if (gd.wasCanceled())
			return;
		
		algo = gd.getNextEnumChoice(Algorithm.class);
		imp.unlock();
		
		IjUtils.runPlugInFilter(algo.pluginClass);
//		IJ.runPlugIn(imp, algo.pluginClass.getCanonicalName(), null);
	}

}
