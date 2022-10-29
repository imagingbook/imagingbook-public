package More_;

import More_.lib.YesNoDialog;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.plugin.PlugIn;
import imagingbook.core.resource.ImageResource;
import imagingbook.sampleimages.GeneralSampleImage;

public class Open_Demo_Image2 implements PlugIn {
	
	private static ImageResource sampleImage = GeneralSampleImage.MapleLeafSmall;

	@Override
	public void run(String arg) {

		ImagePlus im = WindowManager.getCurrentImage();
		if (im == null) {
			if (!getSampleImage()) {
				return;
			}
			im = sampleImage.getImage();
			im.show();	
		}
		
		IJ.log("just opened " + im.getTitle());
	}
	
	private boolean getSampleImage() {
		YesNoDialog bd = new YesNoDialog(getClass().getSimpleName(), 
				"There are no images open.\nOpen sample image?", 
				"   Yes   ", "Quit"
				);
		return bd.yesPressed();
	}

}
