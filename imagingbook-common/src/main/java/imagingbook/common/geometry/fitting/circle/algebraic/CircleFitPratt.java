package imagingbook.common.geometry.fitting.circle.algebraic;

import static imagingbook.common.math.Arithmetic.sqr;

import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularValueDecomposition;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.PntUtils;
import imagingbook.common.math.Matrix;


/**
 *
 */
public class CircleFitPratt extends CircleFitAlgebraic {
	
	private static final RealMatrix Bi =	// inverse of constraint matrix B
			MatrixUtils.createRealMatrix(new double[][] { 
				{  0,   0, 0, -0.5 },
				{  0,   1, 0,  0 },
				{  0,   0, 1,  0 },
				{ -0.5, 0, 0,  0 }});
	
	private final double[] q;	// p = (A,B,C,D) circle parameters
	
	public CircleFitPratt(Pnt2d[] points) {
		this.q = fit(points);
	}
	
//	@Override
//	public GeometricCircle getGeometricCircle() {
//		if (isZero(p[0])) {			// (abs(2 * A / s) < (1 / Rmax))
//			return null;			// return a straight line
//		}
//		else {
//			return GeometricCircle.from(new AlgebraicCircle(p));
//		}
//	}

	// enforce constraint B^2 + C^2 - 4 A D = 1 :
	@SuppressWarnings("unused")
	private double[] normalizeP(double[] q) {
		final double A = q[0];
		final double B = q[1];
		final double C = q[2];
		final double D = q[3];
		double s  = Math.sqrt(sqr(B) + sqr(C) - 4 * A * D);
		return new double[] {A/s, B/s, C/s, D/s};
	}
	

	private double[] fit(Pnt2d[] pts) {	// Version in Chernov (Table 5.1) - this version also works for the HyperFitter!
//		IJ.log(this.getClass().getSimpleName() + " -- Version B");
		final int nPoints = pts.length;
		if (nPoints < 3) {
			throw new IllegalArgumentException("at least 3 points are required");
		}
		
//		IJ.log("n = " + nPoints);
		Pnt2d ctr = PntUtils.centroid(pts);
		final double xr = ctr.getX();
		final double yr = ctr.getY();

		double[][] Xa = new double[Math.max(nPoints, 4)][4];	// Xa must have at least 4 rows!
		for (int i = 0; i < pts.length; i++) {
			double x = pts[i].getX() - xr;
			double y = pts[i].getY() - yr;
			Xa[i][0] = sqr(x) + sqr(y);
			Xa[i][1] = x;
			Xa[i][2] = y;
			Xa[i][3] = 1;
		}
		// if nPoints = 3 (special case) the last row of the
		// 4x4 matrix contains all zeros to make X singular!

		RealMatrix X = MatrixUtils.createRealMatrix(Xa);
//		IJ.log("X = \n" + Matrix.toString(X.getData()));

		SingularValueDecomposition svd = new SingularValueDecomposition(X);
		RealMatrix S = svd.getS();	
		RealMatrix V = svd.getV();
		double[] svals = svd.getSingularValues(); 	// note: singular values are all positive (or zero)
		
		int minIdx = Matrix.idxMin(svals);
		double smin = svals[minIdx];
		int maxIdx = Matrix.idxMax(svals);
		double smax = svals[maxIdx];
		
		RealVector qq = null;		// = \dot{q} solution vector (algebraic circle parameters)

		double icond = smin / smax;
//		IJ.log("Pratt fitter: icond = " + icond);
		
		if (icond < 1e-12) { 			// smin/smax = inverse condition number of X, 1e-12
			// singular case (X is rank deficient)
			qq = V.getColumnVector(minIdx);
		} else {
			// regular (non-singular) case
		
			// Version1 ---------------------------------------------------
//			RealMatrix Y = V.multiply(S).multiply(V.transpose());
//			RealMatrix Z = Y.multiply(Bi).multiply(Y); // = Y * Bi * Y
			// Version2 ---------------------------------------------------
			RealMatrix Y = V.multiply(S);
			RealMatrix Z = Y.transpose().multiply(Bi).multiply(Y); // = Y^T * Bi * Y
			// ------------------------------------------------------------

			EigenDecomposition ed = new EigenDecomposition(Z);
			
			double[] evals = ed.getRealEigenvalues();
			int k = getSmallestPositiveIdx(evals);

//			IJ.log("l = " + l);
			RealVector ek = ed.getEigenvector(k);
			
			// Version1 ---------------------------------------------------
//			qq = Matrix.solve(S.multiply(svd.getVT()), ek);		// solve S * V^T * p = ek
			// Version2 ---------------------------------------------------
			qq = V.operate(MatrixUtils.inverse(S).operate(ek));	// simpler since S is diagonal
			// ------------------------------------------------------------
		}
		
		double[][] M = 
			{{ 1, 0, 0, 0 },
			 {-2*xr, 1, 0, 0 },
			 {-2*yr, 0, 1, 0 },
			 {sqr(xr) + sqr(yr), -xr, -yr, 1}};
		
		RealMatrix MM = MatrixUtils.createRealMatrix(M);
		
		double[] q = MM.operate(qq).toArray();
		return q;
	}
	
	private int getSmallestPositiveIdx(double[] x) {
		double minval = Double.POSITIVE_INFINITY;
		int minidx = -1;
		for (int i = 0; i < x.length; i++) {
			// ignore complex eigenvalues (x[i] == NaN)
			if (Double.isFinite(x[i]) && x[i] >= 0 && x[i] < minval) {
				minval = x[i];
				minidx = i;
			}
		}
		return minidx;
	}

	public double[] getParameters() {
		return this.q;
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

	
//	public static void main(String[] args) {
//		
//		System.out.println("-------------- PRATT (WB) -----------------------------");
//		{
//			Pnt2d[] pnts = PntUtils.fromDoubleArray(PA);
//			CircleFitPratt fit = new CircleFitPratt(pnts);
//			double[] p = fit.getParameters();
//			System.out.println("p = " + Arrays.toString(p));
//			
//			GeometricCircle circle = fit.getGeometricCircle();
//			System.out.println("circle = " + circle);
//		}
//		
//	}
		
}
