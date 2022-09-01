/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.color;

import imagingbook.core.resource.NamedResource;

public enum IccProfile implements NamedResource {
	AdobeRGB1998("AdobeRGB1998.icc"),
	CIERGB("CIERGB.icc"),
	PAL_SECAM("PAL_SECAM.icc"),
	SMPTE_C("SMPTE-C.icc"),
	VideoHD("VideoHD.icc"),
	VideoNTSC("VideoNTSC.icc"),
	VideoPAL("VideoPAL.icc");

	private final String filename;

	IccProfile(String filename) {
		this.filename = filename;
	}

	@Override
	public String getFileName() {
		return filename;
	}

}
