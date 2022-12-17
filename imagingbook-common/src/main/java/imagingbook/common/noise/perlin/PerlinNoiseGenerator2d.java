/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.common.noise.perlin;

import imagingbook.common.noise.hashing.HashFunction;

/**
 * <p>
 * This class implements a 2D Perlin noise [1] generator. See Ch. 8 of [2] for
 * details.
 * </p>
 * <p>
 * [1] K. Perlin. Improving noise. In "SIGGRAPH’02: Proceedings of the 29th
 * Annual Conference on Computer Graphics and Interactive Techniques", pp.
 * 681–682, San Antonio, Texas (2002).<br>
 * [2] W. Burger and M.J. Burge. "Principles of Digital Image Processing -
 * Advanced Methods" (Vol. 3). Undergraduate Topics in Computer Science.
 * Springer-Verlag, London (2013).
 * </p>
 * 
 * @author WB
 * @version 2022/11/24
 */
public class PerlinNoiseGenerator2d extends PerlinNoiseGenerator {
	
	/**
	 * Constructor.
	 * @param f_min minimum frequency
	 * @param f_max maximum frequency
	 * @param persistence persistence
	 * @param hf hash function
	 */
	public PerlinNoiseGenerator2d(double f_min, double f_max, double persistence, HashFunction hf) {
		super(f_min, f_max, persistence, hf);
	}
	
	/**
	 * 2D combined (multi-frequency) Perlin noise function. 
	 * Returns the value of the combined Perlin
	 * noise function for the two-dimensional position (x,y).
	 * 
	 * @param x interpolation position x
	 * @param y interpolation position y
	 * @return the noise value for position (x,y)
	 */ 
	public double getNoiseValue(double x, double y) {
		double sum = 0;
		for (int i = 0; i < F.length; i++) {
			sum = sum + A[i] * noise(F[i] * x, F[i] * y);
		}
		return sum;
	}
	
	/**
	 * 2D elementary (single-frequency) Perlin noise function. 
	 * @param x Interpolation position x.
	 * @param y Interpolation position y.
	 * @return The value of the elementary Perlin
	 * noise function for the two-dimensional position (x,y).
	 */
	private double noise(double x, double y) {
		int px = ffloor(x);
		int py = ffloor(y);
		double[] g00 = gradient(px, py);
		double[] g10 = gradient(px + 1, py);
		double[] g01 = gradient(px, py + 1);
		double[] g11 = gradient(px + 1, py + 1);
		double x01 = x - px; // x01 is in [0,1]
		double y01 = y - py; // y01 is in [0,1]
		double w00 = g00[0] * (x01) + g00[1] * (y01);
		double w10 = g10[0] * (x01 - 1) + g10[1] * (y01);
		double w01 = g01[0] * (x01) + g01[1] * (y01 - 1);
		double w11 = g11[0] * (x01 - 1) + g11[1] * (y01 - 1);
		return interpolate(x01, y01, w00, w10, w01, w11);
	}
	
	/**
	 * @param px discrete horiz. position
	 * @param py discrete vert. position
	 * @return A pseudo-random gradient vector for the discrete position (px,py).
	 */
	private double[] gradient(int px, int py) {
		double[] g = hashFun.hash(px, py); // hash() always returns a new double[] in [0,1]
		g[0] = 2.0 * g[0] - 1;
		g[1] = 2.0 * g[1] - 1;
		return g;
	}

	/**
	 * Local interpolation function.
	 * @param x01 Horizontal interpolation position in [0,1]
	 * @param y01 Vertical interpolation position in [0,1]
	 * @param w00 Tangent value for position (0,0).
	 * @param w01 Tangent value for position (1,0).
	 * @param w10 Tangent value for position (0,1).
	 * @param w11 Tangent value for position (1,1).
	 * @return  The interpolated noise value at position (x01,y01).
	 */
	private double interpolate(double x01, double y01, double w00, double w10, double w01, double w11) { 
		double sx = this.s(x01); 
		double w0 = (1 - sx) * w00 + sx * w10;
		double w1 = (1 - sx) * w01 + sx * w11;
		double sy = this.s(y01);
		double w = (1 - sy) * w0 + sy * w1;
		return w;
	}	

}
