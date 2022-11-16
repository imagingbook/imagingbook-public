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
 * (Exercise 5.8) of [1] for additional details. Works for RGB color images
 * only. The input image is modified.
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
	
	private static double A = 0.05;
	private static double B = 0.3;
	private static boolean ShowOriginalEdgeMagnitude = false;
	private static boolean ShowSoftenedEdgeMagnitude = false;
	
	@Override
	public int setup(String arg, ImagePlus im) {
		return DOES_RGB;	// TODO: make this work for DOES_8G
	}

	@Override
	public void run(ImageProcessor ip) {
		if (!getUserInput()) {
			return;
		}
		
		int w = ip.getWidth();
		int h = ip.getHeight();
		
		GrayscaleEdgeDetector ed = new GrayscaleEdgeDetector(ip);
		FloatProcessor mag = ed.getEdgeMagnitude();
		
		if (ShowOriginalEdgeMagnitude) {
			ImageProcessor mag2 = mag.duplicate();
			mag2.invert();
			mag2.resetMinAndMax();
			new ImagePlus("E (inverted)", mag2).show();
		}
		
		double magMax = mag.getMax();
		
		// soft-threshold edge magnitude
		for (int u = 0; u < w; u++) {
			for (int v = 0; v < h; v++) {
				double s = mag.getf(u, v) / magMax;	// scale to 1.0
				mag.setf(u, v, (float) f(s, A, B));
			}
		}
		
		if (ShowSoftenedEdgeMagnitude) {
			ImageProcessor mag3 = mag.duplicate();
			mag3.resetMinAndMax();
			new ImagePlus("f(E)", mag3).show();
		}

		// burn-in edges:
		final int[] RGB = new int[3];
		ColorProcessor cp = (ColorProcessor) ip;
		for (int u = 0; u < w; u++) {
			for (int v = 0; v < h; v++) {
				cp.getPixel(u, v, RGB);
				float s = mag.getf(u, v);
				// darken pixel value by factor s
				RGB[0] = Math.round(RGB[0] * s);
				RGB[1] = Math.round(RGB[1] * s);
				RGB[2] = Math.round(RGB[2] * s);
				cp.putPixel(u, v, RGB);
			}
		}	
	}
	
	// soft-threshold function
	private double f(double m, double a, double b) {
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
		gd.addNumericField("Parameter a", A, 2);
		gd.addNumericField("Parameter b", B, 2);
		gd.addCheckbox("Show original edge magnitude", ShowOriginalEdgeMagnitude);
		gd.addCheckbox("Show softened edge magnitude", ShowSoftenedEdgeMagnitude);
		
		gd.showDialog();
		if (gd.wasCanceled()) {
			return false;
		}
		A = gd.getNextNumber();
		B = gd.getNextNumber();
		ShowOriginalEdgeMagnitude = gd.getNextBoolean();
		ShowSoftenedEdgeMagnitude = gd.getNextBoolean();
		return true;
	}
	
}
