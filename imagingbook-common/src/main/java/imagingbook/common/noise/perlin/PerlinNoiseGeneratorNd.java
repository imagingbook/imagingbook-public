/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.noise.perlin;

import imagingbook.common.math.Matrix;
import imagingbook.common.math.Matrix.IncompatibleDimensionsException;
import imagingbook.common.noise.hashing.HashFunction;

/**
 * <p>
 * This class implements an N-dimensional Perlin noise [1] generator. See Ch. 8
 * of [2] for details.
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
public class PerlinNoiseGeneratorNd extends PerlinNoiseGenerator {
	
	private final int N;		// dimensionality
	private final int K;		// number of hypercube vertices, default 2
	private final int[][] Q;	// vertex coordinates of the unit hypercube
		
	/**
	 * Constructor.
	 * @param N number of dimensions
	 * @param f_min minimum frequency
	 * @param f_max maximum frequency
	 * @param persistence persistence
	 * @param hf hash function
	 */
	public PerlinNoiseGeneratorNd(int N, double f_min, double f_max, double persistence, HashFunction hf) {
		super(f_min, f_max, persistence, hf);
		this.N = N;
		this.K = (int) Math.pow(2, N);	// number of hypercube vertices
		this.Q = new int[K][N];			// vertices of the unit hypercube
		for (int j = 0; j < K; j++) {
			Q[j] = vertex(j, N);
		}
	}
	
	/**
	 * N-dim combined (multi-frequency) Perlin noise function. 
	 * @param X Interpolation position X (N-dimensional).
	 * @return The value of the combined Perlin
	 * noise function for the N-dimensional position X.
	 */
	public double getNoiseValue(double[] X) {
		double sum = 0;
		for (int i = 0; i < F.length; i++) { // for all frequencies
			sum = sum + A[i] * noise(Matrix.multiply(F[i], X));
		}
		return sum;
	}
	
	/**
	 * 2D elementary (single-frequency) Perlin noise function. 
	 * @param X Interpolation position X (N-dimensional).
	 * @return The value of the elementary Perlin
	 * noise function for the N-dimensional position X.
	 */
	private double noise(double[] X) {
		int[] P0 = floor(X);		// origin of hypercube around X
		 
		// get the 2^N gradient vectors for all hypercube corners:
		double[][] G = new double[K][N];
		for(int j=0; j<K; j++) { 	
			G[j] = gradient(add(P0,Q[j])); 			// gradient vector at cube corner j
		}
		
		double[] X01 = subtract(X,P0);					// X01[k] are in [0,1]
		
		// get the 2^N gradient values at all vertices for position X
		double[] W = new double[K];
		for (int j = 0; j < K; j++) { 	
			W[j] = Matrix.dotProduct(G[j], subtract(X01, Q[j]));
		}
		
		return interpolate(X01, W, 0);
	}
	
	/**
	 * @param p discrete position.
	 * @return A pseudo-random gradient vector for 
	 * the discrete lattice point p (N-dimensional).
	 */
	private double[] gradient(int[] p) {	
		if (p.length == 2) {
			return gradient(p[0],p[1]);
		}
		// hash() always returns a new double[], g[i] in [0,1]
		double[] g = hashFun.hash(p);	// STILL TO BE DONE!!!
		for (int i=0; i<g.length; i++) {
			g[i] = 2.0 * g[i] - 1;
		}
		return g;
	}
	
	/**
	 * Local interpolation function (recursive).
	 * @param X01 Interpolation position in [0,1]^N
	 * @param WW  A vector of length 2^(N-d) with
	 * the tangent values for the hypercube corners.
	 * @param k The interpolation dimension (axis).
	 * @return  The interpolated noise value at position X01.
	 */
	private double interpolate(double[] X01, double[] WW, int k) {
		if (WW.length == 1) { // (d == N)
			return WW[0]; // done, end of recursion
		} else { // d < N
			double x01 = X01[k]; // select dimension d of vector X
			double s = this.s(x01); // blending function
			int M = WW.length / 2;
			double[] W = new double[M]; // W is half the length of WW
			for (int i = 0; i < M; i++) {
				double wa = WW[2 * i];
				double wb = WW[2 * i + 1];
				W[i] = (1 - s) * wa + s * wb; // the actual interpolation
			}
			return interpolate(X01, W, k + 1);
		}
	}

	/**
	 * @param j Vertex number (0..2^N-1)
	 * @param N Dimension of the hypercube
	 * @return The coordinate vector for vertex j of the N-dimensional
	 * hypercube.
	 */
	private int[] vertex(int j, int N) { 
		int[] v = new int[N];
		// copy the bit representation of j into v,
		// v[0] is the most significant bit 
		for (int k = 0; k < N; k++) {
			 v[k] = j & 0x00000001;		// select least significant bit (bit 0)
			 j = j >>> 1;				// j <- j/2
		}
		return v;
	}
	
	// from 2D example
	private double[] gradient(int i, int j) {
		double[] g = hashFun.hash(i,j);		// hash() always returns a new double[]
		g[0] = 2.0 * g[0] - 1;
		g[1] = 2.0 * g[1] - 1;
		return g;
	}
	
	private double[] subtract(double[] a, int[] b) {
		if (a.length != b.length)
			throw new IncompatibleDimensionsException();
		final int n = a.length;
		double[] c = new double[n];
		for (int i = 0; i < n; i++) {
			c[i] = a[i] - b[i];
		}
		return c;
	}
	
	private int[] add(int[] a, int[] b) {
		if (a.length != b.length)
			throw new IncompatibleDimensionsException();
		final int n = a.length;
		int[] c = new int[n];
		for (int i = 0; i < n; i++) {
			c[i] = c[i] + b[i];
		}
		return c;
	}
	
	private int[] floor(double[] a) {
		int[] b = new int[a.length];
		for (int i = 0; i < a.length; i++) {
			b[i] = (int) Math.floor(a[i]);
		}
		return b;
	}

}
