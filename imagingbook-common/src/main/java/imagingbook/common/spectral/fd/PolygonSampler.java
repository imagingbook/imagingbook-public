/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.spectral.fd;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.Pnt2d.PntDouble;

/**
 * @version 2020/04/01
 * @author WB
 *
 */
public class PolygonSampler {
	
	public PolygonSampler() {
	}
	
	/**
	 * Samples the closed polygon path specified by V at M
	 * equi-distant positions. 
	 * @param V the vertices of the (closed) polygon.
	 * @param M the number of sample points.
	 * @return the sample points as an array of Point objects.
	 */
	public Pnt2d[] samplePolygonUniformly(Pnt2d[] V, int M) {
		int N = V.length;
		double Delta = pathLength(V) / M;	// constant segment length in Q
		// distribute N points along polygon path P
		Pnt2d[] S = new Pnt2d[M];
//		S[0] = (Point) V[0].clone();	// q_0 = p_0 (duplicate p_0)
		S[0] = PntDouble.from(V[0]);		// q_0 = p_0 (duplicate p_0)
		int i = 0;			// lower index of segment (i,i+1) in P
		int j = 1;			// index of next point to be added to Q
		double alpha = 0;	// lower boundary of current path segment in P
		double beta = Delta;	// path position of next point to be added to Q
		// for all M segments in P do:
		while (i < N && j < M) {
			Pnt2d vA = V[i];
			Pnt2d vB = V[(i + 1) % N];
			double delta = vA.distance(vB);
			// handle segment (i,i+1) with path boundaries (a,a+d), knowing a < b
			while (beta <= alpha + delta && j < M) {
				// a < b <= a+d
				S[j] = interpolate(vA, vB, (beta - alpha) / delta);
				j = j + 1;
				beta = beta + Delta;
			}
			alpha = alpha + delta;
			i = i + 1;
		}	
		return S;
	}
	
	/**
	 * This is for testing: allows to choose an arbitrary start point by
	 * setting 'startFrac' in [0,1].
	 * @param V the vertices of the (closed) polygon.
	 * @param M the number of sample points.
	 * @param startFrac the position of the first sample as a fraction of the 
	 * polggon's circumference in [0,1].
	 * @return the sample points as an array of Point objects.
	 */
	public Pnt2d[] samplePolygonUniformly(Pnt2d[] V, int M, double startFrac) {
		int startPos = (int) Math.round(V.length * startFrac) % V.length;
		return samplePolygonUniformly(shiftLeft(V, startPos), M);
	}
	
	private Pnt2d[] shiftLeft(Pnt2d[] V, int startPos) {
		int polyLen = V.length;
		Pnt2d[] P2 = new Pnt2d[polyLen]; 
		for (int i = 0; i < P2.length; i++) {
//			P2[i] = (Point) V[(i + startPos) % polyLen].clone();
			P2[i] = PntDouble.from(V[(i + startPos) % polyLen]);
		}
		return P2;
	}
	
	
	protected double pathLength(Pnt2d[] V) {
		double L = 0;
		final int N = V.length;
		for (int i = 0; i < N; i++) {
			Pnt2d p0 = V[i];
			Pnt2d p1 = V[(i + 1) % N];
			L = L + p0.distance(p1);
		}
		return L;
	}
	
	protected Pnt2d interpolate(Pnt2d p0, Pnt2d p1, double alpha) {
		// alpha is in [0,1]
		double x = p0.getX() + alpha * (p1.getX() - p0.getX());
		double y = p0.getY() + alpha * (p1.getY() - p0.getY());
		return Pnt2d.PntDouble.from(x, y);
	}
	
}
