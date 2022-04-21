package imagingbook.common.geometry.fitting.line;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularValueDecomposition;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.PntUtils;
import imagingbook.common.geometry.line.AlgebraicLine;
import imagingbook.common.math.Matrix;

public class OrthogonalLineFitSvd implements LineFit {
	
	private final Pnt2d[] points;
	private final double[] p;	// line parameters A,B,C
	
	public OrthogonalLineFitSvd(Pnt2d[] points) {
		if (points.length < 2) {
			throw new IllegalArgumentException("line fit requires at least 2 points");
		}
		this.points = points;
		this.p = fit();
	}
	
	@Override
	public int getSize() {
		return points.length;
	}
	
//	@Override
//	public Pnt2d[] getPoints() {
//		return points;
//	}

	@Override
	public double[] getLineParameters() {
		return p;
	}
	
	private double[] fit() {
		final int n = points.length;
		final double[][] X = new double[n][2];
		Pnt2d ctr = PntUtils.centroid(points);
		final double xc = ctr.getX();
		final double yc = ctr.getY();
		for (int i = 0; i < n; i++) {
			X[i][0] = points[i].getX() - xc;
			X[i][1] = points[i].getY() - yc;
		}
		
		SingularValueDecomposition svd =  
				new SingularValueDecomposition(MatrixUtils.createRealMatrix(X));
		
		RealMatrix V = svd.getV();
		double[] s = svd.getSingularValues();
		int k = Matrix.idxMin(s);
		double[] e = V.getColumn(k);

		double A = e[0];
		double B = e[1];
		double C = -A * xc - B * yc;
		return new double[] {A,B,C};
	}
	
	// -------------------------------------------------------------------
	
//	static double[][] X = {{ 10, 6 }, { 4, 3 }, { 18, 2 }, { 7, 1 }, { 5, 6 }};
//	static double[][] X = {{ 10, 6 }, { 4, 3 }};
//	static double[][] X = {{ 1, 1 }, { 3, 3 },  { 13, 13 }};
	static double[][] X = {{1, 8}, {4, 5}, {4, 7}, {6, 4}, {9, 4}}; // book example
	
	public static void main(String[] args) {
		Pnt2d[] pts = PntUtils.fromDoubleArray(X);
		LineFit fit = new OrthogonalLineFitSvd(pts);
		System.out.println(fit.getClass());
		AlgebraicLine line = fit.getLine();
		System.out.println("line = " + line);
		System.out.println("avg error = " + fit.getOrthogonalError(pts) / X.length);
	}
	
//	class Fitting_Lines.lib.OrthogonalLineFitSvd
//	line = AlgebraicLine <a=0.497, b=0.868, c=-7.245>
//	avg error = 0.5329166870097324
	
}
