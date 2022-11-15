package imagingbook.common.color.colorspace;

import static imagingbook.common.color.colorspace.StandardIlluminant.D50;
import static imagingbook.common.color.colorspace.StandardIlluminant.D65;

import java.awt.color.ColorSpace;

@SuppressWarnings("serial")
public class XYZ65ColorSpace extends ColorSpace implements DirectD65Conversion {
	private static sRgbColorSpace srgbCS = sRgbColorSpace.getInstance();
	
	private static final ChromaticAdaptation catD65toD50 = BradfordAdaptation.getInstance(D65, D50);
	private static final ChromaticAdaptation catD50toD65 = BradfordAdaptation.getInstance(D50, D65);

	
	private static final XYZ65ColorSpace instance = new XYZ65ColorSpace();
	public static XYZ65ColorSpace getInstance() {
		return instance;
	}

	private XYZ65ColorSpace() {
		super(TYPE_XYZ, 3);
	}

	// ----------------------------------------------------

	@Override	// convert this D65.based XYZ to sRGB 
	public float[] toRGB(float[] thisXYZ65) {
		float[] XYZ50 = this.toCIEXYZ(thisXYZ65);	// could be done directly!!
		float[] srgb = srgbCS.fromCIEXYZ(XYZ50);
		return srgb;
	}

	@Override
	public float[] fromRGB(float[] srgb) {
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
	
	// -------------------------------------------------

	@Override
	public float[] fromCIEXYZ65(float[] xyz65) {
		return xyz65;
	}

	@Override
	public float[] toCIEXYZ65(float[] xyz65) {
		return xyz65;
	}

}
