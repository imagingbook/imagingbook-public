/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.mser;

import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import ij.process.ImageProcessor;
import imagingbook.common.filter.linear.GaussianFilter;
import imagingbook.common.geometry.ellipse.GeometricEllipse;
import imagingbook.common.geometry.mappings.linear.AffineMapping2D;
import imagingbook.common.geometry.mappings.linear.Translation2D;
import imagingbook.common.image.ImageMapper;
import imagingbook.common.image.interpolation.InterpolationMethod;
import imagingbook.common.math.Matrix;
import imagingbook.common.mser.components.Component;


/**
 * <p>
 * Provides functionality to extract local affine frames from a given image.
 * Assumes that the specified image has a white background (255) and black (0) objects.
 * See Section 26.5 of [1] for a detailed description.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Approach</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @see AffineMapping2D
 * @see ImageMapper
 */
public class LocalAffineFrameExtractor {	// TODO: needs testing!
	
	private static InterpolationMethod interpolMethod = InterpolationMethod.Bilinear;
	
	private final ImageProcessor sourceIp;
	private final int m;					// size of frame to extract (W = H = 2 m + 1)
	private final double s;					// size ratio between outer and inner ellipse
	private final boolean useAntiAliasingFilter;	
	
	/**
	 * Full constructor.
	 * @param sourceIp the image to extract frames from
	 * @param m size of the (square) frames to extract (W = H = 2 m + 1)
	 * @param s size ratio between outer and inner ellipse
	 * @param useAntiAliasingFilter turn anti-aliasing filter on or off
	 */
	public LocalAffineFrameExtractor(ImageProcessor sourceIp, int m, double s, boolean useAntiAliasingFilter) {
		this.sourceIp = sourceIp;
		this.m = m;
		this.s = s;
		this.useAntiAliasingFilter = useAntiAliasingFilter;
	}
	
	/**
	 * Short constructor.
	 * @param sourceIp the image to extract frames from
	 * @param m size of the (square) frames to extract (W = H = 2 m + 1)
	 */
	public LocalAffineFrameExtractor(ImageProcessor sourceIp, int m) {
		this(sourceIp, m, 1.5, true);
	}
	
	/**
	 * Extracts a single local-affine frame for the specified
	 * MSER component.
	 * The extracted image has the same type as the original source image
	 * (passed to the constructor).
	 * 
	 * @param mser a MSER component
	 * @return the local-affine frame (instance of {@link ImageProcessor})
	 */
	public ImageProcessor getLocalAffineFrame(Component<MserData> mser) {
		MserData props = mser.getProperties();
		double[] xc = props.getCenter().toDoubleArray(); //{mu10, mu01};
		double[][] S = props.getCovarianceMatrix();
		
		EigenDecomposition ed = new EigenDecomposition(MatrixUtils.createRealMatrix(S));
		RealMatrix V = ed.getV();
		RealMatrix D = ed.getD();
		
		for (int i = 0; i < 2; i++) {
			D.setEntry(i, i, Math.sqrt(D.getEntry(i, i)));
		}
		
		RealMatrix A = V.multiply(D).scalarMultiply(2.0 * s / m);
		double detA = Matrix.determinant2x2(A.getData());

		if (detA < 0) {	// reflection
			RealMatrix reflect = MatrixUtils.createRealMatrix(new double[][] {{1,0},{0,-1}});
			A = A.multiply(reflect);	// undo reflection!!
		}
		
		ImageProcessor ip2 = sourceIp.duplicate();
		ip2.invert(); // makes the background black, objects white
		
		if (useAntiAliasingFilter) {
			GeometricEllipse ellipse = props.getEllipse();
			double dMax = s * ellipse.ra / m;	// scale ratio
			double sigma = 0;
			if (dMax > 1.5) {	// down-scaling, sample width increases by factor dScale
				sigma = (dMax - 1) * 0.5;	// factor 0.5 is ad hoc (full dMax blurs too much)
			}
			if (sigma > 0) {
				GaussianFilter gf = new GaussianFilter(sigma);
				gf.applyTo(ip2);
			}
		}
		
		int frameSize = 2 * m + 1;
		ImageProcessor laf = sourceIp.createProcessor(frameSize, frameSize);
		
		AffineMapping2D T1 = new Translation2D(-m, -m);
		AffineMapping2D AA = new AffineMapping2D(A.getData());
		AffineMapping2D T2 = new Translation2D(xc);
		
		AffineMapping2D M = T1.concat(AA).concat(T2);
		ImageMapper mapper = new ImageMapper(M, null, interpolMethod);
		
		mapper.map(ip2, laf);
		
		laf.invert(); // makes background white again		
		return laf;
	}

}
