/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.color.cie;

import imagingbook.core.resource.NamedResource;

import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.io.IOException;

public enum NamedIccProfile implements NamedResource {
	AdobeRGB1998("AdobeRGB1998.icc"),
	AppleRGB("AppleRGB.icc"),
	CIERGB("CIERGB.icc"),
	PAL_SECAM("PAL_SECAM.icc"),
	SMPTE_C("SMPTE-C.icc"),
	VideoHD("VideoHD.icc"),
	VideoNTSC("VideoNTSC.icc"),
	VideoPAL("VideoPAL.icc"),
	WideGamutRGB("WideGamutRGB.icc")
	;

	private final String filename;
	private ICC_Profile profile = null;
	private ICC_ColorSpace colorspace = null;

	NamedIccProfile(String filename) {
		this.filename = filename;
		this.profile = null;		// singleton profile instance
		this.colorspace = null;		// singleton color space instance
	}

	@Override
	public String getFileName() {
		return filename;
	}
	
	/**
	 * Returns the {@link ICC_Profile} associated with this enum item or
	 * {@code null} if the profile could not be read (which is an error).
	 * 
	 * @return the {@link ICC_Profile} associated with this enum item
	 */
	public ICC_Profile getProfile() {
		if (this.profile == null) {
			try {
				profile = ICC_Profile.getInstance(this.getStream());
			} catch (IOException e) {
				throw new RuntimeException("could not load ICC profile " + this.toString());
			}
		}
		return this.profile;
	}
	
	/**
	 * Returns a {@link ICC_ColorSpace} instance obtained from the associated ICC
	 * color profile. An exception is thrown if the profile could not be read.
	 * 
	 * @return the {@link ICC_ColorSpace} associated with this enum item
	 */
	public ICC_ColorSpace getColorSpace() {
		if (this.colorspace == null) {
			ICC_Profile profile = this.getProfile();
			this.colorspace = new ICC_ColorSpace(profile);
		}
		return this.colorspace;
	}

}
