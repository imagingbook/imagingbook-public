package imagingbook.common.geometry.fitting.circle.algebraic;


import static imagingbook.common.math.Arithmetic.sqr;

import java.util.Arrays;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.circle.GeometricCircle;
import imagingbook.common.math.Arithmetic;

/**
 * Performs an exact circle fit on 3 given (non-collinear) points.
 * @author WB
 *
 */
public class CircleFit3Points extends CircleFitAlgebraic {
	
	private final double[] p;
	
	public CircleFit3Points(Pnt2d ... pts) {				// Pnt2d p0, Pnt2d p1, Pnt2d p2
		if (pts.length != 3) {
			throw new IllegalArgumentException("exactly 3 points required for exact circle fit");
		}
		final double x0 = pts[0].getX();
		final double y0 = pts[0].getY();
		final double x1 = pts[1].getX();
		final double y1 = pts[1].getY();
		final double x2 = pts[2].getX();
		final double y2 = pts[2].getY();
		
		final double A = x0 * (y1 - y2) - y0 * (x1 - x2) + x1 * y2 - x2 * y1;		
		if (Arithmetic.isZero(A)) {
			this.p = null;
		} 
		else {
			final double R0 = sqr(x0) + sqr(y0);
			final double R1 = sqr(x1) + sqr(y1);
			final double R2 = sqr(x2) + sqr(y2);
			final double B = R0 * (y2 - y1) + R1 * (y0 - y2) + R2 * (y1 - y0);
			final double C = R0 * (x1 - x2) + R1 * (x2 - x0) + R2 * (x0 - x1);
			final double D = R0 * (x2 * y1 - x1 * y2) + R1 * (x0 * y2 - x2 * y0) + R2 * (x1 * y0 - x0 * y1);	
			this.p = new double[] {A, B, C, D};
		}
	}

	@Override
	public double[] getParameters() {
		return this.p;
	}
	
	// ----------------------------------------------------------------
	
	public static void main(String[] args) {
		Pnt2d p0 = Pnt2d.from(6, 7);
		Pnt2d p1 = Pnt2d.from(1, 9);
		Pnt2d p2 = Pnt2d.from(-3, 5);
		
		
		CircleFitAlgebraic fitA = new CircleFitKasaA(new Pnt2d[] {p0, p1, p2});
		System.out.println(fitA.getClass().getSimpleName());
		double[] pA = fitA.getParameters();
		double[] pAn = {1, pA[1]/pA[0], pA[2]/pA[0], pA[3]/pA[0]};
		
		System.out.println(Arrays.toString(pAn));
		GeometricCircle cA = fitA.getGeometricCircle();
		System.out.println(cA);
		
		System.out.println("CircleFit3Points:");
		
		CircleFit3Points fit3 = new CircleFit3Points(p0, p1, p2);
		double[] p3 = fit3.getParameters();
		System.out.println(Arrays.toString(p3));
		GeometricCircle c = fit3.getGeometricCircle();
		System.out.println(c);
		
		
	}

}
