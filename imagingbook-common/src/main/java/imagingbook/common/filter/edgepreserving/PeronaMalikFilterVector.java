/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.common.filter.edgepreserving;

import imagingbook.common.filter.generic.GenericFilterVector;
import imagingbook.common.image.PixelPack;
import imagingbook.common.math.Matrix;

import static imagingbook.common.math.Matrix.subtract;

/**
 * <p>
 * Vector version (simplified ) with greatly reduced memory requirements. This code is based on the Anisotropic
 * Diffusion filter proposed by Perona and Malik, as proposed in [1]. See Sec. 17.3.2 of [2] for additional details. The
 * filter operates on all types of grayscale (scalar) and RGB color images. Consult the source code of the related
 * ImageJ plugins for examples.
 * </p>
 * <p>
 * [1] Pietro Perona and Jitendra Malik, "Scale-space and edge detection using anisotropic diffusion", IEEE Transactions
 * on Pattern Analysis and Machine Intelligence, vol. 12, no. 4, pp. 629-639 (July 1990).
 * <br>
 * [2] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2021/01/04
 */	
public class PeronaMalikFilterVector extends GenericFilterVector implements PeronaMalikF {
	
	private final float alpha;
	private final int iterations; 		// number of iterations
	private final ConductanceFunction g;
	private ColorMode colorMode;
	
	// constructor - using default parameters
	public PeronaMalikFilterVector () {
		this(new Parameters());
	}
	
	// constructor - use this version to set all parameters
	public PeronaMalikFilterVector (Parameters params) {
		this.iterations = params.iterations;
		this.alpha = (float) params.alpha;
		this.g = ConductanceFunction.get(params.conductanceFunType, (float)params.kappa);
		this.colorMode = params.colorMode;
	}
	
	// ------------------------------------------------------
	
	@Override
	protected float[] doPixel(PixelPack pack, int u, int v) {
		/*   
		 *  NH pixels:      directions:
		 *      p4              3
		 *   p3 p0 p1         2 x 0
		 *      p2              1
		 */
		float[][] A = new float[4][];	// p[i][k]: 4 pixel colors from the 3x3 neigborhood
		float[] Ac =  pack.getPix(u, v);
//		A[0] = pack.getVec(u, v);
		A[0] = pack.getPix(u + 1, v);
		A[1] = pack.getPix(u, v - 1);
		A[2] = pack.getPix(u - 1, v);
		A[3] = pack.getPix(u, v + 1);
		
		float[] result = Ac.clone(); //A[0].clone();
		
		switch (colorMode) {
		case BrightnessGradient:
			float bc = getBrightness(Ac);
			for (int i = 0; i < 4; i++) {
				float bi = getBrightness(A[i]);
				for (int k = 0; k < 3; k++) {
					float gi = g.eval(Math.abs(bi - bc));
					result[k] = result[k] + alpha * gi * (A[i][k] - Ac[k]);
				}
			}
			break;
		case ColorGradient:
			for (int i = 0; i < 4; i++) {
				float[] D = subtract(A[i], Ac);
				float gi = g.eval(Matrix.normL2(D));	// g applied to color gradient magnitude
				for (int k = 0; k < 3; k++) {
					result[k] = result[k] + alpha * gi * D[k];
				}
			}
			break;
		default:
			throw new UnsupportedOperationException("color mode option " + colorMode.name() +
					" not implemented in class " + this.getClass().getSimpleName());
		}
		
		return result;
	}

	@Override
	protected final int passesRequired() {
		return iterations;	// this filter needs T passes
	}
	
	private final float getBrightness(float[] p) {
		return 0.299f * p[0] + 0.587f * p[1] + 0.114f * p[2];
	}
	
}
