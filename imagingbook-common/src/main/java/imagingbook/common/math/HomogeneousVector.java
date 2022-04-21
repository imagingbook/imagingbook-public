/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.math;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

/**
 * This class represents a homogeneous coordinate vector.
 * Obsolete, not used anywhere.
 * @author W. Burger
 * @deprecated
 */
public class HomogeneousVector extends ArrayRealVector {
	private static final long serialVersionUID = 1;
	
	public static HomogeneousVector Homogen(RealVector c) {
		return new HomogeneousVector(c);
	}
	
	public static RealVector Cartesian(HomogeneousVector h) {
		return h.toCartesian();
	}
	
	/**
	 * Creates a new homogeneous vector from Cartesian
	 * coordinates.
	 * @param c Cartesian coordinates.
	 */
	public HomogeneousVector(double[] c) {
		super(c, new double[] {1});
	}
	
	/**
	 * Creates a new homogeneous vector from Cartesian
	 * coordinates.
	 * @param c Cartesian coordinates.
	 */
	public HomogeneousVector(RealVector c) {
		this(c.toArray());
	}
	
	/**
	 * Converts this homogeneous vector back to
	 * Cartesian coordinates.
	 * 
	 * @return the Cartesian vector
	 */
	public RealVector toCartesian() {
		final int n = getDimension();
		RealVector cv = getSubVector(0, n - 1);
		cv.mapDivideToSelf(this.getEntry(n - 1));
		return cv;
	}

//	public static void main(String[] args) {
//		HomogeneousVector hv = new HomogeneousVector(new double[] {1,2,3});
//		System.out.println("hv = " + hv.toString());
//		
//		hv.mapMultiplyToSelf(10);
//		System.out.println("hv * 10 = " + hv.toString());
//		
//		RealVector cv = hv.toCartesian();
//		System.out.println("cv = " + cv.toString());
//	}
	
	
}
