/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
package GenericFilterDemos;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.common.filter.GenericFilterVector;
import imagingbook.common.image.data.PixelPack;

/**
 * This ImageJ plugin shows how to construct a 3x3 custom generic vector filter
 * based on class {@link GenericFilterVector}.
 * This plugin works for all types of images.
 * 
 * @author WB
 * @version 2022/03/30
 *
 */
public class Custom_Generic_Filter_Vector implements PlugInFilter {


    public int setup(String arg, ImagePlus imp) {
        return DOES_ALL;
    }

    public void run(ImageProcessor ip) {
    	new ExampleFilterVector3x3().applyTo(ip);    
    }

    // -------------------------------------------------------------
    
    private static class ExampleFilterVector3x3 extends GenericFilterVector {
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
    	protected float[] doPixel(PixelPack pack, int u, int v) {
    		int depth = pack.getDepth();
    		float[] sum = new float[depth];
    		for (int j = 0; j < height; j++) {
    			int vj = v + j - yc;
    			for (int i = 0; i < width; i++) {
    				int ui = u + i - xc;
    				float[] p = pack.getVec(ui, vj);
    				for (int k = 0; k < depth; k++) {
    					sum[k] = sum[k] + (p[k] * H[i][j]) / s;
    				}
    			}
    		}
    		return sum;
    	}
    }
}
