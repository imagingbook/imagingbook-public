/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.corners;

import java.awt.Shape;
import java.awt.geom.Path2D;
import java.util.Arrays;
import java.util.Locale;

import imagingbook.common.geometry.basic.Pnt2d;


/**
 * This class represents a 2D corner.
 * A corner is essentially a {@link Pnt2d} plus a scalar quantity
 * {@link #q} for the corner strength.
 * 
 * @version 2020/10/02
 */
public class Corner implements Pnt2d, Comparable<Corner> {
	
	private final float x, y;
	private final float q;

	public Corner(float x, float y, float q) {
		this.x = x;
		this.y = y;
		this.q = q;
	}
	
	@Override
	public double getX() {
		return x;
	}

	@Override
	public double getY() {
		return y;
	}

	public double getQ() {
		return q;
	}

	// used for sorting corners by corner strength q
	@Override
	public int compareTo(Corner other) {
		return Float.compare(other.q, this.q);
	}
	
	// ----------------------------------------------------------------
	
	@Override
	public String toString() {
		return String.format(Locale.US, "Corner <%.3f, %.3f, %.3f>", x, y, q);
	}
	
	
	@Override
	public Shape getShape(double size) {
		Path2D path = new Path2D.Double();
		path.moveTo(x - size, y);
		path.lineTo(x + size, y);
		path.moveTo(x, y - size);
		path.lineTo(x, y + size);
		return path;
	}
	
	// ----------------------------------------------------------------

	public static void main(String[] args) {
		Corner c1 = new Corner(1,0,1);
		Corner c2 = new Corner(2,0,2);
		Corner c3 = new Corner(3,0,3);
		Corner c4 = new Corner(4,0,2);
		
		Corner[] corners = {c1, c2, c3, c4};
		System.out.println("corners orig =   " + Arrays.toString(corners));
		
		Arrays.sort(corners);
		System.out.println("corners sorted = " + Arrays.toString(corners));
	}
}

