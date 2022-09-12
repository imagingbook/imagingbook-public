/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.ij;

import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;

import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.color.RgbUtils;
import imagingbook.common.math.Matrix;
import imagingbook.sampleimages.GeneralSampleImage;
import imagingbook.testutils.ImageTestUtils;

public class IjUtilsTest {

	private static int W = 97;
	private static int H = 51;
	
	@Test
	public void testByteArrayReadWrite() {
		ByteProcessor bp1 = makeRandomByteProcessor(W, H);
		byte[][] A = IjUtils.toByteArray(bp1);
		
		ByteProcessor bp2 = IjUtils.toByteProcessor(A);
		assertTrue(ImageTestUtils.match(bp1, bp2));
	}
	
	@Test
	public void testIntArrayReadWrite() {
		ByteProcessor bp1 = makeRandomByteProcessor(W, H);
		int[][] A = bp1.getIntArray();
		
		ByteProcessor bp2 = IjUtils.toByteProcessor(A);
		assertTrue(ImageTestUtils.match(bp1, bp2));
	}
	
	@Test
	public void testFloatArrayReadWrite() {
		FloatProcessor fp1 = makeRandomFloatProcessor(W, H);
		float[][] A = IjUtils.toFloatArray(fp1);
		
		FloatProcessor fp2 = IjUtils.toFloatProcessor(A);
		assertTrue(ImageTestUtils.match(fp1, fp2, 1e-6));
	}
	
	@Test
	public void testDoubleArrayReadWrite() {
		FloatProcessor fp1 = makeRandomFloatProcessor(W, H);
		double[][] A = IjUtils.toDoubleArray(fp1);
		
		FloatProcessor fp2 = IjUtils.toFloatProcessor(A);
		assertTrue(ImageTestUtils.match(fp1, fp2, 1e-6));
	}
	
	// ----------------------------------------------------------------
	
	@Test	// convert using default weights
	public void testToByteProcessorColorProcessor() {
		ImageProcessor ip = GeneralSampleImage.Clown.getImage().getProcessor();
		assertTrue(ip instanceof ColorProcessor);
		ByteProcessor fp1 = IjUtils.toByteProcessor((ColorProcessor) ip, null);
		ByteProcessor fp2 = makeLumaByte((ColorProcessor) ip, RgbUtils.getDefaultWeights());	// see below
		assertTrue(ImageTestUtils.match(fp1, fp2, 1e-3));
	}

	@Test	// convert using default weights
	public void testToByteProcessorColorProcessorWeights1() {
		ImageProcessor ip = GeneralSampleImage.Clown.getImage().getProcessor();
		assertTrue(ip instanceof ColorProcessor);
		ByteProcessor fp1 = IjUtils.toByteProcessor((ColorProcessor) ip, null);
		ByteProcessor fp2 = makeLumaByte((ColorProcessor) ip, RgbUtils.getDefaultWeights());	// see below
		assertTrue(ImageTestUtils.match(fp1, fp2, 1e-3));
	}
	
	@Test	// default conversion must honor the processor's weights (if set)
	public void testToByteProcessorColorProcessorWeights2() {
		double[] weights = {0.3, 0.5, 0.2};	// special weights!
		ImageProcessor ip = GeneralSampleImage.Clown.getImage().getProcessor();
		assertTrue(ip instanceof ColorProcessor);
		((ColorProcessor) ip).setRGBWeights(weights);
		
		ByteProcessor fp1 = IjUtils.toByteProcessor((ColorProcessor) ip);
		ByteProcessor fp2 = makeLumaByte((ColorProcessor) ip, weights);	// see below
		assertTrue(ImageTestUtils.match(fp1, fp2, 1e-3));
	}
	
	// ----------------------------------------------------------------

	@Test	// convert using default weights
	public void testToFloatProcessorColorProcessor() {
		ImageProcessor ip = GeneralSampleImage.Clown.getImage().getProcessor();
		assertTrue(ip instanceof ColorProcessor);
		FloatProcessor fp1 = IjUtils.toFloatProcessor((ColorProcessor) ip, null);
		FloatProcessor fp2 = makeLumaFloat((ColorProcessor) ip, RgbUtils.getDefaultWeights());	// see below
		assertTrue(ImageTestUtils.match(fp1, fp2, 1e-3));
	}

	@Test	// convert using specific weights
	public void testToFloatProcessorColorProcessorWeights1() {
		double[] weights = RgbUtils.ITU601RgbWeights;
		ImageProcessor ip = GeneralSampleImage.Clown.getImage().getProcessor();
		assertTrue(ip instanceof ColorProcessor);
		FloatProcessor fp1 = IjUtils.toFloatProcessor((ColorProcessor) ip, weights);
		FloatProcessor fp2 = makeLumaFloat((ColorProcessor) ip, weights);	// see below
		assertTrue(ImageTestUtils.match(fp1, fp2, 1e-3));
	}
	
	@Test	// default conversion must honor the processor's weights (if set)
	public void testToFloatProcessorColorProcessorWeights2() {
		double[] weights = {0.3, 0.5, 0.2};	// special weights!
		ImageProcessor ip = GeneralSampleImage.Clown.getImage().getProcessor();
		assertTrue(ip instanceof ColorProcessor);
		((ColorProcessor) ip).setRGBWeights(weights);	
		
		FloatProcessor fp1 = IjUtils.toFloatProcessor((ColorProcessor) ip);
		FloatProcessor fp2 = makeLumaFloat((ColorProcessor) ip, weights);	// see below
		assertTrue(ImageTestUtils.match(fp1, fp2, 1e-3));
	}
	
	// ----------------------------------------------------------------
	
	@Test
	public void testConvolve() {
		float[][] H = {
				{1, 2, 3},
				{5, 6, 4},
				{2, 1, 0}};
		Matrix.multiplyD((float)(1/Matrix.sum(H)), H);

		ImageProcessor ip = GeneralSampleImage.MonasterySmall.getImage().getProcessor();
		IjUtils.convolve(ip, H);
		// TODO: unfinished, checks missing!
	}
	
	@Test
	public void testConvolveX() {
		float[] h = {1, 3, 2, 0, -1};
		Matrix.multiplyD((float)(1/Matrix.sum(h)), h);

		ImageProcessor ip = GeneralSampleImage.MonasterySmall.getImage().getProcessor();
		IjUtils.convolveX(ip, h);
		// TODO: unfinished, checks missing!
	}
	
	@Test
	public void testConvolveY() {
		float[] h = {1, 3, 2, 0, -1};
		Matrix.multiplyD((float)(1/Matrix.sum(h)), h);

		ImageProcessor ip = GeneralSampleImage.MonasterySmall.getImage().getProcessor();
		IjUtils.convolveY(ip, h);
		// TODO: unfinished, checks missing!
	}
	
	@Test
	public void testConvolveXY() {
		float[] h = {1, 3, 2, 0, -1};
		Matrix.multiplyD((float)(1/Matrix.sum(h)), h);

		ImageProcessor ip = GeneralSampleImage.MonasterySmall.getImage().getProcessor();
		IjUtils.convolveXY(ip, h);
		// TODO: unfinished, checks missing!
	}
	
	
	// ----------------------------------------------------------------
	// ----------------------------------------------------------------
	
	private ByteProcessor makeRandomByteProcessor(int w, int h) {
		Random rg = new Random(17);
		ByteProcessor bp = new ByteProcessor(w, h);
		for (int v = 0; v < h; v++) {
			for (int u = 0; u < w; u++) {
				bp.set(u, v, rg.nextInt(256));
			}
		}
		return bp;
	}
	
	private FloatProcessor makeRandomFloatProcessor(int w, int h) {
		Random rg = new Random(17);
		FloatProcessor fp = new FloatProcessor(w, h);
		for (int v = 0; v < h; v++) {
			for (int u = 0; u < w; u++) {
				fp.setf(u, v, rg.nextFloat());
			}
		}
		return fp;
	}
	
	// ------------------------------------------------------------------------
	
	private FloatProcessor makeLumaFloat(ColorProcessor cp, double[] weights) {
		int width = cp.getWidth();
		int height = cp.getHeight();
		FloatProcessor fp = new FloatProcessor(width, height);
		int[] rgb = new int[3];
		for (int u = 0; u < width; u++) {
			for (int v = 0; v < height; v++) {
				cp.getPixel(u, v, rgb);
				fp.setf(u, v, RgbUtils.rgbToFloat(rgb, weights));
			}
		}
		return fp;
	}
	
	private ByteProcessor makeLumaByte(ColorProcessor cp, double[] weights) {
		int width = cp.getWidth();
		int height = cp.getHeight();
		ByteProcessor bp = new ByteProcessor(width, height);
		int[] rgb = new int[3];
		for (int u = 0; u < width; u++) {
			for (int v = 0; v < height; v++) {
				cp.getPixel(u, v, rgb);
				bp.set(u, v, RgbUtils.rgbToInt(rgb, weights));
			}
		}
		return bp;
	}

}
