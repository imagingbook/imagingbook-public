/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.common.histogram;

/**
 * <p>
 * This class represents a discrete "cumulative distribution function" that is piecewise linear. See Sec. 3.6.3 (Fig.
 * 3.12) of [1] for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 */
public class PiecewiseLinearCdf {
	
	private final int K;
	private final int[] iArr;
	private final double[] pArr;

	/**
	 * <p>
	 * Constructor, creates a {@link PiecewiseLinearCdf} from a sequence of brightness / cumulative probability pairs.
	 * See Sec. 3.6.3 (Fig. 3.12) of [1] for additional details. Usage example:
	 * </p>
	 * <pre>
	 * int[] ik = {28, 75, 150, 210};
	 * double[] Pk = {.05, .25, .75, .95};
	 * PiecewiseLinearCdf pLCdf = new PiecewiseLinearCdf(256, ik, Pk);</pre>
	 * <p>
	 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed,
	 * Springer (2022).
	 * </p>
	 *
	 * @param K number of brightness values (typ. 256)
	 * @param a a sequence of brightness values serving as control points
	 * @param b a sequence of cumulative probability values in [0,1], one for each control point
	 */
	public PiecewiseLinearCdf(int K, int[] a, double[] b) {
		this.K = K; // number of intensity values (typ. 256)
		int N = a.length;
		iArr = new int[N + 2];		// array of intensity values
		pArr = new double[N + 2];	// array of cum. distribution values
		iArr[0] = -1; 
		pArr[0] = 0;
		for (int i = 0; i < N; i++) {
			iArr[i + 1] = a[i];
			pArr[i + 1] = b[i];
		}
		iArr[N + 1] = K - 1;
		pArr[N + 1] = 1;
	}
	
	/**
	 * Returns the cumulative probability for the specified intensity value.
	 * 
	 * @param i the intensity value
	 * @return the associated cumulative probability
	 */
	public double getCdf(int i) {
		if (i < 0)
			return 0;
		else if (i >= K - 1)
			return 1;
		else {
			int s = 0, N = iArr.length - 1;
			for (int j = 0; j <= N; j++) { // find s (segment index)
				if (iArr[j] <= i)
					s = j;
				else
					break;
			}
			return pArr[s] + (i - iArr[s])
					* ((pArr[s + 1] - pArr[s]) / (iArr[s + 1] - iArr[s]));
		}
	}

	/**
	 * Returns the cumulative probabilities for the intensity values 0 to 255 as a {@code double[]}.
	 *
	 * @return the array of cumulative probabilities
	 */
	public double[] getCdf() {
		double[] P = new double[256];
		for (int i = 0; i < 256; i++) {
			P[i] = this.getCdf(i);
		}
		return P;
	}

	/**
	 * Returns the inverse cumulative probability function a = P<sup>-1</sup>(a), that is, the intensity value a
	 * associated with a given cum. probability P.
	 *
	 * @param P a cumulative probability
	 * @return the associated intensity
	 */
	public double getInverseCdf(double P) {
		if (P < getCdf(0))
			return 0;
		else if (P >= 1)
			return K - 1;
		else {
			int r = 0, N = iArr.length - 1;
			for (int j = 0; j <= N; j++) { // find r (segment index)
				if (pArr[j] <= P)
					r = j;
				else
					break;
			}
			return iArr[r] + (P - pArr[r]) * ((iArr[r + 1] - iArr[r]) / (pArr[r + 1] - pArr[r]));
		}
	}

	/**
	 * Returns the probability function for this distribution as a discrete array of probabilities.
	 *
	 * @return the probability array
	 */
	public double[] getPdf() {	
		double[] prob = new double[K];
		prob[0] =  getCdf(0);
		for (int i = 1; i < K; i++) {
			prob[i] =  getCdf(i) - getCdf(i-1);
		}
		return prob;
	}
	
}
