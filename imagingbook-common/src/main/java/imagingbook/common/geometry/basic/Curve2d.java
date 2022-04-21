package imagingbook.common.geometry.basic;

/**
 * Interface specifying a 2D curve capable of calculating the minimum
 * distance to a given point.
 *  
 * @author WB
 *
 */
public interface Curve2d {
	
	/**
	 * Returns the minimum distance from the specified point to this curve.
	 * 
	 * @param p a 2D point
	 * @return the minimum distance
	 */
	public double getDistance(Pnt2d p);

}
