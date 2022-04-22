/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
package ColorEdges;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.color.edge.ColorEdgeDetector;
import imagingbook.common.color.edge.MonochromaticEdgeDetector;
import imagingbook.common.color.edge.MonochromaticEdgeDetector.Parameters;
import imagingbook.common.math.VectorNorm.NormType;
import imagingbook.common.util.Enums;
import imagingbook.core.FileUtils;

/**
 * This is a simple color edge detector based on monochromatic techniques.
 * @author W. Burger
 * @version 2013/05/30
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
    	
    	if (!setParameters(params)) return;
    	
    	ColorProcessor cp = (ColorProcessor) ip;
    	ColorEdgeDetector ced = new MonochromaticEdgeDetector(cp, params);
    	   	
    	if (showEdgeMagnitude) {
    		FloatProcessor edgeMagnitude = ced.getEdgeMagnitude();
    		(new ImagePlus("Edge Magnitude (Mono) " + params.norm.name(), edgeMagnitude)).show();
    	}
    		
		if (showEdgeOrientation) {
			FloatProcessor edgeOrientation = ced.getEdgeOrientation();
			(new ImagePlus("Edge Orientation (Mono) " + params.norm.name(), edgeOrientation)).show();
		}
    }
    
    boolean setParameters(Parameters params) {
		GenericDialog gd = new GenericDialog("Monochromatic Color Edges");
		gd.addChoice("Color norm", Enums.getEnumNames(NormType.class), params.norm.name());
		gd.addCheckbox("Show edge magnitude", showEdgeMagnitude);
		gd.addCheckbox("Show edge orientation", showEdgeOrientation);
		gd.showDialog();
		if(gd.wasCanceled()) 
			return false;
		params.norm = NormType.valueOf(gd.getNextChoice());
		showEdgeMagnitude = gd.getNextBoolean();
		showEdgeOrientation =  gd.getNextBoolean();
		return true;
    }
      
}

