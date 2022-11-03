/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package Ch05_Edges_Contours;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.edges.CannyEdgeDetector;
import imagingbook.common.edges.CannyEdgeDetector.Parameters;

/**
 * <p>
 * ImageJ plugin showing the use of the Canny edge detector in
 * its simplest form. It works on all image types.
 * The original image is not modified. 
 * See Sec. 5.5 of [1] for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic
 * Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 *
 * @see CannyEdgeDetector
 */
public class Canny_Edges implements PlugInFilter {
	
	private static CannyEdgeDetector.Parameters params = new Parameters();
	static {
		params.gSigma = 3.0;	// sigma of Gaussian (3.0)
		params.hiThr  = 20.0;	// high threshold (20% of max. edge magnitude)
		params.loThr  = 5.0;	// low threshold (5% of max. edge magnitude)
	}
	
	private ImagePlus im;
	
	@Override
	public int setup(String arg0, ImagePlus im) {
		this.im = im;
		return DOES_ALL + NO_CHANGES;
	}

	@Override
	public void run(ImageProcessor ip) {			
		CannyEdgeDetector detector = new CannyEdgeDetector(ip, params);
		
		ByteProcessor edge = detector.getEdgeBinary();
		(new ImagePlus(im.getShortTitle() + "-CannyEdges", edge)).show();
		
//		FloatProcessor eMag = detector.getEdgeMagnitude();
//		FloatProcessor eOrt = detector.getEdgeOrientation();
//		List<List<Point>> edgeTraces = detector.getEdgeTraces();	

	}
}
