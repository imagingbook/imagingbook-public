/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.lucaskanade.utils;

import ij.process.ImageProcessor;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.Pnt2d.PntInt;
import imagingbook.common.geometry.mappings.linear.AffineMapping2D;
import imagingbook.common.geometry.mappings.linear.LinearMapping2D;
import imagingbook.common.geometry.mappings.linear.ProjectiveMapping2D;
import imagingbook.common.image.access.ImageAccessor;

/**
 * Use to extract warped images for testing the Lucas-Kanade matcher.
 * 
 * @author WB
 *
 */
public class ImageExtractor {
	
	
//	public static ImageProcessor extract(ImageProcessor source, int width, int height, LinearMapping T) {
//		ImageExtractor ie = new ImageExtractor(source);
//		return ie.extractImage(width, height, T);
//	}
	
		
	private int interpolationMethod = ImageProcessor.BILINEAR;
	private final ImageProcessor I;
	
	/**
	 * Creates a new instance of {@link ImageExtractor} for the image {@code I}.
	 * @param I the target image.
	 */
	public ImageExtractor(ImageProcessor I) {
		this.I = I;
	}
	
	public void setInterpolationMethod(int interpolationMethod) {
		this.interpolationMethod = interpolationMethod;
	}
	
	/**
	 * Extracts an image {@code R} of size {@code width} x {@code height} from the source image 
	 * {@code I} (referenced by {@code this} object).
	 * The image {@code R} is extracted from a quadrilateral patch of the source image,
	 * defined by the transformation of the boundary of {@code R} by {@code T(x)}.
	 * @param width the width of the target image {@code R}.
	 * @param height the height of the target image {@code R}.
	 * @param T a {@link LinearMapping2D} object.
	 * @return the extracted image {@code R}, which is of the same type as the source image.
	 */	

	public ImageProcessor extractImage(int width, int height, LinearMapping2D T) {
		ImageProcessor R = I.createProcessor(width, height);
		extractImage(R, T);
		return R;
	}
	
	public ImageProcessor extractImage(int width, int height, Pnt2d[] sourcePnts) {
		ImageProcessor R = I.createProcessor(width, height);
		ProjectiveMapping2D T = getMapping(width, height, sourcePnts);
		extractImage(R, T);
		return R;
	}
	
	/**
	 * Fills the image {@code R} from the source image 
	 * {@code I} (referenced by {@code this} object).
	 * The image {@code R} is extracted from a quadrilateral patch of the source image,
	 * defined by the transformation of the boundary of {@code R} by {@code T(x)}.
	 * Grayscale and color images my not be mixed (i.e., {@code R} must be of the same type as {@code I}).
	 * @param R the image to be filled.
	 * @param T a {@link LinearMapping2D} object.
	 */	
	public void extractImage(ImageProcessor R, LinearMapping2D T) {
		int prevInterpolationMethod = I.getInterpolationMethod();
		// save current interpolation method
		I.setInterpolationMethod(interpolationMethod);
		
		ImageAccessor iaI = ImageAccessor.create(I);
		ImageAccessor iaR = ImageAccessor.create(R);
	
		int wT = R.getWidth();
		int hT = R.getHeight();
		for (int u = 0; u < wT; u++) {
			for (int v = 0; v < hT; v++) {
				Pnt2d uv = PntInt.from(u, v);
				Pnt2d xy = T.applyTo(uv);
				float[] val = iaI.getPix(xy.getX(), xy.getY());
				iaR.setPix(u, v, val);
			}
		}
		// restore interpolation method
		I.setInterpolationMethod(prevInterpolationMethod);
	}
	
	/**
	 * Extracts a warped sub-image of the associated target image I,
	 * defined by a sequence of 3 or 4 points. In the case of 3 
	 * points in sourcePnts, an {@link AffineMapping2D} is used; with 4 points,
	 * a {@link ProjectiveMapping2D} is used. The 3 or 4 points map clockwise to
	 * the corner points of the target image R, starting with the top-left corner.
	 * @param R the target image;
	 * @param sourcePnts an array of 3 or 4 {@link Pnt2d} objects.
	 */
	public void extractImage(ImageProcessor R, Pnt2d[] sourcePnts) {
		ProjectiveMapping2D T = getMapping(R.getWidth(), R.getHeight(), sourcePnts);
		extractImage(R, T);
	}
	
	private ProjectiveMapping2D getMapping(int w, int h, Pnt2d[] sourcePnts) {
		Pnt2d[] targetPnts = {
				PntInt.from(0, 0), PntInt.from(w - 1, 0),
				PntInt.from(w - 1, h - 1), PntInt.from(0, h - 1)
			};
		ProjectiveMapping2D T = null;
		switch (sourcePnts.length) {
		case (3) : T = AffineMapping2D.fromPoints(targetPnts, sourcePnts); break;
		case (4) : T = ProjectiveMapping2D.fromPoints(targetPnts, sourcePnts); break;
		default : throw new IllegalArgumentException("wrong number of source points");
		}
		return T;
	}
	
}
