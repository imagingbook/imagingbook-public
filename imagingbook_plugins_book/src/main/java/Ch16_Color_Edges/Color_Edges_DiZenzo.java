/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package Ch16_Color_Edges;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.edges.EdgeDetector;
import imagingbook.common.edges.MultiGradientEdgeDetector;
import imagingbook.common.ij.DialogUtils;
import imagingbook.core.plugin.JavaDocHelp;
import imagingbook.sampleimages.GeneralSampleImage;

import static imagingbook.common.ij.IjUtils.noCurrentImage;

/**
 * <p>
 * This ImageJ plugin implements a multi-gradient (DiZenzo/Cumani-style) color edge detector. See Sec. 16.2 (Alg. 16.2) of [1]
 * for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2022/12/12
 * @see MultiGradientEdgeDetector
 */
public class Color_Edges_DiZenzo implements PlugInFilter, JavaDocHelp {

	private static boolean ShowEdgeMagnitude = true;
	private static boolean ShowEdgeOrientation = false;
	
	private ImagePlus im = null;

	/**
	 * Constructor, asks to open a predefined sample image if no other image is currently open.
	 */
	public Color_Edges_DiZenzo() {
		if (noCurrentImage()) {
			DialogUtils.askForSampleImage(GeneralSampleImage.Balloons600);
		}
	}

	@Override
    public int setup(String arg, ImagePlus im) {
    	this.im = im;
        return DOES_RGB + NO_CHANGES;
    }

	@Override
    public void run(ImageProcessor ip) {
    	if (!runDialog()) {
			return;
		}
    	
    	ColorProcessor cp = (ColorProcessor) ip;
    	EdgeDetector ced = new MultiGradientEdgeDetector(cp);
    	
    	if (ShowEdgeMagnitude) {
    		FloatProcessor edgeMagnitude = ced.getEdgeMagnitude();
    		(new ImagePlus("Edge Magnitude (DiZenzo)", edgeMagnitude)).show();
    	}
		if (ShowEdgeOrientation) {
			FloatProcessor edgeOrientation = ced.getEdgeOrientation();
			(new ImagePlus("Edge Orientation (DiZenzo)", edgeOrientation)).show();
		}
    }
    
    boolean runDialog() {
		GenericDialog gd = new GenericDialog("Multi-Gradient Color Edges");
		gd.addHelp(getJavaDocUrl());
		gd.addCheckbox("Show edge magnitude", ShowEdgeMagnitude);
		gd.addCheckbox("Show edge orientation", ShowEdgeOrientation);

		gd.showDialog();
		if(gd.wasCanceled())
			return false;

		ShowEdgeMagnitude = gd.getNextBoolean();
		ShowEdgeOrientation =  gd.getNextBoolean();
		return true;
    }
}

