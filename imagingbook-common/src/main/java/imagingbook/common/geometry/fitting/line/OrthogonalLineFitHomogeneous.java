package imagingbook.common.geometry.fitting.line;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.QRDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularValueDecomposition;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.PntUtils;
import imagingbook.common.geometry.line.AlgebraicLine;
import imagingbook.common.math.Matrix;

/**
 * Line fit by solving a homogeneous system, as described in
 * Gander, Gander, Kwok: "Scientific Computing", Springer 2014 (Sec. 6.7.1).
 * @author WB
 *
 */
public class OrthogonalLineFitHomogeneous implements LineFit {
	
	public static boolean VERBOSE = false;
	
	private final Pnt2d[] points;
	private final double[] p;	// line parameters A,B,C
	
	public OrthogonalLineFitHomogeneous(Pnt2d[] points) {
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
		final int n = points.length; //.max(3, pts.length);
		final double[][] X = new double[n][3];
		
		for (int i = 0; i < points.length; i++) {
			X[i][0] = 1;
			X[i][1] = points[i].getX();
			X[i][2] = points[i].getY();
		}
		
//		System.out.println("X = \n" + Matrix.toString(X));
		
		QRDecomposition qr = new QRDecomposition(MatrixUtils.createRealMatrix(X));
//		RealMatrix Q = qr.getQ();
		RealMatrix R = qr.getR();
//		System.out.println("Q = \n" + Matrix.toString(Q));
//		System.out.println("R = \n" + Matrix.toString(R));
		
//		RealMatrix RR = R.getSubMatrix(1, 2, 1, 2);
		RealMatrix RR = new Array2DRowRealMatrix(2,2);
		RR.setEntry(0, 0, R.getEntry(1, 1));
		RR.setEntry(0, 1, R.getEntry(1, 2));
		if (R.getRowDimension() >= 3) {
			RR.setEntry(1, 1, R.getEntry(2, 2));
		}
		
//		System.out.println("RR = \n" + Matrix.toString(RR));
	
		SingularValueDecomposition svd = new SingularValueDecomposition(RR);
		
		RealMatrix V = svd.getV();
//		System.out.println("V = \n" + Matrix.toString(V));
		double[] s = svd.getSingularValues();
		int k = Matrix.idxMin(s);
		double[] e = V.getColumn(k);
		
		double R01 = R.getEntry(0, 1);
		double R02 = R.getEntry(0, 2);
		double R00 = R.getEntry(0, 0);

		double A = e[0];
		double B = e[1];
		double C = ( -A * R01 - B * R02) / R00;
		return new double[] {A,B,C};
	}
	

	// -------------------------------------------------------------------
	
//	static double[][] X = {{ 10, 6 }, { 4, 3 }, { 18, 2 }, { 7, 1 }, { 5, 6 }};
//	static double[][] X = {{ 10, 6 }, { 4, 3 }};
//	static double[][] X = {{ 1, 1 }, { 3, 3 },  { 13, 13 }};
	static double[][] X = {{1, 8}, {4, 5}, {4, 7}, {6, 4}, {9, 4}}; // book example
	
	public static void main(String[] args) {
		Pnt2d[] pts = PntUtils.fromDoubleArray(X);
		OrthogonalLineFitHomogeneous fit = new OrthogonalLineFitHomogeneous(pts);
		System.out.println(fit.getClass());
		AlgebraicLine line = fit.getLine();
		System.out.println("line = " + line);
		System.out.println("avg error = " + fit.getOrthogonalError(pts) / X.length);
	}
	
//	line = AlgebraicLine <a=-0.497, b=-0.868, c=7.245>
//	avg error = 0.532916687009732
	
}
