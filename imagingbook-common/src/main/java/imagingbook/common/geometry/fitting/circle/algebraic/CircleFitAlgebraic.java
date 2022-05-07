/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.geometry.fitting.circle.algebraic;

import static imagingbook.common.math.Arithmetic.isZero;
import static imagingbook.common.math.Arithmetic.sqr;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.circle.AlgebraicCircle;
import imagingbook.common.geometry.circle.GeometricCircle;

/**
 * Abstract super-class of all algebraic circle fits.
 * @author WB
 *
 */
public abstract class CircleFitAlgebraic {
	
	public enum FitType {
		KasaA,
		KasaB,
		KasaC,
		Pratt,
		Taubin,
		HyperSimple,
		HyperStable
	}
	
	public static CircleFitAlgebraic getFit(FitType type, Pnt2d[] points) {
		switch (type) {
		case KasaA: 	return new CircleFitKasaA(points);
		case KasaB: 	return new CircleFitKasaB(points);
		case KasaC: 	return new CircleFitKasaC(points);
		case Pratt: 	return new CircleFitPratt(points);
		case Taubin: 	return new CircleFitTaubin(points);
		case HyperSimple: 	return new CircleFitHyperSimple(points);
		case HyperStable: 	return new CircleFitHyperStable(points);
		}
		return null;
	}
	
	/**
	 * Returns parameters (A, B, C, D) of the {@link AlgebraicCircle}
	 * or {@code null} if the fit was unsuccessful.
	 * Parameters are not normalized.
	 * 
	 * @return the algebraic circle parameters or {@code null}
	 */
	public abstract double[] getParameters();
	
	
	/**
	 * Returns a {@link AlgebraicCircle} instance for this fit 
	 * or {@code null} if the fit was unsuccessful.
	 * @return a {@link AlgebraicCircle} instance or null
	 */
	public AlgebraicCircle getAlgebraicCircle() {
		double[] p = getParameters();
		return (p == null) ? null : new AlgebraicCircle(p);
	}
	
	/**
	 * Returns a {@link GeometricCircle} instance for this fit 
	 * or {@code null} if the fit was unsuccessful.
	 * @return a {@link GeometricCircle} instance or null
	 */
	public GeometricCircle getGeometricCircle() {
		double[] q = this.getParameters();	// assumed to be (A, B, C, D)
		if (q == null || isZero(q[0])) {	// (abs(2 * A / s) < (1 / Rmax))
			return null;					// return a straight line?
		}
		else {
			return new GeometricCircle(getAlgebraicCircle());
		}
	}
	
	/**
	 * Used to transform the algebraic parameter vector for sample data
	 * shifted to the reference point specified.
	 * If qq is the parameter vector for the data centered at (xr, yr),
	 * the original parameters q are obtained as
	 * <pre>q = M * qq</pre>
	 * 
	 * @param xr reference point (x)
	 * @param yr reference point (y)
	 * @return the transformation matrix
	 */
	protected static RealMatrix getDecenteringMatrix(double xr, double yr) {
		return MatrixUtils.createRealMatrix(new double[][]
				{{ 1, 0, 0, 0 },
				 {-2*xr, 1, 0, 0 },
				 {-2*yr, 0, 1, 0 },
				 {sqr(xr) + sqr(yr), -xr, -yr, 1}});
	}
}
