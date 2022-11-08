package imagingbook.common.color.colorspace;

import static imagingbook.common.color.colorspace.StandardIlluminant.D50;
import static imagingbook.common.color.colorspace.StandardIlluminant.D65;

import java.awt.color.ColorSpace;

public class XYZ65ColorSpace extends ColorSpace {
	private static final long serialVersionUID = 1L;
	
	private static final ChromaticAdaptation catD65toD50 = new BradfordAdaptation(D65, D50);
	private static final ChromaticAdaptation catD50toD65 = new BradfordAdaptation(D50, D65);

	
	private static final XYZ65ColorSpace instance = new XYZ65ColorSpace();
	public static XYZ65ColorSpace getInstance() {
		return instance;
	}

	private XYZ65ColorSpace() {
		super(TYPE_XYZ, 3);
	}

	// ----------------------------------------------------

	@Override
	public float[] toRGB(float[] thisXYZ65) {
		float[] XYZ50 = this.toCIEXYZ(thisXYZ65);
		sRgb50ColorSpace srgbCS = sRgb50ColorSpace.getInstance();
		float[] srgb = srgbCS.fromCIEXYZ(XYZ50);
		return srgb;
	}

	@Override
	public float[] fromRGB(float[] srgb) {
		sRgb50ColorSpace srgbCS = sRgb50ColorSpace.getInstance();
		float[] XYZ50 = srgbCS.toCIEXYZ(srgb);
		return this.fromCIEXYZ(XYZ50);
	}
	
	// -------------------------------------------------
	
	@Override	// convert this CS D65 XYZ to standard D50 XYZ
	public float[] toCIEXYZ(float[] thisXYZ65) {
		return catD65toD50.applyTo(thisXYZ65);
	}
	
	@Override	// convert standard D50 XYZ to this CS D65 XYZ
	public float[] fromCIEXYZ(float[] XYZ50) {
		return catD50toD65.applyTo(XYZ50);
	}

}
