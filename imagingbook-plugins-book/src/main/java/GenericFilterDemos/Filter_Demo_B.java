package GenericFilterDemos;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

public class Filter_Demo_B implements PlugInFilter {
	
	public int setup(String arg, ImagePlus imp) {
		return DOES_ALL;	// works for any type of image!
	}

	public void run(ImageProcessor I) {
		float[] H = { 		// this is a 1D array!
				1, 2, 1,
				2, 4, 2,
				1, 2, 1 
		};
		I.convolve(H, 3, 3);		// apply H as a 3 x 3 filter kernel
	}
}