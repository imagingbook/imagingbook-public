/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.geometry.fitting.line;

import static imagingbook.common.math.Arithmetic.sqr;

import org.apache.commons.math3.linear.MatrixUtils;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.PntUtils;
import imagingbook.common.geometry.line.AlgebraicLine;
import imagingbook.common.math.Matrix;
import imagingbook.common.math.PrintPrecision;
import imagingbook.common.math.eigen.EigenDecompositionJama;
import imagingbook.common.util.SortMap;

public class OrthogonalLineFitEigen implements LineFit {
	
	public static boolean VERBOSE = false;
	
	private final int n;
	private final double[] p;	// line parameters A,B,C
	
	public OrthogonalLineFitEigen(Pnt2d[] points) {
		if (points.length < 2) {
			throw new IllegalArgumentException("line fit requires at least 2 points");
		}
		this.n = points.length;
		this.p = fit(points);
	}
	
	@Override
	public int getSize() {
		return n;
	}

	@Override
	public double[] getLineParameters() {
		return p;
	}
	
	private double[] fit(Pnt2d[] points) {
		final int n = points.length;
	
		double Sx = 0, Sy = 0, Sxx = 0, Syy = 0, Sxy = 0;
		
		for (int i = 0; i < n; i++) {
			final double x = points[i].getX();
			final double y = points[i].getY();
			Sx += x;
			Sy += y;
			Sxx += sqr(x);
			Syy += sqr(y);
			Sxy += x * y;
		}
		
		double sxx = Sxx - sqr(Sx) / n;
		double syy = Syy - sqr(Sy) / n;
		double sxy = Sxy - Sx * Sy / n;
		
		double[][] S = {
				{sxx, sxy},
				{sxy, syy} 
		};
		
		
//		RealEigensolver es = new EigensolverNxN(S);
		EigenDecompositionJama es = new EigenDecompositionJama(MatrixUtils.createRealMatrix(S));
//		double[] e = es.getEigenvector(1);
		int k = SortMap.getNthSmallestIndex(es.getRealEigenvalues(), 0);
		double[] e = es.getEigenvector(k).toArray();
		
		double A = e[0];
		double B = e[1];
		double C = -(A * Sx + B * Sy) / n;
		
		if (VERBOSE) {
			System.out.println("Sx = " + Sx);
			System.out.println("Sy = " + Sy);
			System.out.println("xc = " + PntUtils.centroid(points));
			System.out.println("S = \n" + Matrix.toString(S));
//			System.out.println("eVal = " + Matrix.toString(es.getEigenvalues()));
//			System.out.println("eVec = \n" + Matrix.toString(es.getEigenvectors()));
			System.out.println("eVal = " + Matrix.toString(es.getRealEigenvalues()));
			System.out.println("eVec = \n" + Matrix.toString(es.getV()));
			System.out.println("A = \n" + A);
			System.out.println("B = \n" + B);
			System.out.println("C = \n" + C);
		}
		return new double[] {A, B, C};
	}

	// -------------------------------------------------------------------
	
//	static double[][] X = {{ 10, 6 }, { 4, 3 }, { 18, 2 }, { 7, 1 }, { 5, 6 }};
//	static double[][] X = {{ 10, 6 }, { 4, 3 }};
//	static double[][] X = {{ 1, 1 }, { 3, 3 },  { 13, 13 }};
	static double[][] X = {{1, 8}, {4, 5}, {4, 7}, {6, 4}, {9, 4}}; // book example
//	static double[][] X = {{1, 8}, {4, 5}};
	
	public static void main(String[] args) {
		VERBOSE = true;
		PrintPrecision.set(4);
		Pnt2d[] pts = PntUtils.fromDoubleArray(X);	
		LineFit fit = new OrthogonalLineFitEigen(pts);
		AlgebraicLine line = fit.getLine();
		System.out.println("line = " +  Matrix.toString(line.getParameters()));
		System.out.println("mean square error = " + fit.getSquaredOrthogonalError(pts) / X.length);
	}

	// class Fitting_Lines.lib.OrthogonalLineFitEigen
	// line = {-0.4969, -0.8678, 7.2448}
	// square error = 2.6645834350486606
	// mean square error = 0.5329166870097322
}

/*
Sx = 24.0
Sy = 28.0
xc = PntDouble[4.800, 5.600]
S = 
{{34.8000, -18.4000}, 
{-18.4000, 13.2000}}
eVal = {2.6646, 45.3354}
eVec = 
{{-0.4969, 0.8678}, 
{-0.8678, -0.4969}}
A = 
-0.4968900437902618
B = 
-0.8678135078357052
C = 
7.244827854073206
line = {0.4969, 0.8678, -7.2448}
mean square error = 0.5329166870097322
*/