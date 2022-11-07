package imagingbook.common.color.colorspace;

import java.awt.color.ColorSpace;

/**
 * Implementation of a D50-based XYZ color space, as a substitute for
 * Java's built-in standard connection space (obtained by
 * {@code  ColorSpace.getInstance(ColorSpace.CS_CIEXYZ)}),
 * with improved accuracy.
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

	@Override
	public float[] toRGB(float[] thisXyz) {
		sRgb50ColorSpace cs = sRgb50ColorSpace.getInstance();
		float[] srgb = cs.fromCIEXYZ(thisXyz);
		return srgb;
	}

	@Override
	public float[] fromRGB(float[] srgb) {
		sRgb50ColorSpace cs = sRgb50ColorSpace.getInstance();
		float[] thisXyz = cs.toCIEXYZ(srgb);
		return thisXyz;
	}
	
	// -------------------------------------------------

	@Override
	public float[] toCIEXYZ(float[] thisColor) {
		return thisColor;	// nothing to doD50-based XYZ already
	}

	@Override
	public float[] fromCIEXYZ(float[] xyz50) {
		return xyz50;		// nothing to do, D50-based XYZ is what we want
	}

}
