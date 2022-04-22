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

public abstract class CircleFitAlgebraic {
	
	public enum FitType {
		KasaOrig,
		KasaA,
		KasaB,
		Pratt,
		Hyper,
		Taubin
	}
	
	public static CircleFitAlgebraic getFit(FitType type, Pnt2d[] points) {
		switch (type) {
		case Hyper: 	return new CircleFitHyper(points);
		case KasaA: 	return new CircleFitKasaA(points);
		case KasaB: 	return new CircleFitKasaB(points);
		case KasaOrig: 	return new CircleFitKasa(points);
		case Pratt: 	return new CircleFitPratt(points);
		case Taubin: 	return new CircleFitTaubin(points);
		default: return null;
		}
	}
	
	/**
	 * Returns the parameters (A, B, C, D) of the algebraic circle
	 * A (x^2 + y^2) + B x + C y + D = 0.
	 * @return the algebraic circle parameters (A, B, C, D)
	 */
	public abstract double[] getParameters();
	
	public GeometricCircle getGeometricCircle() {
		double[] q = this.getParameters();	// assumed to be (A, B, C, D)
		if (q == null || isZero(q[0])) {	// (abs(2 * A / s) < (1 / Rmax))
			return null;			// return a straight line
		}
		else {
			return GeometricCircle.from(new AlgebraicCircle(q));
		}
	}
	
}
