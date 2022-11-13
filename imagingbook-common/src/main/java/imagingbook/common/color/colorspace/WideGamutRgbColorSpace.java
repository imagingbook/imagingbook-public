package imagingbook.common.color.colorspace;

import java.awt.color.ICC_ColorSpace;


/**
 * This color space class is based in the "WideGamutRGB.icc" profile. It only
 * serves as an example for creating color spaces from ICC profiles.
 * 
 * @author WB
 * @version 2022/11/13
 */
@SuppressWarnings("serial")
public class WideGamutRgbColorSpace extends ICC_ColorSpace {
	
	private static WideGamutRgbColorSpace instance = null;
	
	private WideGamutRgbColorSpace() {
		super(NamedIccProfile.WideGamutRGB.getProfile());
	}

	public static WideGamutRgbColorSpace getInstance() {
		if (instance == null) {
			instance = new WideGamutRgbColorSpace();
		}
		return instance;
	}
	
}
