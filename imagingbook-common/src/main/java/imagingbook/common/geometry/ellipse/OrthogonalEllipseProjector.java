package imagingbook.common.geometry.ellipse;

import static imagingbook.common.math.Arithmetic.isZero;
import static imagingbook.common.math.Arithmetic.sqr;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.math.Arithmetic;
import imagingbook.common.math.PrintPrecision;


/**
 * <p>
 * Calculates orthogonal projections of points onto an ellipse.
 * Robust algorithm, based on
 * </p>
 * <blockquote>
 * D. Eberly: "Distance from a point to an ellipse, an ellipsoid, or a hyperellipsoid",
 * Technical Report, Geometric Tools, www.geometrictools.com, Redmont, WA (June 2013).
 * </blockquote>
 * <p>
 * In contrast to the Newton-based algorithm, his version returns valid results for
 * points close to the x- and y-axis but requires significantly more iterations to 
 * converge.
 * </p>
 * @version 2022/04/09
 */
public class OrthogonalEllipseProjector extends EllipseProjector {
	
	private static final int MaxIterations = 150;
	private static final double Epsilon = 1e-6;
	
	private final double ra, rb, rab;
	private int lastIterationCount = 0;	// number of root-finding iterations performed in last projection
	
	public OrthogonalEllipseProjector(GeometricEllipse ellipse) {
		super(ellipse);
		this.ra = ellipse.ra;
		this.rb = ellipse.rb;
		this.rab = sqr(ra / rb);	// ratio of squared axes lengths
	}
	
	@Override
	protected double[] projectCanonical(double[] u1) {
		// coordinates of p (mapped to first quadrant of canonical coordinates)
		final double u = u1[0];	// u,v are both positive
		final double v = u1[1];
		
		double[] ub = null;	// the ellipse contact point (in canonical coordinates)
		lastIterationCount = 0;
		
		if (v > 0) {
			if (u > 0) {
				double uu = u / ra;
				double vv = v / rb;
				double ginit = sqr(uu) + sqr(vv) - 1;
				if (!isZero(ginit)) {
					double s = getRoot(uu, vv, ginit);
					ub = new double[] {rab * u / (s + rab), v / (s + 1)};
				}
				else {
					ub = new double[] {u, v};
				}
			}
			else {	// u = 0
				ub = new double[] {0, rb};
			}
		}	
		else {	// v = 0
			double numer0 = ra * u;
			double denom0 = sqr(ra) - sqr(rb);
			if (numer0 < denom0) {
				double xde0 = numer0 / denom0;
				ub = new double[] {ra * xde0, rb * Math.sqrt(1 - sqr(xde0))};
			}
			else {
				ub = new double[] {ra, 0};
			}
		}
		return ub;
	}

	// Find the root of function
	// G(s) = [(rab * uu) / (s + rab)]^2 + [vv / (s + 1)]^2 - 1
	// using the bisection method.
	private double getRoot(double uu, double vv, double g0) {
		double s0 = vv - 1;
		double s1 = (g0 < 0) ? 0 : Math.hypot(rab * uu, vv) - 1;
		double s = 0;
		
		int i;
		for (i = 0; i < MaxIterations; i++) {
			s = (s0 + s1) / 2;
			if (Arithmetic.equals(s, s0) || Arithmetic.equals(s, s1)) {
				break;
			}
			double g = sqr((rab * uu)/(s + rab)) + sqr(vv/(s + 1)) - 1; // = G(s)
			if (g > Epsilon) {			// G(s) is positive
				s0 = s;
			}
			else if (g < -Epsilon) {	// G(s) is negative
				s1 = s;
			}
			else {						// G(s) ~ 0 (done)
				break;
			}
		}
		if (i >= MaxIterations) {
			throw new RuntimeException(this.getClass().getSimpleName() + 
					": max. iteration count exceeded");
		}
		lastIterationCount = i;
		return s;
	}
	
	// for statistics only
	public int getLastIterationCount() {
		return this.lastIterationCount;
	}
	
	// -------------------------------------------------

	public static void main(String[] args) {
		PrintPrecision.set(8);
		
//		Ellipse ell = new Ellipse(5, 3, 1, 1, 1.1);
//		Pnt2d p = Pnt2d.from(6, 1);
		
		// critical case: 
//		GeometricEllipse ell = new GeometricEllipse(353613.76725979, 987.23614032, 353503.20032614, -9010.22308359, 3.11555492);
//		Pnt2d p = Pnt2d.from(30.000000000, 210.000000000);
		
		GeometricEllipse ell = new GeometricEllipse(6, 5, 0, 0, 0);
//		Pnt2d p = Pnt2d.from(1, 0);
//		Pnt2d p = Pnt2d.from(6, 0);
		Pnt2d p = Pnt2d.from(0.1, 0.1);
		
		OrthogonalEllipseProjector projector = new OrthogonalEllipseProjector(ell);
		
		System.out.println("p  = " + p);
		
		Pnt2d p0 = projector.project(p);
		System.out.println("p0 = " + p0);
		System.out.println("iterations  = " + projector.getLastIterationCount());
		
//		System.out.println("dist = " + projector.getDistance(p.toDoubleArray()));
	}

}