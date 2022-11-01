package More_;

import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import imagingbook.core.resource.ImageResource;
import imagingbook.sampleimages.GeneralSampleImage;

/**
 * Allows the user to select and open one of the internal sample images.
 * The image is loaded from the associated JAR file.
 * 
 * @author WB
 */
public class Open_Sample_Image implements PlugIn {

	@Override
	public void run(String arg) {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		GeneralSampleImage init = GeneralSampleImage.values()[0];
		gd.addEnumChoice("Select", init);
		
		gd.showDialog();
		if(gd.wasCanceled()) 
			return;
		
		ImageResource ir = gd.getNextEnumChoice(GeneralSampleImage.class);
		ir.getImage().show();
	}

}
