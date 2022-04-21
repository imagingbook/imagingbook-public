package GenericFilterDemos;

import ij.ImagePlus;
import ij.plugin.filter.Convolver;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

public class Filter_Demo_A implements PlugInFilter {
	
	public int setup(String arg, ImagePlus im) {
		return DOES_ALL;				// works for any type of image!
	}

	public void run(ImageProcessor I) {
		float[] H = { 							// coefficient array H is one-dimensional!
			0.075f, 0.125f, 0.075f,
			0.125f, 0.200f, 0.125f,
			0.075f, 0.125f, 0.075f };

		Convolver cv = new Convolver();
		cv.setNormalize(false);			// turn off kernel normalization
		cv.convolve(I, H, 3, 3);		// apply the filter H to I
	}
}