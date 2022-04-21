package imagingbook.common.ransac;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import imagingbook.common.geometry.basic.Curve2d;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.util.ParameterBundle;

/**
 * Generic RANSAC detector. This abstract class defines the core RANSAC
 * functionality used by all derived (concrete) classes.
 * TODO: Consider moving {@link RandomDraw} here.
 * 
 * @author WB
 * 
 * @see RansacDetectorLine
 * @see RansacDetectorCircle
 * @see RansacDetectorEllipse
 *
 * @param <T> primitive type
 */
public abstract class GenericRansacDetector<T extends Curve2d> {
	
	/**
	 * Parameters used by all RANSAC types.
	 * @author WB
	 *
	 */
	public static class RansacParameters implements ParameterBundle {
		@DialogLabel("Max. iterations")
		public int maxIterations = 1000;
		
		@DialogLabel("Distance threshold")
		public double distanceThreshold = 2.0;
		
		@DialogLabel("Min. support count")
		public int minSupportCount = 100;
	}
	
	// -----------------------------------------------------------
	
	private final RansacParameters params;
	private final int K;						// number of points to draw
	private final Random rand;					// random number generator
	private final RandomDraw<Pnt2d> randomDraw;	// 
	
	protected GenericRansacDetector(int K, RansacParameters params) {
		this.K = K;
		this.params = params;
		this.rand = new Random();
		this.randomDraw = new RandomDraw<>(rand);
	}
	
	// -----------------------------------------------------------
	
	/**
	 * Returns this detector's random generator. This can be used, e.g.,
	 * to set its seed (by {@link Random#setSeed(long)}).
	 * 
	 * @return the random generator
	 */
	public Random getRandom() {
		return this.rand;
	}
	
	// ----------------------------------------------------------
	
	protected int countInliers(T curve, Pnt2d[] points) {
		int count = 0;
		for (Pnt2d p : points) {
			if (p != null) {
				double d = curve.getDistance(p);
				if (d < params.distanceThreshold) {
					count++;
				}
			}
		}
		return count;
	}
	
	protected Pnt2d[] collectInliers(Curve2d curve, Pnt2d[] points, boolean removeInliers) {
		List<Pnt2d> pList = new ArrayList<>();
		for (int i = 0; i < points.length; i++) {
			Pnt2d p = points[i];
			if (p != null) {
				double d = curve.getDistance(p);
				if (d < params.distanceThreshold) {
					pList.add(p);
					if (removeInliers) {
						points[i] = null;
					}
				}
			}
		}
		return pList.toArray(new Pnt2d[0]);
	}
	
	/**
	 * Performs a single RANSAC step on the supplied point set and removes
	 * all associated inlier points.
	 * 
	 * @param points an array of {@link Pnt2d} instances (modified)
	 * @return the detected primitive (of generic type T) or {@code null} if unsuccessful
	 * 
	 * @see #findNext(Pnt2d[], boolean)
	 */
	public RansacResult<T> findNext(Pnt2d[] points) {
		return findNext(points, true);
	}
	
	/**
	 * Performs a single RANSAC step on the supplied point set. Optionally,
	 * all associated inlier points are removed from the point set by setting
	 * array elements to {@code null}.
	 * 
	 * 
	 * @param points an array of {@link Pnt2d} instances (modified)
	 * @param removeInliers set true to remove inliers are from the point set
	 * @return the detected primitive (of generic type T) or {@code null} if unsuccessful
	 */
	public RansacResult<T> findNext(Pnt2d[] points, boolean removeInliers) {
		Pnt2d[] drawInit = null;
		double scoreInit = -1;
		T primitiveInit = null;
		
		for (int i = 0; i < params.maxIterations; i++) {
			Pnt2d[] draw = drawRandomPoints(points);
			T primitive = fitInitial(draw);
			if (primitive == null) {
				continue;
			}
			double score = countInliers(primitive, points);
			if (score >= params.minSupportCount && score > scoreInit) {
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
			Pnt2d[] inliers = collectInliers(primitiveInit, points, removeInliers);
			T primitiveFinal = fitFinal(inliers);	
			if (primitiveFinal != null)
				return new RansacResult<T>(drawInit, primitiveInit, primitiveFinal, scoreInit, inliers);
			else
				throw new RuntimeException("final fit failed!");
		}
	}
	
	/**
	 * Randomly selects {@link #K} unique points from the supplied {@link Pnt2d} array.
	 * Inheriting classes may override this method to enforce specific constraints 
	 * on the selected points (e.g., see {@link RansacDetectorLine}).
	 * 
	 * @param points an array of {@link Pnt2d} instances
	 * @return an array of {@link #K} unique points
	 */
	protected Pnt2d[] drawRandomPoints(Pnt2d[] points) {	
		return randomDraw.drawFrom(points, K);
	}
	
	// abstract methods to be implemented by specific sub-classes: -----------------------
	
	/**
	 * Fits an initial primitive to the {@link #K} specified points.
	 * This abstract method must be implemented by inheriting classes.
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
	 * @return a new primitive of type T
	 */
	protected abstract T fitFinal(Pnt2d[] inliers);
	
}
