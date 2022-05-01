package imagingbook.common.util;

import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;

import imagingbook.common.geometry.basic.Pnt2d;

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

}
