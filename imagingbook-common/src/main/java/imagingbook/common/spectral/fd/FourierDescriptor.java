/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.spectral.fd;

import java.awt.geom.Path2D;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.univariate.BrentOptimizer;
import org.apache.commons.math3.optim.univariate.SearchInterval;
import org.apache.commons.math3.optim.univariate.UnivariateObjectiveFunction;
import org.apache.commons.math3.optim.univariate.UnivariateOptimizer;
import org.apache.commons.math3.optim.univariate.UnivariatePointValuePair;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.math.Arithmetic;
import imagingbook.common.math.Complex;

/**
 * This is the abstract super-class for Fourier descriptors. It cannot
 * be instantiated.
 * @author W. Burger
 * @version 2020/04/01
 */
public abstract class FourierDescriptor implements Cloneable {

	static int minReconstructionSamples = 50;

	protected Complex[] g;	// complex-valued samples (used only for display purposes)
	protected Complex[] G;	// complex-valued DFT spectrum
	protected double reconstructionScale = 1.0;		// remembers original scale after normalization

	// ----------------------------------------------------------------

	public double getReconstructionScale() {
		return reconstructionScale;
	}

	/**
	 * Truncates this Fourier descriptor to the {@code Mp} lowest-frequency coefficients.
	 * For example, the original Fourier descriptor with 10 coefficients a,b,...,j
	 * <pre>
	 * m    = 0 1 2 3 4 5 6 7 8 9
	 * G[m] = a b c d e f g h i j
	 * </pre>
	 * becomes (with {@code P} = 3)
	 * <pre>
	 * m    = 0 1 2 3 4 5 6
	 * G[m] = a b c d h i j
	 * </pre>
	 * @param Mp number of coefficients to remain
	 * @return a new (truncated) instance of {@link FourierDescriptor}
	 */
	public FourierDescriptor truncate(int Mp) {
		FourierDescriptor fd = this.clone();
		fd.truncateSelf(Mp);
		return fd;
	}

	private void truncateSelf(int Mp) {
		int M = G.length;
		if (Mp > 0 && Mp < M) {
			Complex[] Gnew = new Complex[Mp];
			for (int m = 0; m < Mp; m++) {
				if (m <= Mp / 2)
					Gnew[m] = G[m];
				else
					Gnew[m] = G[M - Mp + m];
			}
			G = Gnew;
		}
	}

	public FourierDescriptor clone() {
		FourierDescriptor fd2 = null;
		try {
			fd2 = (FourierDescriptor) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		fd2.g = duplicate(this.g);
		fd2.G = duplicate(this.G);
		return fd2;
	}

//	public int getMaxNegHarmonic() {
//		return -(G.length - 1)/2;
//	}
//
//	public int getMaxPosHarmonic() {
//		return G.length/2;
//	}

	
	public int getMaxNegHarmonic() {
		return -G.length/2;			// = -M/2
	}

	public int getMaxPosHarmonic() {
		return (G.length - 1)/2;		// (M-1)/2
	}

	
	public int getMaxCoefficientPairs() {
		return G.length / 2; // was (G.length - 1)/2;!!
	}

	// ----------------------------------------------------------------

	protected static Complex[] makeComplex(Pnt2d[] points) {
		int N = points.length;
		Complex[] samples = new Complex[N];
		for (int i = 0; i < N; i++) {
			samples[i] = new Complex(points[i].getX(), points[i].getY());
		}
		return samples;
	}
	

	public static Complex[] duplicate(Complex[] g1) {
		Complex[] g2 = new Complex[g1.length];
		for (int i = 0; i < g1.length; i++) {
			g2[i] = new Complex(g1[i].re, g1[i].im);
		}
		return g2;
	}

	// ------------------------------------------------------------------

	public Complex[] getSamples() {
		return g;
	}

	public Complex[] getCoefficients() {
		return G;
	}

	public int size() {
		return G.length;	// = M
	}

	public int getCoefficientIndex(int m) {
		return Arithmetic.mod(m, G.length);
	}

	public Complex getCoefficient(int m) {
		int mm = Arithmetic.mod(m, G.length);
		return new Complex(G[mm]);
	}

	public void setCoefficient(int m, Complex z) {
		int mm = Arithmetic.mod(m, G.length);
		G[mm] = new Complex(z);
	}

	public void setCoefficient(int m, double a, double b) {
		int mm = Arithmetic.mod(m, G.length);
		G[mm] = new Complex(a, b);
	}

	// ------------------------------------------------------------------

	/**
	 * Calculates a reconstruction from the full DFT spectrum with N samples.
	 * 
	 * @param N number of samples
	 * @return reconstructed contour points
	 */
	public Complex[] getReconstruction(int N) {
		Complex[] S = new Complex[N];
		for (int i = 0; i < N; i++) {
			double t = (double) i / N;
			S[i] = getReconstructionPoint(t);
		}
		return S;
	}


	/**
	 * Calculate a reconstruction from the partial DFT spectrum with N sample
	 * points and using Mp coefficient pairs.
	 * 
	 * @param N number of samples
	 * @param Mp number of coefficient pairs
	 * @return reconstructed contour points
	 */
	public Complex[] getReconstruction(int N, int Mp) {
		Complex[] S = new Complex[N];
		Mp = Math.min(Mp, -getMaxNegHarmonic());
		for (int i = 0; i < N; i++) {
			double t = (double) i / N;
			S[i] = getReconstructionPoint(t, -Mp, +Mp);
		}
		return S;
	}


	/**
	 * Reconstructs a single spatial point from the complete FD
	 * at the fractional path position t in [0,1].
	 * 
	 * @param t path position
	 * @return single contour point
	 */
	public Complex getReconstructionPoint(double t) {
		int mm = getMaxNegHarmonic();
		int mp = getMaxPosHarmonic();
		return getReconstructionPoint(t, mm, mp);
	}

	public Complex getReconstructionPoint(double t, int Mp) {
		return getReconstructionPoint(t, -Mp, Mp);
	}


	/**
	 * Reconstructs a single spatial point from this FD using
	 * coefficients [mm,...,mp] = [m-,...,m+] at the fractional path position t in [0,1].
	 * 
	 * @param t path position
	 * @param mm most negative frequency index
	 * @param mp most positive frequency index
	 * @return single contour point
	 */
	private Complex getReconstructionPoint(double t, int mm, int mp) {
		double x = G[0].re;
		double y = G[0].im;
		for (int m = mm; m <= mp; m++) {
			if (m != 0) {
				Complex Gm = getCoefficient(m);
				double A = reconstructionScale * Gm.re;
				double B = reconstructionScale * Gm.im;
				double phi = 2 * Math.PI * m * t;
				double sinPhi = Math.sin(phi);
				double cosPhi = Math.cos(phi);
				x = x + A * cosPhi - B * sinPhi;
				y = y + A * sinPhi + B * cosPhi;
			}
		}
		return new Complex(x, y);
	}

	// -----------------------------------------------------------------------


	public Path2D makeEllipse(Complex G1, Complex G2, int m, double xOffset, double yOffset) {
		Path2D path = new Path2D.Float();
		int recPoints = Math.max(minReconstructionSamples, G.length * 3);
		for (int i = 0; i < recPoints; i++) {
			double t = (double) i / recPoints;
			Complex p1 = this.getEllipsePoint(G1, G2, m, t);
			double xt = p1.re;
			double yt = p1.im;
			if (i == 0) {
				path.moveTo(xt + xOffset, yt + yOffset);
			}
			else {
				path.lineTo(xt + xOffset, yt + yOffset);
			}
		}
		path.closePath();
		return path;
	}


	/**
	 * Get the reconstructed point for two DFT coefficients G1, G2 at a given
	 * position t.
	 * @param G1 first coefficient
	 * @param G2 second coefficient
	 * @param m frequency number
	 * @param t contour position
	 * @return reconstructed point
	 */
	public Complex getEllipsePoint(Complex G1, Complex G2, int m, double t) {
		Complex p1 = getReconstructionPoint(G1, -m, t);
		Complex p2 = getReconstructionPoint(G2, m, t);
		return p1.add(p2);
	}


	/**
	 * Returns the spatial point reconstructed from a single
	 * DFT coefficient 'Gm' with frequency 'm' at 
	 * position 't' in [0,1].
	 * 
	 * @param Gm single DFT coefficient
	 * @param m frequency index
	 * @param t contour position
	 * @return reconstructed point
	 */
	private Complex getReconstructionPoint(Complex Gm, int m, double t) {
		double wm = 2 * Math.PI * m;
		double Am = Gm.re;
		double Bm = Gm.im;
		double cost = Math.cos(wm * t);
		double sint = Math.sin(wm * t);
		double xt = Am * cost - Bm * sint;
		double yt = Bm * cost + Am * sint;
		return new Complex(xt, yt);
	}


	/**
	 * Reconstructs the shape using all FD pairs.
	 * 
	 * @return reconstructed shape
	 */
	public Path2D makeFourierPairsReconstruction() {
		int M = G.length;
		return  makeFourierPairsReconstruction(M/2);
	}

	/**
	 * Reconstructs the shape obtained from FD-pairs 0,...,Mp as a polygon (path).
	 * 
	 * @param Mp number of Fourier coefficient pairs
	 * @return reconstructed shape
	 */
	public Path2D makeFourierPairsReconstruction(int Mp) {
		int M = G.length;
		Mp = Math.min(Mp, M/2);
		int recPoints = Math.max(minReconstructionSamples, G.length * 3);
		Path2D path = new Path2D.Float();
		for (int i = 0; i < recPoints; i++) {
			double t = (double) i / recPoints;
			Complex pt = new Complex(getCoefficient(0));	// assumes that coefficient 0 is never scaled
			// calculate a particular reconstruction point 
			for (int m = 1; m <= Mp; m++) {
				Complex ep = getEllipsePoint(getCoefficient(-m), getCoefficient(m), m, t);
				pt = pt.add(ep.multiply(reconstructionScale));
			}
			double xt = pt.re; 
			double yt = pt.im; 
			if (i == 0) {
				path.moveTo(xt, yt);
			}
			else {
				path.lineTo(xt, yt);
			}
		}
		path.closePath();
		return path;
	}


	public int getMaxDftMagnitudeIndex() {
		double maxMag = -1;
		int maxIdx = -1;
		for (int i=0; i<G.length; i++) {
			double mag = G[i].abs();
			if (mag > maxMag) {
				maxMag = mag;
				maxIdx = i;
			}
		}
		return maxIdx;
	}

	public double getMaxDftMagnitude() {
		int maxIdx = getMaxDftMagnitudeIndex();
		return G[maxIdx].abs();
	}

	// Invariance -----------------------------------------------------

	public FourierDescriptor[] makeInvariant() {
		int Mp = getMaxCoefficientPairs();
		return makeInvariant(Mp);
	}

	public FourierDescriptor[] makeInvariant(int Mp) {
		makeScaleInvariant(Mp);
		FourierDescriptor[] fdAB = makeStartPointInvariant(Mp);	// = [fdA, fdB]
		fdAB[0].makeRotationInvariant(Mp);	// works destructively!
		fdAB[1].makeRotationInvariant(Mp);
		return fdAB;
	}

	public FourierDescriptor[] makeStartPointInvariant() {
		int Mp = getMaxCoefficientPairs();
		return makeStartPointInvariant(Mp);
	}

	private FourierDescriptor[] makeStartPointInvariant(int Mp) {
		double phiA = getStartPointPhase(Mp);
		double phiB = phiA + Math.PI;
		FourierDescriptor fdA = clone();
		FourierDescriptor fdB = clone();
		fdA.shiftStartPointPhase(phiA, Mp);
		fdB.shiftStartPointPhase(phiB, Mp);
		return new FourierDescriptor[] {fdA, fdB};
	}

	// -----------------------------------------------------------------

	/**
	 * Sets the zero (DC) coefficient to zero.
	 */
	public void makeTranslationInvariant() {
		G[0] = new Complex(0,0);
	}


	/**
	 * Normalizes this descriptor destructively to the L2 norm of G, 
	 * keeps G_0 untouched.
	 * 
	 * @return the scale factor used for normalization
	 */
	public double makeScaleInvariant() {
		double s = 0;
		for (int m = 1; m < G.length; m++) {
			s = s + G[m].abs2();
		}
		// scale coefficients
		double norm = Math.sqrt(s);
		reconstructionScale = norm;		// keep for later reconstruction
		double scale = 1 / norm;
		for (int m = 1; m < G.length; m++) {
			G[m] =  G[m].multiply(scale);
		}
		return scale;
	}


	/**
	 * Normalizes the L2 norm of the sub-vector (G_{-Mp}, ..., G_{Mp}),
	 * keeps G_0 untouched.
	 * 
	 * @param Mp most positive/negative frequency index
	 * @return normalized coefficient sub-vector
	 */
	private double makeScaleInvariant(int Mp) {
		double s = 0;
		for (int m = 1; m <= Mp; m++) {
			s = s + getCoefficient(-m).abs2() + getCoefficient(m).abs2();
		}
		// scale Fourier coefficients:
		double norm = Math.sqrt(s);
		reconstructionScale = norm;		// keep for later reconstruction
		double scale = 1 / norm;
		for (int m = 1; m <= Mp; m++) {
			setCoefficient(-m, getCoefficient(-m).multiply(scale));
			setCoefficient( m, getCoefficient( m).multiply(scale));
		}
		return scale;
	}

	
	
	public double makeRotationInvariant() {	// works destructively.
		int Mp = getMaxCoefficientPairs();
		return makeRotationInvariant(Mp);
	}

	private double makeRotationInvariant(int Mp) {
		Complex z = new Complex(0,0);
		for (int m = 1; m <= Mp; m++) {
			Complex Gm = getCoefficient(-m);
			Complex Gp = getCoefficient(+m);
			double w = 1.0 / m;
			z = z.add(Gm.multiply(w));
			z = z.add(Gp.multiply(w));
		}
		double beta = z.arg();
		for (int m = 1; m <= Mp; m++) {
			setCoefficient(-m, getCoefficient(-m).rotate(-beta));
			setCoefficient( m, getCoefficient( m).rotate(-beta));
		}
		return beta;
	}

	/**
	 * For testing: apply shape rotation to this FourierDescriptor (phi in radians)
	 * 
	 * @param phi rotation angle
	 */
	public void rotate(double phi) {
		rotate(G, phi);
	}

	/**
	 * For testing: apply shape rotation to this FourierDescriptor (phi in radians)
	 * 
	 * @param C complex point
	 * @param phi angle
	 */
	private void rotate(Complex[] C, double phi) {
		Complex rot = new Complex(phi);
		for (int m = 1; m < G.length; m++) {
			C[m] = C[m].multiply(rot);
		}
	}

	/**
	 * Apply a particular start-point phase shift
	 * 
	 * @param phi start point phase
	 * @param Mp most positive/negative frequency index
	 */
	private void shiftStartPointPhase(double phi, int Mp) {
		Mp = Math.min(Mp, G.length/2);
		for (int m = -Mp; m <= Mp; m++) {
			if (m != 0) {
				setCoefficient(m, getCoefficient(m).rotate(m * phi));
			}
		}
	}


	/**
	 * Calculates the 'canonical' start point. This version uses 
	 * (a) a coarse search for a global maximum of fp() and subsequently 
	 * (b) a numerical optimization using Brent's method
	 * (implemented with Apache Commons Math).
	 * 
	 * @param Mp number of Fourier coefficient pairs
	 * @return start point phase
	 */
	public double getStartPointPhase(int Mp) {
		Mp = Math.min(Mp, (G.length-1)/2);
		UnivariateFunction fp =  new TargetFunction(Mp);
		// search for the global maximum in coarse steps
		double cmax = Double.NEGATIVE_INFINITY;
		int kmax = -1;
		int K = 25;	// number of steps over 180 degrees
		for (int k = 0; k < K; k++) {
			final double phi = Math.PI * k / K; 	// phase to evaluate
			final double c = fp.value(phi);
			if (c > cmax) {
				cmax = c;
				kmax = k;
			}
		}
		// optimize using previous and next point as the bracket.
		double minPhi = Math.PI * (kmax - 1) / K;
		double maxPhi = Math.PI * (kmax + 1) / K;	

		UnivariateOptimizer optimizer = new BrentOptimizer(1E-4, 1E-6);
		int maxIter = 20;
		UnivariatePointValuePair result = optimizer.optimize(
				new MaxEval(maxIter),
				new UnivariateObjectiveFunction(fp),
				GoalType.MAXIMIZE,
				new SearchInterval(minPhi, maxPhi)
				);
		double phi0 = result.getPoint();
		return phi0;	// the canonical start point phase
	}

	/**
	 * This inner class defines the target function for calculating the
	 * canonical start point phase. {@link UnivariateFunction} is defined in the
	 * Apache Common Maths framework.
	 */
	private class TargetFunction implements UnivariateFunction {
		final int Mp;

		TargetFunction(int Mp) {
			this.Mp = Mp;
		}

		/** 
		 * The value returned is the sum of the cross products of the FD pairs,
		 * with all coefficients rotated to the given start point phase phi.
		 * TODO: This could be made a lot more efficient!
		 */
		public double value(double phi) {
			double sum = 0;
			for (int m = 1; m <= Mp; m++) {
				Complex Gm = getCoefficient(-m).rotate(-m * phi);
				Complex Gp = getCoefficient( m).rotate( m * phi);
				sum = sum + crossProduct(Gp, Gm);
			}
			return sum;
		}
	}
	
	private double crossProduct(Complex c1, Complex c2) {
		return c1.re * c2.im - c1.im * c2.re;
	}


	public double distanceComplex(FourierDescriptor fd2) {
		return distanceComplex(fd2, G.length/2);
	}


	public double distanceComplex(FourierDescriptor fd2, int Mp) {
		FourierDescriptor fd1 = this;
		Mp = Math.min(Mp, G.length/2);
		double sum = 0;
		for (int m = -Mp; m <= Mp; m++) {
			if (m != 0) {
				Complex G1m = fd1.getCoefficient(m);
				Complex G2m = fd2.getCoefficient(m);
				double dRe = G1m.re - G2m.re;
				double dIm = G1m.im - G2m.im;
				sum = sum + dRe * dRe + dIm * dIm;
			}
		}
		return Math.sqrt(sum);
	}


	public double distanceMagnitude(FourierDescriptor fd2) {
		int Mp = getMaxCoefficientPairs();
		return distanceMagnitude(fd2, Mp);
	}


	public double distanceMagnitude(FourierDescriptor fd2, int Mp) {
		FourierDescriptor fd1 = this;
		Mp = Math.min(Mp, G.length/2);
		double sum = 0;
		for (int m = -Mp; m <= Mp; m++) {
			if (m != 0) {
				double mag1 = fd1.getCoefficient(m).abs();
				double mag2 = fd2.getCoefficient(m).abs();
				double dmag = mag2 - mag1;
				sum = sum + (dmag * dmag);
			}
		}
		return Math.sqrt(sum);
	}

}
