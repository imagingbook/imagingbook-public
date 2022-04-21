package imagingbook.common.geometry.delaunay.guibas;

import imagingbook.common.geometry.basic.Pnt2d;

/**
 * Implements a 2D point or vector with {@code double} coordinates.
 * Instances of this class are immutable.
 */
public class Vector2D implements Pnt2d {

    private final double x;
    private final double y;
    
	/**
	 * Constructor from any object that implements the generic {@link Pnt2d} 
	 * interface.
	 * @param pt original point object (of unknown class)
	 */
	public Vector2D(Pnt2d pt) {
		this.x = pt.getX();
		this.y = pt.getY();
	}
	
	/**
	 * Constructor.
	 * @param x the x coordinate 
	 * @param y the y coordinate
	 */
    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

	@Override
	public double getX() {
		return x;
	}

	@Override
	public double getY() {
		return y;
	}

	/**
	 * Subtracts the given vector from this vector.
	 * 
	 * @param vector the vector to be subtracted from this vector
	 * @return a new instance holding the result of the vector subtraction
	 */
    public Vector2D sub(Vector2D vector) {
        return new Vector2D(this.x - vector.x, this.y - vector.y);
    }

	/**
	 * Adds the given vector to this vector.
	 * 
	 * @param vector the vector to be added to this vector
	 * @return a new instance holding the result of the vector addition
	 */
    public Vector2D add(Vector2D vector) {
        return new Vector2D(this.x + vector.x, this.y + vector.y);
    }

	/**
	 * Multiplies this vector by the given scalar.
	 * 
	 * @param scalar the scalar to be multiplied by this
	 * @return a new instance holding the result of the multiplication
	 */
    public Vector2D mult(double scalar) {
        return new Vector2D(this.x * scalar, this.y * scalar);
    }

	/**
	 * Computes the magnitude or length of this vector.
	 * 
	 * @return the magnitude of this vector
	 */
    public double mag() {
        return Math.sqrt(this.x * this.x + this.y * this.y);
    }

    /**
	 * Computes the dot product of this vector and the given vector.
	 * 
	 * @param vector the vector to be multiplied by this vector
	 * @return a new instance holding the result of the multiplication
	 */
    public double dot(Vector2D vector) {
        return this.x * vector.x + this.y * vector.y;
    }

	/**
	 * Computes the 2D pseudo cross product Dot(Perp(this), vector) of this vector
	 * and the given vector.
	 * 
	 * @param vector the vector to be multiplied to the perpendicular vector of this vector
	 * @return a new instance holding the result of the pseudo cross product
	 */
    public double cross(Vector2D vector) {
        return this.y * vector.x - this.x * vector.y;
    }

    @Override
    public String toString() {
        return Vector2D.class.getSimpleName() + "[" + x + ", " + y + "]";
    }

}