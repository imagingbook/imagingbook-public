/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package Thresholding;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.common.ij.IjUtils;

/**
 * Demo plugin making available a selection of global thresholders.
 * 
 * @author WB
 * @version 2022/04/02
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
