package imagingbook.common.color.filters;

import ij.ImagePlus;
import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
import imagingbook.common.ij.GuiTools;

public abstract class Utils {
	
	public static void showMask(CircularMask mask, String title) {
		ByteProcessor bp = new FloatProcessor(mask.getMask()).convertToByteProcessor(false);
		bp.threshold(0);
		ImagePlus im = new ImagePlus(title, bp);
		im.show();
		GuiTools.zoomExact(im, 32);
	}

}
