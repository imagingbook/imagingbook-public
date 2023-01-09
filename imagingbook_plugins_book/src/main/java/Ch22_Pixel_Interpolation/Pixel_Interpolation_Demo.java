/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package Ch22_Pixel_Interpolation;

import Ch21_Geometric_Operations.Draw_Test_Grid;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.common.ij.DialogUtils;
import imagingbook.common.ij.IjUtils;
import imagingbook.common.image.OutOfBoundsStrategy;
import imagingbook.common.image.access.ImageAccessor;
import imagingbook.common.image.interpolation.InterpolationMethod;

/**
 * <p>
 * This ImageJ plugin demonstrates the use of various pixel interpolation methods and out-of-bounds strategies.
 * Sub-pixel translation is used as the geometric transformation (parameters can be specified). Note the use if the
 * {@link ImageAccessor} class which gives uniform access to all types of images. See Ch. 22 of [1] for more details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2022/11/28
 * @see InterpolationMethod
 * @see OutOfBoundsStrategy
 * @see ImageAccessor
 */
public class Pixel_Interpolation_Demo implements PlugInFilter {
	
	private static InterpolationMethod IPM = InterpolationMethod.Bicubic;
	private static OutOfBoundsStrategy OBS = OutOfBoundsStrategy.ZeroValues;
	
	private static double dx = 10.50;	// translation parameters
	private static double dy = -3.25;
	
	private ImagePlus im;
	
	/**
	 * Constructor, asks to open a predefined sample image if no other image
	 * is currently open.
	 */
	public Pixel_Interpolation_Demo() {
//		if (IjUtils.noCurrentImage()) {
//			DialogUtils.askForSampleImage(GeneralSampleImage.Kepler);
//		}
		if (IjUtils.noCurrentImage() && DialogUtils.askForSampleImage()) {
			IjUtils.runPlugIn(Draw_Test_Grid.class);
		}
	}
	
	// ----------------------------------------
	
    @Override
	public int setup(String arg, ImagePlus im) {
    	this.im = im;
        return DOES_ALL + NO_CHANGES;
    }

    @Override
	public void run(ImageProcessor source) {
    	if (!runDialog())
			return;
    	
    	final int w = source.getWidth();
    	final int h = source.getHeight();
    	
    	// create the target image (same type as source):
    	ImageProcessor target = source.createProcessor(w, h);
    	
    	// create ImageAccessor's for the source and target  image:
    	ImageAccessor sA = ImageAccessor.create(source, OBS, IPM);
    	ImageAccessor tA = ImageAccessor.create(target);
    	
    	// iterate over all pixels of the target image:
    	for (int u = 0; u < w; u++) {	// discrete target position (u,v)
    		for (int v = 0; v < h; v++) {
    			double x = u + dx;	// continuous source position (x,y)
    			double y = v + dy;
    			float[] val = sA.getPix(x, y);
    			tA.setPix(u, v, val);	// update target pixel
    		}
    	}
    	
    	// display the target image:
    	(new ImagePlus(im.getShortTitle() + "-" + IPM.toString(), target)).show();
    }
    
    // --------------------------------------------
    
	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addNumericField("Translation dx", dx, 2);
		gd.addNumericField("Translation dy", dy, 2);
		gd.addEnumChoice("Interpolation method", IPM);
		gd.addEnumChoice("Out-of-bounds strategy", OBS);

		gd.showDialog();
		if (gd.wasCanceled())
			return false;
		
		dx = gd.getNextNumber();
		dy = gd.getNextNumber();
		IPM = gd.getNextEnumChoice(InterpolationMethod.class);
		OBS = gd.getNextEnumChoice(OutOfBoundsStrategy.class);
		return true;
	}
}
