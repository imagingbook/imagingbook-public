package imagingbook.common.geometry.ellipse;

import static imagingbook.common.math.Arithmetic.sqr;
import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.pow;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.math.PrintPrecision;


/**
 * Calculates orthogonal projections of points onto an ellipse using
 * Newton-based iterative root finding.
 * NOTE that this version is quick but may fail to return a valid result if the 
 * target point is close to the x- or y-axis.
 * See {@link OrthogonalEllipseProjector} for a robust solution
 * or {@link ConfocalConicEllipseProjector} for an approximate but 
 * non-iterative (i.e., fast) alternative.
 * 
 * @author WB
 * @see OrthogonalEllipseProjector
 */
public class OrthogonalEllipseProjectorNewton extends EllipseProjector {
	
	private final static double NewtonMinStep = 1e-6;
	private final static int MaxIterations = 100;
	
	private final double ra, rb, ra2, rb2;
	private int lastIterationCount = 0;	// number of root-finding iterations performed in last projection
	
	public OrthogonalEllipseProjectorNewton(GeometricEllipse ellipse) {
		super(ellipse);
		this.ra = ellipse.ra;
		this.rb = ellipse.rb;
		this.ra2 = sqr(ra);
		this.rb2 = sqr(rb);
	}
	
	@Override
	protected double[] projectCanonical(double[] u1) {
		// coordinates of p (mapped to first quadrant)
		final double u = u1[0];	
		final double v = u1[1]; 
		
		double[] ub = null;	// the unknown ellipse point

		if (u + v < 1e-6) {	// (u,v) is very close to the ellipse center; u,v >= 0
			ub = new double[] {0, rb};
		}
		else {						
			double t = max(ra * u - ra2, rb * v - rb2);
			double gprev = Double.POSITIVE_INFINITY;
			double deltaT, deltaG;
			int k = 0;
			do {
				k = k + 1;
				double g  = sqr((ra * u) / (t + ra2)) + sqr((rb * v) / (t + rb2)) - 1;
				double dg = 2 * (sqr(ra * u) / pow(t + ra2, 3) + sqr(rb * v) / pow(t + rb2, 3));
				deltaT = g / dg;
				t = t + deltaT; 			// Newton iteration
				
				// in rare cases g(t) is very flat and checking deltaT is not enough for convergence!
				deltaG = g - gprev;			// change of g value
				gprev = g;	
				
			}  while(abs(deltaT) > NewtonMinStep && abs(deltaG) > NewtonMinStep && k < MaxIterations);
			
			lastIterationCount = k;		// remember iteration count
			
			if (k >= MaxIterations) {
				throw new RuntimeException("max. mumber of iterations exceeded");
			}
			
			ub = new double[] {ra2 * u / (t + ra2), rb2 * v / (t + rb2)};
		}
		return ub;
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
//		 GeometricEllipse ell = new GeometricEllipse(353613.76725979, 987.23614032, 353503.20032614, -9010.22308359, 3.11555492);
//		 Pnt2d p = Pnt2d.from(30.000000000, 210.000000000);
		 
			GeometricEllipse ell = new GeometricEllipse(6, 5, 0, 0, 0);
//			Pnt2d p = Pnt2d.from(1, 0);
//			Pnt2d p = Pnt2d.from(6, 0);
			Pnt2d p = Pnt2d.from(0.1, 0.1);
		
		 OrthogonalEllipseProjectorNewton projector = new OrthogonalEllipseProjectorNewton(ell);
		
		System.out.println("p  = " + p);
		Pnt2d p0 = projector.project(p);
		System.out.println("p0 = " + p0);
		System.out.println("iterations  = " + projector.getLastIterationCount());
		
		System.out.println("dist = " + projector.getDistance(p.toDoubleArray()));
	}

}
