/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.geometry.fitting.circle.algebraic;

import static imagingbook.common.math.Arithmetic.sqr;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularValueDecomposition;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.PntUtils;
import imagingbook.common.geometry.circle.GeometricCircle;
import imagingbook.common.geometry.fitting.circle.CircleSampler;
import imagingbook.common.math.Matrix;
import imagingbook.common.math.eigen.EigenvalueDecomposition;
import imagingbook.common.util.SortMap;


/**
 * This is an implementation of the algebraic circle fitting algorithm by Pratt [1],
 * as described in [2] (Sec. 5.5-5.6). The algorithm uses singular-value decomposition
 * (SVD) and eigen-decomposition. See [3, Alg. 11.2] for additional details.
 * <p>
 * Fits to exactly 3 (non-collinear) points are handled properly.
 * Data centering is used to improve numerical stability (alternatively, a reference
 * point can be specified).
 * </p>
 * <p>
 * [1] V. Pratt. "Direct least-squares fitting of algebraic surfaces". <em>ACM
 * SIGGRAPH Computer Graphics</em> <strong>21</strong>(4), 145–152 (July 1987).
 * <br>
 * [2] N. Chernov. "Circular and Linear Regression: Fitting Circles and
 * Lines by Least Squares". Monographs on Statistics and Applied Probability.
 * Taylor &amp; Francis (2011).
 * <br>
 * [3] W. Burger, M.J. Burge, <em>Digital Image Processing - An Algorithmic Approach</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 *
 */
public class CircleFitPratt implements CircleFitAlgebraic {
	
	private static final RealMatrix Ci =	// inverse of constraint matrix C
			MatrixUtils.createRealMatrix(new double[][] { 
				{  0,   0, 0, -0.5 },
				{  0,   1, 0,  0 },
				{  0,   0, 1,  0 },
				{ -0.5, 0, 0,  0 }});
	
	private final double[] q;	// q = (A,B,C,D) circle parameters
	
	@Override
	public double[] getParameters() {
		return this.q;
	}
	
	/**
	 * Constructor.
	 * The centroid of the sample points is used as the reference point.
	 * 
	 * @param points sample points
	 */
	public CircleFitPratt(Pnt2d[] points) {
		this(points, null);
	}
	
	/**
	 * Constructor.
	 * The centroid of the sample points is used as the reference point for data
	 * centering if {@code null} is passed for {@code xref}.
	 * 
	 * @param points sample points
	 * @param xref reference point or {@code null}
	 */
	public CircleFitPratt(Pnt2d[] points, Pnt2d xref) {
		this.q = fit(points, xref);
	}

	private double[] fit(Pnt2d[] pts, Pnt2d xref) {
		final int n = pts.length;
		if (n < 3) {
			throw new IllegalArgumentException("at least 3 points are required");
		}
		
		if (xref == null) {
			xref = PntUtils.centroid(pts);
		}
		final double xr = xref.getX();
		final double yr = xref.getY();

		double[][] Xa = new double[Math.max(n, 4)][4];	// Xa must have at least 4 rows!
		for (int i = 0; i < pts.length; i++) {
			double x = pts[i].getX() - xr;		// = x_i
			double y = pts[i].getY() - yr;		// = y_i
			Xa[i][0] = sqr(x) + sqr(y);			// = z_i
			Xa[i][1] = x;
			Xa[i][2] = y;
			Xa[i][3] = 1;
		}
		// if nPoints = 3 (special case) the last row of the
		// 4x4 matrix contains all zeros (make X singular)!

		RealMatrix X = MatrixUtils.createRealMatrix(Xa);

		SingularValueDecomposition svd = new SingularValueDecomposition(X);
		RealMatrix S = svd.getS();	
		RealMatrix V = svd.getV();
		double[] svals = svd.getSingularValues(); 	// note: singular values are all positive (>= 0)
		
		int k = Matrix.idxMin(svals);
		double smin = svals[k];
		double smax = Matrix.max(svals); 
		
		RealVector qq = null;		// = \dot{q} solution vector (algebraic circle parameters)

		double icond = smin / smax;
		if (icond < 1e-12) { 			// smin/smax = inverse condition number of X, 1e-12
			// singular case (X is rank deficient)
			qq = V.getColumnVector(k);
		} else {
			// regular (non-singular) case
		
			// Version1 (seems to create smaller roundoff errors, better matrix symmetry):
			RealMatrix Y = V.multiply(S).multiply(V.transpose());
			RealMatrix Z = Y.multiply(Ci).multiply(Y); // = Y * Ci * Y
			
			// Version2:
//			RealMatrix Y = V.multiply(S);
//			RealMatrix Z = Y.transpose().multiply(Ci).multiply(Y); // = Y^T * Ci * Y
			
			EigenvalueDecomposition ed = new EigenvalueDecomposition(Z); 
			double[] evals = ed.getRealEigenvalues();
			int l = new SortMap(evals).getIndex(1);	// index of the 2nd-smallest eigenvalue			
			RealVector el = ed.getEigenvector(l);
			
			// Version1 ---------------------------------------------------
//			qq = Matrix.solve(S.multiply(svd.getVT()), el);		// solve S * V^T * p = el
			
			// Version2 ---------------------------------------------------
			qq = V.operate(MatrixUtils.inverse(S).operate(el));	// simpler since S is diagonal (i.e., easy to invert)
		}

		RealMatrix M = CircleFitAlgebraic.getDecenteringMatrix(xr, yr);
		return M.operate(qq).toArray();  // q = (A,B,C,D)
	}

	
	// -------------------------------------------------------------------------------------
	
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
		
//		Pnt2d[] points = PntUtils.fromDoubleArray(ProblemPointSet1);
//		CircleFitPratt fit = new CircleFitPratt(points);
//		System.out.println("circle = " + fit.getGeometricCircle());
		
		double xc = 200;
		double yc = 190;
		double rad = 150;	
		long seed = 17;
		
		GeometricCircle realCircle = new GeometricCircle(xc, yc, rad);
		CircleSampler sampler = new CircleSampler(realCircle, seed);
		
		runTest2(FitType.Pratt, sampler, realCircle);
		
	}
	
	private static void runTest2(FitType type, CircleSampler sampler, GeometricCircle realCircle) {
		int n = 50;
		double angle0 = 0;
		double angle1 = Math.PI;
		double sigma = 2.0;
		
		for (int i = 0; i < 100; i++) {
			Pnt2d[] pnts = sampler.getPoints(n, angle0, angle1, sigma);
			CircleFitPratt fit = new CircleFitPratt(pnts);
			GeometricCircle fitCircle = fit.getGeometricCircle();
			System.out.println(i + " " + type + " = " + fitCircle + " " + realCircle.equals(fitCircle, 4.0, 4.0, 4.0));
//			assertNotNull("geometric circle is null: " + type, fitCircle);
//			System.out.println(type + ": " + fitCircle);
//			assertTrue("failed fit-type: " + type, realCircle.equals(fitCircle, 4.0, 4.0, 4.0));
		}
		
	}
		
}