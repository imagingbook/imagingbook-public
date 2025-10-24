/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package GenericFilterDemos;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.common.filter.generic.GenericFilterScalar;
import imagingbook.common.image.PixelPack.PixelSlice;
import imagingbook.core.jdoc.JavaDocHelp;

/**
 * This ImageJ plugin shows how to construct a 3x3 custom generic filter based on class {@link GenericFilterScalar}.
 * This plugin works for all types of images. On RGB color images, the filter is applied independently to the three
 * color channels. The active image is modified.
 *
 * @author WB
 * @version 2022/03/30
 */
public class Custom_Generic_Filter_Scalar implements PlugInFilter, JavaDocHelp {

    public int setup(String arg, ImagePlus imp) {
        return DOES_ALL;
    }

    public void run(ImageProcessor ip) {	
    	new ExampleFilterScalar3x3().applyTo(ip);
    }
    
    // ------------------------------------------------------------------
    
    private static class ExampleFilterScalar3x3 extends GenericFilterScalar {
    	private final static float[][] H = {
    			{1, 2, 1},
    			{2, 4, 2},
    			{1, 2, 1}};
    	
    	private final int width = 3;
    	private final int height = 3;
    	private final int xc = 1;
    	private final int yc = 1;
    	private final float s = 16;
    	
    	@Override
		protected float doPixel(PixelSlice plane, int u, int v) {
    		float sum = 0;
    		for (int j = 0; j < height; j++) {
    			int vj = v + j - yc;
    			for (int i = 0; i < width; i++) {
    				int ui = u + i - xc;
    				sum = sum + plane.getVal(ui, vj) * H[i][j];
    			}
    		}
    		return sum / s;
    	}

    }

}
