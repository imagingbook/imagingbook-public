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
