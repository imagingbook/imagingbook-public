/*
 *  This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge.
 * All rights reserved. Visit https://imagingbook.com for additional details.
 */
package Ch14_ColorimetricColor;

import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.io.IOException;

import ij.IJ;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import imagingbook.common.color.cie.CieUtils;
import imagingbook.common.color.cie.NamedIccProfile;
import imagingbook.common.math.Matrix;


/**
 * This plugin lists the contents of a user-selected ICC profile that is retrieved
 * from a Java resource.
 * TODO: needs revision, add illustrative application of color profile
 * 
 * @author WB
 *
 */
public class ICC_Profile_From_JAR implements PlugIn {

	private static NamedIccProfile TheIccProfile = NamedIccProfile.AdobeRGB1998;
	private static boolean ListDeviceColorMapping = false;
	
	@Override
	public void run(String arg) {
		
		if (!runDialog())
			return;
		
		IJ.log("Selected ICC profile: " + TheIccProfile);
		IJ.log("Reading from JAR: " + (TheIccProfile.isInsideJar()));
		
		ICC_Profile profile = null;
		try {
			profile = ICC_Profile.getInstance(TheIccProfile.getStream());
		} catch (IOException e) { }
		
		if (profile == null) {
			IJ.error("Could not read ICC profile " + TheIccProfile);
			return;
		}

		IJ.log("profile class = " + profile.getClass().getSimpleName());
		ICC_ColorSpace iccColorSpace = new ICC_ColorSpace(profile);
		int nComp = iccColorSpace.getNumComponents();
		if (nComp != 3) {
			IJ.error("Color space must have 3 components, this one has " + nComp);
			return;
		}
		
		IJ.log("color space class = " + iccColorSpace.getClass().getSimpleName());
		IJ.log("color space type = " + iccColorSpace.getType());
		IJ.log("color space ncomp = " + iccColorSpace.getNumComponents());
		
		
		// specify a device-specific color:
//		float[] deviceColor = {0.77f, 0.13f, 0.89f};
		//float[] deviceColor = {0.0f, 0.0f, 0.0f};
		float[] deviceColor = {1, 1, 1};
		IJ.log("device color = " + Matrix.toString(deviceColor));
		
		// convert to sRGB:
		float[] sRGBColor = iccColorSpace.toRGB(deviceColor);
		IJ.log("sRGB = " + Matrix.toString(sRGBColor));
		
		// convert to (D50-based) XYZ:
		float[] XYZColor = iccColorSpace.toCIEXYZ(deviceColor);
		IJ.log("XYZ = " + Matrix.toString(XYZColor));
		IJ.log("xy (white point D50) = " + Matrix.toString(CieUtils.XYZToxy(Matrix.toDouble(XYZColor))));
				
		deviceColor = iccColorSpace.fromRGB(sRGBColor);
		IJ.log("device color direct (check) = " + Matrix.toString(deviceColor));
		
		deviceColor = iccColorSpace.fromCIEXYZ(XYZColor);
		IJ.log("device color via XYZ (check) = " + Matrix.toString(deviceColor));
		
		if (ListDeviceColorMapping) {
		IJ.log("");
			// list sRGB Values (components in [0,1])
			for (int ri = 0; ri <= 10; ri++) {
				for (int gi = 0; gi <= 10; gi++) {
					for (int bi = 0; bi <= 10; bi++) {
						float[] devCol1 = {ri * 0.1f, gi * 0.1f, bi * 0.1f};
						float[] sRGB = iccColorSpace.toRGB(devCol1);
						float[] devCol2 = iccColorSpace.fromRGB(sRGB);
						IJ.log(Matrix.toString(devCol1) + " -> " + Matrix.toString(sRGB) + " -> " 
								+ Matrix.toString(devCol2) + warning(devCol1, devCol2));
					}
				}
			}
		}
	}
	
	/**
	 * Checks if any component of the specified pair of colors deviates by
	 * more than 0.05 and returns a warning string if this is the case.
	 * @param col1 first color
	 * @param col2 second color
	 * @return a warning string
	 */
	private String warning(float[] col1, float[] col2) {
		float t = 0.05f;
		for (int i = 0; i < col1.length; i++) {
			if (Math.abs(col1[i] - col2[i]) > t)
				return " ***";
		}
		return "";
	}
	
	// -----------------------------------------------------------
	
	
	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(ICC_Profile_From_JAR.class.getSimpleName());
		gd.addMessage("Select an ICC profile:");
		//gd.addChoice("Profile:", choices, choices[0]);
		gd.addEnumChoice("Profile", TheIccProfile);
		gd.addCheckbox("List device color mapping", ListDeviceColorMapping);

		gd.showDialog();
		if (gd.wasCanceled()) {
			return false;
		}
		
		TheIccProfile = gd.getNextEnumChoice(NamedIccProfile.class);
		ListDeviceColorMapping = gd.getNextBoolean();
		return true;
	}
}
