/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.sift;

import imagingbook.common.ij.DialogUtils.DialogHide;
import imagingbook.common.ij.DialogUtils.DialogLabel;
import imagingbook.common.sift.SiftDetector.NeighborhoodType3D;
import imagingbook.common.util.ParameterBundle;

/**
 * Default parameters; a (usually modified) instance of this class may be passed to the constructor of
 * {@link SiftDetector}.
 */
public class SiftParameters implements ParameterBundle<SiftDetector> {
	
	/** Set true to output debug information. */
	@DialogHide
	public boolean DEBUG = false;
	
	/** Type of neigborhood used for peak detection in 3D scale space. */
	@DialogLabel("Neighborhood for 3D peak detection")
	public NeighborhoodType3D nhType = NeighborhoodType3D.NH18;
	
	/** Sampling scale (nominal smoothing level of the input image). */
	@DialogLabel("Nominal sampling scale (sigmaS)")
	public double sigmaS = 0.5;
	
	/** Base scale at level 0 (base smoothing). */
	@DialogLabel("Base scale at level 0 (sigma0)")
	public double sigma0 = 1.6;
	
	/** Number of octaves in Gaussian/DoG scale space. */
	@DialogLabel("Number of scale space octaves (P)")
	public int P = 4;
	
	/** Scale steps (levels) per octave. */
	@DialogLabel("Scale levels per octaves (Q)")
	public int Q = 3;
	
	/** Minimum magnitude required in DoG peak detection (abs. value). */
	@DialogLabel("Minimum detection magnitude (tMag)")
	public double tMag = 0.01;
	
	
	/** Minimum DoG magnitude required for extrapolated peaks (abs. value). */
	@DialogLabel("Minimum peak magnitude (tPeak)")
	public double tPeak = tMag;
	
	/** Minimum difference to all neighbors in DoG peak detection (max. 0.0005). */
	@DialogLabel("Minimum neigborhood difference (tExtrm)")
	public double tExtrm = 0.0;
	
	/** Maximum number of iterations for refining the position of a key point. */
	@DialogLabel("Maximum position refinement steps (nRefine)")
	public int nRefine = 5;
	
	/** Maximum principal curvature ratio used to eliminate line-like structures (3..10). */
	@DialogLabel("Maximum principal curvature ratio (rhoMax=3..10)")
	public double rhoMax = 10.0;
	
	/** Number of orientation bins in the feature descriptor (angular resolution). */
	@DialogLabel("Number of orientation bins (nOrient)")
	public int nOrient = 36;
	
	/** Number of smoothing steps applied to the orientation histogram. */
	@DialogLabel("Histogram smoothing steps (nSmooth)")
	public int nSmooth = 2;
	
	/** Minimum value in orientation histogram for dominant orientations (rel. to max. entry). */
	@DialogLabel("Minimum value in orientation histogram (tDomOr)")
	public double tDomOr = 0.8;
	
	/** Number of spatial descriptor bins along each x/y axis. */
	@DialogLabel("Number of spatial descriptor bins (nSpat)")
	public int nSpat = 4;
	
	/** Number of angular descriptor bins. */
	@DialogLabel("Number of angular descriptor bins (nAngl)")
	public int nAngl = 8;
	
	/** Maximum value in normalized feature vector (0.2 recommended by Lowe). */
	@DialogLabel("Maximum normalized feature value (tFclip)")
	public double tFclip = 0.2;
	
	/** Scale factor for converting normalized features to byte values in [0,255]. */
	@DialogLabel("Feature integer conversion scale (sFscale)")
	public double sFscale = 512.0;
	
	/** Spatial size factor of descriptor (relative to feature scale). */
	@DialogLabel("Descriptor display size factor (sDesc)")
	public double sDesc = 10.0;
	
//	/** Set true to sort detected keypoints by response magnitude. */
//	@DialogLabel("Sort keypoints by score magnitude")
//	public boolean sortKeyPoints = true;
}