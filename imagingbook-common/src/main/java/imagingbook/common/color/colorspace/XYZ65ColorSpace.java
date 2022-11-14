package imagingbook.common.color.colorspace;

import static imagingbook.common.color.colorspace.StandardIlluminant.D50;
import static imagingbook.common.color.colorspace.StandardIlluminant.D65;

import java.awt.color.ColorSpace;

public class XYZ65ColorSpace extends CustomColorSpace {
	private static final long serialVersionUID = 1L;
	
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
	public double[] toRGB(double[] thisXYZ65) {
		double[] XYZ50 = this.toCIEXYZ(thisXYZ65);	// could be done directly!!
		sRgbColorSpace srgbCS = sRgbColorSpace.getInstance();
		double[] srgb = srgbCS.fromCIEXYZ(XYZ50);
		return srgb;
	}

	@Override
	public double[] fromRGB(double[] srgb) {
		sRgbColorSpace srgbCS = sRgbColorSpace.getInstance();
		double[] XYZ50 = srgbCS.toCIEXYZ(srgb);
		return this.fromCIEXYZ(XYZ50);
	}
	
	// -------------------------------------------------
	
	@Override	// convert this CS D65 XYZ to standard D50 XYZ
	public double[] toCIEXYZ(double[] thisXYZ65) {
		return catD65toD50.applyTo(thisXYZ65);
	}
	
	@Override	// convert standard D50 XYZ to this CS D65 XYZ
	public double[] fromCIEXYZ(double[] XYZ50) {
		return catD50toD65.applyTo(XYZ50);
	}
	
	// -------------------------------------------------

	@Override
	public double[] fromCIEXYZ65(double[] xyz65) {
		return xyz65;
	}

	@Override
	public double[] toCIEXYZ65(double[] xyz65) {
		return xyz65;
	}

}
