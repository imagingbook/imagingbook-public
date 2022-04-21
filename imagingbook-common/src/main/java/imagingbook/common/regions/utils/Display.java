package imagingbook.common.regions.utils;

import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;
import imagingbook.common.color.iterate.RandomHueGenerator;
import imagingbook.common.regions.segment.BinaryRegionSegmentation;

public abstract class Display {
	
	/**
	 * Utility method that creates an image to display a binary segmentation.
	 * 
	 * @param labeling The region segmentation.
	 * @param color Set {@code true} to color the detected regions.
	 * @return an image of the label array.
	 */
	public static ImageProcessor makeLabelImage(BinaryRegionSegmentation labeling, boolean color) {
		return (color) ?  makeLabelImageColor(labeling) : makeLabelImageGray(labeling);
	}

	private static ColorProcessor makeLabelImageColor(BinaryRegionSegmentation segmentation) {
		int minLabel = segmentation.getMinLabel();
		int maxLabel = segmentation.getMaxLabel();
		int[] colorLUT = new int[maxLabel+1];
		RandomHueGenerator rcg = new RandomHueGenerator();
		
		for (int i = minLabel; i <= maxLabel; i++) {
			colorLUT[i] = rcg.next().getRGB(); //makeRandomColor();
		}
		
		int width = segmentation.getWidth();
		int height = segmentation.getHeight();
		
		ColorProcessor cp = new ColorProcessor(width, height);
		for (int v = 0; v < height; v++) {
			for (int u = 0; u < width; u++) {
				int lb = segmentation.getLabel(u, v);
				if (lb >= 0 && lb < colorLUT.length) {
					cp.putPixel(u, v, colorLUT[lb]);
				}
			}
		}
		return cp;
	}
	
	private static ShortProcessor makeLabelImageGray(BinaryRegionSegmentation labeling) {
		int width = labeling.getWidth();
		int height = labeling.getHeight();
		ShortProcessor sp = new ShortProcessor(width, height);
		for (int v = 0; v < height; v++) {
			for (int u = 0; u < width; u++) {
				int lb = labeling.getLabel(u, v);
				sp.set(u, v, (lb >= 0) ? lb : 0);
			}
		}
		sp.resetMinAndMax();
		return sp;
	}
	
//	private static int makeRandomColor() {
//		double saturation = 0.2;
//		double brightness = 0.2;
//		float h = (float) Math.random();
//		float s = (float) (saturation * Math.random() + 1 - saturation);
//		float b = (float) (brightness * Math.random() + 1 - brightness);
//		return Color.HSBtoRGB(h, s, b);
//	}

}
