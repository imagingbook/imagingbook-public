/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package Ch17_EdgePreserving_Smoothing;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.filter.edgepreserving.KuwaharaF.Parameters;
import imagingbook.common.filter.edgepreserving.KuwaharaFilterScalar;
import imagingbook.common.filter.edgepreserving.KuwaharaFilterVector;
import imagingbook.common.ij.DialogUtils;
import imagingbook.core.plugin.JavaDocHelp;
import imagingbook.sampleimages.GeneralSampleImage;

import static imagingbook.common.ij.DialogUtils.addToDialog;
import static imagingbook.common.ij.DialogUtils.getFromDialog;
import static imagingbook.common.ij.IjUtils.noCurrentImage;

/**
 * <p>
 * This ImageJ plugin demonstrates the use of the Kuwahara filter, similar to the filter suggested in [1]. It structures
 * the filter region into  five overlapping, square subregions of size (r+1) x (r+1). Unlike the original Kuwahara
 * filter, it includes a centered subregion. This plugin works for all types of images and stacks. See Sec. 17.1 of [2]
 * for additional details.
 * </p>
 * <p>
 * [1] F. Tomita and S. Tsuji. Extraction of multiple regions by smoothing in selected neighborhoods. IEEE Transactions
 * on Systems, Man, and Cybernetics 7, 394–407 (1977).
 * <br>
 * [2] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2022/12/12
 * @see KuwaharaFilterScalar
 * @see KuwaharaFilterVector
 */
public class Kuwahara_Filter implements PlugInFilter, JavaDocHelp {

	private static Parameters params = new Parameters();
	private static boolean UseVectorFilter = false;
	
	private boolean isColor;

	/**
	 * Constructor, asks to open a predefined sample image if no other image is currently open.
	 */
	public Kuwahara_Filter() {
		if (noCurrentImage()) {
			DialogUtils.askForSampleImage(GeneralSampleImage.Postcard2c);
		}
	}

	public int setup(String arg, ImagePlus imp) {
		return DOES_ALL + DOES_STACKS;
	}

	public void run(ImageProcessor ip) {
		isColor = (ip instanceof ColorProcessor);
		if (!runDialog())
			return;
		if (isColor && UseVectorFilter) {
			new KuwaharaFilterVector(params).applyTo((ColorProcessor)ip);
		}
		else {
			new KuwaharaFilterScalar(params).applyTo(ip);
		}
	}

	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		addToDialog(params, gd);
		if (isColor)
			gd.addCheckbox("Use vector filter (color only)", UseVectorFilter);
		
		gd.showDialog();
		if(gd.wasCanceled()) 
			return false;
		
		getFromDialog(params, gd);
		if (isColor)
			UseVectorFilter = gd.getNextBoolean();
		
		params.radius = Math.max(params.radius, 1);
		params.tsigma = Math.max(params.tsigma, 0);
		return params.validate();
	}
}

