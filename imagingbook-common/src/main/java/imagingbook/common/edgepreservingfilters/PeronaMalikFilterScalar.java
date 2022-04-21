/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.edgepreservingfilters;

import imagingbook.common.filter.GenericFilterScalar;
import imagingbook.common.image.data.PixelPack.PixelSlice;


/**
 * Scalar version, without gradient array.
 * 
 * This code is based on the Anisotropic Diffusion filter proposed by Perona and Malik,
 * as proposed in Pietro Perona and Jitendra Malik, "Scale-space and edge detection 
 * using anisotropic diffusion", IEEE Transactions on Pattern Analysis 
 * and Machine Intelligence, vol. 12, no. 4, pp. 629-639 (July 1990).
 * 
 * The filter operates on all types of grayscale (scalar) and RGB color images.
 * This class is based on the ImageJ API and intended to be used in ImageJ plugins.
 * How to use: consult the source code of the related ImageJ plugins for examples.
 * 
 * @author W. Burger
 * @version 2021/01/02
 */
public class PeronaMalikFilterScalar extends GenericFilterScalar implements PeronaMalikF {
	
	private final int T; 		// number of iterations
	private final float alpha;
	private final ConductanceFunction g;
	private final float[] A = new float[4];			// tmp array for neighbor values
	
	// constructor - using default parameters
	public PeronaMalikFilterScalar () {
		this(new Parameters());
	}
	
	// constructor - use this version to set all parameters
	public PeronaMalikFilterScalar (Parameters params) {
		this.T = params.iterations;
		this.alpha = (float)params.alpha;
		this.g = ConductanceFunction.get(params.conductanceFunType, (float)params.kappa);
	}
	
	// ------------------------------------------------------
	
	@Override
	protected float doPixel(PixelSlice plane, int u, int v) {
		/*   
		 *  NH pixels:      directions:
		 *      a1              1
		 *   a2 ac a0        2  x  0
		 *      a3              3
		 */
		
		float ac = plane.getVal(u, v);
		A[0] = plane.getVal(u + 1, v);
		A[1] = plane.getVal(u, v - 1);
		A[2] = plane.getVal(u - 1, v);
		A[3] = plane.getVal(u, v + 1);
			
		float delta = 0;
		for (int i = 0; i < 4; i++) {
			float d = A[i] - ac;
			delta = delta + (g.eval(Math.abs(d))) * d;
		}
		
		return ac + alpha * delta;
	}
	
//	@Override
//	protected float doPixel(PixelSlice plane, int u, int v) {
//		/*   
//		 *  NH pixels:      directions:
//		 *      p4              3
//		 *   p3 p0 p1        2  x  0
//		 *      p2              1
//		 */
//		float[] p = new float[5];
//		p[0] = plane.getVal(u, v);
//		p[1] = plane.getVal(u + 1, v);
//		p[2] = plane.getVal(u, v + 1);
//		p[3] = plane.getVal(u - 1, v);
//		p[4] = plane.getVal(u, v - 1);
//			
//		float result = p[0];
//		for (int i = 1; i <= 4; i++) {
//			float d = p[i] - p[0];
//			result = result + alpha * (g.eval(Math.abs(d))) * (d);
//		}
//		
//		return result;
//	}

	@Override
	protected final int passesRequired() {
		return T;	// this filter needs T passes
	}

}
