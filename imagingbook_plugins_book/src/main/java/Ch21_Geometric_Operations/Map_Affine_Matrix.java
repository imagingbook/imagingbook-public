/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package Ch21_Geometric_Operations;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.common.geometry.mappings.linear.AffineMapping2D;
import imagingbook.common.ij.DialogUtils;
import imagingbook.common.ij.IjUtils;
import imagingbook.common.image.ImageMapper;
import imagingbook.core.plugin.JavaDocHelp;
import imagingbook.sampleimages.GeneralSampleImage;

import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.util.Locale;

/**
 * <p>
 * ImageJ plugin, applies a configurable affine transformation to the current image. See Sec. 21.1.3 of [1] for details.
 * Optionally opens a sample image if no image is currently open.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2022/11/28
 * @see ImageMapper
 * @see AffineMapping2D
 */
public class Map_Affine_Matrix implements PlugInFilter, JavaDocHelp {

	private static String[][] ElemNames = {
			{ "a00", "a01", "a02" },
			{ "a10", "a11", "a12" }};

	private static double[][] A = {
			{ 1, 0, 0 },
			{ 0, 1, 0 }};
	
	/**
	 * Constructor, asks to open a predefined sample image if no other image
	 * is currently open.
	 */
	public Map_Affine_Matrix() {
		if (IjUtils.noCurrentImage()) {
			DialogUtils.askForSampleImage(GeneralSampleImage.Kepler);
		}
	}
		
	@Override
	public int setup(String arg, ImagePlus imp) {
		return DOES_ALL;
	}

	@Override
	public void run(ImageProcessor ip) {
		if (!runDialog()) {
			return;
		}
		AffineMapping2D imap = new AffineMapping2D(A).getInverse();
		new ImageMapper(imap).map(ip);
	}

	// --------------------------------------------------------------------------------------

	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addMessage("Affine forward transformation matrix (source to target):");
		
		TextField[] txtField = new TextField[A[0].length * A.length];
		Panel panel = makePanel(A, ElemNames, txtField);
		gd.addPanel(panel);
		
		gd.showDialog();
		if (gd.wasCanceled()) {
			return false;
		}
		
		return getPanelValues(A, txtField);
	}

	private Panel makePanel(double[][] vals, String[][] names, TextField[] textfield) {
		Panel panel = new Panel();
		panel.setLayout(new GridLayout(vals.length, vals[0].length * 2));
		int i = 0;
		for (int row = 0; row < vals.length; row++) {
			for (int col = 0; col < vals[row].length; col++) {
				textfield[i] = new TextField(String.format(Locale.US, "%.2f", vals[row][col]));
				panel.add(textfield[i]);
				panel.add(new Label(names[row][col]));
				i++;
			}
		}
		return panel;
	}

	private boolean getPanelValues(double[][] A, TextField[] tf) {
		int i = 0; 
		for (int r = 0; r < A.length; r++) {
			for (int c = 0; c < A[0].length; c++) {
				try {
					A[r][c] = Double.valueOf(tf[i].getText());
				} catch (NumberFormatException e) {	
					IJ.log("NumberFormatException: " + e.getMessage());
					return false;
				}
				i++;
			}
		}
		return true;
	}

}
