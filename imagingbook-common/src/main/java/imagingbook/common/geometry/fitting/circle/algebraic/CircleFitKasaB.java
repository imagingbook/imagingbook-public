package imagingbook.common.geometry.fitting.circle.algebraic;

import static imagingbook.common.math.Arithmetic.sqr;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularMatrixException;
import org.apache.commons.math3.linear.SingularValueDecomposition;

import imagingbook.common.geometry.basic.Pnt2d;

/**
 * 
 * WB: numerically potentially more stable solution using Moore-Penrose pseudoinverse (see Chernov book p. 104, eq. 5.12)
 * Original: Kasa, I., A curve fitting procedure and its error analysis, IEEE Trans. Inst. Meas., 25, 8-14, 1976.
 * See https://people.cas.uab.edu/~mosya/cl/CircleFitByKasa.cpp (orig code)
 * http://www.ne.jp/asahi/paleomagnetism.rock-magnetism/basics/pmag/circ/circ1E.html
 * http://www.ne.jp/asahi/paleomagnetism.rock-magnetism/basics/pmag/circ/circ2E.html
 * 
 * @author WB
 *
 */
public class CircleFitKasaB extends CircleFitAlgebraic {

	private final double[] q;	// p = (B,C,D) circle parameters
	
	public CircleFitKasaB(Pnt2d[] points) {
		q = fit(points);
	}
	
	@Override
	public double[] getParameters() {
		return new double[] {1, q[0], q[1], q[2]};
	}
	
	
	
	/**
	 * Ported from Doube: FitCircle.java (adapted to use Apache Commons Math)
	 *
	 * @author Michael Doube, ported from Nikolai Chernov's MATLAB scripts
	 * @see
	 *      <p>
	 *      Al-Sharadqha & Chernov (2009)
	 *      <a href="http://dx.doi.org/10.1214/09-EJS419"> Error analysis for circle
	 *      fitting algorithms</a>. Electronic Journal of Statistics 3, pp. 886-911
	 *      <br/>
	 *      <br />
	 *      <a href="http://www.math.uab.edu/~chernov/cl/MATLABcircle.html" >http://
	 *      www.math.uab.edu/~chernov/cl/MATLABcircle.html</a>
	 *      </p>
	 *
	 */
	private double[] fit(Pnt2d[] pts) {
		final int n = pts.length;
		if (n < 3) {
			throw new IllegalArgumentException("at least 3 points are required");
		}
		
		final double[] z = new double[n];
		final double[][] Xa = new double[n][];
		for (int i = 0; i < n; i++) {
			final double x = pts[i].getX();
			final double y = pts[i].getY();
			Xa[i] = new double[] {x, y, 1};
			z[i] = -(sqr(x) + sqr(y));
		}

		RealMatrix X = MatrixUtils.createRealMatrix(Xa);
		
		RealMatrix Xi = null;
		try {
			SingularValueDecomposition svd = new SingularValueDecomposition(X);
			Xi = svd.getSolver().getInverse();		// get (3,N) pseudoinverse of X
//			IJ.log("solver = " + svd.getSolver());
//			IJ.log("rank X = " + svd.getRank());
		} catch (SingularMatrixException e) { }
		
		if (Xi == null) {
			return null;
		}
		else {
			double[] q = Xi.operate(z);	// solution vector q = X^-1 * z = (B, C, D)
			return q;
		}
	}

}
