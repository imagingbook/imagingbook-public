package Ransac;

import imagingbook.common.ij.IjUtils;
import imagingbook.testimages.RansacTestImage;

import ij.ImagePlus;
import ij.plugin.PlugIn;

/**
 * RANSAC line detection demo. Opens a local sample image and then runs
 * plugin {@link Lines_Detect}.
 * 
 * @author WB
 *
 */
public class Lines_Demo implements PlugIn {
	
	private static RansacTestImage resource = RansacTestImage.NoisyLines;

	@Override
	public void run(String arg) {
		
		ImagePlus im = resource.getImage();
		im.show();
		
		IjUtils.run(new Lines_Detect());
		im.close();
	}

}
