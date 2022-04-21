/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.color.edge;

import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.math.Matrix;
import imagingbook.common.util.ParameterBundle;

/**
 * Simple grayscale edge detector for color images. The color image
 * is converted to grayscale for edge detection.
 * @author W. Burger
 * @version 2014/02/17
 */
public class GrayscaleEdgeDetector extends ColorEdgeDetector {
	

	final ImageProcessor I;
	final int M;	// image width
	final int N;	// image height
	final Parameters params;
	
	final FloatProcessor Emag;	// edge magnitude map
	final FloatProcessor Eort;	// edge orientation map
	
	double wr = 0.2126, wg = 0.7152, wb = 0.0722;	// ITU BR.709 luma weights

	/**
	 * Currently unused, no parameters to set
	 */
	public static class Parameters implements ParameterBundle {
	}
	
	// Sobel-kernels for x/y-derivatives:
    final float[] HxS = Matrix.multiply(1.0f/8, new float[] {
			-1, 0, 1,
		    -2, 0, 2,
		    -1, 0, 1
		    });
    
    final float[] HyS = Matrix.multiply(1.0f/8, new float[] {
			-1, -2, -1,
			 0,  0,  0,
			 1,  2,  1
			 });
    
    final int R = 0, G = 1, B = 2;		// RGB channel indexes
	
    private FloatProcessor Ix;
    private FloatProcessor Iy;
 
	public GrayscaleEdgeDetector(ImageProcessor I) {
		this(I, new Parameters());
	}
	
	public GrayscaleEdgeDetector(ImageProcessor I, Parameters params) {
		this.params = params;
		this.I = I;
		this.M = this.I.getWidth();
		this.N = this.I.getHeight();
		Emag = new FloatProcessor(M, N);
		Eort = new FloatProcessor(M, N);
		setup();
		findEdges();
	}
	
	protected void setup() {
		// convert to a grayscale (float) image with specified RGB weights:
		if (I instanceof ColorProcessor) {
			((ColorProcessor)I).setRGBWeights(wr, wg, wb);
		}
		Ix = I.convertToFloatProcessor();
		Iy = (FloatProcessor) Ix.duplicate();
	}

	void findEdges() {
		Ix.convolve(HxS, 3, 3);
		Iy.convolve(HyS, 3, 3);
		
		for (int v = 0; v < N; v++) {
			for (int u = 0; u < M; u++) {
				// extract the gradients of the R, G, B channels:
				final float dx = Ix.getf(u, v);	
				final float dy = Iy.getf(u, v);		
				
				// calculate local edge magnitude:
				final float eMag = (float) Math.sqrt(dx * dx + dy * dy);
				Emag.setf(u, v, eMag);	
				
				// calculate edge orientation for the maximum channel:
				float eOrt = (float) Math.atan2(dy, dx);
				Eort.setf(u, v, eOrt);
			}
		}
	}
	
	public FloatProcessor getEdgeMagnitude() {
		return Emag;
	}

	public FloatProcessor getEdgeOrientation() {
		return Eort;
	}
	
}
