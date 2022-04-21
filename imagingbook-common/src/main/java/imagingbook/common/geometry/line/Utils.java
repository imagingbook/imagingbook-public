package imagingbook.common.geometry.line;

import ij.process.ImageProcessor;

public abstract class Utils {
	
	/**
	 * This is a brute-force drawing method which simply marks all image pixels that
	 * are sufficiently close to the HoughLine hl. The drawing color for ip must be
	 * previously set.
	 *  @param line the line to be drawn.
	 * @param ip the {@link ImageProcessor} instance to draw to.
	 * @param thickness the thickness of the lines to be drawn.
	 */
	public static void draw(AlgebraicLine line, ImageProcessor ip, double thickness) {
		final int w = ip.getWidth();
		final int h = ip.getHeight();
		final double dmax = 0.5 * thickness;
		for (int u = 0; u < w; u++) {
			for (int v = 0; v < h; v++) {
				// get the distance between (u,v) and the line hl:
				double d = line.getDistance(u, v);
				if (d <= dmax) {
					ip.drawPixel(u, v);
				}
			}
		}
	}
	

}
