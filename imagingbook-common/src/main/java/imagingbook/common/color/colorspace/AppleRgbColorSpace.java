package imagingbook.common.color.colorspace;

import java.awt.color.ICC_ColorSpace;

import imagingbook.common.math.Matrix;
import imagingbook.common.math.PrintPrecision;

/**
 * This color space class is based in the "AppleRGB.icc" profile. It only serves
 * as an example for creating color spaces from ICC profiles. Note that this
 * color space does not pass the to/fromRGB inversion test.
 * 
 * @author WB
 * @version 2022/11/13
 */
@SuppressWarnings("serial")
public class AppleRgbColorSpace extends ICC_ColorSpace implements CustomRgbColorSpace {
	
	private static AppleRgbColorSpace instance = null;
	
	private AppleRgbColorSpace() {
		super(NamedIccProfile.AppleRGB.getProfile());
	}

	public static AppleRgbColorSpace getInstance() {
		if (instance == null) {
			instance = new AppleRgbColorSpace();
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
		AppleRgbColorSpace cs = AppleRgbColorSpace.getInstance();
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