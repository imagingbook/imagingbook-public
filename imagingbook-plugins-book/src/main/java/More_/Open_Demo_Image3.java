package More_;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import imagingbook.core.resource.ImageResource;
import imagingbook.sampleimages.GeneralSampleImage;

public class Open_Demo_Image3 implements PlugIn {
	
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
		GenericDialog gd = new GenericDialog(getClass().getSimpleName());
		gd.addMessage("No suitable image.");
		gd.setOKLabel("Open sample image");
//		gd.setCancelLabel("Quit");
		gd.showDialog();
		if (gd.wasCanceled()) {
			return false;
		}
		return true;
	}
	
//	private boolean getSampleImage() {
//		GenericDialog gd = new GenericDialog(getClass().getSimpleName());
//		gd.addMessage("No suitable image open.\nOpen sample image?");
//		gd.showDialog();
//		if (gd.wasCanceled()) {
//			return false;
//		}
//		return true;
//	}

}
