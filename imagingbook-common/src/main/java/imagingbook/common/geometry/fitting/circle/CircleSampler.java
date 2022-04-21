package imagingbook.common.geometry.fitting.circle;

import static imagingbook.common.math.Arithmetic.mod;
import static java.lang.Math.PI;

import java.util.Random;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.circle.GeometricCircle;

public class CircleSampler {
	
	private final Random rg;
	
	private final GeometricCircle circle;
	
	public CircleSampler(GeometricCircle circle) {
		this.circle = circle;
		this.rg = new Random();
	}
	
	public CircleSampler(GeometricCircle circle, long seed) {
		this.circle = circle;
		this.rg = new Random(seed);
	}
	
	/**
	 * Generate coordinates of a circular arc using Gaussian noise.
	 * x/y positions are disturbed by additive Gaussian noise.
	 *
	 * @param n          number of points
	 * @param startAngle initial angle (in radians)
	 * @param endAngle   final angle (in radians)
	 * @param sigma      Add noise of intensity 'noise'
	 *
	 * @return
	 */
	public Pnt2d[] getPoints(int n, double startAngle, double endAngle, double sigma) {
		double xc = circle.getXc();
		double yc = circle.getYc();
		double r = circle.getR();

		
		Pnt2d[] pts = new Pnt2d[n];
		
		startAngle = mod(startAngle, 2 * PI);
		endAngle = mod(endAngle, 2 * PI);
		double dAngle = (endAngle >= startAngle) ? 
				(endAngle - startAngle) :
				(endAngle + 2 * PI - startAngle);
		
		for (int i = 0; i < n; i++) {
			double alpha = startAngle + dAngle * i / n;
			double x = xc + r * Math.cos(alpha) + sigma * rg.nextGaussian();
			double y = yc + r * Math.sin(alpha) + sigma * rg.nextGaussian();
			pts[i] = Pnt2d.from(x, y);
		}
		return pts;
	}

//	/**
//	 * Generate coordinates of a circular arc using Gaussian noise.
//	 * x/y positions are disturbed by additive Gaussian noise.
//	 *
//	 * @param xc         x-center
//	 * @param yc         y-center
//	 * @param r          circle radius
//	 * @param startAngle initial angle
//	 * @param arcAngle   arc angle
//	 * @param n          number of points
//	 * @param noise      Add noise of intensity 'noise'
//	 *
//	 * @return
//	 */
//	@Deprecated
//	public static Pnt2d[] getPoints(double xc, double yc, double r, int n,
//			double startAngle, double arcAngle, double sigma) {	
//
//		Random rd= new Random(RandomSeed);
//		Pnt2d[] pts = new Pnt2d[n];
//		
//		startAngle = mod(startAngle, 2 * PI);
//		arcAngle = mod(arcAngle, 2 * PI);
////		double dAngle = (arcAngle >= startAngle) ? 
////				(arcAngle - startAngle) :
////				(arcAngle + 2 * PI - startAngle);
//		
//		for (int i = 0; i < n; i++) {
//			double alpha = startAngle + arcAngle * i / n;
//			double x = xc + r * Math.cos(alpha) + sigma * rd.nextGaussian();
//			double y = yc + r * Math.sin(alpha) + sigma * rd.nextGaussian();
//			pts[i] = Pnt2d.from(x, y);
//		}
//		return pts;
//	}
	
//	// This version only disturbs the radius. Strange: seems harder to fit!!
//	public static Pnt2d[] getPoints2(double xc, double yc, double r, int n,
//			double startAngle, double endAngle, double sigma) {	
//
//		Random rd= new Random(RandomSeed);
//		Pnt2d[] pts = new Pnt2d[n];
//		double arc = (endAngle - startAngle) / (2 * Math.PI);
//		for (int i = 0; i < n; i++) {
//			double alpha = startAngle + i * 2 * Math.PI * arc / n;
//			double rr = r + sigma * rd.nextGaussian();
//			double x = xc + rr * Math.cos(alpha);
//			double y = yc + rr * Math.sin(alpha);
//			pts[i] = Pnt2d.from(x, y);
//		}
//		return pts;
//	}

	
//	// -----------------------------------------------------------------------
//
//	// Only for compatibility with Doube's code.
//	public static double[][] makeTestCircleArr(double xc, double yc, double r, int n,
//									double startAngle, double endAngle, double sigma) {
//		Pnt2d[] pts = getPoints(xc, yc, r, n, startAngle, endAngle, sigma);
////		double[][] arr = new double[n][2];
////		for (int i = 0; i < n; i++) {
////			arr[i][0] = pts[i].getX();
////			arr[i][1] = pts[i].getY();
////		}
////		return arr;
//		return toArray(pts);
//	}
//
//	// Only for compatibility with Doube's code.
//	public static Pnt2d[] toPoints(double[][] pa) {
//		Pnt2d[] pts = new Pnt2d[pa.length];
//		for (int i = 0; i < pa.length; i++) {
//			pts[i] = Pnt2d.from(pa[i]);
//		}
//		return pts;
//	}
//	
//	public static double[][] toArray(Pnt2d[] points) {
//		double[][] pa = new double[points.length][2];
//		for (int i = 0; i < points.length; i++) {
//			pa[i][0] = points[i].getX();
//			pa[i][1] = points[i].getY();
//		}
//		return pa;
//	}
//
//
	public static Pnt2d[] makeTestGander(double s) {
		Pnt2d[] points = {
				Pnt2d.from(s*1, s*7),
				Pnt2d.from(s*2, s*6),
				Pnt2d.from(s*8, s*8),
				Pnt2d.from(s*7, s*7),
				Pnt2d.from(s*9, s*5),
				Pnt2d.from(s*3, s*7)
		};
		return points;
	}
//	
//	public static Pnt2d[] make3Points(double s) {
//		Pnt2d[] points = {
//				Pnt2d.from(s*1, s*7),
//				Pnt2d.from(s*2, s*6),
//				Pnt2d.from(s*8, s*8)
//		};
//		return points;
//	}
//	
//	public static Pnt2d[] collinearPoints1() {
//		Pnt2d[] points = {
//				Pnt2d.from(10, 10),
//				Pnt2d.from(15, 15),
//				Pnt2d.from(17, 17),
//				Pnt2d.from(20, 20)
//		};
//		return points;
//	}
}
