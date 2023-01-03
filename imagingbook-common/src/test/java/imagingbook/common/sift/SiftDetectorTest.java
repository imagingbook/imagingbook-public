/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.sift;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.sift.SiftDetector.NeighborhoodType3D;
import imagingbook.common.sift.scalespace.DogOctave;
import imagingbook.common.sift.scalespace.DogScaleSpace;
import imagingbook.common.sift.scalespace.GaussianOctave;
import imagingbook.common.sift.scalespace.GaussianScaleSpace;
import imagingbook.common.sift.scalespace.ScaleLevel;
import imagingbook.core.resource.ImageResource;
import imagingbook.sampleimages.GeneralSampleImage;
import imagingbook.testimages.SiftTestImage;


public class SiftDetectorTest {
	
	private static SiftParameters params = new SiftParameters();
	static {
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
	}
	
	@Test
	public void testGaussianScaleSpace() {
		ImageProcessor ip = GeneralSampleImage.MonasterySmall.getImagePlus().getProcessor();
		FloatProcessor fp = ip.convertToFloatProcessor();
		SiftDetector detector = new SiftDetector(fp, params);
		
		GaussianScaleSpace G = detector.getGaussianScaleSpace();
		assertEquals(params.P, G.getP());
		assertEquals(params.Q, G.getQ());
		assertEquals(params.sigmaS, G.getSigma_s(), 1e-6);
		assertEquals(params.sigma0, G.getSigma_0(), 1e-6);
		
		int botLevel = G.getBottomLevelIndex();
		assertEquals(-1, botLevel);
		int topLevel = G.getTopLevelIndex();
		assertEquals(params.Q + 1, topLevel);
		
		for (int p = 0; p < G.getP(); p++) { 
			GaussianOctave oct = G.getOctave(p);
			assertNotNull(oct);
			assertEquals(p, oct.getOctaveIndex());
			
			for (int q = botLevel; q <= topLevel; q++) {
				ScaleLevel lvl = oct.getLevel(q);
				assertNotNull(lvl);
				assertNotNull(lvl.getData());
				assertEquals(oct.getAbsoluteScale(p, q), lvl.getAbsoluteScale(), 1e-6);
			}
		}
	}
	
	@Test
	public void testDogScaleSpace() {
		ImageProcessor ip = GeneralSampleImage.MonasterySmall.getImagePlus().getProcessor();
		FloatProcessor fp = ip.convertToFloatProcessor();
		SiftDetector detector = new SiftDetector(fp, params);
		
		DogScaleSpace D = detector.getDogScaleSpace();
		assertEquals(params.P, D.getP());
		assertEquals(params.Q, D.getQ());
		assertEquals(params.sigmaS, D.getSigma_s(), 1e-6);
		assertEquals(params.sigma0, D.getSigma_0(), 1e-6);
		
		int botLevel = D.getBottomLevelIndex();
		assertEquals(-1, botLevel);
		int topLevel = D.getTopLevelIndex();
		assertEquals(params.Q, topLevel);
		
		for (int p = 0; p < D.getP(); p++) { 
			DogOctave oct = D.getOctave(p);
			assertNotNull(oct);
			assertEquals(p, oct.getOctaveIndex());
			for (int q = botLevel; q <= topLevel; q++) {
				ScaleLevel lvl = oct.getLevel(q);
				assertNotNull(lvl);
				assertNotNull(lvl.getData());
				assertEquals(oct.getAbsoluteScale(p, q), lvl.getAbsoluteScale(), 1e-6);
			}
		}	
	}

	@Test
	public void testSiftDetectionOnImages() {
		runSift(GeneralSampleImage.MonasterySmall, 291);
		runSift(SiftTestImage.Box00, 38);
		runSift(SiftTestImage.HalfDiskH, 8);
		runSift(SiftTestImage.HalfDiskV, 8);
		runSift(SiftTestImage.StarsH, 259);
		runSift(SiftTestImage.StarsV, 251);
	}
	
	
	@Test(expected = IllegalArgumentException.class)
	public void testSiftDetectorFlatImage() {
		ByteProcessor bp = new ByteProcessor(50, 30);	// flat image
		bp.setColor(17);
		bp.fill();
		new SiftDetector(bp.convertToFloatProcessor());
	}
	
	// ---------------------------------------------------
	
	private void runSift(ImageResource res, int siftExpected) {
//		System.out.println("running " + res);
		ImageProcessor ip = res.getImagePlus().getProcessor();
		FloatProcessor fp = ip.convertToFloatProcessor();
		
		SiftDetector detector = new SiftDetector(fp, params);
		List<SiftDescriptor> features = detector.getSiftFeatures();
		
//		System.out.println(res + ": " + features.size());
		assertEquals("detected SIFT features (" + res + ")", siftExpected, features.size());
	}

}
