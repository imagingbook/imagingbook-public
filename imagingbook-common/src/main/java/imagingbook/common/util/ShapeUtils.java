package imagingbook.common.util;

import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.Arrays;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.circle.GeometricCircle;

@Deprecated
public class ShapeUtils {

	public static double distance(Pnt2d pnt, Shape s, double flatness) {
		Point2D p = pnt.toAwtPoint2D();
		
		if (s.contains(p)) {
			return 0;
		}
		
		final PathIterator pi = s.getPathIterator(null, flatness);
		final Line2D line = new Line2D.Double();
		double bestDistSq = Double.POSITIVE_INFINITY;
		double firstX = Double.NaN;
		double firstY = Double.NaN;
		double lastX = Double.NaN;
		double lastY = Double.NaN;
		final double coords[] = new double[6];
		while (!pi.isDone()) {
			final boolean validLine;
			switch (pi.currentSegment(coords)) {
			case PathIterator.SEG_MOVETO:
				lastX = coords[0];/* from ww w . j a va2s.co m */
				lastY = coords[1];
				firstX = lastX;
				firstY = lastY;
				validLine = false;
				break;
			case PathIterator.SEG_LINETO: {
				final double x = coords[0];
				final double y = coords[1];
				line.setLine(lastX, lastY, x, y);
				lastX = x;
				lastY = y;
				validLine = true;
				break;
			}
			case PathIterator.SEG_CLOSE:
				line.setLine(lastX, lastY, firstX, firstY);
				validLine = true;
				break;
			default:
				throw new AssertionError();
			}
			if (validLine) {
				final double distSq = line.ptSegDistSq(p);
				if (distSq < bestDistSq) {
					bestDistSq = distSq;
				}
			}
			pi.next();
		}
		return Math.sqrt(bestDistSq);
	}
	
	
	static void listPoints(Shape s) {
		double flatness = 1;
		final PathIterator pi = s.getPathIterator(null, flatness);
		final Line2D line = new Line2D.Double();
		double firstX = Double.NaN;
		double firstY = Double.NaN;
		double lastX = Double.NaN;
		double lastY = Double.NaN;
		final double coords[] = new double[6];
		while (!pi.isDone()) {
			switch (pi.currentSegment(coords)) {
			case PathIterator.SEG_MOVETO:
				System.out.println("SEG_MOVETO " + Arrays.toString(coords));
				lastX = coords[0];/* from ww w . j a va2s.co m */
				lastY = coords[1];
				firstX = lastX;
				firstY = lastY;
				break;
			case PathIterator.SEG_LINETO: {
				System.out.println("SEG_LINETO " + Arrays.toString(coords));
				final double x = coords[0];
				final double y = coords[1];
				line.setLine(lastX, lastY, x, y);
				lastX = x;
				lastY = y;
				break;
			}
			case PathIterator.SEG_CLOSE:
				System.out.println("SEG_MOVETO " + Arrays.toString(coords));
				line.setLine(lastX, lastY, firstX, firstY);
				break;
			default:
				throw new AssertionError();
			}
//			if (validLine) {
//				final double distSq = line.ptSegDistSq(p);
//				if (distSq < bestDistSq) {
//					bestDistSq = distSq;
//				}
//			}
			pi.next();
		}
	}
	
	private static Shape makeLineShape(Point2D p1, Point2D p2) {
		double x1 = p1.getX();
		double y1 = p1.getY();
		double x2 = p2.getX();
		double y2 = p2.getY();
		Path2D path = new Path2D.Double();
		path.moveTo(x1, y1);
		path.lineTo(x2, y2);
		return path;
	}
	
	public static void main(String[] args) {
		Point2D p1 = new Point2D.Double(0, 0);
		Point2D p2 = new Point2D.Double(200, 500);
		
		Shape line = makeLineShape(p1, p2);
		System.out.println("line = " + line);
		
//		System.out.println("contains 1 = " + line.contains(p1));
//		System.out.println("contains 2 = " + line.contains(p2));
		
		listPoints(line);
		
		GeometricCircle gc = new GeometricCircle(20, 30, 100);
		Shape circle = gc.getShape();
		System.out.println("circle = " + circle);
		listPoints(circle);
		
	}

}
