package Tools_;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import imagingbook.common.util.DirectoryWalker;

import java.io.File;
import java.util.Collection;

/**
 * This is a simple tool for converting images of a specific type in a given directory to a new type using ImageJ. There
 * is no safety net except the option for performing a dry run. Converted files are placed in the same directory as the
 * original files. Note that nothing is polished, this is intended for quick use only.
 *
 * @author WB
 * @version 2022/12/27
 */
public class Convert_Images_To_PNG implements PlugIn {
	private static String SourceExtension = ".pgm";
	private static String TargetExtension = ".png";
	private static String CurrentDirectory =  IJ.getDirectory("current");
	private static boolean DoDryRun = true;
	private static boolean InvertImages = false;
	private static boolean InvertLookupTables = false;
	
	public void run(String arg0) {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addStringField("Source file extension", SourceExtension);
		gd.addStringField("Target file extension", TargetExtension);
		gd.addDirectoryField("Select directory", CurrentDirectory);
		gd.addCheckbox("Invert images", InvertImages);
		gd.addCheckbox("Invert lookup tables", InvertLookupTables);
		gd.addCheckbox("Dry run", DoDryRun);

		gd.showDialog();
		if (gd.wasCanceled()) {
			return;
		}

		SourceExtension = gd.getNextString();
		TargetExtension = gd.getNextString();
		CurrentDirectory = gd.getNextString();
		InvertImages = gd.getNextBoolean();
		InvertLookupTables = gd.getNextBoolean();
		DoDryRun = gd.getNextBoolean();

		// -------------------

		String dir = CurrentDirectory;
		if (dir == null)
			return;

		if (DoDryRun) {
			IJ.log("Doing a dry run only, nothing will be changed!");
		}
		IJ.log("Scanning directory " + dir + " ...");
		Collection<String> paths =  new DirectoryWalker(SourceExtension).collectFiles(dir);

		int cnt = 0;
		for (String p : paths) {
			ImagePlus im = IJ.openImage(p);
			if (im == null) {
				IJ.log("WARNING: could not read/open image" + p);
				continue;
			}
			File f = new File(dir + im.getShortTitle() + TargetExtension);
			String savepath = f.getAbsolutePath();
			IJ.log("Converting " + p + " --> " + savepath);

			if (!DoDryRun) {
				if (InvertImages)
					IJ.run(im, "Invert", "");	// im.getProcessor().invert();
				if (InvertLookupTables)
					IJ.run(im, "Invert LUT", "");

				IJ.save(im, savepath);
			}
			im.close();
			cnt++;
		}

		IJ.log("Processed " + cnt + " files.");
	}
}

