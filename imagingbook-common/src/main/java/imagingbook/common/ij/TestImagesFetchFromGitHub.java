/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.ij;

import java.net.MalformedURLException;
import java.net.URL;

import ij.ImagePlus;
import ij.io.Opener;

@Deprecated		// experimental (too slow for testing)!!
public abstract class TestImagesFetchFromGitHub {
	
	public static final String RepositoryUrl = "https://raw.github.com/imagingbook/imagingbook-test-images/master/img/";
	
	public static URL getImageUrl(String relativePath) {
		try {
			URL context = new URL(RepositoryUrl);
			return new URL(context, relativePath);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static ImagePlus getImage(String relPath) {
		URL url = getImageUrl(relPath);
		return (url == null) ? null : (new Opener()).openURL(url.toString());  // IJ.openImage(url.toString())
	}
	
	public static void main(String[] args) {
		String relPath = "ransac/lines/noisy-lines-inv.png";
		System.out.println("Trying to open " + getImageUrl(relPath));
		ImagePlus im = getImage(relPath);
		System.out.println(im);
		if (im != null) {
			im.show();
		}
	}

}
