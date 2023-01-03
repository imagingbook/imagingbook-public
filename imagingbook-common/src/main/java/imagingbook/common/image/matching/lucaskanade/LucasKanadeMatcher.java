/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.image.matching.lucaskanade;

import ij.ImagePlus;
import ij.process.FloatProcessor;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.Pnt2d.PntDouble;
import imagingbook.common.geometry.mappings.linear.AffineMapping2D;
import imagingbook.common.geometry.mappings.linear.ProjectiveMapping2D;
import imagingbook.common.geometry.mappings.linear.Translation2D;
import imagingbook.common.math.Matrix;
import imagingbook.common.util.ParameterBundle;

/**
 * <p>
 * This is the common super-class for different variants of the Lucas-Kanade matcher [1]. See Ch. 24 of [2] for
 * additional details.
 * </p>
 * <p>
 * [1] B. D. Lucas and T. Kanade. "An iterative image registration technique with an application to stereo vision". In
 * Proceedings of the 7th International Joint Conference on Artificial Intelligence IJCAI’81, pp. 674–679, Vancouver, BC
 * (1981).
 * <br>
 * [2] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2022/12/16
 */
public abstract class LucasKanadeMatcher {

	/**
	 * Default parameters for the containing class and its sub-classes; a (usually modified) instance of this class is
	 * passed to the constructor of a non-abstract sub-class.
	 */
	public static class Parameters implements ParameterBundle<LucasKanadeMatcher> {
		/** Convergence limit */
		public double tolerance = 0.00001;
		/** Maximum number of iterations */
		public int maxIterations = 100;
		/** Set true to output debug information */
		public boolean debug = false;
		/** Set true to display the steepest-descent images */
		public boolean showSteepestDescentImages = false;
		/** Set true to display the Hessian matrices */
		public boolean showHessians = false;
	}
	
	final FloatProcessor I; 		// search image
	final FloatProcessor R; 		// reference image
	final Parameters params;		// parameter object
	
	final int wR, hR;				// width/height of R
	final double xc, yc;			// center (origin) of R
	
	int iteration = -1;
	
	/**
	 * Constructor.
	 * @param I the search image (of type {@link FloatProcessor})
	 * @param R the reference image (of type {@link FloatProcessor})
	 * @param params a parameter object (of type {@link LucasKanadeMatcher.Parameters})
	 */
	LucasKanadeMatcher(FloatProcessor I, FloatProcessor R, Parameters params) {
		this.I = I;	// search image
		this.R = R;	// reference image
		this.params = params;
		wR = R.getWidth();
		hR = R.getHeight();
		xc = 0.5 * (wR - 1);
		yc = 0.5 * (hR - 1);
	}

	/**
	 * Calculates the projective transformation that maps the reference image R (centered at the origin) to some other
	 * quad Q.
	 *
	 * @param Q an arbitrary quad (should be inside the search image I)
	 * @return the transformation from R's bounding rectangle to Q
	 */
	public ProjectiveMapping2D getReferenceMappingTo(Pnt2d[] Q) {	// TODO: move to plugin where used
		Pnt2d[] Rpts = this.getReferencePoints();
		return ProjectiveMapping2D.fromPoints(Rpts, Q);
	}
	
	/**
	 * Returns the corner points of the bounding rectangle of R, centered at the origin.
	 * @return the corner points of the bounding rectangle of R
	 */
	public Pnt2d[] getReferencePoints() {
		double xmin = -xc;
		double xmax = -xc + wR - 1;
		double ymin = -yc;
		double ymax = -yc + hR - 1;
		Pnt2d[] pts = new Pnt2d[4];
		pts[0] = PntDouble.from(xmin, ymin);
		pts[1] = PntDouble.from(xmax, ymin);
		pts[2] = PntDouble.from(xmax, ymax);
		pts[3] = PntDouble.from(xmin, ymax);
		return pts;
	}

	/**
	 * Performs the full optimization on the given image pair (I, R).
	 *
	 * @param Tinit the transformation from the reference image R to the initial search patch, assuming that R is
	 * centered at the coordinate origin!
	 * @return the transformation to the best-matching patch in the search image I (again assuming that R is centered at
	 * the coordinate origin) or null if no match was found.
	 */
	public ProjectiveMapping2D getMatch(ProjectiveMapping2D Tinit) {
		ProjectiveMapping2D Tp = Tinit;
		do {
			Tp = iterateOnce(Tp);		// to be implemented by sub-classes
		} while (Tp != null && !hasConverged() && getIteration() < params.maxIterations);
		return Tp;
	}

	/**
	 * Performs a single matching iteration on the given image pair (I, R).
	 *
	 * @param Tp the warp transformation from the reference image R to the initial search patch, assuming that R is
	 * centered at the coordinate origin!
	 * @return a new warp transformation (again assuming that R is centered at the coordinate origin) or null if the
	 * iteration was unsuccessful.
	 */
	public abstract ProjectiveMapping2D iterateOnce(ProjectiveMapping2D Tp);

	/**
	 * Checks if the matcher has converged.
	 * @return true if minimization criteria have been reached.
	 */
	public abstract boolean hasConverged();

	/**
	 * Measures the RMS intensity difference between the reference image R and the patch in the search image I defined
	 * by the current warp Tp.
	 *
	 * @return the RMS error under the current warp
	 */
	public abstract double getRmsError();

	/**
	 * Returns the current iteration number.
	 * @return the current iteration number
	 */
	public int getIteration() {
		return iteration;
	}
	
	// ------------------------------------------------------------------------------------
	
	FloatProcessor gradientX(FloatProcessor fp) {
		// Sobel-kernel for x-derivatives:
	    final float[] Hx = Matrix.multiply(1f/8, new float[] {
				-1, 0, 1,
			    -2, 0, 2,
			    -1, 0, 1
			    });
	    FloatProcessor fpX = (FloatProcessor) fp.duplicate();
	    fpX.convolve(Hx, 3, 3);
	    return fpX;
	}
	
	FloatProcessor gradientY(FloatProcessor fp) {
		// Sobel-kernel for y-derivatives:
		final float[] Hy = Matrix.multiply(1f/8, new float[] {
						-1, -2, -1,
						 0,  0,  0,
						 1,  2,  1
						 });
	    FloatProcessor fpY = (FloatProcessor) fp.duplicate();
	    fpY.convolve(Hy, 3, 3);
	    return fpY;
	}
	
	// ------------------------- utility methods --------------------------
	
	/* We must be precise about the corner points of a rectangle:
	 * If rectangle r = <u, v, w, h>, all integer values, then the first
	 * top-left corner point (u, v) corresponds to the center of pixel
	 * (u, v). The rectangle covers w pixels horizontally, i.e., 
	 * pixel 0 = (u,v), 1 = (u+1,v), ..., w-1 = (u+w-1,v).
	 * Thus ROIs must have width/height > 1!
	 */
	
//	@Deprecated
//	private Point[] getPoints(Rectangle2D r) {	// does -1 matter? YES!!! CORRECT!
//		IJ.log("getpoints1:  r = " + r.toString());
//		double x = r.getX();
//		double y = r.getY();
//		double w = r.getWidth();
//		double h = r.getHeight();
//		Point[] pts = new Point[4];
//		pts[0] = new Point.Double(x, y);
//		pts[1] = new Point.Double(x + w - 1, y);
//		pts[2] = new Point.Double(x + w - 1, y + h - 1);
//		pts[3] = new Point.Double(x, y + h - 1);
//		//IJ.log("getpoints1:  p1-4 = " + pts[0] + ", " + pts[1] + ", " + pts[2] + ", " + pts[3]);
//		return pts;
//	}
	
	void showSteepestDescentImages(double[][][] S) {	// S[u][v][n]
		String titlePrefix = "sd";
		int w = S.length;
		int h = S[0].length;
		int n = S[0][0].length;
		for (int i = 0; i < n; i++) {
			FloatProcessor sdip = new FloatProcessor(w, h);
			for (int u = 0; u < w; u++) {
				for (int v = 0; v < h; v++) {
					sdip.setf(u, v, (float) S[u][v][i]);
				}
			}
			(new ImagePlus(titlePrefix + i, sdip)).show();
		}
	}
	
	// ported from ProjectiveMapping2D --------------------------------
	
	double[] getParameters(ProjectiveMapping2D map) {
		double[][] A = map.getTransformationMatrix();
		return new double[] {
				A[0][0]-1, A[0][1], A[1][0], A[1][1]-1, A[2][0], A[2][1], A[0][2], A[1][2]};
		//return new double[] { a00 - 1, a01, a10, a11 - 1, a20, a21, a02, a12 };
	}
	
	ProjectiveMapping2D toProjectiveMap(double[] param) {
		if (param.length < 8) {
			throw new IllegalArgumentException("Affine mapping requires 8 parameters");
		}
		return new ProjectiveMapping2D(
				param[0] + 1,   param[1],        param[6],
				param[2],       param[3] + 1,    param[7],
				param[4],       param[5]             );
	}
	
	// ---------------------
	
	double[] getParameters(AffineMapping2D map) {
		double[][] A = map.getTransformationMatrix();
		return new double[] { 
				A[0][0] - 1, A[0][1], A[1][0], A[1][1]-1, A[0][2], A[1][2]};
		//return new double[] { a00 - 1, a01, a10, a11 - 1, a02, a12 };
	}
	
	AffineMapping2D toAffineMap(double[] param) {
		if (param.length < 6) {
			throw new IllegalArgumentException("Affine mapping requires 6 parameters");
		}
		return new AffineMapping2D(
				param[0] + 1, param[1],     param[4],
				param[2],     param[3] + 1, param[5]);
	}
	
	// --------------------
	
	double[] getParameters(Translation2D map) {
		double[][] A = map.getTransformationMatrix();
//		double[] p = new double[] {a02,	a12};
		return new double[] {A[0][2], A[1][2]};
	}
	
	Translation2D toTranslation(double[] p) {
		if (p.length < 2) {
			throw new IllegalArgumentException("Translation requires 2 parameters");
		}
		return new Translation2D(p[0], p[1]);
	}

}
