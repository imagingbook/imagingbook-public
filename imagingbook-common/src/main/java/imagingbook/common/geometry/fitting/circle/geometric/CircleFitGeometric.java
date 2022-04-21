package imagingbook.common.geometry.fitting.circle.geometric;

import java.util.List;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.circle.GeometricCircle;

public abstract class CircleFitGeometric {
	
	public enum FitType {
		CoordinateBased,
		DistanceBased
	}
	
	public static CircleFitGeometric getFit(FitType type, Pnt2d[] points, GeometricCircle initCircle) {
		switch (type) {
		case CoordinateBased: return new CircleFitGeometricCoord(points, initCircle);
		case DistanceBased: return new CircleFitGeometricDist(points, initCircle);
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
	 * Returns a geometric Circle
	 * @return
	 */
	public GeometricCircle getCircle() {
		return new GeometricCircle(getParameters());
	}

}
