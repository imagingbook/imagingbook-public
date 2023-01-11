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
import ij.process.ImageProcessor;
import imagingbook.common.filter.edgepreserving.TschumperleDericheF.Parameters;
import imagingbook.common.filter.edgepreserving.TschumperleDericheFilter;
import imagingbook.common.filter.generic.GenericFilter;
import imagingbook.common.ij.DialogUtils;
import imagingbook.common.ij.IjProgressBarMonitor;
import imagingbook.common.util.progress.ProgressMonitor;
import imagingbook.core.jdoc.JavaDocHelp;
import imagingbook.sampleimages.GeneralSampleImage;

import static imagingbook.common.ij.DialogUtils.addToDialog;
import static imagingbook.common.ij.DialogUtils.getFromDialog;
import static imagingbook.common.ij.IjUtils.noCurrentImage;

/**
 * <p>
 * This ImageJ plugin demonstrates the use of the Anisotropic Diffusion filter described in [1]. See Sec. 17.3.5 for
 * additional details. This plugin works for all types of images and stacks. It also demonstrates the use of
 * {@link IjProgressBarMonitor}.
 * </p>
 * <p>
 * [1] D. Tschumperle and R. Deriche, "Diffusion PDEs on vector-valued images", IEEE Signal Processing Magazine, vol.
 * 19, no. 5, pp. 16-25 (Sep. 2002).
 * <br>
 * [2] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2022/12/12
 * @see TschumperleDericheFilter
 * @see IjProgressBarMonitor
 */

public class Tschumperle_Deriche_Filter implements PlugInFilter, JavaDocHelp {

	private static Parameters params = new Parameters();

	/**
	 * Constructor, asks to open a predefined sample image if no other image is currently open.
	 */
	public Tschumperle_Deriche_Filter() {
		if (noCurrentImage()) {
			DialogUtils.askForSampleImage(GeneralSampleImage.Postcard2c);
		}
	}

	@Override
	public int setup(String arg0, ImagePlus imp) {
		return DOES_ALL + DOES_STACKS;
	}

	@Override
	public void run(ImageProcessor ip) {
		if (!runDialog())
			return;
		
		GenericFilter filter = new TschumperleDericheFilter(params);
		try (ProgressMonitor m = new IjProgressBarMonitor(filter)) {
			filter.applyTo(ip);
		}
	}

	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addHelp(getJavaDocUrl());
		addToDialog(params, gd);
		
		gd.showDialog();
		if (gd.wasCanceled()) return false;

		getFromDialog(params, gd);
		return params.validate();
	}

}



