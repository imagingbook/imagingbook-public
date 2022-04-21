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
import imagingbook.common.math.VectorNorm;
import imagingbook.common.math.VectorNorm.NormType;
import imagingbook.common.util.ParameterBundle;

/**
 * Monochromatic color edge detector, as described in UTICS Vol. 3, Alg. 4.1.
 * @author W. Burger
 * @version 2014/02/16
 */
public class MonochromaticEdgeDetector extends ColorEdgeDetector {
	

	final ColorProcessor I;
	final int M;	// image width
	final int N;	// image height
	final Parameters params;
	
	private FloatProcessor Emag;	// edge magnitude map
	private FloatProcessor Eort;	// edge orientation map

	public static class Parameters implements ParameterBundle {
		/** Specify which color distance to use */
		public NormType norm = NormType.L2;
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
	
    FloatProcessor[] Ix;
    FloatProcessor[] Iy;
 
	public MonochromaticEdgeDetector(ColorProcessor I) {
		this(I, new Parameters());
	}
	
	public MonochromaticEdgeDetector(ColorProcessor I, Parameters params) {
		this.params = params;
		this.I = I;
		this.M = this.I.getWidth();
		this.N = this.I.getHeight();
		setup();
		findEdges();
	}
	
	protected void setup() {
		Emag = new FloatProcessor(M, N);
		Eort = new FloatProcessor(M, N);
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
		
		//VectorNorm vNorm = VectorNorm.create(params.norm);
		VectorNorm vNorm = params.norm.create();
		for (int v = 0; v < N; v++) {
			for (int u = 0; u < M; u++) {
				// extract the gradients of the R, G, B channels:
				final float Rx = Ix[R].getf(u, v);	float Ry = Iy[R].getf(u, v);
				final float Gx = Ix[G].getf(u, v);	float Gy = Iy[G].getf(u, v);
				final float Bx = Ix[B].getf(u, v);	float By = Iy[B].getf(u, v);
				
				final float Er2 = Rx * Rx + Ry * Ry;
				final float Eg2 = Gx * Gx + Gy * Gy;
				final float Eb2 = Bx * Bx + By * By;
				
				// calculate local edge magnitude:
				double[] Ergb = {Math.sqrt(Er2), Math.sqrt(Eg2), Math.sqrt(Eb2)};
				float eMag = (float) vNorm.magnitude(Ergb);
//				float eMag = (float) Math.sqrt(Er2 + Eg2 + Eb2);	// special case of L2 norm
				Emag.setf(u, v, eMag);	
				
				// find the maximum gradient channel:
				float e2max = Er2, cx = Rx, cy = Ry;
				if (Eg2 > e2max) {
					e2max = Eg2; cx = Gx; cy = Gy;
				}
				if (Eb2 > e2max) {
					e2max = Eb2; cx = Bx; cy = By;
				}
				
				// calculate edge orientation for the maximum channel:
				float eOrt = (float) Math.atan2(cy, cx);
				Eort.setf(u, v, eOrt);
			}
		}
	}

	
//	float mag(float er2, float eg2, float eb2, ColorDistanceNorm norm) {
//		double dist = 0;
//		switch (norm) {
//			case L1 : dist = Math.sqrt(er2) + Math.sqrt(eg2) + Math.sqrt(eb2); break;
//			case L2 : dist = Math.sqrt(er2*er2 + eg2*eg2 + eb2*eb2); break;
//			case Lmax : dist = Math.sqrt(Math.max(er2, (Math.max(eg2, eb2)))); break;
//		}
//		return (float) dist;
//	}
	
	public FloatProcessor getEdgeMagnitude() {
		return Emag;
	}

	public FloatProcessor getEdgeOrientation() {
		return Eort;
	}
	
}
