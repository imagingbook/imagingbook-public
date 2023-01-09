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
import imagingbook.common.filter.edgepreserving.NagaoMatsuyamaF.Parameters;
import imagingbook.common.filter.edgepreserving.NagaoMatsuyamaFilterScalar;
import imagingbook.common.filter.edgepreserving.NagaoMatsuyamaFilterVector;
import imagingbook.common.ij.DialogUtils;
import imagingbook.core.plugin.JavaDocHelp;
import imagingbook.sampleimages.GeneralSampleImage;

import static imagingbook.common.ij.IjUtils.noCurrentImage;

/**
 * <p>
 * This plugin demonstrates the 5x5 Nagao-Matsuyama filter, as described in [1]]. See Sec. 17.1 of [2] for additional
 * details.This plugin works for all types of images and stacks.
 * <p>
 * [1] M. Nagao and T. Matsuyama. Edge preserving smoothing. Computer Graphics and Image Processing 9(4), 394–407
 * (1979).
 * <br>
 * [2] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2022/12/12
 * @see NagaoMatsuyamaFilterScalar
 * @see NagaoMatsuyamaFilterVector
 */
public class Nagao_Matsuyama_Filter implements PlugInFilter, JavaDocHelp {
	
	private static Parameters params = new Parameters();
	private static boolean UseVectorFilter = false;
	private boolean isColor;

	/**
	 * Constructor, asks to open a predefined sample image if no other image is currently open.
	 */
	public Nagao_Matsuyama_Filter() {
		if (noCurrentImage()) {
			DialogUtils.askForSampleImage(GeneralSampleImage.Postcard2c);
		}
	}

	@Override
	public int setup(String arg0, ImagePlus imp) {
			return DOES_ALL;
	}

	@Override
    public void run(ImageProcessor ip) {
    	isColor = (ip instanceof ColorProcessor);
		if (!runDialog())
			return;
		if (isColor && UseVectorFilter) {
			new NagaoMatsuyamaFilterVector(params).applyTo((ColorProcessor)ip);
		}
		else {
			new NagaoMatsuyamaFilterScalar(params).applyTo(ip);
		}
    }
    
    private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addNumericField("Variance threshold", params.varThreshold, 0);
		if (isColor)
			gd.addCheckbox("Use vector filter", UseVectorFilter);

		gd.showDialog();
		if (gd.wasCanceled())
			return false;

		params.varThreshold = Math.max(gd.getNextNumber(),0);
		if (isColor)
			UseVectorFilter = gd.getNextBoolean();
		return true;
    }
    
}

