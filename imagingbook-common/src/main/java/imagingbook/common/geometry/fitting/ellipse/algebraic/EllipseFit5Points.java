package imagingbook.common.geometry.fitting.ellipse.algebraic;

import static imagingbook.common.math.Arithmetic.sqr;

import java.util.Random;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularMatrixException;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.math.PrintPrecision;

/**
 * 
 * @author WB
 *
 */
public class EllipseFit5Points implements EllipseFitAlgebraic {
	
	private final double[] p;	// p = (A,B,C,D,E,F) ellipse parameters
	
	public EllipseFit5Points(Pnt2d[] points) {
		this.p = fit(points);
	}

	@Override
	public double[] getParameters() {
		return this.p;
	}
	
	private double[] fit(Pnt2d[] points) {
		if (points.length < 5) {
			throw new IllegalArgumentException("5 points are needed for " + this.getClass().getSimpleName());
		}
		final int n = points.length;

		// create design matrix X:
		RealMatrix X = new Array2DRowRealMatrix(n, 5);
		RealVector b = new ArrayRealVector(n);
		for (int i = 0; i < n; i++) {
			double x = points[i].getX();
			double y = points[i].getY();
			X.setEntry(i, 0, sqr(x) - sqr(y));
			X.setEntry(i, 1, x * y);
			X.setEntry(i, 2, x);
			X.setEntry(i, 3, y);
			X.setEntry(i, 4, 1);
			b.setEntry(i, - sqr(y));
		}
		
//		System.out.println("X = \n" + Matrix.toString(X));
//		System.out.println("b = " + Matrix.toString(b));
		
		LUDecomposition decomp = new LUDecomposition(X); 
		// see https://commons.apache.org/proper/commons-math/userguide/linear.html
		 
		DecompositionSolver solver = decomp.getSolver();
		RealVector x = null;
		try {
			x = solver.solve(b);
		} catch(SingularMatrixException e) { }

		if (x == null) {
			return null;
		}
		
		double A = x.getEntry(0);
		double B = x.getEntry(1);
		double C = 1 - A;
		double D = x.getEntry(2);
		double E = x.getEntry(3);
		double F = x.getEntry(4);
		
		
		boolean isEllipse = (4 * A * C - sqr(B)) > 0;
		
		if (isEllipse) {
			double[] p = {A,B,C,D,E,F};
			return p;
		}
		else {
			return null;
		}
		
		/*
		double[] p = {A,B,C,D,E,F};
		System.out.println("p = " + Matrix.toString(p));
		
		
		
		if (isEllipse) {	
			AlgebraicEllipse ea = new AlgebraicEllipse(p);
			Ellipse eg = Ellipse.from(ea);
			System.out.println("P1: ellipse = " + eg);
		}
		else {
			System.out.println("P1: NO ellipse!");
		}
		
		EllipseFitAlgebraic fit2 = new EllipseFitFitzgibbonStable(points);
		double[] p2 = fit2.getParameters();
		if (p2 == null) {
			System.out.println("P2: NULL ellipse!");
		}
		else {
			boolean isEllipse2 = (4 * p2[0] * p2[2] - sqr(p2[1])) > 0;
			System.out.println("P2: is ellipse = " +isEllipse2);
			if (isEllipse2) {
				Ellipse ell2 = Ellipse.from(fit2.getEllipse());
				System.out.println("P2: error = " + ell2.getError(points));
			}
			
		}
		System.out.println();
		return p;
		*/
	}
	
	public static void main(String[] args) {
		PrintPrecision.set(9);
		Pnt2d p0 = Pnt2d.from(40, 53);
		Pnt2d p1 = Pnt2d.from(107, 20);
		Pnt2d p2 = Pnt2d.from(170, 26);
		Pnt2d p3 = Pnt2d.from(186, 55);
		Pnt2d p4 = Pnt2d.from(135, 103);
		
		Pnt2d[] points = {p0, p1, p2, p3, p4};
		EllipseFit5Points fit = new EllipseFit5Points(points);
		System.out.println("fit ellipse = " + fit.getEllipse());
		
		Random rg = new Random();
		
		int N = 1000;
		int nullCnt = 0;
		for (int k = 0; k < N; k++) {
			for (int i = 0; i < points.length; i++) {
				double x = rg.nextInt(200);
				double y = rg.nextInt(200);
				points[i] = Pnt2d.from(x, y);
			}
			fit = new EllipseFit5Points(points);
			if (fit.getEllipse() == null) {
				nullCnt++;
			}
//			System.out.println("fit ellipse = " + fit.getEllipse());
			
		}
		
		System.out.println("null count: " + nullCnt);
	}
}
