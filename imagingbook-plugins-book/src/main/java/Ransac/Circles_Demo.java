package Ransac;

import imagingbook.common.ij.IjUtils;
import imagingbook.sampleimages.RansacTestImage;
import ij.ImagePlus;
import ij.plugin.PlugIn;

/**
 * RANSAC circle detection demo. Opens a local sample image and then runs
 * plugin {@link Circles_Detect}.
 * 
 * @author WB
 *
 */
public class Circles_Demo implements PlugIn {

	private static RansacTestImage resource = RansacTestImage.NoisyCircles;
	
	@Override
	public void run(String arg) {
		
		ImagePlus im = resource.getImage();
		im.show();
		
		IjUtils.run(new Circles_Detect());
		im.close();
	}

}
