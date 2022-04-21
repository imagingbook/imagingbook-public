package imagingbook.common.math.testing;

import java.util.Random;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import imagingbook.common.math.MahalanobisDistance;
import imagingbook.common.math.Matrix;
import imagingbook.common.math.Statistics;

@Deprecated 			// move to tests!
public class MahalanobisTest2 {
		
	static final int N = 1000;
	static final double[] Sigma = {2, 5, 3};
	static final double[] Mu    = {0, -1, 1};
	static final int K = Sigma.length;

	public static void main(String[] args) {
		
		double[][] samples = makeSamples();
		MahalanobisDistance mhd = new MahalanobisDistance(samples);
	
		double[] mu = mhd.getMeanVector();
		System.out.println("mu = (sample mu)\n" + Matrix.toString(mu));
		System.out.println();

		// covariance matrix cov (3x3)
		double[][] cov = mhd.getCovarianceMatrix();
		System.out.println("cov = (sample covariance matrix)\n" + Matrix.toString(cov));
		System.out.println();
		
		double[][] icov =  mhd.getInverseCovarianceMatrix();
		System.out.println("icov = (inverse covariance matrix)\n" + Matrix.toString(icov)); 
		System.out.println();
		
		RealMatrix U = MatrixUtils.createRealMatrix(mhd.getWhiteningTransformation());
		System.out.println("U = (whitening transformation)\n" + Matrix.toString(U.getData()));
		System.out.println();
		
		RealMatrix UTU = U.transpose().multiply(U);
		System.out.println("UT*U = (must be same as icov)\n" + Matrix.toString(UTU.getData()));
		System.out.println();
		
		double[][] whitened = whiten(samples, U);
		double[][] covW = Statistics.covarianceMatrix(whitened, false);
		System.out.println("covW = (ovariance matrix of whitened samples must be close to identity)\n" + Matrix.toString(covW));

	}
	
	static double[][] makeSamples() {
		Random rd = new Random(77);
		double[][] s = new double[N][K];
		for (int i = 0; i < N; i++) {
			for (int k = 0; k < K; k++) {
				s[i][k] = Sigma[k] * rd.nextGaussian() + Mu[k];
			}
		}
		return s;
	}
	
	static double[][] whiten(double[][] samples, RealMatrix U) {
		double[][] whitened = new double[samples.length][];
		for (int i = 0; i < samples.length; i++) {
			whitened[i] = U.operate(samples[i]);
		}
		return whitened;
	}
	
}

