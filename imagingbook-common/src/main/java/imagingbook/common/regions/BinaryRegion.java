package imagingbook.common.regions;

import java.awt.Rectangle;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.Pnt2d.PntDouble;
import imagingbook.common.geometry.ellipse.GeometricEllipse;
import imagingbook.common.math.eigen.Eigensolver2x2;

/**
 * <p>
 * This class represents a connected component or binary region. 
 * Instances of this class support iteration over the contained pixel
 * coordinates of type {@link Pnt2d}, e.g., by
 * </p>
 * <pre>
 * import imagingbook.pub.geometry.basic.Pnt2d;
 * 
 * BinaryRegion R = ...;
 * for (Pnt2d p : R) {
 *    // process point p ...
 * }</pre>
 * <p>
 * The advantage of providing iteration only is that it avoids the
 * creation of (possibly large) arrays of pixel coordinates.
 * </p>
 */
public abstract class BinaryRegion implements Comparable<BinaryRegion>, Iterable<Pnt2d> {

	/**
	 * Returns the sum of the x-coordinates of the points
	 * contained in this region.
	 * @return the sum of x-values.
	 */
	public abstract long getX1Sum();

	/**
	 * Returns the sum of the y-coordinates of the points
	 * contained in this region.
	 * @return the sum of y-values.
	 */
	public abstract long getY1Sum();

	/**
	 * Returns the sum of the squared x-coordinates of the points
	 * contained in this region.
	 * @return the sum of squared x-values.
	 */
	public abstract long getX2Sum();

	/**
	 * Returns the sum of the squared y-coordinates of the points
	 * contained in this region.
	 * @return the sum of squared y-values.
	 */
	public abstract long getY2Sum();

	/**
	 * Returns the sum of the mixed x*y-coordinates of the points
	 * contained in this region.
	 * @return the sum of xy-values.
	 */
	public abstract long getXYSum();
	
	/**
	 * Calculates and returns a vector of ordinary moments:
	 * (m10, m01, m20, m02, m11).
	 * @return vector of ordinary moments
	 */
	public double[] getMoments() {
		final int n = this.getSize();
		if (n == 0) {
			throw new IllegalStateException("empty region, moments are undefined");
		}
		double m10 = getX1Sum() / (double) n;
		double m01 = getY1Sum() / (double) n;
		double m20 = getX2Sum() / (double) n;
		double m02 = getY2Sum() / (double) n;
		double m11 = getXYSum() / (double) n;
		return new double[] {m10, m01, m20, m02, m11};
	}

	/**
	 * Calculates and returns a vector of central moments:
	 * (mu20, mu02, mu11).
	 * @return vector of central moments
	 */
	public double[] getCentralMoments() {
		final int n = this.getSize();
		if (n == 0) {
			throw new IllegalStateException("empty region, moments are undefined");
		}
		double mu20 = getX2Sum() - getX1Sum() * getX1Sum() / (double) n;
		double mu02 = getY2Sum() - getY1Sum() * getY1Sum() / (double) n;
		double mu11 = getXYSum() - getX1Sum() * getY1Sum() / (double) n;
		return new double[] {mu20, mu02, mu11};
	}

	/**
	 * Returns the 2x2 covariance matrix for the pixel coordinates
	 * contained in this region:
	 * <pre>
	 * | &sigma;_20 &sigma;_11 | 
	 * | &sigma;_11 &sigma;_02 | 
	 * </pre>
	 * @return the covariance matrix
	 */
	public double[][] getCovarianceMatrix() {
		final int n = this.getSize();
		double[] mu = getCentralMoments(); // = [mu20, mu02, mu11]
		double[][] S = {
				{mu[0]/n, mu[2]/n},
				{mu[2]/n, mu[1]/n}};
		return S;
	}


	/**
	 * Get the size of this region.
	 * @return the number of region points.
	 */
	public abstract int getSize();

	/**
	 * Get the x/y axes-parallel bounding box as a rectangle
	 * (including the extremal coordinates).
	 * @return the bounding box rectangle.
	 */
	public abstract Rectangle getBoundingBox();


	/**
	 * Returns the center of this region as a 2D point.
	 * @return the center point of this region.
	 */
	public Pnt2d getCenter() {
		final int n = this.getSize();
		if (n == 0) {
			throw new IllegalStateException("empty region, center is undefined");
		}
		return PntDouble.from(((double)this.getX1Sum())/n, ((double)this.getY1Sum())/n);
	}

	/**
	 * Calculates and returns this region's equivalent ellipse.
	 * @return the equivalent elipse
	 */
	public GeometricEllipse getEquivalentEllipse() {
		final double n = this.getSize();
		Pnt2d xc = this.getCenter();
		double[] moments = this.getCentralMoments(); // = (mu20, mu02, mu11)
		final double mu20 = moments[0];
		final double mu02 = moments[1];
		final double mu11 = moments[2];
		
		Eigensolver2x2 solver = new Eigensolver2x2(mu20, mu11, mu11, mu02);
		double ra = 2 * Math.sqrt(solver.getEigenvalue(0) / n);
		double rb = 2 * Math.sqrt(solver.getEigenvalue(1) / n);
		double[] e0 = solver.getEigenvector(0);
		double theta = Math.atan2(e0[1], e0[0]);
		return new GeometricEllipse(ra, rb, xc.getX(), xc.getY(), theta);
	}
	
	public abstract void setOuterContour(Contour.Outer contr);

	/**
	 * Get the (single) outer contour of this region.
	 * Points on an outer contour are arranged in clockwise
	 * order.
	 * @return the outer contour.
	 */
	public abstract Contour getOuterContour();

	/**
	 * Get all inner contours of this region.
	 * Points on inner contours are arranged in counter-clockwise order.
	 * @return the list of inner contours.
	 */
	public abstract List<Contour> getInnerContours();	// sort!!!

	// Compare method for sorting by region size (larger regions at front)
	@Override
	public int compareTo(BinaryRegion other) {
		return Integer.compare(other.getSize(), this.getSize());
	}

	/**
	 * Checks if the given pixel position is contained in this
	 * {@link BinaryRegion} instance.
	 * @param u x-coordinate
	 * @param v y-coordinate
	 * @return true if (u,v) is contained in this region
	 */
	public abstract boolean contains(int u, int v);

	// ------------------------------------------------------------
	// ------------------------------------------------------------


	/* Methods for attaching region properties dynamically.
	 * Properties can be used to hash results of region calculations
	 * to avoid multiple calculations.
	 * Currently, only 'double' values are supported.
	 * 
	 * E.g. calculate major axis angle theta for region r, then do
	 *    r.setProperty("angle", theta);
	 * and subsequently
	 *    double theta = r.getProperty("angle");
	 */

	private Map<Object, Double> properties = null;

	/**
	 * Sets the specified property of this region to the given value.
	 * @param key The key of the property.
	 * @param val The value associated with this property.
	 */
	public void setProperty(Object key, double val) {
		if (key == null) {
			throw new IllegalArgumentException("property key must not be null");
		}
		if (properties == null) {
			properties = new HashMap<>();
		}
		properties.put(key, val);
	}

	/**
	 * Retrieves the specified region property. 
	 * {@link IllegalArgumentException} is thrown if the property 
	 * is not defined for this region.
	 * 
	 * @param key The key of the property.
	 * @return The value associated with the specified property.
	 */
	public double getProperty(Object key) {
		if (key == null) {
			throw new IllegalArgumentException("property key must not be null");
		}
		Double value;
		if (properties == null || (value = properties.get(key)) == null) {
			throw new IllegalArgumentException("Region property " + key + " is undefined.");
		}
		return value.doubleValue();
	}

	/**
	 * Removes all properties attached to this region.
	 */
	public void clearProperties() {
		properties.clear();
	}
	
	@Override
	public String toString() {
		Pnt2d xc = this.getCenter();
		try (Formatter fm = new Formatter(new StringBuilder(), Locale.US)) {
			fm.format("%s:", this.getClass().getSimpleName());
			fm.format(" area = %d", this.getSize());
			fm.format(", bounding box = %s", this.getBoundingBox());
			fm.format(", center = (%.2f, %.2f)", xc.getX(), xc.getY());
	//		if (innerContours != null)
	//			fm.format(", holes = %d", innerContours.size());
			//String s = fm.toString();
			return fm.toString();
		} //fm.close();
		//return s;
	}
	

}