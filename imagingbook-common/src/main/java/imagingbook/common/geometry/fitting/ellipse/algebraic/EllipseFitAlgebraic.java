/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.geometry.fitting.ellipse.algebraic;

import static imagingbook.common.math.Arithmetic.sqr;

import org.apache.commons.math3.linear.RealMatrix;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.ellipse.AlgebraicEllipse;
import imagingbook.common.math.Matrix;

/**
 * Interface for algebraic ellipse fits.
 * 
 * @author WB
 */
public interface EllipseFitAlgebraic {
	
	public enum FitType {
		FitzgibbonNaive,
		FitzgibbonOriginal,
		FitzgibbonStable,
		Taubin1,
		Taubin2
	}
	
	public static EllipseFitAlgebraic getFit(FitType type, Pnt2d[] points, Pnt2d xref) {
		switch (type) {
		case FitzgibbonNaive: 		return new EllipseFitFitzgibbonNaive(points);
		case FitzgibbonOriginal: 	return new EllipseFitFitzgibbonOriginal(points);
		case FitzgibbonStable: 		return new EllipseFitFitzgibbonStable(points, xref);
		case Taubin1: 				return new EllipseFitTaubin1(points, xref);
		case Taubin2: 				return new EllipseFitTaubin2(points, xref);
		}
		throw new RuntimeException("unknown algebraic fit type: " + type);
	}
	
	/**
	 * Returns a vector of algebraic ellipse parameters:
	 * {@code (a,b,c,d,e,f)}, representing the ellipse by
	 * {@code a x^2 + b x y + c y^2 + d x + e y + f = 0}.
	 * @return the ellipse parameters
	 */
	public double[] getParameters();
	
	/**
	 * Returns a algebraic ellipse constructed from the 
	 * estimated ellipse parameters.
	 * 
	 * @return an {@link AlgebraicEllipse} instance
	 */
	public default AlgebraicEllipse getEllipse() {
		double[] p = getParameters();
		return (p == null) ? null : new AlgebraicEllipse(p);
	}
	
	public default boolean isEllipse() {
		double[] p = getParameters();	// p = (a,b,c,d,e,f)
		if (p == null) {
			return false;
		}
		else {
			double a = p[0];
			double b = p[1];
			double c = p[2];
			return (sqr(b) - 4*a*c) < 0;
		}
	}
	
	/**
	 * <p>
	 * Returns a 6x6 matrix (U) to convert ellipse parameters qr = (ar,...,fr)
	 * calculated for data centered at (xr, yr) to ellipse parameters q = (a,...,f)
	 * for the original non-centered data: q = U * qr. See [1, Sec. 11.2.1] (e.g.,
	 * Alg. 11.6) for additional information.
	 * </p>
	 * <p>
	 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic
	 * Introduction</em>, 3rd ed, Springer (2022).
	 * </p>
	 * 
	 * @param xr data reference point (x)
	 * @param yr data reference point (y)
	 * @return the offset correction matrix (U)
	 */
	public default RealMatrix getDataOffsetCorrectionMatrix(double xr, double yr) {
		return Matrix.makeRealMatrix(6, 6,
				1,       0,     0,        0,   0,  0 ,
				0,       1,     0,        0,   0,  0 ,
				0,       0,     1,        0,   0,  0 ,
			   -2*xr,   -yr,    0,        1,   0,  0 ,
				0,      -xr,   -2*yr,     0,   1,  0 ,
				sqr(xr), xr*yr, sqr(yr), -xr, -yr, 1 );
	}
	
	// ----------------------------------------------------------------------
	
	public static void main(String[] args) {
		Pnt2d p1 = Pnt2d.from(31, 90);
		Pnt2d p2 = Pnt2d.from(79, 25);
		Pnt2d p3 = Pnt2d.from(159, 31);
		Pnt2d p4 = Pnt2d.from(174, 55);
		Pnt2d p5 = Pnt2d.from(173, 84);
		Pnt2d p6 = Pnt2d.from(135, 114);
		
		Pnt2d[] pts = {p1, p2, p3, p4, p5, p6};
		Pnt2d xr = Pnt2d.from(100,100);
		
		for (FitType type : FitType.values()) {
			EllipseFitAlgebraic fit = EllipseFitAlgebraic.getFit(type, pts, xr);
			System.out.println(type);
			if (fit != null) {
				System.out.println(fit.getEllipse());
			}
		}
		
	}


}
