package imagingbook.common.color.colorspace;

import static imagingbook.common.color.colorspace.StandardIlluminant.D50;
import static imagingbook.common.color.colorspace.StandardIlluminant.D65;

public interface CustomColorSpace {
	
	static final ChromaticAdaptation catD65toD50 = BradfordAdaptation.getInstance(D65, D50);
	static final ChromaticAdaptation catD50toD65 = BradfordAdaptation.getInstance(D50, D65);
	
	// methods required by all implementations of ColorSpace:
	public float[] toCIEXYZ(float[] value);
	public float[] fromCIEXYZ(float[] value);
	public float[] toRGB(float[] value);
	public float[] fromRGB(float[] value);
	
	// -----------------------------------------------------------------
	
	/**
	 * Analogous to {@link #fromCIEXYZ(float[])} but accepts D65-based instead of
	 * D50-based PCS color values. D65-based color spaces should override this
	 * method to avoid the additional D65 to D50 conversion.
	 * 
	 * @param xyz65 D65-based XYZ color values
	 * @return color values in this color space
	 */
	public default float[] fromCIEXYZ65(float[] xyz65) {
		float[] XYZ50 = catD65toD50.applyTo(xyz65);
		return this.fromCIEXYZ(XYZ50);
	}
	
	/**
	 * Analogous to {@link #toCIEXYZ(float[])} but returns D65-based instead of
	 * D50-based PCS color values. D65-based color spaces should override this
	 * method to avoid the additional D50 to D65 conversion.
	 * 
	 * @param value color value in this color space
	 * @return D65-based XYZ color values
	 */
	public default float[] toCIEXYZ65(float[] value) {
		float[] XYZ50 = this.toCIEXYZ(value);
		return catD50toD65.applyTo(XYZ50);
	}
	
}
