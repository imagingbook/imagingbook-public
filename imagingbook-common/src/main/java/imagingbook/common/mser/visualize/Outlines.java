package imagingbook.common.mser.visualize;

import java.util.ArrayList;
import java.util.Collection;

import ij.process.ByteProcessor;

public abstract class Outlines {
	
	/**
	 * Collects the outlines of all foreground regions in the 
	 * given image as a collection of line segments ('crack edges').
	 * Note that line segments are not ordered.
	 *
	 * 
	 * @param bp a binary image (background = 0, foreground > 0)
	 * @return an unordered collection of line segments
	 */
	public static Collection<LineSegment2d> getCrackOutlines(ByteProcessor bp) {
		Collection<LineSegment2d> lines = new ArrayList<>();
		
		int width = bp.getWidth();
		int height = bp.getHeight();
		
		for (int u = 0; u < width; u++) {
			for (int v = 0; v < height; v++) {
				if (bp.get(u, v) > 0) {
					if (bp.getPixel(u + 1, v) == 0) {
						lines.add(new LineSegment2d(u + 1, v, u + 1, v + 1));
					}
					if (bp.getPixel(u, v - 1) == 0) {
						lines.add(new LineSegment2d(u, v, u + 1, v));
					}
					if (bp.getPixel(u - 1, v) == 0) {
						lines.add(new LineSegment2d(u, v + 1, u, v));
					}
					if (bp.getPixel(u, v + 1) == 0) {
						lines.add(new LineSegment2d(u + 1, v + 1, u, v + 1));
					}	
				}
			}
		}
		return lines;
	}

}
