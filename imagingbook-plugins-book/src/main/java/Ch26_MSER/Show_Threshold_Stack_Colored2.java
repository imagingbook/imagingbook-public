package Ch26_MSER;

import java.awt.Color;

import ij.ImagePlus;
import ij.ImageStack;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;

public class Show_Threshold_Stack_Colored implements PlugInFilter {
	
	static Color FillColor = Color.blue; // Color(0, 150,255);
	
	private ImagePlus img;
	
	private ImageStack colorStack;
	private ImageStack thrshStack;
	
	@Override
	public int setup(String arg0, ImagePlus img) {
		this.img = img;
		return DOES_8G + NO_CHANGES;
	}
	
	@Override
	public void run(ImageProcessor ip) {
		makeStacks((ByteProcessor) ip);
		new ImagePlus("ColorStack-" + img.getShortTitle(), colorStack).show();
		new ImagePlus("ThesholdStack-" + img.getShortTitle(), thrshStack).show();
	}
	
	/**
	 * Creates a stack of binary images, one for thresholding the 
	 * given image at each gray value 0,..,255.
	 * @param bp
	 * @return
	 */
	private void makeStacks(ByteProcessor bp) {
		int width = bp.getWidth();
		int height = bp.getHeight();
		int[] rgb = {FillColor.getRed(), FillColor.getGreen(), FillColor.getBlue()};
		
		colorStack = new ImageStack(width, height);
		thrshStack = new ImageStack(width, height);
		
		for (int level = 0; level < 256; level++) {
			ColorProcessor cp = bp.convertToColorProcessor();
			ByteProcessor tp = (ByteProcessor) bp.duplicate();
			//IJ.log("level = " + level);
			for (int v = 0; v < cp.getHeight(); v++) {
				for (int u = 0; u < cp.getWidth(); u++) {
					if (bp.get(u, v) <= level) {
						cp.putPixel(u, v, rgb);
						tp.putPixel(u, v, level);
					}
				}
			}
			
			colorStack.addSlice("Level=" + level, cp);
			thrshStack.addSlice("Level=" + level, tp);
		}
	}

}
