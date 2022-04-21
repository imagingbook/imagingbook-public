/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.spectral.fd;

import java.util.Arrays;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.PntUtils;
import imagingbook.common.math.Arithmetic;
import imagingbook.common.math.Complex;


/**
 * Subclass of FourierDescriptor whose constructors assume
 * that input polygons are uniformly sampled.
 * 
 * @author W. Burger
 * @version 2020/04/01
 */
public class FourierDescriptorUniform extends FourierDescriptor {
	
	/**
	 * Creates a new Fourier descriptor from a uniformly sampled polygon V
	 * with the maximum number of Fourier coefficient pairs.
	 * 
	 * @param V polygon
	 */
	public FourierDescriptorUniform(Pnt2d[] V) {
		g = makeComplex(V);
		G = DFT(g);
	}
	
	
	/**
	 * Creates a new Fourier descriptor from a uniformly sampled polygon V
	 * with Mp coefficient pairs.
	 * 
	 * @param V polygon
	 * @param Mp number of coefficient pairs
	 */
	public FourierDescriptorUniform(Pnt2d[] V, int Mp) {
		g = makeComplex(V);
		G = DFT(g, 2 * Mp + 1);
	}
	
	// -------------------------------------------------------------------
	

	/**
	 * DFT with the resulting spectrum (signal, if inverse) of the same length
	 * as the input vector g. Not using sin/cos tables.
	 * 
	 * @param g signal vector
	 * @return DFT spectrum
	 */
	private Complex[] DFT(Complex[] g) {
		int M = g.length;
//		double[] cosTable = makeCosTable(M);	// cosTable[m] == cos(2*pi*m/M)
//		double[] sinTable = makeSinTable(M);
		Complex[] G = new Complex[M];
		double s = 1.0 / M; //common scale factor (fwd/inverse differ!)
		for (int m = 0; m < M; m++) {
			double Am = 0;
			double Bm = 0;
			for (int k = 0; k < M; k++) {
				double x = g[k].re;
				double y = g[k].im;
				double phi = 2 * Math.PI * m * k / M;
				double cosPhi = Math.cos(phi);
				double sinPhi = Math.sin(phi);
				Am = Am + x * cosPhi + y * sinPhi;
				Bm = Bm - x * sinPhi + y * cosPhi;
			}
			G[m] = new Complex(s * Am, s * Bm);
		}
		return G;
	}
	

	/**
	 * As above, but the length P of the resulting spectrum (signal, if inverse) 
	 * is explicitly specified.
	 * @param g signal vector
	 * @param MM length of the resulting  DFT spectrum
	 * @return DFT spectrum
	 */
	private Complex[] DFT(Complex[] g, int MM) {
		int M = g.length;
		if (MM > M) {
			throw new IllegalArgumentException("truncated spectrum must be shorter than original MM=" + MM);
		}
//		double[] cosTable = makeCosTable(M);	// cosTable[m] == cos(2*pi*m/M)
//		double[] sinTable = makeSinTable(M);
		Complex[] G = new Complex[MM];
		double s = 1.0/M; //common scale factor (fwd/inverse differ!)
		
//		for (int j = Mp/2-Mp+1; j <= Mp/2; j++) {
		for (int j = -MM/2; j <= (MM-1)/2; j++) {
			double Am = 0;
			double Bm = 0;
			for (int k = 0; k < M; k++) {
				double x = g[k].re;
				double y = g[k].im;
				//int mk = (m * k) % M; double phi = 2 * Math.PI * mk / M;
				double phi = 2 * Math.PI * j * k / M;	
				double cosPhi = Math.cos(phi);
				double sinPhi = Math.sin(phi);
				Am = Am + x * cosPhi + y * sinPhi;
				Bm = Bm - x * sinPhi + y * cosPhi;
			}
			G[Arithmetic.mod(j, MM)] = new Complex(s * Am, s * Bm);
		}
		return G;
	}

//	private double[] makeCosTable(int M) {
//		double[] cosTab = new double[M];
//		for (int m = 0; m < M; m++) {
//			cosTab[m] = Math.cos(2 * Math.PI * m / M);
//		}
//		return cosTab;
//	}

//	private double[] makeSinTable(int M) {
//		double[] sinTab = new double[M];
//		for (int m = 0; m < M; m++) {
//			sinTab[m] = Math.sin(2 * Math.PI * m / M);
//		}
//		return sinTab;
//	}
	
	// ------------------------------------------------------------------------------------
	
	public static void main(String[] args) {
		double[][] points = {{3,2}, {5,4}, {7,10}, {6,11}, {4, 7}};

		Pnt2d[] V = PntUtils.fromDoubleArray(points);
		
		FourierDescriptorUniform fd1 = new FourierDescriptorUniform(V); 
		System.out.println(Arrays.toString(fd1.getCoefficients()));
		
		FourierDescriptorUniform fd2 = new FourierDescriptorUniform(V, 2); 
		System.out.println(Arrays.toString(fd2.getCoefficients()));
		
		
	}
	

}
