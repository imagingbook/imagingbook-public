/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook;

import java.util.jar.Attributes;
import java.util.jar.Manifest;

import imagingbook.common.util.FileUtils;

/**
 * This class provides version information for this library.
 * See https://stackoverflow.com/questions/3697449/retrieve-version-from-maven-pom-xml-in-code
 * for alternatives.
 * 
 * 
 * @author WB
 *
 */
public abstract class Info {
	
	/**
	 * Reads version information from the MANIFEST.MF file of the JAR file from
	 * which this class was loaded. 
	 * 
	 * @return A string with the version information. 
	 * "UNKNOWN" is returned if the library was not loaded from a JAR file or if 
	 * the version information could not be determined.
	 */
	public static String getVersionInfo() {
		Manifest mf = FileUtils.getJarManifest(Info.class);
		if (mf == null) {
			return "UNKNOWN";
		}
		//IJ.log("listing attributes");
		Attributes attr = mf.getMainAttributes();
		String version = null;
		String buildTime = null;
		try {
			version = attr.getValue("Implementation-Version");
			buildTime = attr.getValue("Build-Time");
		} catch (IllegalArgumentException e) { }
		return version + " (" + buildTime + ")";
	}

}
