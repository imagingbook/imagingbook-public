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

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.PntUtils;
import imagingbook.common.geometry.line.AlgebraicLine;
import imagingbook.common.geometry.line.SlopeInterceptLine;
import imagingbook.common.math.Matrix;
import imagingbook.common.math.PrintPrecision;

public class LinearRegressionFit implements LineFit {
	
	public static boolean VERBOSE = false;
	
//	private final Pnt2d[] points;
	private final int n;
	private final double[] p;	// line parameters A,B,C
	private double k, d;
	
	public LinearRegressionFit(Pnt2d[] points) {
		if (points.length < 2) {
			throw new IllegalArgumentException("line fit requires at least 2 points");
		}
//		this.points = points;
		this.n = points.length;
		this.p = fit(points);
	}
	
	@Override
	public int getSize() {
//		return points.length;
		return n;
	}

//	@Override
//	public Pnt2d[] getPoints() {
//		return points;
//	}

	@Override
	public double[] getLineParameters() {
		return p;
	}
	
	public double getK() {
		return k;
	}
	
	public double getD() {
		return d;
	}
	
	
	private double[] fit(Pnt2d[] points) {
		final int n = points.length;
	
		double Sx = 0, Sy = 0, Sxx = 0, Sxy = 0;
		
		for (int i = 0; i < n; i++) {
			final double x = points[i].getX();
			final double y = points[i].getY();
			Sx += x;
			Sy += y;
			Sxx += sqr(x);
			Sxy += x * y;
		}
			
		double den = sqr(Sx) - n * Sxx;
		this.k = (Sx * Sy - n * Sxy) / den;
		this.d = (Sx * Sxy - Sxx * Sy) / den;
		
		if (VERBOSE) {
			System.out.println("Sx = " + Sx);
			System.out.println("Sy = " + Sy);
			System.out.println("Sxx = " + Sxx);
			System.out.println("Sxy = " + Sxy);
			System.out.println("k = " + k);
			System.out.println("d = " + d);
		}
	
		AlgebraicLine line = AlgebraicLine.from(new SlopeInterceptLine(k, d));
		return line.getParameters();
	}


	/**
	 * Calculates and returns the sum of the squared differences between
	 * the y-coordinates of the data points (xi, yi) and the associated y-value
	 * of the regression line (y = k x + d).
	 * 
	 * @param points an array of 2D points
	 * @return the squared regression error
	 */
	public double getSquaredRegressionError(Pnt2d[] points) {
		double s2 = 0;
		for (Pnt2d p : points) {
			double y = k * p.getX() + d;
			s2 = s2 + sqr(y - p.getY());
		}
		return s2;
	}
	
	// -------------------------------------------------------------------
	
//	static double[][] X = {{ 10, 6 }, { 4, 3 }, { 18, 2 }, { 7, 1 }, { 5, 6 }};
//	static double[][] X = {{ 10, 6 }, { 4, 3 }};
//	static double[][] X = {{ 1, 1 }, { 3, 3 },  { 13, 13 }};
	static double[][] X = {{1, 8}, {4, 5}, {4, 7}, {6, 4}, {9, 4}}; // book example
	
	
	public static void main(String[] args) {
		VERBOSE = true;
		PrintPrecision.set(6);
		Pnt2d[] pts = PntUtils.fromDoubleArray(X);	
		LinearRegressionFit fit = new LinearRegressionFit(pts);
		AlgebraicLine line = fit.getLine();
		System.out.println("k = " + fit.getK());
		System.out.println("d = " + fit.getD());
		System.out.println("line = " + Matrix.toString(line.getParameters()));
		System.out.println("regression error = " + fit.getSquaredRegressionError(pts));
		System.out.println("orthogonal error = " + fit.getSquaredOrthogonalError(pts));
	}


//	Sx = 24.0
//	Sy = 28.0
//	Sxx = 150.0
//	Sxy = 116.0
//	k = -0.5287356321839081
//	d = 8.137931034482758
//	line = {0.467421, 0.884035, -7.194216}
//	regression error = 3.471264367816092
//	orthogonal error = 2.7128549303045952

}
