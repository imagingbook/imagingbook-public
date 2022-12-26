package Tools_;

import ij.IJ;
import ij.ImagePlus;
import ij.io.LogStream;
import ij.plugin.PlugIn;
import imagingbook.common.math.PrintPrecision;
import imagingbook.common.util.DirectoryWalker;

import java.io.File;
import java.util.Collection;


public class Convert__Images_To_PNG implements PlugIn {
	
	static {
		LogStream.redirectSystem();
		PrintPrecision.set(9);
	}
//	public static final String DataBaseFileName = "C:/tmp/flusserMoments2.ser";
	
	static String TMP_DIR = IJ.getDirectory("temp");

	
	public void run(String arg0) {
		
		String dir = IJ.getDirectory("Select start directory...");
		if (dir == null)
			return;

		IJ.log("Collecting files in: " + dir);
		//Collection<String> paths = DirectoryWalker.collectFiles(dir, ".gif");
		DirectoryWalker dw = new DirectoryWalker(".pgm");
		Collection<String> paths = dw.collectFiles(dir);

		for (String p : paths) {
			IJ.log("processing " + p);

			ImagePlus im = IJ.openImage(p);

			String savepath = dir + im.getShortTitle() + ".png";
			File f = new File(savepath);
			IJ.log("   save as " + f.getAbsolutePath());
			IJ.save(im, savepath);

		}
		

		IJ.log("done.");
	}


}

