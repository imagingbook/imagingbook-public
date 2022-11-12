package imagingbook.common.color.colorspace;

import java.awt.color.ColorSpace;

/**
 * Implementation of a D50-based XYZ color space, as a substitute for
 * Java's built-in standard connection space (obtained with
 * {@code  ColorSpace.getInstance(ColorSpace.CS_CIEXYZ)}),
 * with improved accuracy.
 * The reference white point of this color space is D50 and
 * any XYZ coordinate passed to {@link #fromCIEXYZ(float[])}
 * is assumed to be relative to D50 as well, thus no 
 * conversion (color adaptation) is necessary between XYZ values.
 * 
 * @author WB
 *
 */
public class XYZ50ColorSpace extends ColorSpace {
	private static final long serialVersionUID = 1L;
	
	private static final XYZ50ColorSpace instance = new XYZ50ColorSpace();
	
	public static XYZ50ColorSpace getInstance() {
		return instance;
	}
	
	private XYZ50ColorSpace() {
		super(TYPE_XYZ, 3);
	}

	// -------------------------------------------------

	@Override	// convert (D50-based) thisXyz to sRGB
	public float[] toRGB(float[] thisXyz) {
		sRgbColorSpace srgbCS = sRgbColorSpace.getInstance();
		float[] srgb = srgbCS.fromCIEXYZ(thisXyz);
		return srgb;
	}

	@Override	// convert sRGB to (D50-based) thisXyz
	public float[] fromRGB(float[] srgb) {
		sRgbColorSpace srgbCS = sRgbColorSpace.getInstance();
		float[] thisXyz = srgbCS.toCIEXYZ(srgb);
		return thisXyz;
	}
	
	// -------------------------------------------------

	@Override
	public float[] toCIEXYZ(float[] thisXyz) {
		return thisXyz;	// nothing to do since D50-based XYZ already
	}

	@Override
	public float[] fromCIEXYZ(float[] xyz50) {
		return xyz50;		// nothing to do, since D50-based XYZ is what we want
	}

}
