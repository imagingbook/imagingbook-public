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
import imagingbook.common.math.Matrix;
import imagingbook.common.util.ParameterBundle;

/**
 * Multi-Gradient ("DiZenzo/Cumani-style") color edge detector, as described in UTICS Vol. 3, Alg. 4.2.
 * @author W. Burger
 * @version 2013/05/30
 */
public class DiZenzoCumaniEdgeDetector extends ColorEdgeDetector {
	
	/**
	 * Currently unused, no parameters to set
	 */
	public static class Parameters implements ParameterBundle {
	}
	
	final Parameters params;
	
	int M;	// image width
	int N;	// image height
	ColorProcessor I;	// original image
	FloatProcessor E_mag;	// edge magnitude map
	FloatProcessor E_ort;	// edge orientation map

	// Sobel-kernels for x/y-derivatives:
    final float[] HxS = Matrix.multiply(1.0f/8, 
        new float[] {
			-1, 0, 1,
		    -2, 0, 2,
		    -1, 0, 1
		    });
    
    final float[] HyS = Matrix.multiply(1.0f/8, 
		 new float[] {
			-1, -2, -1,
			 0,  0,  0,
			 1,  2,  1
			 });
    
    final int R = 0, G = 1, B = 2;		// RGB channel indexes
	
    FloatProcessor[] Ix;
    FloatProcessor[] Iy;
 
	public DiZenzoCumaniEdgeDetector(ColorProcessor I) {
		this(I, new Parameters());
	}
	
	public DiZenzoCumaniEdgeDetector(ColorProcessor I, Parameters params) {
		this.params = params;
		this.I = I;
		setup();
		findEdges();
	}
	
	protected void setup() {
		M = this.I.getWidth();
		N = this.I.getHeight();
		E_mag = new FloatProcessor(M, N);
		E_ort = new FloatProcessor(M, N);
		Ix = new FloatProcessor[3];
		Iy = new FloatProcessor[3];
	}

	void findEdges() {
		for (int c = R; c <= B; c++) {
			Ix[c] =  getRgbFloatChannel(I, c);
			Iy[c] =  getRgbFloatChannel(I, c);
			Ix[c].convolve(HxS, 3, 3);
			Iy[c].convolve(HyS, 3, 3);
		}
		
		for (int v = 0; v < N; v++) {
			for (int u = 0; u < M; u++) {
				float rx = Ix[R].getf(u, v), ry = Iy[R].getf(u, v);
				float gx = Ix[G].getf(u, v), gy = Iy[G].getf(u, v);
				float bx = Ix[B].getf(u, v), by = Iy[B].getf(u, v);
				
				float A = rx*rx + gx*gx + bx*bx;
				float B = ry*ry + gy*gy + by*by;
				float C = rx*ry + gx*gy + bx*by;
				
				float lambda0 = 0.5f * (A + B + (float) Math.sqrt(sqr(A-B) + 4 * sqr(C)));
				float theta0 =  0.5f * (float) Math.atan2(2 * C, A - B);

				E_mag.setf(u, v, (float) Math.sqrt(lambda0));
				E_ort.setf(u, v, theta0);
//				edgeOrientation.setf(u, v, 2 * CC);
//				edgeOrientation.setf(u, v, AA - BB);
				
			}
		}
	}
	
	public FloatProcessor getEdgeMagnitude() {
		return E_mag;
	}

	public FloatProcessor getEdgeOrientation() {
		return E_ort;
	}
	
	float sqr(float x) {
		return x * x;
	}
}
