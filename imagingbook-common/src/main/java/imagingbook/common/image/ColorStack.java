/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.image;

import static java.lang.Math.round;

import java.awt.color.ColorSpace;

import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import imagingbook.common.color.RgbUtils;
import imagingbook.common.color.colorspace.LabColorSpace;
import imagingbook.common.color.colorspace.LuvColorSpace;
import imagingbook.common.color.colorspace.sRgbUtil;


// TODO: add JavaDoc, unit tests

/**
 * This class defines static methods for handling color images represented as 
 * multi-plane image stacks.
 * The component values in all these images are assumed to be in [0,1].
 * Used for experimenting with different color spaces.
 */
public class ColorStack {
	
	public enum ColorStackType {
		sRGB("sR", "sG", "sB"),
		LinearRGB("R", "G", "B"), 
		Lab("L", "a", "b"), 
		Luv("L", "u", "v"),
//		XYZ("X", "Y", "Z"), 		// TODO: to be implemented
//		YCbCr("Y", "Cb", "Cr"), 	// TODO: to be implemented
		;

		protected final String[] componentLabels;

		ColorStackType(String... labels) {
			this.componentLabels = labels;
		}
	}
	
	private ColorStackType colorspace;
	private final int width, height;
	private final FloatProcessor[] processors;
	
	// ---------------------------------------------------------------------
	
	/**
	 * Constructor.
	 * @param cp a color image assumed to be in sRGB
	 */
	public ColorStack(ColorProcessor cp) {
		this.width = cp.getWidth();
		this.height = cp.getHeight();
		this.processors = makeProcessors(cp);
		setColorSpace(ColorStackType.sRGB);
	}
	
	private FloatProcessor[] makeProcessors(ColorProcessor cp) {		
		final int[] rgbPix = (int[]) cp.getPixels();
		
		FloatProcessor proc0 = new FloatProcessor(width, height);
		FloatProcessor proc1 = new FloatProcessor(width, height);
		FloatProcessor proc2 = new FloatProcessor(width, height);
		
		final float[] rPix = (float[]) proc0.getPixels();
		final float[] gPix = (float[]) proc1.getPixels();
		final float[] bPix = (float[]) proc2.getPixels();
		
		final int[] RGB = new int[3];
		for (int i = 0; i < rgbPix.length; i++) {
			RgbUtils.intToRgb(rgbPix[i], RGB);
    		rPix[i] = RGB[0] / 255f;
    		gPix[i] = RGB[1] / 255f;
    		bPix[i] = RGB[2] / 255f;
    	}

		return new FloatProcessor[] {proc0, proc1, proc2};
	}
	
	private void setColorSpace(ColorStackType newtype) {
		this.colorspace = newtype;
	}
	
	// -----------------------------------------------------------------
	
	public FloatProcessor[] getProcessors() {
		return this.processors;
	}
	
	public ColorStackType getColorspace() {
		return this.colorspace;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}

	// -----------------------------------------------------------------
	
	public void convertToLab() {
		if (this.colorspace == ColorStackType.Lab) {
			return; // nothing to do
		}
		if (this.colorspace == ColorStackType.sRGB) {
			sRgbToLab();
		}
		else {
			throw new IllegalStateException("cannot convert ColorStack to LAB from " + this.colorspace);
		}
	}
	
	private void sRgbToLab() {
		if (this.colorspace != ColorStackType.sRGB) {
			throw new IllegalStateException("color stack is not of type sRGB");
		}
		final float[] pix0 = (float[]) this.processors[0].getPixels();	// red
		final float[] pix1 = (float[]) this.processors[1].getPixels();	// green
		final float[] pix2 = (float[]) this.processors[2].getPixels();	// blue
		
		final ColorSpace lcs = new LabColorSpace();
		final float[] SRGB = new float[3];
		
		for (int i = 0; i < pix0.length; i++) {
			SRGB[0] = clipTo01(pix0[i]); 
			SRGB[1] = clipTo01(pix1[i]); 
			SRGB[2] = clipTo01(pix2[i]); 
			final float[] lab = lcs.fromRGB(SRGB);
			pix0[i] = lab[0];
			pix1[i] = lab[1];
			pix2[i] = lab[2];
		}
		
		setColorSpace(ColorStackType.Lab);
	}
	
	// -----------------------------------------------------------------
	
	public void convertToLuv() {
		if (this.colorspace == ColorStackType.Luv) {
			return; // nothing to do
		}
		if (this.colorspace == ColorStackType.sRGB) {
			sRgbToLuv();
		}
		else {
			throw new IllegalStateException("cannot convert ColorStack to LUV from " + this.colorspace);
		}
	}
	
	private void sRgbToLuv() {
		if (this.colorspace != ColorStackType.sRGB) {
			throw new IllegalStateException("color stack is not of type sRGB");
		}
		
		final float[] pix0 = (float[]) processors[0].getPixels();
		final float[] pix1 = (float[]) processors[1].getPixels();
		final float[] pix2 = (float[]) processors[2].getPixels();	
				
		final LuvColorSpace lcs = new LuvColorSpace();
		final float[] SRGB = new float[3];
		
		for (int i = 0; i < pix0.length; i++) {
			SRGB[0] = clipTo01(pix0[i]); 
			SRGB[1] = clipTo01(pix1[i]); 
			SRGB[2] = clipTo01(pix2[i]);
			final float[] luv = lcs.fromRGB(SRGB);
			pix0[i] = luv[0];
			pix1[i] = luv[1];
			pix2[i] = luv[2];
		}
		
		setColorSpace(ColorStackType.Luv);
	}
	
	// -----------------------------------------------------------------
	
	public void convertToLinearRgb() {
		if (this.colorspace == ColorStackType.LinearRGB) {
			return; // nothing to do
		}
		if (this.colorspace == ColorStackType.sRGB) {
			sRgbToLinearRgb();
		}
		else {
			throw new IllegalStateException("cannot convert ColorStack to LinearRGB from " + this.colorspace);
		}
	}
	
	private void sRgbToLinearRgb() {
		if (this.colorspace != ColorStackType.sRGB) {
			throw new IllegalStateException("color stack is not of type sRGB");
		}

		for (int k = 0; k < 3; k++) {
			final float[] pixels = (float[]) processors[k].getPixels();
			for (int i = 0; i < pixels.length; i++) {
				pixels[i] = (float) sRgbUtil.gammaInv(clipTo01(pixels[i]));
			}
		}

		setColorSpace(ColorStackType.LinearRGB);
	}
	
	// -----------------------------------------------------------------
	
	public void convertToSrgb() {
		 switch (this.colorspace) {
		 	case Lab : 	this.labToSrgb(); break;
		 	case Luv: 	this.luvToSrgb(); break;
			case LinearRGB: 	this.LinearRgbToSrgb(); break;
			case sRGB: 	break;	// already in sRGB, nothing to do
			default: throw new IllegalStateException("unknown color space: " + this.colorspace);
		 }
	}
	
	private void labToSrgb() {
		if (this.colorspace != ColorStackType.Lab) {
			throw new IllegalStateException("color stack is not of type Lab");
		}
		final LabColorSpace lcs = new LabColorSpace();
		
		final float[] pix0 = (float[]) (float[]) processors[0].getPixels();
		final float[] pix1 = (float[]) (float[]) processors[1].getPixels();
		final float[] pix2 = (float[]) (float[]) processors[2].getPixels();
		
		final float[] LAB = new float[3];
		for (int i = 0; i < pix0.length; i++) {
			LAB[0] = pix0[i];
			LAB[1] = pix1[i];
			LAB[2] = pix2[i];
			final float[] srgb = lcs.toRGB(LAB);
			pix0[i] = clipTo01(srgb[0]);
			pix1[i] = clipTo01(srgb[1]);
			pix2[i] = clipTo01(srgb[2]);
		}

		setColorSpace(ColorStackType.sRGB);
	}
	
	private void luvToSrgb() {
		if (this.colorspace != ColorStackType.Luv) {
			throw new IllegalStateException("color stack is not of type Luv");
		}
		final ColorSpace lcs = new LuvColorSpace();
		
		final float[] pix0 = (float[]) processors[0].getPixels();
		final float[] pix1 = (float[]) processors[1].getPixels();
		final float[] pix2 = (float[]) processors[2].getPixels();
		
		final float[] LUV = new float[3];
		for (int i = 0; i < pix0.length; i++) {
			LUV[0] = pix0[i];
			LUV[1] = pix1[i];
			LUV[2] = pix2[i]; 
			final float[] srgb = lcs.toRGB(LUV);
			pix0[i] = clipTo01(srgb[0]);
			pix1[i] = clipTo01(srgb[1]);
			pix2[i] = clipTo01(srgb[2]);
		}

		setColorSpace(ColorStackType.sRGB);
	}
	
	private void LinearRgbToSrgb() {
		if (this.colorspace != ColorStackType.LinearRGB) {
			throw new IllegalStateException("color stack is not of type RGB");
		}

		for (int k = 0; k < 3; k++) {
			final float[] pixels = (float[]) processors[k].getPixels();		
			for (int i = 0; i <pixels.length; i++) {
				pixels[i] = (float) sRgbUtil.gammaFwd(clipTo01(pixels[i]));
			}
		}

		setColorSpace(ColorStackType.sRGB);
	}
	
	// ---------------------------------------------------------------
	
	public ColorProcessor toColorProcessor() {
		if (this.colorspace != ColorStackType.sRGB) {
			throw new IllegalStateException("color stack must be of type sRGB to comvert to ColorProcessor");
		}
		final float[] pix0 = (float[]) processors[0].getPixels();
		final float[] pix1 = (float[]) processors[1].getPixels();
		final float[] pix2 = (float[]) processors[2].getPixels();
		
		ColorProcessor cp = new ColorProcessor(this.getWidth(), this.getHeight());
		final int[] srgbPix = (int[]) cp.getPixels();
		
		for (int i = 0; i < srgbPix.length; i++) {
            int r = clipTo255(round(pix0[i] * 255));
    		int g = clipTo255(round(pix1[i] * 255));
    		int b = clipTo255(round(pix2[i] * 255));
//    		if (r < 0) r = 0; else if (r > 255) r = 255;
//    		if (g < 0) g = 0; else if (g > 255) g = 255;
//    		if (b < 0) b = 0; else if (b > 255) b = 255;	
    		//srgbPix[i] = ((r & 0xff)<<16) | ((g & 0xff)<<8) | b & 0xff;
    		srgbPix[i] = RgbUtils.rgbToInt(r, g, b);
    	}
		return cp;
	}
	
	// ---------------- numeric operations ---------------------------
	
	public void multiply(double value) {
		for (int k = 0; k < 3; k++)  {
			this.processors[k].multiply(value);
		}
	}
	
	public void convolve(float[] kernel, int w, int h) {
		for (int k = 0; k < 3; k++)  {
			this.processors[k].convolve(kernel, w, h);
		}
	}
	
	public FloatProcessor max() { 
		FloatProcessor rp = (FloatProcessor) this.processors[0].duplicate();
		float[] rpix = (float[]) rp.getPixels();
		for (int k = 1; k < 3; k++) {
			float[] pixels = (float[]) this.processors[0].getPixels();
			for (int i = 0; i < pixels.length; i++) {
				float p = pixels[i];
				if (p > rpix[i])
					rpix[i] = p;
			}
		}
		return rp;
	}
	
	// ------------------------------------------------------------------
	
	private float clipTo01(float val) {
		if (val < 0) return 0f;
		if (val > 1) return 1f;
		return val;
	}
	
	private int clipTo255(int val) {
		if (val < 0) return 0;
		if (val > 255) return 255;
		return val;
	}
    
}
