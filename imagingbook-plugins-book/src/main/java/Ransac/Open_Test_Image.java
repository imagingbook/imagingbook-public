package Ransac;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import imagingbook.testimages.RansacTestImage;

/**
 * Select and open a local RANSAC-related test image.
 * @author WB
 *
 */
public class Open_Test_Image implements PlugIn {
	
	static RansacTestImage selection = RansacTestImage.NoisyCircles;

	@Override
	public void run(String arg) {
		
		GenericDialog gd = new GenericDialog(getClass().getSimpleName());
		gd.addEnumChoice("Select image", selection);
		
		gd.showDialog();
		if (gd.wasCanceled())
			return;
		
		selection = gd.getNextEnumChoice(RansacTestImage.class);
		
		ImagePlus im = selection.getImage();
		im.show();
	}

}
