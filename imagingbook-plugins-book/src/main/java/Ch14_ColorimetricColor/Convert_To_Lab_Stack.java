package Ch14_ColorimetricColor;

import ij.ImagePlus;
import ij.ImageStack;
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.color.colorspace.LabColorSpace;
import imagingbook.common.image.ColorPack;

/**
 * ImageJ plugin, converts a sRGB color image to Lab color space and shows the
 * resulting L, a, b components as a image stack.
 * 
 * @author WB
 * @see imagingbook.common.color.colorspace.LabColorSpace
 * @see imagingbook.common.image.ColorPack
 */
public class Convert_To_Lab_Stack implements PlugInFilter {
	
	private ImagePlus im;

	@Override
	public int setup(String arg, ImagePlus im) {
		this.im = im;
		return DOES_RGB + NO_CHANGES;
	}

	@Override
	public void run(ImageProcessor ip) {
		
		LabColorSpace lab = LabColorSpace.getInstance();
		
		ColorPack colStack = new ColorPack((ColorProcessor) ip);
		colStack.convertFromSrgbTo(lab);
		
		ImageStack is = colStack.toImageStack();
		is.setSliceLabel("L", 1);
		is.setSliceLabel("a", 2);
		is.setSliceLabel("b", 3);
		new ImagePlus(im.getShortTitle() + "-Lab", is).show();
		
		colStack.convertToSrgb();
		ColorProcessor cp = colStack.toColorProcessor();
		new ImagePlus(im.getShortTitle() + "-RGB", cp).show();	
	}

}
