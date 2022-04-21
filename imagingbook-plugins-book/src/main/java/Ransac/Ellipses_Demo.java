package Ransac;

import imagingbook.common.ij.IjUtils;
import imagingbook.testimages.RansacTestImage;

import ij.ImagePlus;
import ij.plugin.PlugIn;

/**
 * RANSAC ellipse detection demo. Opens a local sample image and then runs
 * plugin {@link Ransac_Ellipses_Detect_OLD}.
 * 
 * @author WB
 *
 */
public class Ellipses_Demo implements PlugIn {
	
	private static RansacTestImage resource = RansacTestImage.NoisyEllipses;

	@Override
	public void run(String arg) {
		
		ImagePlus im = resource.getImage();
		im.show();
		
		IjUtils.run(new Ellipses_Detect());
		im.close();
	}

}
