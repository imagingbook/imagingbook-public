/*
 *  This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge.
 * All rights reserved. Visit https://imagingbook.com for additional details.
 */
package imagingbook.common.geometry.fitting.circle.geometric;

import java.util.List;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.circle.GeometricCircle;

/**
 * Common interface for geometric circle fits.
 * @author WB
 *
 */
public interface CircleFitGeometric {
	
	public enum FitType {
		CoordinateBased,
		DistanceBased
	}
	
	public static CircleFitGeometric getFit(FitType type, Pnt2d[] points, GeometricCircle initCircle) {
		switch (type) {
		case CoordinateBased:
			return new CircleFitGeometricCoord(points, initCircle);
		case DistanceBased:
			return new CircleFitGeometricDist(points, initCircle);
		}
		//throw new IllegalArgumentException("unknown geometric fit type: " + type);
		return null;
	}
	
	public static boolean VERBOSE = false;
	public static boolean RecordHistory = false;	
	
	public abstract double[] getParameters();	
	public abstract int getIterations();
	public abstract List<double[]> getHistory();
	
	/**
	 * Returns the geometric circle produced by this fit.
	 * @return a {@link GeometricCircle} instance
	 */
	public default GeometricCircle getCircle() {
		return new GeometricCircle(getParameters());
	}

}
