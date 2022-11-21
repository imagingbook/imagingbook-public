package Ch25_SiftFeatures;

import java.awt.Rectangle;

import ij.ImagePlus;
import ij.ImageStack;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

public class Split_To_Stack implements PlugInFilter {
	
	private ImagePlus im = null;

	@Override
	public int setup(String arg, ImagePlus im) {
		this.im = im;
		return DOES_ALL + NO_CHANGES;
	}

	@Override
	public void run(ImageProcessor ip) {
		Rectangle rect = ip.getRoi();	//roi.getBounds();
		int x = rect.x;
		int y = rect.y;
		int w = rect.width / 2;
		int h = rect.height;
		String title = im.getShortTitle();
		
		ip.setRoi(new Rectangle(x, y, w, h));
		ImageProcessor ip1 = ip.crop();
		ip.setRoi(new Rectangle(x + w, y, w, h));
		ImageProcessor ip2 = ip.crop();
		
//		new ImagePlus("ip1", ip1).show();
//		new ImagePlus("ip2", ip2).show();
		
		ImageStack stack = new ImageStack(w, h);
		stack.addSlice("left", ip1);
		stack.addSlice("right", ip2);
		
		new ImagePlus(title + "-stack", stack).show();
	}

}
