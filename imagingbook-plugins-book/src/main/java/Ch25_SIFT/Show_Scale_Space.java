/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package Ch25_SIFT;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.ij.DialogUtils;
import imagingbook.common.sift.SiftDetector;
import imagingbook.common.sift.scalespace.DogScaleSpace;
import imagingbook.common.sift.scalespace.GaussianScaleSpace;
import imagingbook.core.resource.ImageResource;
import imagingbook.sampleimages.GeneralSampleImage;

import static imagingbook.common.ij.DialogUtils.makeHtmlString;
import static imagingbook.common.ij.IjUtils.noCurrentImage;

/**
 * <p>
 * This ImageJ plugin visualizes the hierarchical scale space structures used for SIFT feature detection (see
 * {@link SiftDetector}). Optionally the Gaussian scale space or the derived DoG scale space (or both) are shown. Each
 * scale space octave is displayed as an image stack, with one frame for each scale level. See Ch. 25 of [1] for
 * details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2022/11/23
 */
public class Show_Scale_Space implements PlugInFilter {

	private static String HelpText = makeHtmlString(
		"This ImageJ plugin visualizes the hierarchical scale space structures",
		"used for SIFT feature detection. Optionally the Gaussian scale space or the",
		"derived DoG scale space (or both) are shown. Each scale space octave",
		"is displayed as an image stack, with one frame for each scale level."
	);



	private static String makeHelpString(String... lines) {
		String joined = String.join(" ", lines);
		String broken = DialogUtils.splitLines(40, joined);

		String text = "<html>\n" + joined + "\n</html>";
		// String text = "<html>\n" + DialogUtils.splitLines(20, lines) + "\n</html>";
		IJ.log(text);
		return text;
	}

	private static ImageResource SampleImage = GeneralSampleImage.Castle;
	private static boolean ShowGaussianScaleSpace = true;
	private static boolean ShowDoGScaleSpace = false;
	
	/**
	 * Constructor, asks to open a predefined sample image if no other image is currently open.
	 */
	public Show_Scale_Space() {
		if (noCurrentImage()) {
			DialogUtils.askForSampleImage(SampleImage);
		}
	}
	
	// ---------------------------------------------------

	@Override
	public int setup(String arg0, ImagePlus arg1) {
		return DOES_8G + DOES_32 + NO_CHANGES;
	}
	
	@Override
	public void run(ImageProcessor ip) {
		if (!runDialog()) {
			return;
		}
		
		if (!ShowGaussianScaleSpace && !ShowDoGScaleSpace) {
			return;
		}
		
		FloatProcessor fp = (FloatProcessor) ip.convertToFloat();
		SiftDetector detector = new SiftDetector(fp);
		if (ShowGaussianScaleSpace) {
			GaussianScaleSpace gss = detector.getGaussianScaleSpace();
			ImagePlus[] images = gss.getImages("Gaussian");
			for (ImagePlus im : images) {
				im.show();
			}
		}
		
		if (ShowDoGScaleSpace) {
			DogScaleSpace dss = detector.getDogScaleSpace();
			ImagePlus[] images = dss.getImages("DoG");
			for (ImagePlus im : images) {
				im.show();
			}
		}
	}
	
	// ---------------------------
	
	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addHelp(HelpText);
		gd.addCheckbox("Show Gaussian scale space)", ShowGaussianScaleSpace);
		gd.addCheckbox("Show DoG scale space)", ShowDoGScaleSpace);
		
		gd.showDialog();
		if (gd.wasCanceled()) {
			return false;
		}
		
		ShowGaussianScaleSpace = gd.getNextBoolean();
		ShowDoGScaleSpace = gd.getNextBoolean();
		return true;
	}

	// EXPERIMENTAL!  -------------------------------------------------------------------------
	private static final String LINEBREAK = "<br>\n";
	private static final String SPACE_SEPARATOR = " ";
	//if text has \n, \r or \t symbols it's better to split by \s+
	private static final String SPLIT_REGEXP= "\\s+";

	/**
	 * Splits a long string into multiple lines of the specified maximum length and builds a new string with newline
	 * ({@literal \n}) characters separating successive lines. Multiple input strings are first joined into a single
	 * string using blank spaces as separators. Intended mainly to format message texts of plugin dialogs. Inspired by:
	 * https://stackoverflow.com/a/21002193
	 *
	 * @param columns the maximum number of characters per line
	 * @param input the input text to be formatted
	 * @return a new string with newline characters separating successive lines
	 */
	private static String formatText(int columns, String input) {
		// if (textChunks.length == 0) {
		// 	throw new IllegalArgumentException("must pass at least one string");
		// }
		// String input = String.join(SPACE_SEPARATOR, textChunks);
		String[] tokens = input.split(SPLIT_REGEXP);
		StringBuilder output = new StringBuilder(input.length());
		int lineLen = 0;
		for (int i = 0; i < tokens.length; i++) {
			String word = tokens[i];

			if (lineLen + SPACE_SEPARATOR.length() + word.length() > columns) {
				if (i > 0) {
					output.append(LINEBREAK);
				}
				lineLen = 0;
			}
			if (i < tokens.length - 1 &&
				(lineLen + word.length() + SPACE_SEPARATOR.length() + tokens[i + 1].length() <= columns)) {
				word += SPACE_SEPARATOR;
			}
			output.append(word);
			lineLen += word.length();
		}
		return output.toString();
	}

	public static void main(String[] args) {
		String[] lines = {
				"This ImageJ plugin visualizes the hierarchical scale space structures",
				"used for SIFT feature detection. Optionally the Gaussian scale space or the",
				"derived DoG scale space (or both) are shown. Each scale space octave",
				"is displayed as an image stack, with one frame for each scale level."};

		String joined = String.join(" ", lines);
		System.out.println("joined = \n" + joined);
		String broken = formatText(60, joined);
		System.out.println("broken = \n" + broken);
	}

}
