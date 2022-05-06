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
	
}
