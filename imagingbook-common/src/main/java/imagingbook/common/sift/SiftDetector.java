/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.common.sift;

import ij.process.FloatProcessor;
import imagingbook.common.math.Arithmetic;
import imagingbook.common.math.Matrix;
import imagingbook.common.sift.scalespace.DogOctave;
import imagingbook.common.sift.scalespace.DogScaleSpace;
import imagingbook.common.sift.scalespace.GaussianScaleSpace;
import imagingbook.common.sift.scalespace.ScaleLevel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static imagingbook.common.math.Arithmetic.mod;
import static imagingbook.common.math.Arithmetic.sqr;

/**
 * <p>
 * This class implements the detection of SIFT features from images, as described in [1]. See Ch. 25 of [2] for more
 * details.
 * </p>
 * <p>
 * Currently only images of type {@link FloatProcessor} are supported. The input image is normalized to values in [0,1]
 * before SIFT detection starts. A large set of parameters can be specified (see {@link SiftParameters}).
 * </p>
 * <p>
 * [1] D. G. Lowe. Distinctive image features from scale-invariant keypoints. International Journal of Computer Vision
 * 60, 91–110 (2004). <br> [2] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic
 * Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 *
 * @author WB
 * @version 2022/11/20
 * @see SiftParameters
 */
public class SiftDetector {
	
	/**
	 * Types of 3D neighborhoods used in min/max detection 
	 */
	public enum NeighborhoodType3D {
		NH8(8),	NH10(10), NH18(18), NH26(26);
		private final int size;
		private NeighborhoodType3D(int size) {
			this.size = size;
		}
	}

	static private final double PI2 = 2 * Math.PI;
	
	private final SiftParameters params;
	private final int nhSize;
	private final GaussianScaleSpace G;
	private final DogScaleSpace D;

	// Constructors ------------------------------------ 

	/**
	 * Constructor using default parameters.
	 * @param fp the input image
	 * @see #SiftDetector(FloatProcessor, SiftParameters)
	 */
	public SiftDetector(FloatProcessor fp) {
		this(fp, new SiftParameters());	// uses default parameters
	}

	/**
	 * <p>
	 * Constructor using specific parameters. The input image is normalized to values in [0,1], the minimum pixel value
	 * being mapped to 0 and the maximum value to 1. An exception is thrown if the supplied image is "flat", i.e.,
	 * contains only a single pixel value. A large set of parameters can be specified (see {@link SiftParameters}). The
	 * constructor sets up the complete Gaussian and DoG scale spaces but does not perform feature detection itself,
	 * which is done by calling {@link #getSiftFeatures()}.
	 * </p>
	 *
	 * @param fp the input image
	 * @param params an instance of {@link SiftParameters}
	 * @see SiftParameters
	 */
	public SiftDetector(FloatProcessor fp, SiftParameters params) {
		this.params = params;
		this.nhSize = params.nhType.size;
		if (!normalizeTo01(fp))
			throw new IllegalArgumentException("could not normalize input image");
		this.G = new GaussianScaleSpace(fp, params.P, params.Q, params.sigmaS, params.sigma0, -1, params.Q + 1);
		this.D = new DogScaleSpace(G);
	}
	
	private boolean normalizeTo01(FloatProcessor fp) {
		float[] a = (float[])fp.getPixels();
		float minVal = Matrix.min(a);
		float maxVal = Matrix.max(a);
		float diff = maxVal - minVal;
		if (Arithmetic.isZero(diff)) {
			return false;	// only one pixel value
		}
		float scale = 1.0f / diff;
		for (int i = 0; i < a.length; i++) {
			a[i] = (a[i] - minVal) * scale; 
		}
		return true;
	}
	
	// --------------------------------------------------
	
	/**
	 * Calculates and returns a list of SIFT descriptors.
	 * 
	 * @return a list of extracted SIFT descriptors
	 */
	public List<SiftDescriptor> getSiftFeatures() {
		List<KeyPoint> keyPoints = getKeyPoints();
		List<SiftDescriptor> siftDescriptors = new ArrayList<SiftDescriptor>();
		for (KeyPoint kp : keyPoints) {
			for (double phi_d : getDominantOrientations(kp)) {
				SiftDescriptor sd = makeSiftDescriptor(kp, phi_d);
				if (sd != null) {
					siftDescriptors.add(sd);
				}
			}
		}
		return siftDescriptors;
	}

	/**
	 * Returns the {@link GaussianScaleSpace} for this SIFT detector instance. Mainly intended for debugging and
	 * testing.
	 *
	 * @return the {@link GaussianScaleSpace} for this SIFT detector
	 */
	public GaussianScaleSpace getGaussianScaleSpace() {
		return this.G;
	}

	/**
	 * Returns the {@link DogScaleSpace} for this SIFT detector instance. Mainly intended for debugging and testing.
	 *
	 * @return the {@link DogScaleSpace} for this SIFT detector
	 */
	public DogScaleSpace getDogScaleSpace() {
		return this.D;
	}
	
	// --------------------------------------------------

	/**
	 * Only used for debugging: produces key points with orientation histograms attached for display purposes.
	 *
	 * @param keypoints the sequence of original key points
	 * @return a sequence of enhanced key points
	 */
	@SuppressWarnings("unused")
	private  List<KeyPoint> makeRichKeypoints(List<KeyPoint> keypoints) {
		List<KeyPoint> richKeyPoints = new ArrayList<KeyPoint>();
		for (KeyPoint kp : keypoints) {
			float[] oh = getOrientationHistogram(kp);
			smoothCircular(oh,params.nSmooth);
			kp.orientation_histogram = oh;	// TODO: remove, for testing only!!
			List<Double> peakOrientations = findPeakOrientationIndices(oh);
			for (double km : peakOrientations) {
				//for (int i=0; i<Math.min(1, peakOrientations.length); i++) {	// use only 1 descriptor!
				float phi = (float) (km * 2 * Math.PI / oh.length);	// 0 <= phi < 2 PI. Should be in range +/-PI?
				KeyPoint rkp = kp.clone();
				rkp.orientation = phi;
				richKeyPoints.add(rkp);
			}
		}
		return richKeyPoints;
	}

	/**
	 * Used for debugging/illustrations only!
	 * 
	 * @param oh orientation histogram
	 * @return list of histogram indices for the peak orientations
	 */
	private List<Double> findPeakOrientationIndices(float[] oh) {
		int nb = oh.length;
		List<Double> orientIndexes = new ArrayList<Double>(nb);
		// find the maximum entry in the orientation histogram 'oh':
		float maxh = oh[0];
		for (int k = 1; k < nb; k++) {
			if (oh[k] > maxh)
				maxh = oh[k];
		}

		if (maxh > 0.01f) { // ascertain minimum (non-zero) gradient energy
			// collect all peaks > 80% of the maximum entry in 'oh'
			float minh = maxh * (float) params.tDomOr;
			for (int k = 0; k < nb; k++) { // hp ~ hc ~ ha
				// angles[k] = Float.NaN;
				float hc = oh[k]; // center value
				if (oh[k] > minh) { // value is min. 80% of global peak
					float hp = oh[(k - 1 + nb) % nb]; // previous histogram
					// value
					float hn = oh[(k + 1) % nb]; // next histogram value
					if (hc > hp && hc > hn) { // check if 'hc' is a local peak
						// interpolate orientation by a quadratic function (parabola):
						double delta = interpolateQuadratic(hp, hc, hn);
						double k_max = (k + delta + nb) % nb; 
						// interpolated bin index, 0 <= km < nPhi:
						// double phi_max = k_max * 2 * Math.PI / nb; // 0 <=
						// phi < 2 PI. Should be in range +/-PI?
						orientIndexes.add(k_max);
					}
				}
			}
		}
		return orientIndexes;
	}

	private List<KeyPoint> getKeyPoints() {
		List<KeyPoint> keyPts = new ArrayList<KeyPoint>();
		final int P = params.P;
		final int K = params.Q;
		for (int p = 0; p <= P-1; p++) {	// for every octave p
			for (int q = 0; q <= K-1; q++) {	// for every scale level q
				List<KeyPoint> extrema = findExtrema(p, q);
				for (KeyPoint e : extrema) {
					KeyPoint c = refineKeyPosition(D, e);
					if (c != null) {
						keyPts.add(c);
					}
				}
			}
		}
		Collections.sort(keyPts);	// always sort (by decreasing score)
		return keyPts;
	}

	private List<KeyPoint> findExtrema(int p, int q) {
		final float tMag = (float) params.tMag;
		final float tExtrm = (float) params.tExtrm;
		final DogOctave Dp = D.getOctave(p);
		final ScaleLevel Dpq = D.getScaleLevel(p, q);
		final int M = Dpq.getWidth();
		final int N = Dpq.getHeight();
		
		List<KeyPoint> E = new ArrayList<KeyPoint>();
		float scale = (float)D.getAbsoluteScale(p, q); 	//D.getScaleIndexFloat(p, q); needed?

		final float[][][] nh = new float[3][3][3];			// 3x3x3 neighborhood [q][u][v]
		for (int u = 1; u <= M - 2; u++) {
			float x_real = (float) D.getRealX(p, u);		// for display purposes only
			for (int v = 1; v <= N - 2; v++) {
				float y_real = (float) D.getRealY(p, v);	// for display purposes only
				float mag = Math.abs(Dpq.getValue(u, v));
				if (mag > tMag) {
					Dp.getNeighborhood(q, u, v, nh);		// CHANGE to use D not Dp!!
					if (isExtremum(nh, tExtrm)) {
						KeyPoint e = new KeyPoint(p, q, u, v, u, v, x_real, y_real, scale, mag);
						E.add(e);
					}
				}
			}
		}
		return E;
	}

	private KeyPoint refineKeyPosition(DogScaleSpace D, KeyPoint k) { 
		final int p = k.p;
		final int q = k.q;
		int u = k.u;
		int v = k.v;
		
		final DogOctave Dp = D.getOctave(p);
		final double rhoMax = params.rhoMax;
		final double tPeak = params.tPeak;
		final int nRefine = params.nRefine;

		final float[][][] nh = new float[3][3][3];	// 3x3x3 neighborhood nh[q][u][v]
		final float[] grad 	 = new float[3];		// gradient (dx, dy, ds)	
		final float[] d 	 = new float[3];		// relocation displacement (dx, dy, ds)
		final float[][] hess = new float[3][3];		// 3D Hessian matrix:
													// | dxx dxy dxs |
													// | dxy dyy dys |
													// | dxs dys dss |
		
		final float aMax = (float) (sqr(rhoMax + 1) / rhoMax);
		KeyPoint kr = null;							// refined keypoint
		boolean done = false;
		int n = 1;
		while (!done && n <= nRefine && Dp.isInside(q, u, v)) {
			Dp.getNeighborhood(q, u, v, nh);	// TODO: CHANGE to use D instead of Dp!
			gradient(nh, grad);					// result stored in grad
			hessian(nh, hess);					// result stored in hess
			final double detH = Matrix.determinant3x3(hess);
			if (Arithmetic.isZero(detH)) {	// Hessian matrix has zero determinant?
				done = true;	// ignore this point and finish
			}
			else {
				final float dxx = hess[0][0];	// extract 2x2 Hessian for calculating curvature ratio below
				final float dxy = hess[0][1];
				final float dyy = hess[1][1];
//				final float[][] invHess = Matrix.inverse3x3(hess);	// deprecated
				final float[][] invHess = Matrix.inverse(hess);		// alternative (numerically stable)
				Matrix.multiplyD(invHess, grad, d);	// d <-- invHess . grad
				Matrix.multiplyD(-1, d);			// d <-- - d
				final float xx = d[0];	// = x'
				final float yy = d[1]; 	// = y'

				if (Math.abs(xx) < 0.5f && Math.abs(yy) < 0.5f) {	// stay at this lattice point
					done = true;
					final float Dpeak = nh[1][1][1] + 0.5f * (grad[0]*xx + grad[1]*yy + grad[2]*d[2]);
					final float detHxy = dxx * dyy - dxy * dxy;
					if (Math.abs(Dpeak) > tPeak && detHxy > 0) { // check peak magnitude
						final float a = sqr(dxx + dyy) / detHxy;
						if (a <= aMax) { 					// check curvature ratio (edgeness)
							// new, refined keypoint:
							float x = u + xx;
							float y = v + yy;
							float x_real = (float) D.getRealX(p, x);
							float y_real = (float) D.getRealY(p, y);
							float scale  = (float) D.getAbsoluteScale(p, q);
							kr = new KeyPoint(k.p, k.q, k.u, k.v, x, y, x_real, y_real, scale, k.magnitude);
						}
					}
				}
				else { // large displacement, move to a neighboring DoG cell at same level q by at most one unit
					u = u + Math.min(1, Math.max(-1, Math.round(xx)));	// move u by max +/-1
					v = v + Math.min(1, Math.max(-1, Math.round(yy)));	// move v by max +/-1
					// Note: we don't move along the scale axis!
				}
			}
			n = n + 1;
		}
		return kr;
	}

	private boolean isExtremum(float[][][] nh, float tExtrm) {
		return isLocalMin(nh, tExtrm) || isLocalMax(nh, tExtrm);
	}

	private boolean isLocalMin(final float[][][] neighborhood, float tExtrm) {
		final float c = neighborhood[1][1][1] + tExtrm; // center value + threshold
		if (c >= 0) return false;	// local minimum must have a negative value
		// check 8 neighbors in scale plane q
		if (c >= neighborhood[1][0][0]) return false;
		if (c >= neighborhood[1][1][0]) return false;
		if (c >= neighborhood[1][2][0]) return false;
		if (c >= neighborhood[1][0][1]) return false;
		if (c >= neighborhood[1][2][1]) return false;
		if (c >= neighborhood[1][0][2]) return false;
		if (c >= neighborhood[1][1][2]) return false;
		if (c >= neighborhood[1][2][2]) return false;
		if (nhSize >= 10) {
			if (c >= neighborhood[0][1][1]) return false;
			if (c >= neighborhood[2][1][1]) return false;
			if (nhSize >= 18) {
				// check 4 more neighbors in scale plane q-1
				if (c >= neighborhood[0][0][1]) return false;
				if (c >= neighborhood[0][2][1]) return false;
				if (c >= neighborhood[0][1][0]) return false;
				if (c >= neighborhood[0][1][2]) return false;
				// check 4 more neighbors in scale plane q+1
				if (c >= neighborhood[2][0][1]) return false;
				if (c >= neighborhood[2][2][1]) return false;
				if (c >= neighborhood[2][1][0]) return false;
				if (c >= neighborhood[2][1][2]) return false;
				// optionally check 8 remaining neighbors (corners of cube):
				if (nhSize >= 26) {
					if (c >= neighborhood[0][0][0]) return false;
					if (c >= neighborhood[0][2][0]) return false;
					if (c >= neighborhood[0][0][2]) return false;
					if (c >= neighborhood[0][2][2]) return false;
					if (c >= neighborhood[2][0][0]) return false;
					if (c >= neighborhood[2][2][0]) return false;
					if (c >= neighborhood[2][0][2]) return false;
					if (c >= neighborhood[2][2][2]) return false;
				}
			}
		}
		return true;
	}

	private boolean isLocalMax(final float[][][] neighborhood, float tExtrm) {
		final float c = neighborhood[1][1][1] - tExtrm; 	// center value - threshold
		if (c <= 0) return false;	// local maximum must have a positive value
		// check 8 neighbors in scale plane q
		if (c <= neighborhood[1][0][0]) return false;
		if (c <= neighborhood[1][1][0]) return false;
		if (c <= neighborhood[1][2][0]) return false;
		if (c <= neighborhood[1][0][1]) return false;
		if (c <= neighborhood[1][2][1]) return false;
		if (c <= neighborhood[1][0][2]) return false;
		if (c <= neighborhood[1][1][2]) return false;
		if (c <= neighborhood[1][2][2]) return false;
		if (nhSize >= 10) {
			if (c <= neighborhood[0][1][1]) return false;
			if (c <= neighborhood[2][1][1]) return false;
			if (nhSize >= 18) {
				// check 4 more neighbors in scale plane q-1
				if (c <= neighborhood[0][0][1]) return false;
				if (c <= neighborhood[0][2][1]) return false;
				if (c <= neighborhood[0][1][0]) return false;
				if (c <= neighborhood[0][1][2]) return false;
				// check 4 more neighbors in scale plane q+1
				if (c <= neighborhood[2][0][1]) return false;
				if (c <= neighborhood[2][2][1]) return false;
				if (c <= neighborhood[2][1][0]) return false;
				if (c <= neighborhood[2][1][2]) return false;
				// optionally check 8 remaining neighbors (corners of cube):
				if (nhSize >= 26) {
					if (c <= neighborhood[0][0][0]) return false;
					if (c <= neighborhood[0][2][0]) return false;
					if (c <= neighborhood[0][0][2]) return false;
					if (c <= neighborhood[0][2][2]) return false;
					if (c <= neighborhood[2][0][0]) return false;
					if (c <= neighborhood[2][2][0]) return false;
					if (c <= neighborhood[2][0][2]) return false;
					if (c <= neighborhood[2][2][2]) return false;
				}
			}
		}
		return true;
	}

	/*
	 * Calculates the 3-dimensional gradient vector for the 3x3x3 neighborhood
	 * nh[s][x][y]. The result is stored in the supplied vector grad, to which a reference is 
	 * returned.
	 */
	private float[] gradient(final float[][][] nh, final float[] grad) {
		// Note: factor 0.5f not needed, kept for clarity.
		grad[0] = 0.5f * (nh[1][2][1] - nh[1][0][1]);	// = dx
		grad[1] = 0.5f * (nh[1][1][2] - nh[1][1][0]);	// = dy
		grad[2] = 0.5f * (nh[2][1][1] - nh[0][1][1]);	// = ds
		return grad;
	}

	/* 
	 * Calculates the 3x3 Hessian matrix for the 3x3x3 neighborhood nh[s][i][j],
	 * with scale index s and spatial indices i, j.
	 * The result is stored in the supplied array hess, to which a reference is 
	 * returned.
	 */
	private float[][] hessian(final float[][][] nh, final float[][] hess) {
		final float nh_111 = 2 * nh[1][1][1];
		final float dxx = nh[1][0][1] - nh_111 + nh[1][2][1];
		final float dyy = nh[1][1][0] - nh_111 + nh[1][1][2];
		final float dss = nh[0][1][1] - nh_111 + nh[2][1][1];

		final float dxy = (nh[1][2][2] - nh[1][0][2] - nh[1][2][0] + nh[1][0][0]) * 0.25f;
		final float dxs = (nh[2][2][1] - nh[2][0][1] - nh[0][2][1] + nh[0][0][1]) * 0.25f;
		final float dys = (nh[2][1][2] - nh[2][1][0] - nh[0][1][2] + nh[0][1][0]) * 0.25f;

		hess[0][0] = dxx;  hess[0][1] = dxy;  hess[0][2] = dxs;
		hess[1][0] = dxy;  hess[1][1] = dyy;  hess[1][2] = dys;
		hess[2][0] = dxs;  hess[2][1] = dys;  hess[2][2] = dss;
		return hess;
	}

	/*
	 * Returns a list of orientations (angles) for the keypoint c.
	 */
	private List<Double> getDominantOrientations(KeyPoint c) {
		float[] h_phi = getOrientationHistogram(c);
		smoothCircular(h_phi, params.nSmooth);
		return findPeakOrientations(h_phi);
	}

	// Smoothes the array A in-place (i.e., destructively) in 'iterations' passes.
	private void smoothCircular(float[] X, int n_iter) {
		final float[] H = { 0.25f, 0.5f, 0.25f }; // filter kernel
		final int n = X.length;
		for (int i = 1; i <= n_iter; i++) {
			float s = X[0];
			float p = X[n - 1];
			for (int j = 0; j <= n - 2; j++) {
				float c = X[j];
				X[j] = H[0] * p + H[1] * X[j] + H[2] * X[j + 1];
				p = c;
			}
			X[n - 1] = H[0] * p + H[1] * X[n - 1] + H[2] * s;
		}
	}

	/* 
	 * Extracts the peaks in the orientation histogram 'h_phi'
	 * and returns the corresponding angles in a (possibly empty) list. 
	 */
	private List<Double> findPeakOrientations(float[] h_phi) {
		int n = h_phi.length;
		List<Double> angles = new ArrayList<Double>(n);
		// find the maximum entry in the orientation histogram 'h_phi'
		float h_max = h_phi[0];
		for (int k = 1; k < n; k++) {
			if (h_phi[k] > h_max)
				h_max = h_phi[k];
		}
		if (h_max > 0.01f) {	// ascertain minimum (non-zero) gradient energy 
			// collect all peaks > 80% of the maximum entry in 'oh'
			float h_min = h_max * 0.8f;
			for (int k = 0; k < n; k++) {	// hp ~ hc ~ ha
				float hc = h_phi[k];						// center value
				if (hc > h_min) {		// value is min. 80% of global peak
					float hp = h_phi[(k - 1 + n) % n];	// previous histogram value
					float hn = h_phi[(k + 1) % n];		// next histogram value
					if (hc > hp && hc > hn) {			// check if 'hc' is a local peak
						// interpolate orientation by a quadratic function (parabola):
						double delta = interpolateQuadratic(hp, hc, hn);
						double k_max = (k + delta + n) % n;	// interpolated bin index, 0 <= km < nPhi
						double phi_max = k_max * 2 * Math.PI / n;	// 0 <= phi < 2 PI. Should be in range +/-PI?
						angles.add(phi_max);
					}
				}
			}
		}
		return angles;
	}

	private float[] getOrientationHistogram(KeyPoint c) {
		final int n_phi = params.nOrient;
		final int K = params.Q;

		ScaleLevel Gpq = G.getScaleLevel(c.p, c.q);
		final float[] h_phi = new float[n_phi];	// automatically initialized to zero
		final int M = Gpq.getWidth(), N = Gpq.getHeight();
		final double x = c.x, y = c.y;

		final double sigma_w = 1.5 * params.sigma0 * Math.pow(2,(double)c.q/K);
		final double sigma_w22 = 2 * sigma_w * sigma_w;
		final double r_w = Math.max(1, 2.5 * sigma_w);
		final double r_w2 = r_w * r_w;

		final int u_min = Math.max((int)Math.floor(x - r_w), 1);
		final int u_max = Math.min((int)Math.ceil(x + r_w), M-2);
		final int v_min = Math.max((int)Math.floor(y - r_w), 1);
		final int v_max = Math.min((int)Math.ceil(y + r_w), N-2);

		double[] gradPol = new double[2]; // gradient magnitude/orientation

		for (int u = u_min; u <= u_max; u++) {
			double dx = u - x;					// distance to feature's center
			for (int v = v_min; v <= v_max; v++) {
				double dy = v - y;
				double r2 = dx * dx + dy * dy;	// squared distance from center
				if (r2 < r_w2) {				// inside limiting circle
					Gpq.getGradientPolar(u, v, gradPol);
					double E = gradPol[0], phi = gradPol[1];				// gradient magnitude/orientation
					double wG = Math.exp(-(dx*dx + dy*dy)/sigma_w22);		// Gaussian weight
					double z = E * wG;
					double k_phi = n_phi * phi / (2 * Math.PI);				// continuous histogram bin index
					double alpha = k_phi - Math.floor(k_phi);				// weight alpha
					int k_0 = Math.floorMod((int)Math.floor(k_phi), n_phi);	// lower histogram index
					int k_1 = Math.floorMod(k_0 + 1, n_phi);							// upper histogram index
					h_phi[k_0] = (float) (h_phi[k_0] + (1 - alpha) * z);	// distribute z into bins k_0, k_1
					h_phi[k_1] = (float) (h_phi[k_1] + alpha * z);
				}
			}
		}
		return h_phi;
	}

	private SiftDescriptor makeSiftDescriptor(KeyPoint c, double phi_d) {
		final int p = c.p;
		final int q = c.q;
		final double x = c.x;
		final double y = c.y;
		final double mag = c.magnitude;

		ScaleLevel Gpq = G.getScaleLevel(p, q);
		final int M = Gpq.getWidth(), N = Gpq.getHeight(), K = G.getQ();

		double sigma_q = G.getSigma_0() * Math.pow(2, (double) q / K);	// = Gpq.getAbsoluteScale() ?? decimated scale
		double w_d = params.sDesc * sigma_q;		// descriptor width
		double sigma_d = 0.25 * w_d;	// width of Gaussian weighting function
		double sigma_d2 = 2 * sigma_d * sigma_d;
		double r_d = 2.5 * sigma_d;		// limiting radius
		double r_d2 = r_d * r_d;		// squared limiting radius

		double sc = 1.0/w_d;					// scale to canonical frame
		double sin_phi_d = Math.sin(-phi_d);	// precalculated sine
		double cos_phi_d = Math.cos(-phi_d);	// precalculated cosine

		int u_min = Math.max((int)Math.floor(x - r_d), 1);
		int u_max = Math.min((int)Math.ceil(x + r_d), M - 2);

		int v_min = Math.max((int)Math.floor(y - r_d), 1);
		int v_max = Math.min((int)Math.ceil(y + r_d), N - 2);

		// create the 3D orientation histogram, initialize to zero:
		final int n_Spat = params.nSpat;
		final int n_Angl = params.nAngl;
		double[][][] h_grad = new double[n_Spat][n_Spat][n_Angl];
		double[] gmo = new double[2];	// gradient magnitude/orientation

		for (int u = u_min; u <= u_max; u++) {
			double dx = u - x;
			for (int v = v_min; v <= v_max; v++) {
				double dy = v - y;
				double r2 = sqr(dx) + sqr(dy);	// squared distance from center
				if (r2 < r_d2) {					// inside limiting circle
					// map to the canonical coordinate frame, ii,jj \in [-1/2, +1/2]:
					double uu = sc * (cos_phi_d * dx - sin_phi_d * dy);
					double vv = sc * (sin_phi_d * dx + cos_phi_d * dy);
					// calculate the gradient of Gaussian scale level (p,q) at position (u,v):
					Gpq.getGradientPolar(u, v, gmo);
					double E = gmo[0];			// gradient magnitude
					double phi = gmo[1];		// gradient orientation
					double phi_norm = mod(phi - phi_d, PI2);// normalized gradient orientation	
					double w_G = Math.exp(-r2/sigma_d2);	// Gaussian weight
					double z = E * w_G;						// quantity to accumulate
					updateGradientHistogram(h_grad, uu, vv, phi_norm, z);
				}
			}
		}
		int[] f_int = makeFeatureVector(h_grad);
		double sigma_pq = G.getAbsoluteScale(p, q);
		double x_real = G.getRealX(p, x);
		double y_real = G.getRealY(p, y);
		return new SiftDescriptor(x_real, y_real, sigma_pq, p, mag, phi_d, f_int);
	}

	private void updateGradientHistogram(double[][][] h_grad, double uu, double vv, double phi_norm, double z) {
		final int n_Spat = params.nSpat;
		final int n_Angl = params.nAngl;

		final double ii = n_Spat * uu + 0.5 * (n_Spat - 1);	// continuous spatial histogram index i'
		final double jj = n_Spat * vv + 0.5 * (n_Spat - 1);	// continuous spatial histogram index j'
		final double kk = phi_norm * (n_Angl / PI2);		// continuous orientation histogram index k'

		final int i0 = (int) Math.floor(ii);
		final int i1 = i0 + 1;
		
		final int j0 = (int) Math.floor(jj);
		final int j1 = j0 + 1;
		
		final int k0 = Math.floorMod((int) Math.floor(kk), n_Angl);
		final int k1 = (k0 + 1) % n_Angl;			// k0 >= 0
		
		final double alpha0 = 1.0 - (ii - i0);
		final double alpha1 = 1.0 - alpha0;

		final double beta0 = 1.0 - (jj - j0);
		final double beta1 = 1.0 - beta0;

		final double gamma0 = 1.0 - (kk - Math.floor(kk));
		final double gamma1 = 1.0 - gamma0;

		final int[] I = {i0, i1};	// index arrays used in loops below
		final int[] J = {j0, j1};
		final int[] K = {k0, k1};

		final double[] A = {alpha0, alpha1};
		final double[] B = {beta0, beta1};
		final double[] C = {gamma0, gamma1};

		// distribute z over 8 adjacent spatial/angular bins:
		for (int a = 0; a <= 1; a++) {
			int i = I[a];
			if (i >= 0 && i < n_Spat) {
				double wa = A[a];
				for (int b = 0; b <= 1; b++) {
					int j = J[b];
					if (j >= 0 && j < n_Spat) {
						double wb = B[b];
						for (int c = 0; c <= 1; c++) {
							int k = K[c];
							double wc = C[c];
							h_grad[i][j][k] = h_grad[i][j][k] + z * wa * wb * wc;

						}
					}
				}
			}
		}
	}

	private int[] makeFeatureVector(double[][][] h_grad) {
		final int n_Spat = params.nSpat;
		final int n_Angl = params.nAngl;
		float[] f = new float[n_Spat * n_Spat * n_Angl];
		// flatten 3D histogram to a 1D vector
		// the histogram is vectorized such that k (phi) is the fastest
		// varying index, followed by j and i (which is the slowest index).
		int m = 0;
		for (int i = 0; i < n_Spat; i++) {
			for (int j = 0; j < n_Spat; j++) {
				for (int k = 0; k < n_Angl; k++) {
					f[m] = (float) h_grad[i][j][k];
					m = m + 1;
				}
			}
		}
		normalize(f);
		clipPeaks(f, (float) params.tFclip);	
		normalize(f);
		return mapToIntegers(f, (float) params.sFscale);
	}

	private void normalize(float[] x) {
		final double norm = Matrix.normL2(x);
		if (norm > Arithmetic.EPSILON_FLOAT) {
			final float s = (float) (1.0 / norm);
			for (int i = 0; i < x.length; i++) {
				x[i] = s * x[i];
			}
		}
	}

	private void clipPeaks(float[] x, float xmax) {
		for (int i = 0; i<x.length; i++) {
			if (x[i] > xmax) {
				x[i] = xmax;
			}
		}
	}

	private int[] mapToIntegers(float[] x, float s) {
		int[] ivec = new int[x.length];
		for (int i = 0; i < x.length; i++) {
			ivec[i] = Math.round(s * x[i]);
		}
		return ivec;
	}

	//  auxiliary methods -------------------------

	/*
	 * Calculate the extremal position from 3 discrete function values 
	 * at x = -1, 0, +1: f(-1) = 'y1', f(0) = 'y2', f(+1) = 'y3'.
	 */
	private float interpolateQuadratic(float y1, float y2, float y3) {
		float a = (y1 - 2*y2 + y3) / 2;
		if (Math.abs(a) < 0.00001f) {
			throw new IllegalArgumentException("quadratic interpolation failed " 
					+ a + " " + y1 + " " + y2 + " " + y3);
		}
		float b = (y3 - y1) / 2;
//		float c = y2;
		float x_extrm = -b / (2*a);		// extremal position
//		float y_extrm = a*x*x + b*x + c;	// extremal value (not needed)
		return x_extrm;	// x is in [-1,+1]
	}

}
