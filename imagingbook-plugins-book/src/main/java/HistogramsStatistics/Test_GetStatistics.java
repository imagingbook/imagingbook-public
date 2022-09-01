/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package HistogramsStatistics;

import ij.IJ;
import ij.ImagePlus;
import ij.measure.Measurements;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;
import ij.process.ImageStatistics;

/**
 * This plugin tests ImageJ's ImageStatistics class. It is poorly documented and the API is suboptimal.
 * Don't use!
 * 
 * @author WB
 */
public class Test_GetStatistics implements PlugIn {

	public void run(String arg0) {
		ImagePlus imp = IJ.getImage();
		if (imp == null) 
			return;
		
		ImageProcessor ip = imp.getProcessor();
		
		//ImageStatistics stats = ip.getStatistics();
		int mOptions = 
				Measurements.AREA + 
				Measurements.CENTER_OF_MASS +
				Measurements.CENTROID +
				Measurements.CIRCULARITY +
				Measurements.KURTOSIS +
				Measurements.MEAN + 
				Measurements.MODE + 
				Measurements.MEDIAN + 
//				Measurements.ELLIPSE +
				Measurements.MIN_MAX;
		
		ImageStatistics stats = ImageStatistics.getStatistics(ip, mOptions, null);
		
		IJ.log("area = " 			+ stats.area);
		IJ.log("areaFraction = " 	+ stats.areaFraction);
		IJ.log("histMax = " 		+ stats.histMax);
		IJ.log("histMin = " 		+ stats.histMin);
		IJ.log("kurtosis = " 		+ stats.kurtosis);
		IJ.log("mean = " 			+ stats.mean);
		IJ.log("median = " 			+ stats.median);
		IJ.log("stdDev = " 			+ stats.stdDev);
		IJ.log("skewness = " 		+ stats.skewness);
		IJ.log("xCentroid = " 		+ stats.xCentroid);
		IJ.log("yCentroid = " 		+ stats.yCentroid);
		IJ.log("xCenterOfMass = " 	+ stats.xCenterOfMass);
		IJ.log("yCenterOfMass = " 	+ stats.yCenterOfMass);
		IJ.log("major = " 			+ stats.major);
		IJ.log("minor = " 			+ stats.minor);
		IJ.log("angle = " 			+ stats.angle);
		
		IJ.log(stats.toString());
	}

}
