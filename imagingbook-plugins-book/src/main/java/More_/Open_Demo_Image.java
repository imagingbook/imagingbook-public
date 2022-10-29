package More_;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.YesNoCancelDialog;
import ij.plugin.PlugIn;
import imagingbook.core.resource.ImageResource;
import imagingbook.sampleimages.GeneralSampleImage;

public class Open_Demo_Image implements PlugIn {
	
	private static ImageResource sampleImage = GeneralSampleImage.MapleLeafSmall;

	@Override
	public void run(String arg) {

		ImagePlus im = WindowManager.getCurrentImage();
		if (im == null) {
			YesNoCancelDialog bd = new YesNoCancelDialog(null, getClass().getSimpleName(), 
					"There are no images open.", 
					"Open sample image", 
					"Quit"
					);
			if (!bd.yesPressed()) {
				IJ.log("done.");
				return;
			}
			im = sampleImage.getImage();
			im.show();	
		}
		
		IJ.log("just opened " + im.getTitle());
	}

}
