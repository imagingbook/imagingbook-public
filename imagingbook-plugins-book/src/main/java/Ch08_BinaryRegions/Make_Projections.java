/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package Ch08_BinaryRegions;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

/**
 * This ImageJ plugin demonstrates the calculation of horizontal and
 * vertical projections on a grayscale image.
 * 
 * @author WB
 *
 */
public class Make_Projections implements PlugInFilter {
	
	static boolean verbose = true;
	static final int PSIZE = 150;	// size of projection bars
	static final int FOREGROUND = 0;
	static final int BACKGROUND = 255;
	
	ImagePlus origImage = null;
	
	public int setup(String arg, ImagePlus im) { 
		origImage = im;
		return DOES_8G + NO_CHANGES; 
	}
	
	public void run(ImageProcessor I) {
		int M = I.getWidth();
		int N = I.getHeight();
		int[] pHor = new int[N];
		int[] pVer = new int[M];
		for (int v = 0; v < N; v++) {
			for (int u = 0; u < M; u++) {
				int p = I.getPixel(u, v);
				pHor[v] +=  p;
				pVer[u] +=  p;
			}
		}
		
		// make the horizontal projection:
		ByteProcessor hP = new ByteProcessor(PSIZE,N);
		hP.setValue(BACKGROUND); hP.fill();
		hP.setValue(FOREGROUND);
		double maxWhite = M * 255;
		for (int row = 0; row < N; row++) {
			double black = (1 - pHor[row] / maxWhite);	// percentage of black in line 'row'
			int k = (int) (black * PSIZE);
			if (k > 0) {
				hP.drawLine(0, row, k, row);
			}
		}
		(new ImagePlus("Horiz. Proj", hP)).show();
				
		// make the vertical projection:
		ByteProcessor vP = new ByteProcessor(M,PSIZE);
		vP.setValue(BACKGROUND); vP.fill();
		vP.setValue(FOREGROUND);
		maxWhite = N * 255;
		for (int col = 0; col < M; col++) {
			double black = (1 - pVer[col] / maxWhite);	// percentage of black in column 'col'
			int k = (int) (black * PSIZE);
			if (k > 0) {
				vP.drawLine(col, 0, col, k);
			}
		}
		(new ImagePlus("Vert. Proj", vP)).show();
	}

}
