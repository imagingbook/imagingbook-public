/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.mser;

import static imagingbook.sampleimages.MserTestImage.AllBlack;
import static imagingbook.sampleimages.MserTestImage.AllWhite;
import static imagingbook.sampleimages.MserTestImage.Blob1;
import static imagingbook.sampleimages.MserTestImage.Blob2;
import static imagingbook.sampleimages.MserTestImage.Blob3;
import static imagingbook.sampleimages.MserTestImage.BlobLevelTest;
import static imagingbook.sampleimages.MserTestImage.BlobLevelTestNoise;
import static imagingbook.sampleimages.MserTestImage.BlobOriented;
import static imagingbook.sampleimages.MserTestImage.BlobsInWhite;
import static imagingbook.sampleimages.MserTestImage.BoatsTiny;
import static imagingbook.sampleimages.MserTestImage.BoatsTinyB;
import static imagingbook.sampleimages.MserTestImage.BoatsTinyBW;
import static imagingbook.sampleimages.MserTestImage.BoatsTinyW;
import static imagingbook.sampleimages.MserTestImage.BoatsTinyW2;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import ij.process.ByteProcessor;
import imagingbook.common.mser.MserDetector.Parameters;
import imagingbook.common.mser.components.Component;
import imagingbook.common.mser.components.ComponentTree.Method;
import imagingbook.core.resource.ImageResource;

public class MserDetectorTest {

	private Parameters params = null;
	MserDetector detector = null;

	/**
	 * Runs validation on component trees from different images.
	 */
	@Test
	public void test1() {
		params = new Parameters();	// MSER default parameters

		params.method = Method.LinearTime;
		params.delta = 5;
		params.minAbsComponentArea = 	3;
		params.minRelCompSize = 	0.0001;
		params.maxRelCompSize = 	0.25;
		params.maxSizeVariation = 		0.25;
		params.minDiversity = 			0.50;
		params.constrainEllipseSize = 	true;
		params.minCompactness = 		0.2;		
		params.validateComponentTree =	false;

		detector = new MserDetector(params);
		
		runMser(Blob1, 3);
		runMser(Blob2, 6);
		runMser(Blob3, 9);
		runMser(BlobLevelTest, 2);
		runMser(BlobLevelTestNoise, 2);
		runMser(BlobOriented, 3);
		runMser(BlobsInWhite, 3);
		runMser(BoatsTiny, 21);
		runMser(BoatsTinyB, 22);
		runMser(BoatsTinyBW, 1);
		runMser(BoatsTinyW, 21);
		runMser(BoatsTinyW2, 22);
		runMser(AllBlack, 0);
		runMser(AllWhite, 0);
	}

	private void runMser(ImageResource res, int mserExpected) {
//		System.out.println("running " + res);
		ByteProcessor ip = (ByteProcessor) res.getImage().getProcessor();
		List<Component<MserData>> msers = detector.applyTo((ByteProcessor) ip);
//		System.out.println(msers.size());
		assertEquals("detected MSER components (" + res + ")", mserExpected, msers.size());
	}

}
