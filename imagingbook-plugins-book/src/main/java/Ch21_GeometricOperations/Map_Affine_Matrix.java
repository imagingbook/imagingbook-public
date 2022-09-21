/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package Ch21_GeometricOperations;

import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.util.Locale;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.common.geometry.mappings.linear.AffineMapping2D;
import imagingbook.common.image.ImageMapper;

/**
 * ImageJ plugin for configurable affine image transformation.
 * 
 * @author WB
 * @version 2021/10/07
 *
 */
public class Map_Affine_Matrix implements PlugInFilter {

	private static String[][] ElemNames = {
			{ "a00", "a01", "a02" },
			{ "a10", "a11", "a12" }};

	private static double[][] A = {
			{ 1, 0, 0 },
			{ 0, 1, 0 }};
	
	@Override
	public int setup(String arg, ImagePlus imp) {
		return DOES_ALL;
	}

	@Override
	public void run(ImageProcessor ip) {
		if (!showDialog()) {
			return;
		}
		AffineMapping2D imap = new AffineMapping2D(A).getInverse();
		new ImageMapper(imap).map(ip);
	}

	// --------------------------------------------------------------------------------------
	// Dialog example taken from http://rsbweb.nih.gov/ij/plugins/download/Dialog_Grid_Demo.java

	private boolean showDialog() {
		GenericDialog gd = new GenericDialog("Enter affine transformation matrix");
		
		// TODO: define proper class to hold all these items (instances to be passed to/from generic dialog)
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
