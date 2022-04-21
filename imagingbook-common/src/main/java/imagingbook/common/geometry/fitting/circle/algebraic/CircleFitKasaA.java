package imagingbook.common.geometry.fitting.circle.algebraic;

import static imagingbook.common.math.Arithmetic.sqr;

import java.util.Arrays;

//import Fitting.org.doube.geometry.FitCircle;
import ij.IJ;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.PntUtils;
import imagingbook.common.geometry.circle.GeometricCircle;
import imagingbook.common.math.Matrix;

/**
 * Original: Kasa, I., A curve fitting procedure and its error analysis, IEEE Trans. Inst. Meas., 25, 8-14, 1976.
 * See https://people.cas.uab.edu/~mosya/cl/CircleFitByKasa.cpp (orig code)
 * http://www.ne.jp/asahi/paleomagnetism.rock-magnetism/basics/pmag/circ/circ1E.html
 * http://www.ne.jp/asahi/paleomagnetism.rock-magnetism/basics/pmag/circ/circ2E.html
 * 
 * @author WB
 *
 */
public class CircleFitKasaA extends CircleFitAlgebraic {

	private final double[] q;	// q = (B,C,D) circle parameters
	
	public CircleFitKasaA(Pnt2d[] points) {
		q = fit(points);
	}
	
	@Override
	public double[] getParameters() {
		return new double[] {1, q[0], q[1], q[2]};
	}
	
	/**
	 * WB's version: Original fails early on shorter arcs, due to numerical problems
	 * 	   in solving the system of linear equations.
	 * Modified to use (exact) SVD solution on scatter matrix, as described 
	 * in Chernov's book (Eq. 5.5).
	 * This version works with non-centered data!
	 * 
	 * ---------------------------------------------------------------------
	 * 
	 * Circle fit to a given set of data points (in 2D)
	 * 
	 * This is an algebraic fit, disovered and rediscovered by many people. One of
	 * the earliest publications is due to Kasa:
	 * 
	 * I. Kasa, "A curve fitting procedure and its error analysis", IEEE Trans.
	 * Inst. Meas., Vol. 25, pages 8-14, (1976)
	 * 
	 * Input: data - the class of data (contains the given points):
	 * 
	 * data.n - the number of data points data.X[] - the array of X-coordinates
	 * data.Y[] - the array of Y-coordinates
	 * 
	 * Output: circle - parameters of the fitting circle:
	 * 
	 * circle.a - the X-coordinate of the center of the fitting circle circle.b -
	 * the Y-coordinate of the center of the fitting circle circle.r - the radius of
	 * the fitting circle circle.s - the root mean square error (the estimate of
	 * sigma) circle.j - the total number of iterations
	 * 
	 * The method is based on the minimization of the function
	 * 
	 * F = sum [(x-a)^2 + (y-b)^2 - R^2]^2
	 * 
	 * This is perhaps the simplest and fastest circle fit.
	 * 
	 * It works well when data points are sampled along an entire circle or a large
	 * part of it (at least half circle).
	 * 
	 * It does not work well when data points are sampled along a small arc of a
	 * circle. In that case the method is heavily biased, it returns circles that
	 * are too often too small.
	 * 
	 * It is the oldest algebraic circle fit (first published in 1972?). For 20-30
	 * years it has been the most popular circle fit, at least until the more robust
	 * Pratt fit (1987) and Taubin fit (1991) were invented.
	 * 
	 * Nikolai Chernov (September 2012)
	 */
	private double[] fit(Pnt2d[] pts) {
		final int n = pts.length;
		if (n < 3) {
			throw new IllegalArgumentException("at least 3 points are required");
		}

		// calculate means and elements of scatter matrix
		double sx = 0, sy = 0, sz = 0;
		double sxy = 0, sxx = 0, syy = 0, sxz = 0, syz = 0;
		for (int i = 0; i < n; i++) {
			final double x = pts[i].getX();
			final double y = pts[i].getY();
			final double x2 = sqr(x);
			final double y2 = sqr(y);
			final double z = x2 + y2;
			sx  += x;
			sy  += y;
			sz  += z;
			sxx += x2;
			syy += y2;
			sxy += x * y;	
			sxz += x * z;
			syz  += y * z;
		}
		
		double[][] M = {
				{sxx, sxy, sx},
				{sxy, syy, sy},
				{ sx,  sy,  n}};
	    
		double[] b = {-sxz, -syz, -sz};	 // RHS vector
		double[] q = Matrix.solve(M, b); // solve M * q = b (exact), for parameter vector q = (B, C, D)
		if (q == null) {
			IJ.log("KasaA: singular!");
			return null;	// no solution
		}
		else {
			return q;
		}
	}
	
	// ---------------------------------------------------------------------------------------
	
	// Problem point set 1
	static double[][] PA = {
			{110, 70}, 
			{113, 70}, 
			{114, 70}, 
			{115, 70}, 
			{117, 70}, 
			{121, 70}, 
			{123, 70}, 
			{124, 70}, 
			{105, 71}, 
			{107, 71}, 
			{108, 71}, 
			{111, 71}, 
			{125, 71}, 
			{127, 71}, 
			{102, 72}, 
			{107, 72}, 
			{109, 72}, 
			{129, 72}, 
			{132, 72}, 
			{99, 73}, 
			{101, 73}, 
			{109, 73}, 
			{132, 73}, 
			{135, 73}, 
			{95, 74}, 
			{97, 74}, 
			{135, 74}, 
			{136, 74}, 
			{137, 74}, 
			{93, 75}, 
			{94, 75}, 
			{95, 75}, 
			{134, 75}, 
			{139, 75}, 
			{91, 76}, 
			{92, 76}, 
			{140, 76}, 
			{141, 76}, 
			{90, 77}, 
			{139, 77}, 
			{142, 77}, 
			{143, 77}, 
			{144, 77}, 
			{90, 78}, 
			{143, 78}, 
			{144, 78}, 
			{145, 78}, 
			{87, 79}, 
			{88, 79}, 
			{146, 79}, 
			{85, 80}, 
			{86, 80}, 
			{146, 80}, 
			{84, 81}, 
			{85, 81}, 
			{148, 81}, 
			{83, 82}, 
			{84, 82}, 
			{149, 82}, 
			{150, 82}, 
			{82, 83}, 
			{83, 83}, 
			{81, 84}, 
			{152, 84}, 
			{80, 85}, 
			{81, 85}, 
			{78, 87}, 
			{154, 87}, 
			{77, 88}, 
			{78, 88}, 
			{155, 88}, 
			{156, 88}, 
			{157, 88}, 
			{76, 89}, 
			{77, 89}, 
			{157, 89}, 
			{76, 90}, 
			{157, 90}, 
			{158, 90}, 
			{158, 91}, 
			{74, 92}, 
			{75, 92}, 
			{159, 92}, 
			{73, 93}, 
			{160, 93}, 
			{160, 95}, 
			{161, 95}, 
			{72, 96}, 
			{161, 96}, 
			{162, 96}, 
			{71, 97}, 
			{70, 98}, 
			{71, 98}, 
			{73, 98}, 
			{163, 99}, 
			{69, 100}, 
			{70, 100}, 
			{68, 101}, 
			{70, 101}, 
			{162, 101}, 
			{164, 101}, 
			{69, 102}, 
			{70, 102}, 
			{164, 102}, 
			{69, 103}, 
			{164, 103}, 
			{165, 103}, 
			{68, 104}, 
			{165, 104}, 
			{68, 106}, 
			{165, 106}, 
			{66, 107}, 
			{67, 107}, 
			{164, 107}, 
			{166, 107}, 
			{165, 108}, 
			{166, 108}, 
			{66, 109}, 
			{67, 109}, 
			{167, 109}, 
			{66, 110}, 
			{67, 110}, 
			{168, 110}, 
			{165, 111}, 
			{167, 111}, 
			{66, 112}, 
			{167, 113}, 
			{167, 114}, 
			{168, 114}, 
			{65, 115}, 
			{168, 115}, 
			{64, 116}, 
			{65, 116}, 
			{168, 116}, 
			{65, 117}, 
			{168, 117}, 
			{65, 118}, 
			{67, 118}, 
			{168, 118}, 
			{65, 119}, 
			{168, 119}, 
			{167, 120}, 
			{65, 122}, 
			{65, 123}, 
			{168, 123}, 
			{65, 124}, 
			{168, 124}, 
			{65, 125}, 
			{67, 125}, 
			{65, 126}, 
			{168, 126}, 
			{65, 127}, 
			{168, 127}, 
			{168, 128}, 
			{65, 129}, 
			{66, 129}, 
			{167, 129}, 
			{168, 129}, 
			{169, 129}, 
			{167, 130}, 
			{169, 131}, 
			{66, 133}, 
			{65, 134}, 
			{66, 134}, 
			{167, 134}, 
			{166, 135}, 
			{167, 135}, 
			{67, 136}, 
			{67, 137}, 
			{165, 137}, 
			{166, 137}, 
			{168, 137}, 
			{165, 138}, 
			{167, 138}, 
			{68, 139}, 
			{68, 140}, 
			{164, 140}, 
			{165, 140}, 
			{69, 141}, 
			{69, 142}, 
			{68, 143}, 
			{70, 143}, 
			{163, 143}, 
			{163, 144}, 
			{71, 145}, 
			{69, 146}, 
			{71, 146}, 
			{162, 146}, 
			{72, 147}, 
			{160, 148}, 
			{72, 150}, 
			{74, 150}, 
			{159, 150}, 
			{160, 150}, 
			{157, 151}, 
			{158, 151}, 
			{159, 151}, 
			{75, 152}, 
			{75, 153}, 
			{157, 153}, 
			{158, 153}, 
			{77, 154}, 
			{156, 154}, 
			{77, 155}, 
			{78, 155}, 
			{155, 155}, 
			{78, 156}, 
			{79, 156}, 
			{154, 156}, 
			{155, 156}, 
			{80, 157}, 
			{153, 157}, 
			{154, 157}, 
			{80, 158}, 
			{152, 158}, 
			{81, 159}, 
			{82, 159}, 
			{152, 159}, 
			{150, 160}, 
			{151, 160}, 
			{83, 161}, 
			{84, 161}, 
			{149, 161}, 
			{84, 162}, 
			{85, 162}, 
			{148, 162}, 
			{149, 162}, 
			{151, 162}, 
			{85, 163}, 
			{86, 163}, 
			{87, 163}, 
			{146, 163}, 
			{148, 163}, 
			{87, 164}, 
			{88, 164}, 
			{145, 164}, 
			{146, 164}, 
			{90, 165}, 
			{143, 165}, 
			{144, 165}, 
			{90, 166}, 
			{140, 166}, 
			{142, 166}, 
			{143, 166}, 
			{140, 167}, 
			{142, 167}, 
			{93, 168}, 
			{94, 168}, 
			{138, 168}, 
			{139, 168}, 
			{140, 168}, 
			{96, 169}, 
			{97, 169}, 
			{98, 169}, 
			{135, 169}, 
			{136, 169}, 
			{137, 169}, 
			{98, 170}, 
			{102, 171}, 
			{129, 171}, 
			{130, 171}, 
			{131, 171}, 
			{132, 171}, 
			{106, 172}, 
			{108, 172}, 
			{109, 172}, 
			{116, 172}, 
			{125, 172}, 
			{126, 172}, 
			{127, 172}, 
			{128, 172}, 
			{129, 172}, 
			{130, 172}, 
			{110, 173}, 
			{111, 173}, 
			{113, 173}, 
			{114, 173}, 
			{116, 173}, 
			{117, 173}, 
			{118, 173}, 
			{119, 173}, 
			{120, 173}, 
			{123, 173}, 
			{105, 174}};

	
	public static void main(String[] args) {
		
		System.out.println("-------------- KASA A (WB) -----------------------------");
		{
			Pnt2d[] pnts = PntUtils.fromDoubleArray(PA);
			CircleFitKasaA fit = new CircleFitKasaA(pnts);
			double[] p = fit.getParameters();
			System.out.println("p = " + Arrays.toString(p));
			GeometricCircle circle = fit.getGeometricCircle();
			System.out.println("circle = " + circle);
		}
		
		
//		System.out.println("-------------- PRATT (SVD Doube) -----------------------------");
//		{
//			double[] p = FitCircle.prattSVD(PA);
//			System.out.println("p = " + Arrays.toString(p));
//		}
	}

}
