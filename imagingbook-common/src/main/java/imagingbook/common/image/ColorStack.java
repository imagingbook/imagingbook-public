/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.image;

import java.awt.color.ColorSpace;

import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import imagingbook.common.color.colorspace.LabColorSpace;
import imagingbook.common.color.colorspace.LuvColorSpace;
import imagingbook.common.color.colorspace.sRgbUtil;


// TODO: add JavaDoc, unit tests

/**
 * This class defines a "color stack" as a subtype of {@link PixelPack}
 * with exactly 3 components (slices) and all values in [0,1].
 * It allows simple conversion between various colorimetric color 
 * spaces (currently implemented are sRGB, LinearRGB, CIELab and CIELuv).
 * A {@link ColorStack} may be created from an existing
 * {@link ColorProcessor} instance whose pixels are assumed to be in sRGB
 * color space.
 */
public class ColorStack extends PixelPack {
	
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
	
	// ---------------------------------------------------------------------
	
	/**
	 * Constructor.
	 * @param cp a color image assumed to be in sRGB
	 */
	public ColorStack(ColorProcessor cp) {
		super(cp, 1.0/255, null);
		setColorSpace(ColorStackType.sRGB);
	}
	
	// -----------------------------------------------------------------
	
	public FloatProcessor[] getProcessors() {
		FloatProcessor[] processors = new FloatProcessor[3];
		for (int k = 0; k < 3; k++) {
			processors[k] = new FloatProcessor(width, height, data[k]);
		}
		return processors;
	}
	
	public ColorStackType getColorspace() {
		return this.colorspace;
	}
	
	private void setColorSpace(ColorStackType newtype) {
		this.colorspace = newtype;
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

		final ColorSpace lcs = LabColorSpace.getInstance();
		final float[] SRGB = new float[3];
		
		for (int i = 0; i < length; i++) {
			getPix(i, SRGB);
			clipTo01(SRGB);
			float[] lab = lcs.fromRGB(SRGB);
			setPix(i, lab);
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
				
		final LuvColorSpace lcs = LuvColorSpace.getInstance();
		final float[] SRGB = new float[3];
		
		for (int i = 0; i < length; i++) {
			getPix(i, SRGB);
			float[] luv = lcs.fromRGB(SRGB);
			setPix(i, luv);
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
			for (int i = 0; i < length; i++) {
				data[k][i] = (float) sRgbUtil.gammaInv(clipTo01(data[k][i]));
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
		final LabColorSpace lcs = LabColorSpace.getInstance();
		final float[] LAB = new float[3];
		
		for (int i = 0; i < length; i++) {
			getPix(i, LAB);
			float[] srgb = lcs.toRGB(LAB);
			setPix(i, srgb);
		}

		setColorSpace(ColorStackType.sRGB);
	}
	
	private void luvToSrgb() {
		if (this.colorspace != ColorStackType.Luv) {
			throw new IllegalStateException("color stack is not of type Luv");
		}
		final ColorSpace lcs = LuvColorSpace.getInstance();
		
		final float[] LUV = new float[3];
		for (int i = 0; i < length; i++) {
			getPix(i, LUV);
			final float[] srgb = lcs.toRGB(LUV);
			setPix(i, srgb);
		}

		setColorSpace(ColorStackType.sRGB);
	}
	
	private void LinearRgbToSrgb() {
		if (this.colorspace != ColorStackType.LinearRGB) {
			throw new IllegalStateException("color stack is not of type RGB");
		}

		for (int k = 0; k < 3; k++) {
			for (int i = 0; i < length; i++) {
				data[k][i] = (float) sRgbUtil.gammaFwd(clipTo01(data[k][i]));
			}
		}

		setColorSpace(ColorStackType.sRGB);
	}
	
	// ---------------------------------------------------------------
	
	@Override
	public ColorProcessor toColorProcessor() {
		if (this.colorspace != ColorStackType.sRGB) {
			throw new IllegalStateException("color stack must be of type sRGB to comvert to ColorProcessor");
		}
		return super.toColorProcessor(255);
	}
	
	@Override	// arbitrary scaling is inhibited
	public ColorProcessor toColorProcessor(double scale) {
		throw new UnsupportedOperationException();
	}
	
	// ------------------------------------------------------------------
	
	private float clipTo01(float val) {
		if (val < 0) return 0f;
		if (val > 1) return 1f;
		return val;
	}
	
	private void clipTo01(float[] vals) {
		for (int i = 0; i < vals.length; i++) {
			vals[i] = clipTo01(vals[i]);
		}
	}
    
}
