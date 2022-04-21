/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.matching;

import ij.process.ImageProcessor;

/**
 * Objects of this class calculate an approximate distance transform of a given
 * image which is assumed to be binary (pixel value 0 = background, foreground otherwise).
 * 
 * @author W. Burger
 * @version 2014-04-20
 */
public class DistanceTransform {
	
	public enum Norm {L1, L2}
	private final float[][] D;
	
	// default constructor (using L2 norm)
	public DistanceTransform(ImageProcessor I) {
		 this(I, Norm.L2);
	}
	
	// alternate constructor (L1 or L2 norm)
	public DistanceTransform(ImageProcessor I, Norm norm) {
		D = makeDistanceMap(I, norm);
	}
	
	// -----------------------------------------------------------------
	
	private float[][] makeDistanceMap(ImageProcessor I, Norm norm) {
		float m1, m2;
		switch (norm) {
		case L1:
			m1 = 1;	m2 = 2; break;
		case L2:
			m1 = 1; m2 = (float) Math.sqrt(2); break;
		default:
			throw new IllegalArgumentException();
		}
		
		final int M = I.getWidth();
		final int N = I.getHeight();
		final float[][] D = new float[M][N];
		float d0, d1, d2, d3;

		// L->R pass:
		for (int v = 0; v < N; v++) {
			for (int u = 0; u < M; u++) {
				if (I.get(u, v) > 0) {		// a foreground pixel
					D[u][v] = 0;
				}
				else {						// a background pixel
					d0 = d1 = d2 = d3 = Float.POSITIVE_INFINITY;
					if (u > 0) {
						d0 = m1 + D[u - 1][v];
						if (v > 0) 	{
							d1 = m2 + D[u - 1][v - 1];
						}
					}
					if (v > 0) {
						d2 = m1 + D[u][v - 1];
						if (u < M - 1) {
							d3 = m2 + D[u + 1][v - 1];
						}
					}
					// at this point D[u][v] == POSITIVE_INFINITY
					D[u][v] = min(d0, d1, d2, d3);	
				}
			}
		}
		
		// R->L pass:
		for (int v = N - 1; v >= 0; v--) {
			for (int u = M - 1; u >= 0; u--) {
				if (D[u][v] > 0) { 	// a background pixel
					d0 = d1 = d2 = d3 = Float.POSITIVE_INFINITY;
					if (u < M - 1) 	{
						d0 = m1 + D[u + 1][v];
						if (v < N - 1) {
							d1 = m2 + D[u + 1][v + 1];
						}
					}
					if (v < N - 1) {
						d2 = m1 + D[u][v + 1];
						if (u > 0) {
							d3 = m2 + D[u - 1][v + 1];
						}
					}
					D[u][v] = min(D[u][v], d0, d1, d2, d3);
				}
			}
		}
		return D;
	}
	
	private float min(float... a) {
		float minVal = a[0];
		for (int i = 1; i < a.length; i++) {
			if (a[i] < minVal) 
				minVal = a[i];
		}
		return minVal;
	}
	
	public float[][] getDistanceMap() {
		return D;
	}
	

}
