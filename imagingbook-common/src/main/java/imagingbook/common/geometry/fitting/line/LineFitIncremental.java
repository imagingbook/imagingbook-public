/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.geometry.fitting.line;

import static imagingbook.common.math.Arithmetic.sqr;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;

import org.apache.commons.math3.linear.MatrixUtils;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.PntUtils;
import imagingbook.common.geometry.line.AlgebraicLine;
import imagingbook.common.math.Matrix;
import imagingbook.common.math.PrintPrecision;
import imagingbook.common.math.eigen.EigenDecompositionJama;
import imagingbook.common.util.SortMap;

public class LineFitIncremental implements LineFit {
	
	public static boolean VERBOSE = false;
	
	private double[] p = null;	// line parameters A,B,C
	
	private Deque<Pnt2d> points;
	private double Sx = 0, Sy = 0, Sxx = 0, Syy = 0, Sxy = 0;
	
	public LineFitIncremental() {
		this(null);
	}
	
	public LineFitIncremental(Collection<Pnt2d> initPnts) {
//		if (points.length < 2) {
//			throw new IllegalArgumentException("line fit requires at least 2 points");
//		}
		points = new ArrayDeque<>();
		if (initPnts != null) {
			points.addAll(initPnts);
		}
		init();
	}
	
	// --------------------------------------------------------
	
	private void init() {
		Sx = 0; Sy = 0; Sxx = 0; Syy = 0; Sxy = 0;
		for (Pnt2d p : points) {
			addPoint(p);
		}
	}
	
	private void addPoint(Pnt2d pnt) {
		final double x = pnt.getX();
		final double y = pnt.getY();
		Sx += x;
		Sy += y;
		Sxx += sqr(x);
		Syy += sqr(y);
		Sxy += x * y;
		this.p = null;
	}
	
	private void removePoint(Pnt2d pnt) {
		final double x = pnt.getX();
		final double y = pnt.getY();
		Sx -= x;
		Sy -= y;
		Sxx -= sqr(x);
		Syy -= sqr(y);
		Sxy -= x * y;
		this.p = null;
	}
	
	// --------------------------------------------------------
	
	public Pnt2d[] getPoints() {
		return points.toArray(new Pnt2d[0]);
	}
	
	public void add(Pnt2d pnt) {
		addLast(pnt);
	}
	
	public void addFirst(Pnt2d pnt) {
		points.addFirst(pnt);
		addPoint(pnt);
	}
	
	public void addLast(Pnt2d pnt) {
		points.addLast(pnt);
		addPoint(pnt);
	}
	
	public Pnt2d removeFirst() {
		Pnt2d pnt = points.removeFirst();
		removePoint(pnt);
		return pnt;
	}
	
	public Pnt2d removeLast() {
		Pnt2d pnt = points.removeLast();
		removePoint(pnt);
		return pnt;
	}
	
	public Pnt2d getFirst() {
		return points.getFirst();
	}
	
	public Pnt2d getLast() {
		return points.getLast();
	}
	
	// --------------------------------------------------------
	
	public int getSize() {
		return points.size();
	}
	
	@Override
	public double[] getLineParameters() {
		if (this.getSize() < 2) {
			throw new IllegalStateException("cannot fit line, set of point set is less than 2");
		}
		if (p == null) {
			fit(points.toArray(new Pnt2d[0]));
		}
		return p;
	}
	
	private void fit(Pnt2d[] pts) {
		final int n = pts.length;
		//System.out.println("** fitting " + n);
		
		double sxx = Sxx - sqr(Sx) / n;
		double syy = Syy - sqr(Sy) / n;
		double sxy = Sxy - Sx * Sy / n;
		
		double[][] S = {
				{sxx, sxy},
				{sxy, syy} 
		};
		
//		RealEigensolver es = new EigensolverNxN(S);
//		double[] e = es.getEigenvector(1);
		EigenDecompositionJama es = new EigenDecompositionJama(MatrixUtils.createRealMatrix(S));
		int k = SortMap.getNthSmallestIndex(es.getRealEigenvalues(), 0);
		double[] e = es.getEigenvector(k).toArray();
		
		double A = e[0];
		double B = e[1];
		double C = -(A * Sx + B * Sy) / n;
		
		if (VERBOSE) {
			System.out.println("Sx = " + Sx);
			System.out.println("Sy = " + Sy);
			System.out.println("xc = " + PntUtils.centroid(pts));
			System.out.println("S = \n" + Matrix.toString(S));
//			System.out.println("eVal = " + Matrix.toString(es.getEigenvalues()));
//			System.out.println("eVec = \n" + Matrix.toString(es.getEigenvectors()));
//			System.out.println("eVal = " + Matrix.toString(es.getRealEigenvalues()));
//			System.out.println("eVec = \n" + Matrix.toString(es.getV()));
			System.out.println("A = \n" + A);
			System.out.println("B = \n" + B);
			System.out.println("C = \n" + C);
		}
		this.p = new double[] {A, B, C};
	}
	
	// -------------------------------------------------------------------
	
//	static double[][] X = {{ 10, 6 }, { 4, 3 }, { 18, 2 }, { 7, 1 }, { 5, 6 }};
//	static double[][] X = {{ 10, 6 }, { 4, 3 }};
//	static double[][] X = {{ 1, 1 }, { 3, 3 },  { 13, 13 }};
	static double[][] X = {{1, 8}, {4, 5}, {4, 7}, {6, 4}, {9, 4}}; // book example
	
	public static void main(String[] args) {
		PrintPrecision.set(4);
		Pnt2d[] pts = PntUtils.fromDoubleArray(X);
		
//		LineFitIncremental fit = new LineFitIncremental(Arrays.asList(pts));
		
		LineFitIncremental fit = new LineFitIncremental();
		System.out.println("Adding points -------------------------- ");
		fit.add(pts[0]);
		for (int i=1; i < X.length; i++) {
			fit.add(pts[i]);
			System.out.println("size = " + fit.getSize());
			AlgebraicLine line = fit.getLine();
			System.out.println("  line = " +  line);
			System.out.println("  mean square error = " + fit.getSquaredOrthogonalError(fit.getPoints()) / X.length);
		}
		
		System.out.println("Removing points -------------------------- ");
		for (int i=2; i < X.length; i++) {
			fit.removeLast();
			System.out.println("size = " + fit.getSize());
			AlgebraicLine line = fit.getLine();
			System.out.println("  line = " +  line);
			System.out.println("  mean square error = " + fit.getSquaredOrthogonalError(fit.getPoints()) / X.length);
		}
			
		
		
	}
	
//	line = {-0.4969, -0.8678, 7.2448}
//	square error = 2.6645834350486606
//	mean square error = 0.5329166870097322

/*
 Adding points -------------------------- 
size = 2
  line = AlgebraicLine <a=0.707, b=0.707, c=-6.364>
  mean square error = 1.5777218104420237E-31
size = 3
  line = AlgebraicLine <a=0.646, b=0.763, c=-7.026>
  mean square error = 0.25563166262690407
size = 4
  line = AlgebraicLine <a=0.657, b=0.754, c=-6.987>
  mean square error = 0.2561822766777585
size = 5
  line = AlgebraicLine <a=0.497, b=0.868, c=-7.245>
  mean square error = 0.5329166870097322
Removing points -------------------------- 
size = 4
  line = AlgebraicLine <a=0.657, b=0.754, c=-6.987>
  mean square error = 0.2561822766777585
size = 3
  line = AlgebraicLine <a=0.646, b=0.763, c=-7.026>
  mean square error = 0.25563166262690407
size = 2
  line = AlgebraicLine <a=0.707, b=0.707, c=-6.364>
  mean square error = 1.5777218104420237E-31
 */
	
}
