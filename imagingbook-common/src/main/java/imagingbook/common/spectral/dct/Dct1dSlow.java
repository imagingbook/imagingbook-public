/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.spectral.dct;

import imagingbook.common.math.Matrix;

/**
 * This class calculates the 1D DFT.
 * Direct (slow) version for that does NOT use pre-calculated cosine tables.
 * 
 * @author W. Burger
 * @version 2019-12-26
 *
 */
public class Dct1dSlow implements Dct1d.Double {

	final double CM0 = 1.0 / Math.sqrt(2);
	final int M;
	final private double[] tmp;

	public Dct1dSlow(int M) {
		this.M = M;
		this.tmp = new double[M];
	}

	@Override
	public void forward(double[] g) {
		if (g.length != M)
			throw new IllegalArgumentException();
		final double s = Math.sqrt(2.0 / M);
		double[] G = tmp;
		for (int m = 0; m < M; m++) {
			double cm = (m == 0) ? CM0 : 1.0;
			double sum = 0;
			for (int u = 0; u < M; u++) {
				double Phi = Math.PI * m * (2 * u + 1) / (2 * M);
				sum += g[u] * cm * Math.cos(Phi);
			}
			G[m] = s * sum;
		}
		System.arraycopy(G, 0, g, 0, M); // copy G -> g
	}

	@Override
	public void inverse(double[] G) {
		if (G.length != M)
			throw new IllegalArgumentException();
		final double s = Math.sqrt(2.0 / M); //common scale factor
		double[] g = tmp;
		for (int u = 0; u < M; u++) {
			double sum = 0;
			for (int m = 0; m < M; m++) {
				double cm = (m == 0) ? CM0 : 1.0;
				double Phi = Math.PI * m * (2 * u + 1) / (2 * M);
				sum += G[m] * cm * Math.cos(Phi);
			}
			g[u] = s * sum;
		}
		System.arraycopy(g, 0, G, 0, M); // copy g -> G
	}

	// ------------------------------------------------------------------------------------------------

	// test example
	public static void main(String[] args) {

		double[] data = {1,2,3,4,5,3,0};
		System.out.println("Original data:      " + Matrix.toString(data));

		Dct1d.Double dct = new Dct1dSlow(data.length);
		dct.forward(data);
		System.out.println("DCT spectrum:       " + Matrix.toString(data));

		dct.inverse(data);
		System.out.println("Reconstructed data: " + Matrix.toString(data));
	}

	//	Original data:      {1.000, 2.000, 3.000, 4.000, 5.000, 3.000, 0.000}
	//	DCT of data:        {6.803, -0.361, -3.728, 1.692, -0.888, -0.083, 0.167}
	//	Reconstructed data: {1.000, 2.000, 3.000, 4.000, 5.000, 3.000, -0.000}

}
