package More_;

import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import imagingbook.core.resource.ImageResource;
import imagingbook.sampleimages.GeneralSampleImage;

public class Open_Sample_Image implements PlugIn {

	@Override
	public void run(String arg) {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addEnumChoice("Select", GeneralSampleImage.MapleLeafSmall);
		
		gd.showDialog();
		if(gd.wasCanceled()) 
			return;
		
		ImageResource ir = gd.getNextEnumChoice(GeneralSampleImage.class);
		ir.getImage().show();
	}

}
