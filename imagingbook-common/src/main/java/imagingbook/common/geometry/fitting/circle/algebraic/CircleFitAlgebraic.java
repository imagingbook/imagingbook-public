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
