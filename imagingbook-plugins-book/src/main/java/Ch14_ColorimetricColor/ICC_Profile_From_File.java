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
import java.awt.color.ICC_ProfileRGB;
import java.io.IOException;

import ij.IJ;
import ij.io.OpenDialog;
import ij.plugin.PlugIn;
import imagingbook.common.math.Matrix;
import imagingbook.common.math.PrintPrecision;


/**
 * This plugin lists a user-selected ICC profile file
 * to the console.
 * TODO: add illustrative application of color profile
 * 
 * @author WB
 *
 */
public class ICC_Profile_From_File implements PlugIn {

	@Override
	public void run(String arg0) {
		
		String path = askForOpenPath("Select an ICC profile file (.icc or .icm)");
//		String path = IjUtils.askForOpenPath("Select an ICC profile file (.icc or .icm)");
		if (path == null) return;
		
		IJ.log("path = " + path);
		ICC_Profile iccProfile = null;
		
		try {iccProfile = ICC_ProfileRGB.getInstance(path);
		} catch (IOException e) {}
		
		if (iccProfile == null) {
			IJ.error("Could not open ICC profile file " + path);
			return;
		}
		
		PrintPrecision.set(5);
		
		ICC_ColorSpace iccColorSpace = new ICC_ColorSpace(iccProfile);
		int nComp = iccColorSpace.getNumComponents();
		if (nComp != 3) {
			IJ.error("Color space must have 3 components, this one has " + nComp);
			return;
		}
		
		IJ.log("color space class = " + iccColorSpace.getClass().getSimpleName());
		IJ.log("color space type = " + iccColorSpace.getType());
		IJ.log("color space ncomp = " + iccColorSpace.getNumComponents());
		
	
		// specify a device-specific color:
		float[] deviceColor = {0.77f, 0.13f, 0.89f};
		//float[] deviceColor = {0.0f, 0.0f, 0.0f};
		IJ.log("device color = " + Matrix.toString(deviceColor));
		
		// convert to sRGB:
		float[] sRGBColor = iccColorSpace.toRGB(deviceColor);
		IJ.log("sRGB = " + Matrix.toString(sRGBColor));
		
		// convert to (D50-based) XYZ:
		float[] XYZColor = iccColorSpace.toCIEXYZ(deviceColor);
		IJ.log("XYZ = " + Matrix.toString(XYZColor));
		
		deviceColor = iccColorSpace.fromRGB(sRGBColor);
		IJ.log("device color direct (check) = " + Matrix.toString(deviceColor));
		
		deviceColor = iccColorSpace.fromCIEXYZ(XYZColor);
		IJ.log("device color via XYZ (check) = " + Matrix.toString(deviceColor));
		
		// list sRGB Values:
		for (int ri = 0; ri <= 10; ri++) {
			for (int gi = 0; gi <= 10; gi++) {
				for (int bi = 0; bi <= 10; bi++) {
					float[] devCol = {ri * 0.1f, gi * 0.1f, bi * 0.1f};
					float[] sRGB = iccColorSpace.toRGB(devCol);
					IJ.log(Matrix.toString(devCol) + " -> " + Matrix.toString(sRGB));
				}
			}
		}
	}
	
	/**
	 * Queries the user for an arbitrary file to be opened.
	 * TODO: replace by IJ.open()?
	 *  
	 * @param title string to be shown in the interaction window.
	 * @return path of the selected resource.
	 */
	private String askForOpenPath(String title) {
		OpenDialog od = new OpenDialog(title, "");
		String dir = od.getDirectory();
		String name = od.getFileName();
		if (name == null)
			return null;
		return encodeURL(dir + name);
	}
	
	private static String encodeURL(String url) {
		//url = url.replaceAll(" ","%20");	// this doesn't work with spaces
		url = url.replace('\\','/');
		return url;
	}

}
