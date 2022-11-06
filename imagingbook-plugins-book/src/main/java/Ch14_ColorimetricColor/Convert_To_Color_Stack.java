package Ch14_ColorimetricColor;

import java.awt.color.ColorSpace;

import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.color.colorspace.HlsColorSpace;
import imagingbook.common.color.colorspace.HsvColorSpace;
import imagingbook.common.color.colorspace.LabColorSpace;
import imagingbook.common.color.colorspace.LinearRgbColorSpace;
import imagingbook.common.color.colorspace.LuvColorSpace;
import imagingbook.common.image.ColorPack;

/**
 * ImageJ plugin, converts a sRGB color image to Lab, Luv, HLS, HSV or Linear
 * RGB color space and shows the resulting components as a image stack
 * (with float values).
 * 
 * @author WB
 * @see imagingbook.common.color.colorspace.LabColorSpace
 * @see imagingbook.common.color.colorspace.LuvColorSpace
 * @see imagingbook.common.color.colorspace.HlsColorSpace
 * @see imagingbook.common.color.colorspace.HsvColorSpace
 * @see imagingbook.common.color.colorspace.LinearRgbColorSpace
 * @see imagingbook.common.image.ColorPack
 */
public class Convert_To_Color_Stack implements PlugInFilter {
	
	private enum TargetSpaceType {
		Lab, Luv, HLS, HSV, LinearRGB
	}
	
	private static TargetSpaceType TargetSpace = TargetSpaceType.Lab;
	private static boolean ReconstructRGB = false;
	
	private ImagePlus im;

	@Override
	public int setup(String arg, ImagePlus im) {
		this.im = im;
		return DOES_RGB + NO_CHANGES;
	}

	@Override
	public void run(ImageProcessor ip) {
		
		if (!runDialog()) {
			return;
		}
		
		String title = im.getShortTitle();
		
		ColorSpace csp = null;
		switch(TargetSpace) {
		case Lab: csp = LabColorSpace.getInstance(); break;
		case Luv: csp = LuvColorSpace.getInstance(); break; 
		case HLS: csp = HlsColorSpace.getInstance(); break;
		case HSV: csp = HsvColorSpace.getInstance(); break;
		case LinearRGB:	csp = LinearRgbColorSpace.getInstance(); break;
		}
		
		ColorPack cPack = new ColorPack((ColorProcessor) ip);
		cPack.convertFromSrgbTo(csp);
		
		ImageStack stack = cPack.toImageStack();
		new ImagePlus(title + "-" + TargetSpace.toString(), stack).show();
		
		if (ReconstructRGB) {
			cPack.convertToSrgb();
			ColorProcessor cp = cPack.toColorProcessor();
			new ImagePlus(title + "-RGB", cp).show();	
		}
	}
	
	// ---------------------------------------
	
	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addMessage("Convert RGB image to Lab/Luv stack.");
		gd.addEnumChoice("Target color space", TargetSpace);
		gd.addCheckbox("Reconstruct RGB image", ReconstructRGB);
		
		gd.showDialog();
		if (gd.wasCanceled())
			return false;
		
		TargetSpace = gd.getNextEnumChoice(TargetSpaceType.class);
		ReconstructRGB = gd.getNextBoolean();
		return true;
	}

}
