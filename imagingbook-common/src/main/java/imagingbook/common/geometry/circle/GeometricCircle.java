package imagingbook.common.geometry.circle;

import static imagingbook.common.math.Arithmetic.sqr;
import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Path2D;
import java.util.Locale;

import ij.IJ;
import ij.gui.Roi;
import ij.gui.ShapeRoi;
import imagingbook.common.geometry.basic.Curve2d;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.ShapeProvider;
import imagingbook.common.math.Arithmetic;


public class GeometricCircle implements Circle, ShapeProvider, Curve2d {
	
	static int DefaultSteps = 200;
	
	private final double xc, yc, r;
	
	public static int getDefaultSteps() {
		return DefaultSteps;
	}

	public double getXc() {
		return xc;
	}

	public double getYc() {
		return yc;
	}

	public double getR() {
		return r;
	}

	public GeometricCircle(double xc, double yc, double r) {
		if (r < 0) {
			throw new IllegalArgumentException("negative circle radius");
		}
		this.xc = xc;
		this.yc = yc;
		this.r = r;		
	}
	
	public GeometricCircle(double[] p) {
		this(p[0], p[1], p[2]);
	}
	
	public static GeometricCircle from(AlgebraicCircle ac) {
		double[] p = ac.getParameters();
		if (Arithmetic.isZero(p[0])) {
			throw new RuntimeException("infinite circle radius");
		}
		double A = p[0];
		double B = p[1];
		double C = p[2];
		double D = p[3];
		double xc = -B / (2 * A);
		double yc = -C / (2 * A);
		double r =   sqrt(sqr(B) + sqr(C) - 4 * A * D) / abs(2 * A);
		return new GeometricCircle(xc, yc, r);
	}
	
	// --------------------------------------------------------------------------------
	
	@Override
	public double[] getParameters() {
		return new double[] {xc, yc, r};
	}
	
	// --------------------------------------------------------------------------------
	
	/**
	 * Calculate and returns the mean of the squared distances between this circle 
	 * and a set of 2D points.
	 *
	 * @param points a set of sample points (usually the points used for fitting)
	 */
	public double getMeanSquareError(Pnt2d[] points) {
		final int n = points.length;
		double sumR2 = 0;
		for (int i = 0; i < n; i++) {
//			double[] rxy = getError(points[i]);
//			sumR2 += sqr(rxy[0]) + sqr(rxy[1]);
			sumR2 += sqr(getDistance(points[i]));
		}
		return sumR2 / n;
	}
	
//	public double getError2(Pnt2d[] points) {
//		final int n = points.length;
//		double sumR2 = 0;
//		for (int i = 0; i < n; i++) {
//			double x = points[i].getX();							// sample point
//			double y = points[i].getY();		
//			double ri = Math.sqrt(sqr(x - xc) + sqr(y - yc)); 		// sample distance from center
//			sumR2 += sqr(r - ri);									// squared r-difference
//		}
//		
//		return sumR2 / n;
//	}
	
	/**
	 * Returns the (unsigned) distance between the specified point
	 * and this circle. The result is always non-negative.
	 * 
	 * @param p a 2D point
	 * @return the point's distance from the circle
	 */
	@Override
	public double getDistance(Pnt2d p) {
		return Math.abs(getSignedDistance(p));
	}
	
	/**
	 * Returns the signed distance between the specified point
	 * and this circle. The result is positive for points outside
	 * the circle, negative inside.
	 * 
	 * @param p a 2D point
	 * @return the point's signed distance from the circle
	 */
	public double getSignedDistance(Pnt2d p) {
		double dx = p.getX() - this.xc;
		double dy = p.getY() - this.yc;
		double rp = Math.hypot(dx, dy);
		return rp - this.r;
	}
	
//	@Deprecated
//	double[] getError(Pnt2d point) {
//		double x = point.getX();
//		double y = point.getY();
//		double dx = x - xc;
//		double dy = y - yc;
//
//		double ri = sqrt(sqr(dx) + sqr(dy));
//		double rri = ri / r;
//
//		double xm = xc + dx / rri;	// x' predicted point on the current model circle
//		double ym = yc + dy / rri;	// y'
//		
//		return new double[] {(x - xm), (y - ym)};
//	}
	
//	@Deprecated
//	public double[] getResiduals(Pnt2d[] points) {
//		double[] res = new double[points.length *2 ];
//		for (int i = 0, j = 0; i < points.length; i++, j+=2) {
//			double[] rxy = getDistance(points[i]);
//			res[j] =   rxy[0];
//			res[j+1] = rxy[1];
//		}
//		return res;
//	}
	
	public GeometricCircle disturb(double dxc, double dyc, double dr) {
		return new GeometricCircle(xc + dxc, yc + dyc, r + dr);
	}
	
	// --------------------------------------------------------------------------------
	
	@Deprecated
	public static double MarkerRadius = 4;		// marker radius
	@Deprecated
	public static int CircleSubdivisions = 200;	// subdivisions for small circles
	@Deprecated
	public static double CircleMaxSegmentLength = 4.0; 	// max. segment length for large circles
	
	@Deprecated
	public Roi makeRoi(int width, int height, Color strokeColor, double strokeWidth) {			
		Path2D path = new Path2D.Double();
		
		addCenterPath(path, MarkerRadius);
		if (this.r < 5 * (width + height)) {
			IJ.log("drawing small circle " + this.toString());
			addSmallCirclePath(path, CircleSubdivisions);
		}
		else {
			IJ.log("drawing large circle " + this.toString());
			addLargeCirclePath(path, CircleMaxSegmentLength, width, height);
		}
		
		Roi roi = new ShapeRoi(path);
		roi.setStrokeColor(strokeColor);
		roi.setStrokeWidth(strokeWidth);
		return roi;
	}
	
	@Deprecated
	private void addCenterPath(Path2D path, double d) {
		path.moveTo(xc - d, yc);
		path.lineTo(xc + d, yc);
		path.moveTo(xc, yc - d);
		path.lineTo(xc, yc + d);
	}
	
	
	@Deprecated
	private void addSmallCirclePath(Path2D path, int nSteps) {
		for (int i = 0; i <= nSteps; i++) {
			double theta = 2 * Math.PI * i / nSteps;
			double xt = xc + r * Math.cos(theta);
			double yt = yc + r * Math.sin(theta);
			if (i == 0) {
				path.moveTo(xt, yt);
			}
			else {
				path.lineTo(xt, yt);
			}
		}
		//path.closePath();	
	}
	
	/**
	 * Creates a circle path that is only inside the given W*H rectangle.
	 * Primitive, still needs a lot of tests for very large circles.
	 * @param dr max. segment length
	 * @param w width of rectangle
	 * @param h height of rectangle
	 */
	@Deprecated
	private void addLargeCirclePath(Path2D path, double dr, int w, int h) {
		int nSteps = (int) Math.ceil(2 * this.r * Math.PI / dr);
		boolean wasOutside = true;
		double xprev = Double.NaN;	// previous circle point (may be outside)
		double yprev = Double.NaN;
		for (int i = 0; i <= nSteps; i++) {	
			double theta = 2 * Math.PI * i / nSteps;
			double x = xc + r * Math.cos(theta);
			double y = yc + r * Math.sin(theta);
			if (isInside(x, y, w, h)) {
				if (wasOutside) {					// start a new segment
					if (Double.isFinite(xprev)) {	// x/y is not the first point
						// we came from outside and there is a predecessor point
						path.moveTo(xprev, yprev);
						path.lineTo(x, y);
					} else {		
						path.moveTo(x, y);
					}
					wasOutside = false;			// we are inside now!
				} else {						// continue segment
					path.lineTo(x, y);
				}
			} else {
				wasOutside = true;		// we are outside again or still
			}
			xprev = x;
			yprev = y;
		}
	}
	
	// ------------------------------------------------------------------
	
	@Deprecated
	public Path2D getOutlinePath() {
		return getOutlinePath(DefaultSteps);
	}
	
	
	@Deprecated
	public Path2D getCenterPath(double d) {
		Path2D path = new Path2D.Double();
		path.moveTo(xc - d, yc);
		path.lineTo(xc + d, yc);
		path.moveTo(xc, yc - d);
		path.lineTo(xc, yc + d);
		return path;
	}
	
	@Deprecated
	public Path2D getOutlinePath(int nSteps) {
		Path2D path = new Path2D.Double();	
		for (int i = 0; i < nSteps; i++) {
			double theta = 2 * Math.PI * i / nSteps;
			double xt = xc + r * Math.cos(theta);
			double yt = yc + r * Math.sin(theta);
			if (i == 0) {
				path.moveTo(xt, yt);
			}
			else {
				path.lineTo(xt, yt);
			}
		}
		path.closePath();	
		return path;
	}
	
	/**
	 * Creates a circle path that is only inside the given W*H rectangle.
	 * Primitive, still needs a lot of tests for very large circles.
	 * @param dr max. segment length
	 * @param w width of rectangle
	 * @param h height of rectangle
	 * @return
	 */
	@Deprecated
	public Path2D getOutlinePath(double dr, int w, int h) {
		int nSteps = (int) Math.ceil(2 * this.r * Math.PI / dr);
		//IJ.log("nSteps = " + nSteps);
		Path2D path = new Path2D.Double();
//		int cnt = 0;
		boolean wasOutside = true;
		double xprev = Double.NaN;	// previous circle point (may be outside)
		double yprev = Double.NaN;
		for (int i = 0; i <= nSteps; i++) {	
			double theta = 2 * Math.PI * i / nSteps;
			double x = xc + r * Math.cos(theta);
			double y = yc + r * Math.sin(theta);
			if (isInside(x, y, w, h)) {
				if (wasOutside) {					// start a new segment
					if (Double.isFinite(xprev)) {	// x/y is not the first point
						// we came from outside and there is a predecessor point
						path.moveTo(xprev, yprev);
						path.lineTo(x, y);
					} else {		
						path.moveTo(x, y);
					}
					wasOutside = false;			// we are inside now!
				} else {						// continue segment
					path.lineTo(x, y);
				}
//				cnt++;
			} else {
				wasOutside = true;		// we are outside again or still
			}
			xprev = x;
			yprev = y;
		}
		//path.closePath();	
		//IJ.log("cnt = " + cnt);
		return path;		// return null if path is empty??
	}
	
	// This is actually used ------------------------------------
	
	static double DefaultCenterMarkRadius = 3;
	
	public Shape getCenterShape() {
		return getCenterShape(DefaultCenterMarkRadius);
		
	}
	public Shape getCenterShape(double radius) {
		Path2D path = new Path2D.Double();
		path.moveTo(xc - radius, yc);
		path.lineTo(xc + radius, yc);
		path.moveTo(xc, yc - radius);
		path.lineTo(xc, yc + radius);
		return path;
	}
	
	public Shape getOuterShape() {
		return new Arc2D.Double(xc - r, yc - r, 2 * r, 2 * r, 0, 360, Arc2D.OPEN);
	}
	
	@Override
	public Shape getShape(double scale) {
		return getOuterShape() ;
	}
	
	@Override
	public Shape[] getShapes(double scale) {
		return new Shape[] {getOuterShape(), getCenterShape(scale)};
	}
	
	// ---------------------------------------------------------------------------------
	
	@Deprecated
	private boolean isInside(double x, double y, double w, double h) {
		if (x < 0 || x > w) return false;
		if (y < 0 || y > h) return false;
		return true;
	}
	
	@Override
	public String toString() {
		return String.format(Locale.US, "%s [xc=%f, yc=%f, r=%f]", 
				this.getClass().getSimpleName(), xc, yc, r);
	}


	// ---------------------------------------------------------------------------------

	public static void main(String[] args) {
		GeometricCircle c = new GeometricCircle(0, 0, 7);
		Pnt2d p = Pnt2d.from(0, -8);
		System.out.println("d2 = " + c.getDistance(p));
		
		AlgebraicCircle ca = new AlgebraicCircle(1.3, -2, 5, 1);
		GeometricCircle cg = GeometricCircle.from(ca);
		System.out.println("cg = " + cg);
		// cg = GeometricCircle [xc=0.769231, yc=-1.923077, r=1.876356]
	}



}
