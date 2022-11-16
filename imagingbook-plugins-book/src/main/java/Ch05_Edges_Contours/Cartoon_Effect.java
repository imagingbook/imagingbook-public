package Ch05_Edges_Contours;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.edges.GrayscaleEdgeDetector;

/**
 * <p>
 * ImageJ plugin, implementing a "cartoon" or "edge burn-in" effect by
 * controlled darkening of image edges. Pixels are darkened depending on the
 * value of the normalized edge magnitude (as produced by an edge operator). At
 * points of maximum edge magnitude the darkening effect are strongest, while
 * pixels remain unmodified where the edge magnitude is zero. See Ch. 5
 * (Exercise 5.8) of [1] for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An
 * Algorithmic Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 *
 */
public class Cartoon_Effect implements PlugInFilter {
	
	static double strength = 1.0;
	static double s2 = 0.5;
	
	@Override
	public int setup(String arg, ImagePlus im) {
		return DOES_8G + DOES_RGB;
	}

	@Override
	public void run(ImageProcessor ip) {
		if (!getUserInput())
			return;
		
		int w = ip.getWidth();
		int h = ip.getHeight();
		
		double a = 0.05;
		double b = 0.3;
		
		GrayscaleEdgeDetector ed = new GrayscaleEdgeDetector(ip);
		FloatProcessor mag = ed.getEdgeMagnitude();
		
		new ImagePlus("E", mag.duplicate()).show();
		
		double magMax = mag.getMax();
		
		// soft-threshold edge magnitude
		for (int u = 0; u < w; u++) {
			for (int v = 0; v < h; v++) {
				double s = mag.getf(u, v) / magMax;	// scale to 1.0
				s = f(s, a, b);	// best
				mag.setf(u, v, (float) s);
			}
		}
		
		new ImagePlus("f(E)", mag.duplicate()).show();

		// burn-in edges:
		final int[] RGB = new int[3];
		ColorProcessor cp = (ColorProcessor) ip;
		for (int u = 0; u < w; u++) {
			for (int v = 0; v < h; v++) {
				cp.getPixel(u, v, RGB);
				float s = mag.getf(u, v);
				RGB[0] = (int) (RGB[0] * s);
				RGB[1] = (int) (RGB[1] * s);
				RGB[2] = (int) (RGB[2] * s);
				cp.putPixel(u, v, RGB);
			}
		}
		
//		mag.resetMinAndMax();
//		new ImagePlus("Magnitude", mag).show();		
	}
	
	// soft-threshold function
	double f(double m, double a, double b) {
		if (m < a) {
			return 1;
		}
		else if (m <= b) {
			return 0.5 * (1 + Math.cos(Math.PI * (m - a) / (b - a)));
		}
		else {
			return 0;
		}
	}
	
	// ---------------------------------------------------------------------
	
	private boolean getUserInput() {
		GenericDialog gd = new GenericDialog(Cartoon_Effect.class.getSimpleName());
//		gd.addNumericField("Median filter radius = (1,..,50)", rad, 1);
		gd.showDialog();
		if (gd.wasCanceled()) {
			return false;
		}
//		rad = gd.getNextNumber();
//		rad = Math.min(Math.max(rad, 1), 50);	// limit to 1,...,50
		return true;
	}
	
}
