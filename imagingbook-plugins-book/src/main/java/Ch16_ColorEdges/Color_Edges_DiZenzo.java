/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package Ch16_ColorEdges;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.edges.EdgeDetector;
import imagingbook.common.edges.MultiGradientEdgeDetector;
import imagingbook.core.FileUtils;


/**
 * This plugin implements a multi-gradient (DiZenzo/Cumani-style) color 
 * edge detector.
 * @author WB
 * @version 2013/05/30
 */
public class Color_Edges_DiZenzo implements PlugInFilter {
	
	static boolean showEdgeMagnitude = true;
	static boolean showEdgeOrientation = false;
	
	ImagePlus imp = null;

    public int setup(String arg, ImagePlus imp) {
    	this.imp = imp;
        return DOES_RGB + NO_CHANGES;
    }

    public void run(ImageProcessor ip) {
    	String title = imp.getTitle();
    	title = FileUtils.stripFileExtension(title);
    	
    	if (!setParameters()) return;
    	
    	ColorProcessor cp = (ColorProcessor) ip;
    	EdgeDetector ced = new MultiGradientEdgeDetector(cp);
    	
    	if (showEdgeMagnitude) {
    		FloatProcessor edgeMagnitude = ced.getEdgeMagnitude();
    		(new ImagePlus("Edge Magnitude (DiZenzo)", edgeMagnitude)).show();
    	}
		if (showEdgeOrientation) {
			FloatProcessor edgeOrientation = ced.getEdgeOrientation();
			(new ImagePlus("Edge Orientation (DiZenzo)", edgeOrientation)).show();
		}
    }
    
    boolean setParameters() {
		GenericDialog gd = new GenericDialog("Multi-Gradient Color Edges");
		gd.addCheckbox("Show edge magnitude", showEdgeMagnitude);
		gd.addCheckbox("Show edge orientation", showEdgeOrientation);
		gd.showDialog();
		if(gd.wasCanceled()) return false;
		showEdgeMagnitude = gd.getNextBoolean();
		showEdgeOrientation =  gd.getNextBoolean();
		return true;
    }
      
}

