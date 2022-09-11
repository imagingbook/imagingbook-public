/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package ColorEdges;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.edges.EdgeDetector;
import imagingbook.common.edges.MonochromaticEdgeDetector;
import imagingbook.common.edges.MonochromaticEdgeDetector.Parameters;
import imagingbook.core.FileUtils;

/**
 * This is a simple color edge detector based on monochromatic techniques.
 * May be applied to color images only.
 * 
 * @author WB
 * @version 2013/05/30
 * @version 2022/09/11 removed color norm option (not available for this detector)
 */
public class Color_Edges_Mono implements PlugInFilter {
	
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
    	Parameters params = new Parameters();
    	
    	if (!setParameters(params)) 
    		return;
    	
    	EdgeDetector ced = new MonochromaticEdgeDetector((ColorProcessor) ip, params);
    	   	
    	if (showEdgeMagnitude) {
    		FloatProcessor edgeMagnitude = ced.getEdgeMagnitude();
    		(new ImagePlus("Edge Magnitude (Mono)", edgeMagnitude)).show();
    	}
    		
		if (showEdgeOrientation) {
			FloatProcessor edgeOrientation = ced.getEdgeOrientation();
			(new ImagePlus("Edge Orientation (Mono)", edgeOrientation)).show();
		}
    }
    
    boolean setParameters(Parameters params) {
		GenericDialog gd = new GenericDialog("Monochromatic Color Edges");
		gd.addCheckbox("Show edge magnitude", showEdgeMagnitude);
		gd.addCheckbox("Show edge orientation", showEdgeOrientation);
		
		gd.showDialog();
		if(gd.wasCanceled()) 
			return false;
		
		showEdgeMagnitude = gd.getNextBoolean();
		showEdgeOrientation =  gd.getNextBoolean();
		return true;
    }
      
}

