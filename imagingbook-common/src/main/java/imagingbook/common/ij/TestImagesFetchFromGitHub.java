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
