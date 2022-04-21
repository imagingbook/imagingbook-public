package imagingbook.common.math.testing;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import imagingbook.common.math.MahalanobisDistance;
import imagingbook.common.math.Matrix;
import imagingbook.common.math.VectorNorm;

@Deprecated 			// move to tests!
public class MahalanobisTest1 {

	public static void main(String[] args) {
		/*
		 * Test example from Burger/Burge 3rd edition (Appendix D):
		 * N = 4 samples, K = 3 dimensions
		 */

		double[] X1 = {75, 37, 12};
		double[] X2 = {41, 27, 20};
		double[] X3 = {93, 81, 11};
		double[] X4 = {12, 48, 52};

		System.out.println("X1 = " + Matrix.toString(X1));
		System.out.println("X2 = " + Matrix.toString(X2));
		System.out.println("X3 = " + Matrix.toString(X3));
		System.out.println("X4 = " + Matrix.toString(X4));
		System.out.println();

		double[][] samples = {X1, X2, X3, X4};

		MahalanobisDistance mhd = new MahalanobisDistance(samples);

		double[] mu = mhd.getMeanVector();
		System.out.println("mu = " + Matrix.toString(mu));
		System.out.println();

		// covariance matrix cov (3x3)
		double[][] cov = mhd.getCovarianceMatrix();
		System.out.println("cov = \n" + Matrix.toString(cov));
		System.out.println();

		double[][] icov =  mhd.getInverseCovarianceMatrix();
		System.out.println("icov = \n" + Matrix.toString(icov)); 
		System.out.println();

		System.out.format("MH-dist(X1,X1) = %.3f\n", mhd.distance(X1, X1));
		System.out.format("MH-dist(X1,X2) = %.3f\n", mhd.distance(X1, X2));
		System.out.format("MH-dist(X2,X1) = %.3f\n", mhd.distance(X2, X1));
		System.out.format("MH-dist(X1,X3) = %.3f\n", mhd.distance(X1, X3));
		System.out.format("MH-dist(X1,X4) = %.3f\n", mhd.distance(X1, X4));
		System.out.println();

		System.out.format("MH-dist(X1,mu) = %.3f\n", mhd.distance(X1, mu));
		System.out.format("MH-dist(X1)    = %.3f\n", mhd.distance(X1));
		System.out.format("MH-dist(X2,mu) = %.3f\n", mhd.distance(X2, mu));
		System.out.format("MH-dist(X2)    = %.3f\n", mhd.distance(X2));
		System.out.format("MH-dist(X3,mu) = %.3f\n", mhd.distance(X3, mu));
		System.out.format("MH-dist(X3)    = %.3f\n", mhd.distance(X3));
		System.out.format("MH-dist(X4,mu) = %.3f\n", mhd.distance(X4, mu));
		System.out.format("MH-dist(X4)    = %.3f\n", mhd.distance(X4));
		System.out.println();

		VectorNorm L2 = new VectorNorm.L2();
		System.out.format("L2-distance(X1,X2) = %.3f\n", L2.distance(X1, X2));
		System.out.format("L2-distance(X2,X1) = %.3f\n", L2.distance(X2, X1));

		// ------------------------------------------------------

		System.out.println();
		System.out.println("Testing pre-transformed Mahalanobis distances:");
		RealMatrix U = MatrixUtils.createRealMatrix(mhd.getWhiteningTransformation());
		System.out.println("whitening transformation U = \n" + Matrix.toString(U.getData()));

		double[] Y1 = U.operate(X1);
		double[] Y2 = U.operate(X2);
		double[] Y3 = U.operate(X3);
		double[] Y4 = U.operate(X4);
		double[] muT = U.operate(mu);

		System.out.println("Y1 = " + Matrix.toString(Y1));
		System.out.println("Y2 = " + Matrix.toString(Y2));
		System.out.println("Y3 = " + Matrix.toString(Y3));
		System.out.println("Y4 = " + Matrix.toString(Y4));

		System.out.println();
		System.out.format("whitened L2-distance(Y1,Y2) = %.3f\n", L2.distance(Y1, Y2));
		System.out.format("whitened L2-distance(Y1,Y3) = %.3f\n", L2.distance(Y1, Y3));
		System.out.format("whitened L2-distance(Y1,Y4) = %.3f\n", L2.distance(Y1, Y4));
		
		System.out.println();
		System.out.format("whitened L2-distance(Y1,muT) = %.3f\n", L2.distance(Y1, muT));
		System.out.format("whitened L2-distance(Y2,muT) = %.3f\n", L2.distance(Y2, muT));
		System.out.format("whitened L2-distance(Y3,muT) = %.3f\n", L2.distance(Y3, muT));
		System.out.format("whitened L2-distance(Y4,muT) = %.3f\n", L2.distance(Y4, muT));
	}
}

/*
X1 = {75.000, 37.000, 12.000}
X2 = {41.000, 27.000, 20.000}
X3 = {93.000, 81.000, 11.000}
X4 = {12.000, 48.000, 52.000}

mu = {55.250, 48.250, 23.750}

cov = 
{{972.188, 331.938, -470.438}, 
{331.938, 412.687, -53.188}, 
{-470.438, -53.188, 278.188}}

icov = 
{{0.032, -0.019, 0.051}, 
{-0.019, 0.014, -0.030}, 
{0.051, -0.030, 0.083}}

MH-dist(X1,X1) = 0.000
MH-dist(X1,X2) = 2.828
MH-dist(X2,X1) = 2.828
MH-dist(X1,X3) = 2.828
MH-dist(X1,X4) = 2.828

MH-dist(X1,mu) = 1.732
MH-dist(X1)    = 1.732
MH-dist(X2,mu) = 1.732
MH-dist(X2)    = 1.732
MH-dist(X3,mu) = 1.732
MH-dist(X3)    = 1.732
MH-dist(X4,mu) = 1.732
MH-dist(X4)    = 1.732

L2-distance(X1,X2) = 36.332
L2-distance(X2,X1) = 36.332

Testing pre-transformed Mahalanobis distances:
whitening transformation U = 
{{0.179, -0.108, 0.282}, 
{0.000, 0.050, 0.010}, 
{0.000, 0.000, 0.060}}
Y1 = {12.840, 1.959, 0.719}
Y2 = {10.085, 1.536, 1.199}
Y3 = {11.044, 4.142, 0.660}
Y4 = {11.664, 2.888, 3.118}

whitened L2-distance(Y1,Y2) = 2.828
whitened L2-distance(Y1,Y3) = 2.828
whitened L2-distance(Y1,Y4) = 2.828

whitened L2-distance(Y1,muT) = 1.732
whitened L2-distance(Y2,muT) = 1.732
whitened L2-distance(Y3,muT) = 1.732
whitened L2-distance(Y4,muT) = 1.732

*/
