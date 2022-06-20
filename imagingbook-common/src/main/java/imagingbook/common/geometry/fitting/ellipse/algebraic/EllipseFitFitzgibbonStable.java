/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.geometry.fitting.ellipse.algebraic;

import static imagingbook.common.math.Arithmetic.sqr;

import java.util.Arrays;

import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import ij.IJ;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.PntUtils;
import imagingbook.common.math.EigenvalueDecomposition;
import imagingbook.common.math.Matrix;

/**
 * <p>
 * Algebraic ellipse fit based on Fitzgibbon's method [1], numerically
 * improved as suggested by Halir and Flusser [2].
 * See [3, Sec. 11.2.1] for a detailed description.
 * </p>
 * 
 * <p>
 * Note: This implementation performs data centering or, alternatively, 
 * accepts a specific reference point. 
 * Capable of performing an (exact) 5-point fit!
 * </p>
 * 
 * <p>
 * [1] A. W. Fitzgibbon, M. Pilu, and R. B. Fisher. Direct least-
 * squares fitting of ellipses. IEEE Transactions on Pattern Analysis
 * and Machine Intelligence 21(5), 476-480 (1999).
 * <br>
 * [2] R. Halíř and J. Flusser. Numerically stable direct least squares
 * fitting of ellipses. In "Proceedings of the 6th International
 * Conference in Central Europe on Computer Graphics and Visualization
 * (WSCG’98)", pp. 125-132, Plzeň, CZ (February 1998).
 * <br>
 * [3] W. Burger, M.J. Burge, <em>Digital Image Processing - An Algorithmic Approach</em>, 
 * 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2021/11/06
 */
public class EllipseFitFitzgibbonStable implements EllipseFitAlgebraic {
	
	private static final RealMatrix C1 = 
			Matrix.makeRealMatrix(3, 3,
					0.0,  0.0, 2, 
				 	0.0, -1.0, 0.0, 
				 	2,    0.0, 0.0);
	
	private static final RealMatrix C1i = 
			Matrix.makeRealMatrix(3, 3,
					0.0,  0.0, 0.5, 
					0.0, -1.0, 0.0, 
					0.5,  0.0, 0.0);
	
	private final double[] q;	// = (A,B,C,D,E,F) ellipse parameters
	
	public EllipseFitFitzgibbonStable(Pnt2d[] points, Pnt2d xref) {
		this.q = fit(points, xref);
	}
	
	public EllipseFitFitzgibbonStable(Pnt2d[] points) {
		this(points, PntUtils.centroid(points));
	}

	@Override
	public double[] getParameters() {
		return this.q;
	}
	
	private double[] fit(Pnt2d[] points, Pnt2d xref) {
		final int n = points.length;
		if (n < 5) {
			throw new IllegalArgumentException("fitter requires at least 5 sample points instead of " + points.length);
		}

		// reference point
		final double xr = xref.getX();
		final double yr = xref.getY();

		RealMatrix X1 = MatrixUtils.createRealMatrix(n, 3);
		RealMatrix X2 = MatrixUtils.createRealMatrix(n, 3);
		
		for (int i = 0; i < n; i++) {
			final double x = points[i].getX() - xr;	// center data set
			final double y = points[i].getY() - yr;
			double[] f1 = {sqr(x), x*y, sqr(y)};
			double[] f2 = {x, y, 1};
			X1.setRow(i, f1);
			X2.setRow(i, f2);
		}

		// build reduced scatter matrices:
		RealMatrix S1 = X1.transpose().multiply(X1);
		RealMatrix S2 = X1.transpose().multiply(X2);
		RealMatrix S3 = X2.transpose().multiply(X2);		
		RealMatrix S3i = MatrixUtils.inverse(S3);
		
		RealMatrix T = S3i.scalarMultiply(-1).multiply(S2.transpose());		
		RealMatrix Z = C1i.multiply(S1.add(S2.multiply(T)));
		
		// find the eigenvector of Z which satisfies the ellipse constraint:
//		EigenDecomposition ed = new EigenDecomposition(Z);
		EigenvalueDecomposition ed = new EigenvalueDecomposition(Z);
		double[] p1 = null;
		for (int i = 0; i < 3; i++) {
			RealVector e = ed.getEigenvector(i);
			if (e.dotProduct(C1.operate(e)) > 0) {
				p1 = e.toArray();
				break;
			}
		}
		
		if (p1 == null) {
			IJ.log("p1 is null! " + Arrays.toString(points));
			return null;
		}
		
		double[] p2 = T.operate(p1);
		
		RealMatrix U = getDataOffsetCorrectionMatrix(xr, yr);
		
		// assemble q
		return U.operate(Matrix.join(p1, p2));
	}
	
}
