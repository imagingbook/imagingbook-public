package Tools;

/**
 * Closes all open images except the currently active image. 
 * Much copied from from ij.plugin.WindowOrganizer.java
 * 
 * @author WB
 * @version 2012/02/15
 */
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

public class Close_Other_Images implements PlugInFilter {
	
	ImagePlus im;
	
	@Override
	public int setup(String arg0, ImagePlus im) {
		this.im = im;
		return DOES_ALL + NO_CHANGES;
	}
	
	@Override
	public void run(ImageProcessor ip) {
		int[] winIds = WindowManager.getIDList();
		if (winIds == null || winIds.length < 1) {
			return;
		}
		
		GenericDialog dlg = new GenericDialog("Close all images");
		dlg.addCheckbox("Close current image too?", false);
		dlg.showDialog();
		if (dlg.wasCanceled())
			return;
		boolean closeCurrentImage = dlg.getNextBoolean();
				
//		int nImages = winIds.length - 1;
//		YesNoCancelDialog yncd = 
//			new YesNoCancelDialog(null, "Close all images", "Close all " + nImages + " images?");
//		if (yncd.cancelPressed() || !yncd.yesPressed()) {
//			return;
//		}
		
		int thisId = this.im.getID();
		
		for (int id : winIds) {
			if (id != thisId || closeCurrentImage) {
				ImagePlus imp = WindowManager.getImage(id);
				if (imp != null) {
					imp.close();
				}
			}
		}
	}
}

