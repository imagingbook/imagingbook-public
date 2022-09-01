/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.geometry.fitting.ellipse.geometric;

import java.util.List;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.ellipse.GeometricEllipse;

public abstract class EllipseFitGeometric {
	
	public enum FitType {
		CoordinateBased,
		DistanceBased
	}
	
	public static EllipseFitGeometric getFit(FitType type, Pnt2d[] points, GeometricEllipse initEllipse) {
		switch (type) {
		case CoordinateBased: return new EllipseGeometricFitCoord(points, initEllipse);
		case DistanceBased: return new EllipseGeometricFitDist(points, initEllipse);
		}
		throw new RuntimeException("unknown geometric fit type: " + type);
	}
	
	public static boolean VERBOSE = false;
	public static boolean RecordHistory = false;	
	public static int DefaultMaxIterations = 1000;
	public static double DefaultTolerance = 1e-6;
	
	public abstract double[] getParameters();	
	public abstract int getIterations();
	public abstract List<double[]> getHistory();
	
	
	/**
	 * Returns a geometric Ellipse or {@code null} if the fit was unsuccessful.
	 * @return the geometric ellipse or {@code null}
	 */
	public GeometricEllipse getEllipse() {
		double[] p = getParameters();
		return (p != null) ? new GeometricEllipse(p) : null;
	}

}
