package imagingbook.common.color.colorspace;

import java.awt.color.ICC_ColorSpace;

import imagingbook.common.math.Matrix;
import imagingbook.common.math.PrintPrecision;


/**
 * This color space class is based in the "AdobeRGB1998.icc" profile. It only
 * serves as an example for creating color spaces from ICC profiles.
 * 
 * @author WB
 * @version 2022/11/13
 */
@SuppressWarnings("serial")
public class AdobeRgbColorSpace extends ICC_ColorSpace implements RgbColorSpace {
	
	private static AdobeRgbColorSpace instance = null;
	
	private AdobeRgbColorSpace() {
		super(NamedIccProfile.AdobeRGB1998.getProfile());	// constructor of ICC_ColorSpace
	}

	/**
	 * Returns an instance of {@link AdobeRgbColorSpace}.
	 * @return an instance of {@link AdobeRgbColorSpace}
	 */
	public static AdobeRgbColorSpace getInstance() {
		if (instance == null) {
			instance = new AdobeRgbColorSpace();
		}
		return instance;
	}

	@Override
	public double[] getWhitePoint() {
		float[] rgb = {1, 1, 1};
		return Matrix.toDouble(this.toCIEXYZ(rgb));
	}

	@Override
	public double[] getPrimary(int idx) {
		float[] rgb = new float[3];
		rgb[idx] = 1;
		return Matrix.toDouble(this.toCIEXYZ(rgb));
	}
	
	public static void main(String[] args) {
		PrintPrecision.set(6);
		AdobeRgbColorSpace cs = AdobeRgbColorSpace.getInstance();
		System.out.println("w = " + Matrix.toString(cs.getWhitePoint()));
//		System.out.println("R = " + Matrix.toString(cs.getPrimary(0)));
//		System.out.println("G = " + Matrix.toString(cs.getPrimary(1)));
//		System.out.println("B = " + Matrix.toString(cs.getPrimary(2)));
		for (int i = 0; i < 3; i++) {
			double[] p = cs.getPrimary(i);
			double[] xy = CieUtil.XYZToXy(p);
			System.out.println(i + " = " + Matrix.toString(p) + " xy = " + Matrix.toString(xy));
		}
	}
	
}
