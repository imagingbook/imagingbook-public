/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.ransac;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ij.process.ByteProcessor;
import imagingbook.common.geometry.basic.Curve2d;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.ij.IjUtils;
import imagingbook.common.ij.DialogUtils.DialogLabel;
import imagingbook.common.util.ParameterBundle;

/**
 * Generic RANSAC detector for 2D curves. This abstract class defines the core RANSAC
 * functionality used by all derived (concrete) classes.
 * 
 * @author WB
 * 
 * @see RansacLineDetector
 * @see RansacCircleDetector
 * @see RansacEllipseDetector
 * @see Curve2d
 *
 * @param <T> generic type extending {@link Curve2d}
 */
public abstract class RansacCurveDetector<T extends Curve2d> {
	
	/**
	 * Parameters used by all RANSAC types.
	 */
	public static class RansacParameters implements ParameterBundle {
			
		/** The number of iterations (random point draws) to use in each detection cycle.*/
		@DialogLabel("Number of random draws") 
		public int randomPointDraws = 1000;
		
		/** The maximum distance of any point from the curve to be considered an "inlier".*/
		@DialogLabel("Max. inlier distance") 
		public double maxInlierDistance = 2.0;
		
		/** The minimum number of inliers required for successful detection.*/
		@DialogLabel("Min. inlier count") 
		public int minInlierCount = 100;
		
		/** Set true to remove inlier points after each detection.*/
		@DialogLabel("Remove inliers") 
		public boolean removeInliers = true;
		
		/** Random seed used initialization (0 = no seed).*/
		@DialogLabel("Random seed (0 = no seed)") 
		public long randomSeed = 0;
	}
	
	// -----------------------------------------------------------
	
	private final RansacParameters params;
	private final int K;						// number of points to draw
	private final Random rand;					// random number generator
	private final RandomDraw<Pnt2d> randomDraw;	// 
	
	protected RansacCurveDetector(int K, RansacParameters params) {
		this.K = K;
		this.params = params;
		this.rand = (params.randomSeed == 0) ? new Random() : new Random(params.randomSeed);
		this.randomDraw = new RandomDraw<>(rand);
	}
	
	// -----------------------------------------------------------
	
//	/**
//	 * Returns this detector's random generator. This can be used, e.g.,
//	 * to set its seed (by {@link Random#setSeed(long)}).
//	 * 
//	 * @return the random generator
//	 */
//	public Random getRandom() {
//		return this.rand;
//	}
	
	// ----------------------------------------------------------
	
	/**
	 * Performs iterative RANSAC steps on the supplied image,
	 * which is assumed to be binary (all nonzero pixels are considered
	 * input points).
	 * Extracts the point set from the image and calls {@link #detectAll(Pnt2d[], int)}.
	 * 
	 * @param bp a binary image (nonzero pixels are considered points)
	 * @param maxCount the maximum number of primitives to detect
	 * @return the list of detected primitives
	 */
	public List<RansacCurveResult<T>> detectAll(ByteProcessor bp, int maxCount) {
		Pnt2d[] points = IjUtils.collectNonzeroPoints(bp);
		if (points.length == 0) {
			throw new IllegalArgumentException("empty point set");
		}
		return detectAll(points, maxCount);
	}
	
	/**
	 * Performs iterative RANSAC steps on the supplied point set until
	 * either no more primitive was detected or the maximum number
	 * of primitives was reached.
	 * Iteratively calls {@link #detectNext(Pnt2d[])} on the specified
	 * point set.
	 * 
	 * @param points the original point set
	 * @param maxCount the maximum number of primitives to detect
	 * @return the list of detected primitives
	 */
	public List<RansacCurveResult<T>> detectAll(Pnt2d[] points, int maxCount) {
		List<RansacCurveResult<T>> primitives = new ArrayList<>();
		int cnt = 0;
		
		RansacCurveResult<T> sol = detectNext(points);
		while (sol != null && cnt < maxCount) {
			primitives.add(sol);
			cnt = cnt + 1;
			sol = detectNext(points);
		}
		return primitives;
	}
	
	/**
	 * Performs a single RANSAC step on the supplied point set. 
	 * If {@link RansacParameters#removeInliers} is set true, 
	 * all associated inlier points are removed from the point set (by setting
	 * array elements to {@code null}).
	 * 
	 * @param points an array of {@link Pnt2d} instances (modified)
	 * @return the detected primitive (of generic type T) or {@code null} if unsuccessful
	 */
	public RansacCurveResult<T> detectNext(Pnt2d[] points) {
		Pnt2d[] drawInit = null;
		double scoreInit = -1;
		T primitiveInit = null;
		
		for (int i = 0; i < params.randomPointDraws; i++) {
			Pnt2d[] draw = drawRandomPoints(points);
			T primitive = fitInitial(draw);
			if (primitive == null) {
				continue;
			}
			double score = countInliers(primitive, points);
			if (score >= params.minInlierCount && score > scoreInit) {
				scoreInit = score;
				drawInit = draw;
				primitiveInit = primitive;
			}
		}
		
		if (primitiveInit == null) {
			return null;
		}
		else {
			// refit the primitive to all inliers:
			Pnt2d[] inliers = collectInliers(primitiveInit, points);
			T primitiveFinal = fitFinal(inliers);	
			if (primitiveFinal != null)
				return new RansacCurveResult<T>(drawInit, primitiveInit, primitiveFinal, scoreInit, inliers);
			else
				throw new RuntimeException("final fit failed!");
		}
	}
	
	/**
	 * Randomly selects {@link #K} unique points from the supplied {@link Pnt2d} array.
	 * Inheriting classes may override this method to enforce specific constraints 
	 * on the selected points (e.g., see {@link RansacLineDetector}).
	 * 
	 * @param points an array of {@link Pnt2d} instances
	 * @return an array of {@link #K} unique points
	 */
	protected Pnt2d[] drawRandomPoints(Pnt2d[] points) {	
		return randomDraw.drawFrom(points, K);
	}
	
	protected int countInliers(T curve, Pnt2d[] points) {
		int count = 0;
		for (Pnt2d p : points) {
			if (p != null) {
				double d = curve.getDistance(p);
				if (d < params.maxInlierDistance) {
					count++;
				}
			}
		}
		return count;
	}
	
	/**
	 * Find all points that are considered inliers with respect to the
	 * specified curve and the value of {@link RansacParameters#maxInlierDistance}.
	 * If {@link RansacParameters#removeInliers} is set true, these
	 * points are also removed from the original point set, otherwise
	 * they remain.
	 * 
	 * @param curve
	 * @param points
	 * @return
	 */
	protected Pnt2d[] collectInliers(Curve2d curve, Pnt2d[] points) {
		List<Pnt2d> pList = new ArrayList<>();
		for (int i = 0; i < points.length; i++) {
			Pnt2d p = points[i];
			if (p != null) {
				double d = curve.getDistance(p);
				if (d < params.maxInlierDistance) {
					pList.add(p);
					if (params.removeInliers) {
						points[i] = null;
					}
				}
			}
		}
		return pList.toArray(new Pnt2d[0]);
	}
	
	// abstract methods to be implemented by specific sub-classes: -----------------------
	
	/**
	 * Fits an initial primitive to the specified points.
	 * This abstract method must be implemented by inheriting classes,
	 * which must also specify the required number of initial points ({@link #K}).
	 * 
	 * @param draw an array of exactly {@link #K} points
	 * @return a new primitive of type T
	 */
	protected abstract T fitInitial(Pnt2d[] draw);
	
	/**
	 * Fits a primitive to the specified points.
	 * This abstract method must be implemented by inheriting classes.
	 * 
	 * @param inliers an array of at least {@link #K} points
	 * @return a new primitive of type T.
	 */
	protected abstract T fitFinal(Pnt2d[] inliers);
	
}
