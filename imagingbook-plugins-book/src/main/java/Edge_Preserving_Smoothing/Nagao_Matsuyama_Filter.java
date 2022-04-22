/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
package Edge_Preserving_Smoothing;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.edgepreservingfilters.NagaoMatsuyamaFilterScalar;
import imagingbook.common.edgepreservingfilters.NagaoMatsuyamaFilterVector;
import imagingbook.common.edgepreservingfilters.NagaoMatsuyamaF.Parameters;

/**
 * This plugin demonstrates the 5x5 Nagao-Matsuyama filter, as described in
 * NagaoMatsuyama (1979). This plugin works for all types of images and stacks.
 * @author W. Burger
 * @version 2014/03/16
 */
public class Nagao_Matsuyama_Filter implements PlugInFilter {
	
	private static Parameters params = new Parameters();
	private static boolean UseVectorFilter = false;
	private boolean isColor;

	public int setup(String arg0, ImagePlus imp) {
			return DOES_ALL;
	}
	
    public void run(ImageProcessor ip) {
    	isColor = (ip instanceof ColorProcessor);
		if (!getParameters())
			return;
		if (isColor && UseVectorFilter) {
			new NagaoMatsuyamaFilterVector(params).applyTo((ColorProcessor)ip);
		}
		else {
			new NagaoMatsuyamaFilterScalar(params).applyTo(ip);
		}
    }
    
    private boolean getParameters() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addNumericField("Variance threshold", params.varThreshold, 0);
		if (isColor)
			gd.addCheckbox("Use vector filter", UseVectorFilter);
		gd.showDialog();
		if (gd.wasCanceled()) return false;
		params.varThreshold = Math.max(gd.getNextNumber(),0);
		if (isColor)
			UseVectorFilter = gd.getNextBoolean();
		return true;
    }
    
}

