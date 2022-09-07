/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.sift;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.sift.SiftDetector.NeighborhoodType3D;
import imagingbook.core.resource.ImageResource;
import imagingbook.sampleimages.GeneralSampleImage;
import imagingbook.testimages.SiftTestImage;


public class SiftDetectorTest {
	
	private SiftDetector.Parameters params = null;
	

	@Test
	public void test1() {
		params = new SiftDetector.Parameters();	
		
		params.nhType = NeighborhoodType3D.NH18;
		params.sigmaS = 0.5;
		params.sigma0 = 1.6;
		params.P = 4;
		params.Q = 3;
		params.tMag = 0.01;
		params.tPeak = params.tMag;
		params.tExtrm = 0.0;
		params.nRefine = 5;
		params.rhoMax = 10.0;
		params.nOrient = 36;
		params.nSmooth = 2;
		params.tDomOr = 0.8;
		params.sDesc = 10.0;
		params.nSpat = 4;
		params.nAngl = 8;
		params.tFclip = 0.2;
		params.sFscale = 512.0;

		runSift(GeneralSampleImage.MonasterySmall, 291);
		runSift(SiftTestImage.Box00, 38);
		runSift(SiftTestImage.HalfDiskH, 8);
		runSift(SiftTestImage.HalfDiskV, 8);
		runSift(SiftTestImage.StarsH, 259);
		runSift(SiftTestImage.StarsV, 251);
	}
	
	private void runSift(ImageResource res, int siftExpected) {
//		System.out.println("running " + res);
		ImageProcessor ip = res.getImage().getProcessor();
		FloatProcessor fp = ip.convertToFloatProcessor();
		
		SiftDetector detector = new SiftDetector(fp, params);
		List<SiftDescriptor> features = detector.getSiftFeatures();
		
//		System.out.println(res + ": " + features.size());
		assertEquals("detected SIFT features (" + res + ")", siftExpected, features.size());
	}

}
