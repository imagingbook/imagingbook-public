/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.geometry.fitting.ellipse.algebraic;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.math.Matrix;
import imagingbook.common.math.PrintPrecision;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularMatrixException;

import java.util.Random;

import static imagingbook.common.math.Arithmetic.sqr;

/**
 * Performs an exact ellipse fit to 5 given points. If the fit is unsuccessful, {@link #getParameters()} returns
 * {@code null}. The underlying algorithm is described in Section F.3.3 of [1].
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2022/11/17
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
		
		return (isEllipse) ? new double[] {A,B,C,D,E,F} : null;
	}
	
	// --------------------------------------------------------------------------------------------
	
	public static void main(String[] args) {
		PrintPrecision.set(9);
		Pnt2d p0 = Pnt2d.from(40, 53);
		Pnt2d p1 = Pnt2d.from(107, 20);
		Pnt2d p2 = Pnt2d.from(170, 26);
		Pnt2d p3 = Pnt2d.from(186, 55);
		Pnt2d p4 = Pnt2d.from(135, 103);
		
		Pnt2d[] points = {p0, p1, p2, p3, p4};
		EllipseFit5Points fit = new EllipseFit5Points(points);
		System.out.println("fit parameters = " + Matrix.toString(fit.getParameters()));
		System.out.println("fit ellipse = " + fit.getEllipse());
		System.out.println("fit ellipse = " +  Matrix.toString(fit.getEllipse().getParameters()));
		
		// create random 5-point sets and try to fit ellipses, counting null results:
		Random rg = new Random(17);
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
		
		System.out.println(nullCnt + " null results out of " + N);
		
		// fit ellipse = {0.317325319, 0.332954818, 0.875173557, -89.442594143, -150.574265066, 7886.192568730}
	}
}
