package imagingbook.common.color.colorspace;

import java.awt.color.ICC_ColorSpace;

/**
 * This color space class is based in the "AppleRGB.icc" profile. It only serves
 * as an example for creating color spaces from ICC profiles. Note that this
 * color space does not pass the to/fromRGB inversion test.
 * 
 * @author WB
 * @version 2022/11/13
 */
@SuppressWarnings("serial")
public class AppleRgbColorSpace extends ICC_ColorSpace {
	
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
	
}
