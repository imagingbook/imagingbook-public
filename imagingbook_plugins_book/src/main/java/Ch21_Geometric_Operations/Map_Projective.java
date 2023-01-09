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
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.mappings.linear.LinearMapping2D;
import imagingbook.common.geometry.mappings.linear.ProjectiveMapping2D;
import imagingbook.common.ij.DialogUtils;
import imagingbook.common.ij.IjUtils;
import imagingbook.common.image.ImageMapper;
import imagingbook.common.image.OutOfBoundsStrategy;
import imagingbook.common.image.interpolation.InterpolationMethod;
import imagingbook.common.math.Matrix;
import imagingbook.sampleimages.GeneralSampleImage;

/**
 * <p>
 * This plugin demonstrates the use of geometric mappings, as implemented in the imagingbook library. A
 * {@link ProjectiveMapping2D} (transformation) is specified by 4 corresponding point pairs, given by point sequences P
 * and Q. The inverse mapping is required for target-to-source mapping. See Sec. 21.1.4 of [1] for details. The actual
 * pixel transformation is performed by an {@link ImageMapper} object. Try on a suitable test image and check if the
 * image corners (P) are mapped to the points specified in Q. This plugin works for all image types. The transformed
 * image is shown in a new window, the original image remains unchanged. Optionally opens a sample image if no image is
 * currently open.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2022/11/28
 * @see LinearMapping2D
 * @see ProjectiveMapping2D
 * @see ImageMapper
 */
public class Map_Projective implements PlugInFilter {
	
	private static Pnt2d[] P = {		// source quad
			Pnt2d.from(0, 0),
			Pnt2d.from(400, 0),
			Pnt2d.from(400, 400),
			Pnt2d.from(0, 400)};

	private static Pnt2d[] Q = {		// target quad
			Pnt2d.from(0, 60),
			Pnt2d.from(400, 20),
			Pnt2d.from(300, 400),
			Pnt2d.from(30, 200)};

	private ImagePlus im;
	
	/**
	 * Constructor, asks to open a predefined sample image if no other image
	 * is currently open.
	 */
	public Map_Projective() {
		if (IjUtils.noCurrentImage()) {
			DialogUtils.askForSampleImage(GeneralSampleImage.Kepler);
		}
	}
	
	@Override
	public int setup(String arg, ImagePlus im) {
		this.im = im;
		return DOES_ALL + NO_CHANGES;
	}

	@Override
	public void run(ImageProcessor source) {
		int W = source.getWidth();
		int H = source.getHeight();
		
		// create the target image:
		ImageProcessor target = source.createProcessor(W, H); 
		
		// create the target-to source mapping, i.e. Q -> P. 
		// there are 2 alternatives:
		LinearMapping2D m = ProjectiveMapping2D.fromPoints(P, Q);		// P -> Q, then invert
		LinearMapping2D mi = m.getInverse();		
		//Mapping2D mi = ProjectiveMapping2D.fromPoints(Q, P);	// Q -> P = inverse mapping

		// create a mapper instance:
		ImageMapper mapper = 
				new ImageMapper(mi, OutOfBoundsStrategy.ZeroValues, InterpolationMethod.Bicubic);
		
		// apply the mapper:
		mapper.map(source, target);
		
		// display the target image:
		new ImagePlus(im.getShortTitle() + "-transformed", target).show();
		
		IJ.log("A = \n" + Matrix.toString(m.getTransformationMatrix()));
	}
}
