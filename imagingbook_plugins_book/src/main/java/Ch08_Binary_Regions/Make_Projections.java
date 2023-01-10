/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package Ch08_Binary_Regions;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.color.sets.BasicAwtColor;
import imagingbook.common.ij.DialogUtils;
import imagingbook.common.image.Projection;
import imagingbook.core.plugin.IjPluginName;
import imagingbook.core.jdoc.JavaDocHelp;
import imagingbook.sampleimages.GeneralSampleImage;

import static imagingbook.common.ij.IjUtils.noCurrentImage;

/**
 * <p>
 * This ImageJ plugin demonstrates the calculation of horizontal and vertical
 * projections from a grayscale image. See Sec. 8.7 of [1] for additional
 * details. The input image is not assumed binary, i.e., gray values are simply
 * added up without thresholding. This plugin only works for 8-bit grayscale
 * images, the original image is not modified. 
 * If no image is currently open, the plugin optionally loads a suitable
 * sample image.
 * The resulting projections are
 * shown as separate diagrams, which either show the relative amount of white
 * (default) or black, selectable in the user dialog:
 * </p>
 * <ul>
 * <li>The <em>horizontal</em> projection diagram has the same height as the
 * original image and user-adjustable width, with diagram bars being drawn from
 * left to right.</li>
 * <li>The <em>vertical</em> projection diagram has the same width as the
 * original image and user-adjustable height, with diagram bars being drawn from
 * top to bottom.</li>
 * </ul>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic
 * Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2020/12/17
 * @version 2022/09/29 revised to match book examples
 * 
 * @see imagingbook.common.image.Projection
 */
@IjPluginName("Make Projections")
public class Make_Projections implements PlugInFilter, JavaDocHelp {
	
	/** Size of horizontal/vertical projection bars. */
	public static int ProjectionSize = 150;
	
	/** Background color for projection diagrams. */
	public static BasicAwtColor BackgroundColor = BasicAwtColor.White;
	
	/** Foreground value for horizontal projection. */
	public static BasicAwtColor PlotColorH = BasicAwtColor.Green;
	
	/** Foreground value for vertical projection. */
	public static BasicAwtColor PlotColorV = BasicAwtColor.Magenta;
	
	/** Plot the relative amount of white (false) or black (true). */
	public static boolean ShowAmountOfBlack = false;
	
	private ImagePlus im = null;
	
	
	/**
	 * Constructor, asks to open a predefined sample image if no other image
	 * is currently open.
	 */
	public Make_Projections() {
		if (noCurrentImage()) {
			DialogUtils.askForSampleImage(GeneralSampleImage.Cat);
		}
	}
	
	@Override
	public int setup(String arg, ImagePlus im) { 
		this.im = im;
		return DOES_8G + NO_CHANGES; 
	}
	
	@Override
	public void run(ImageProcessor ip) {
		
		if (!runDialog()) {
			return;
		}
		
		String title = im.getShortTitle();
		int M = ip.getWidth();
		int N = ip.getHeight();
		
		Projection proj = new Projection(ip);
		
		// draw the horizontal projection diagram:
		double[] pHor = proj.getHorizontal();	// pHor[v] = sum of pixel values in row v
		ColorProcessor hP = new ColorProcessor(ProjectionSize, N);
		hP.setColor(BackgroundColor.getColor());
		hP.fill();
		hP.setColor(PlotColorH.getColor());
		double maxWhite = M * 255;
		for (int v = 0; v < N; v++) {
			double amount = (ShowAmountOfBlack) ?
					1 - pHor[v] / maxWhite :	// amount of black in line 'v'
					pHor[v] / maxWhite;			// amount of white in line 'v'
			int k = (int) Math.round(amount * ProjectionSize);
			if (k > 0) {
				hP.drawLine(0, v, k, v);
			}
		}	
		(new ImagePlus(title + "-horizontal-proj", hP)).show();
				
		// draw the vertical projection diagram:
		double[] pVer = proj.getVertical();		// pVer[u] = sum of pixel values in column u
		ColorProcessor vP = new ColorProcessor(M, ProjectionSize);
		vP.setColor(BackgroundColor.getColor()); 
		vP.fill();
		vP.setColor(PlotColorV.getColor());
		maxWhite = N * 255;
		for (int u = 0; u < M; u++) {
			double amount = (ShowAmountOfBlack) ?
					1 - pVer[u] / maxWhite :	// amount of black in column 'u'
					pVer[u] / maxWhite;			// amount of white in column 'u'
			int k = (int) (amount * ProjectionSize);
			if (k > 0) {
				vP.drawLine(u, 0, u, k);
			}
		}
		(new ImagePlus(title + "-vertical-proj", vP)).show();
	}

	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addHelp(getJavaDocUrl());
		gd.addNumericField("Size of projection plots", ProjectionSize, 0);
		gd.addCheckbox("Show amount of black", ShowAmountOfBlack);
		gd.addEnumChoice("Horizontal plot color", PlotColorH);
		gd.addEnumChoice("Vertical plot color", PlotColorV);
		gd.addEnumChoice("Plot background color", BackgroundColor);
		
		gd.showDialog();
		if (gd.wasCanceled())
			return false;
		
		ProjectionSize = (int) gd.getNextNumber();
		ShowAmountOfBlack = gd.getNextBoolean();
		PlotColorH = gd.getNextEnumChoice(BasicAwtColor.class);
		PlotColorV = gd.getNextEnumChoice(BasicAwtColor.class);
		BackgroundColor = gd.getNextEnumChoice(BasicAwtColor.class);
		return true;
	}
}
