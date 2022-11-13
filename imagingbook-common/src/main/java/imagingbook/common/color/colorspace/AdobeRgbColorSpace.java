package imagingbook.common.color.colorspace;

import java.awt.color.ICC_ColorSpace;


/**
 * This color space class is based in the "AdobeRGB1998.icc" profile. It only
 * serves as an example for creating color spaces from ICC profiles.
 * 
 * @author WB
 * @version 2022/11/13
 */
@SuppressWarnings("serial")
public class AdobeRgbColorSpace extends ICC_ColorSpace {
	
	private static AdobeRgbColorSpace instance = null;
	
	private AdobeRgbColorSpace() {
		super(NamedIccProfile.AdobeRGB1998.getProfile());
	}

	public static AdobeRgbColorSpace getInstance() {
		if (instance == null) {
			instance = new AdobeRgbColorSpace();
		}
		return instance;
	}
	
}
