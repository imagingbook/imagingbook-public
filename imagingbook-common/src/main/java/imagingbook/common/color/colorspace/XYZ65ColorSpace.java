package imagingbook.common.color.colorspace;

import static imagingbook.common.color.colorspace.StandardIlluminant.D50;
import static imagingbook.common.color.colorspace.StandardIlluminant.D65;

import java.awt.color.ColorSpace;

import imagingbook.common.color.RgbUtils;

public class XYZ65ColorSpace extends ColorSpace {
	private static final long serialVersionUID = 1L;
	
	private static final ChromaticAdaptation catD65toD50 = new BradfordAdaptation(D65, D50);
	private static final ChromaticAdaptation catD50toD65 = new BradfordAdaptation(D50, D65);
	private static final sRgb65ColorSpace sRGBcsp = sRgb65ColorSpace.getInstance();
	
	private static final XYZ65ColorSpace instance = new XYZ65ColorSpace();
	
	public static XYZ65ColorSpace getInstance() {
		return instance;
	}

	private XYZ65ColorSpace() {
		super(TYPE_XYZ, 3);
	}

	// ----------------------------------------------------

	/**
	 * Transforms D65-based XYZ color coordinates to sRGB colors.
	 * This method implements {@link ColorSpace#toRGB(float[])} assuming 
	 * D65-based sRGB color coordinates.
	 * Note that the returned RGB values are in [0,1] (see
	 * {@link RgbUtils#unnormalize(float[])} for conversion to [0,255] int-values).
	 * 
	 * @param XYZ65 a D65-based XYZ color
	 * @return sRGB coordinates (D65-based)
	 */
	@Override
	public float[] toRGB(float[] XYZ65) {
		return sRGBcsp.fromCIEXYZ(XYZ65);
	}

	/**
	 * Transforms a sRGB color to  XYZ65 coordinates.
	 * This method implements {@link ColorSpace#fromRGB(float[])} assuming
	 * D65-based sRGB color coordinates.
	 * Note that sRGB values are assumed to be in [0,1] (see
	 * {@link RgbUtils#normalize(int...)} for conversion from [0,255] int-values).
	 * 
	 * @param srgb a sRGB color (D65-based)
	 * @return the associated D65 based XYZ color
	 */
	@Override
	public float[] fromRGB(float[] srgb) {
		return sRGBcsp.toCIEXYZ(srgb);
	}
	
	/**
	 * Converts the specified D65-based XYZ color to D50-based XYZ coordinates.
	 * This method implements {@link ColorSpace#toCIEXYZ(float[])}) assuming that
	 * the input color coordinate is D50-based.
	 * 
	 * @param XYZ65 D50-based XYZ  color
	 * @return D50-based XYZ coordinates
	 */
	@Override
	public float[] toCIEXYZ(float[] XYZ65) {
		return catD65toD50.applyTo(XYZ65);
	}
	
	/**
	 * Converts color points in D50-based XYZ space to D65-based XYZ coordinates.
	 * This method implements {@link ColorSpace#fromCIEXYZ(float[])}), assuming that 
	 * the specified color coordinate is in D50-based XYZ space (with components in [0,1]).
	 * 
	 * @param XYZ50 a color in D50-based XYZ space (components in [0,1])
	 * @return the associated D65-based XYZ color
	 */
	@Override
	public float[] fromCIEXYZ(float[] XYZ50) {	
		return catD50toD65.applyTo(XYZ50);
	}

}
