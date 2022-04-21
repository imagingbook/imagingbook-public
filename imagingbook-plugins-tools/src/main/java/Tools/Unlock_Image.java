package Tools;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.PlugIn;

/**
 * Unlocks the currently active image.
 * 
 * @author WB
 *
 */
public class Unlock_Image implements PlugIn {

	public void run(String arg0) {
		ImagePlus img = IJ.getImage();
		if (img != null && img.isLocked()) {
			img.unlock();
		}
	}

}
