/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.geometry.fitting.line;

import static imagingbook.common.math.Arithmetic.sqr;

import java.util.ArrayDeque;
import java.util.Deque;

import org.apache.commons.math3.linear.MatrixUtils;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.math.eigen.EigenDecompositionJama;
import imagingbook.common.util.SortMap;

/**
 * <p>
 * This class implements incremental orthogonal line fitting to a set of 2D
 * points using eigendecomposition (see {@link OrthogonalLineFitEigen} for
 * a non-incremental version). See Sec. 10.3 (Alg. 10.4) of [1] for additional
 * details.
 * </p>
 * <p>
 * This fitter behaves like a queue: sample points may be added and removed
 * freely either at its front or its end, while the ordering of the remaining
 * points remains unchanged. This is to simplify back-tracking, for example for
 * incremental contour fitting. Whenever a point is added or removed, the
 * internal statistics (scatter matrix) are updated. The current line fit can be
 * queried any time as long as there are more than two points in the point set
 * (otherwise an exception is thrown).
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing - An Algorithmic
 * Approach</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2022/09/29 revised
 * @see OrthogonalLineFitEigen
 * @see Deque
 */
public class IncrementalLineFit implements LineFit {	// extends ArrayDeque<Pnt2d> 
	
	private final Deque<Pnt2d> points;
	private double[] p;									// current line parameters A,B,C
	private double Sx, Sy, Sxx, Syy, Sxy;	// current scatter statistics
	
	/**
	 * Constructor creating a fitter with an empty set of points.
	 */
	public IncrementalLineFit() {
		this(null);
	}
	
	/**
	 * Constructor accepting a sequence of initial points.
	 * The first point is placed at the from of the queue,
	 * the last point at its end.
	 * 
	 * @param initPnts an array of initial points
	 */
	public IncrementalLineFit(Pnt2d[] initPnts) {	
		points = new ArrayDeque<>();
		p = null;
		Sx = 0;
		Sy = 0;
		Sxx = 0;
		Syy = 0;
		Sxy = 0;
		if (initPnts == null) {
			return;
		}
		for (Pnt2d p : initPnts) {
			this.add(p);
		}
	}
	
	/**
	 * Calculates and returns the sum of the squared orthogonal distances
	 * of the current point set for this line fit.
	 * 
	 * @return the squared orthogonal error
	 * @see LineFit#getSquaredOrthogonalError(Pnt2d[])
	 */
	public double getSquaredOrthogonalError() {
		return getSquaredOrthogonalError(this.getPoints());
	}
	
	// update statistics at point addition/removal --------------------- 
	
	private void addSamplePoint(Pnt2d pnt) {
		final double x = pnt.getX();
		final double y = pnt.getY();
		Sx += x;
		Sy += y;
		Sxx += sqr(x);
		Syy += sqr(y);
		Sxy += x * y;
		this.p = null;	// invalidate previous line parameters
	}
	
	private void removeSamplePoint(Pnt2d pnt) {
		final double x = pnt.getX();
		final double y = pnt.getY();
		Sx -= x;
		Sy -= y;
		Sxx -= sqr(x);
		Syy -= sqr(y);
		Sxy -= x * y;
		this.p = null;	// invalidate previous line parameters
	}
	
	// --------------------------------------------------------
	
	public Pnt2d[] getPoints() {
		return points.toArray(new Pnt2d[0]);
	}
	
	// delegate methods matching those of Deque interface
	
	public boolean add(Pnt2d pnt) {
		addLast(pnt);
		return true;
	}
	
	public void addFirst(Pnt2d pnt) {
		points.addFirst(pnt);
		addSamplePoint(pnt);
	}
	
	public void addLast(Pnt2d pnt) {
		points.addLast(pnt);
		addSamplePoint(pnt);
	}
	
	public Pnt2d removeFirst() {
		Pnt2d pnt = points.removeFirst();
		removeSamplePoint(pnt);
		return pnt;
	}
	
	public Pnt2d removeLast() {
		Pnt2d pnt = points.removeLast();
		removeSamplePoint(pnt);
		return pnt;
	}
	
	public Pnt2d peekFirst() {
		return points.peekFirst();
	}
	
	public Pnt2d peekLast() {
		return points.peekLast();
	}
	
	// --------------------------------------------------------
	
	@Override
	public int getSize() {
		return points.size();
	}
	
	@Override
	public double[] getLineParameters() {
		if (this.getSize() < 2) {
			throw new IllegalStateException("cannot fit line, set of point set is less than 2");
		}
		if (p == null) {
			this.p = fit(points.toArray(new Pnt2d[0]));
		}
		return p;
	}
	
	private double[] fit(Pnt2d[] pts) {
		final int n = pts.length;
		
		double sxx = Sxx - sqr(Sx) / n;
		double syy = Syy - sqr(Sy) / n;
		double sxy = Sxy - Sx * Sy / n;
		
		double[][] S = {
				{sxx, sxy},
				{sxy, syy}};
		
		EigenDecompositionJama es = new EigenDecompositionJama(MatrixUtils.createRealMatrix(S));
		int k = SortMap.getNthSmallestIndex(es.getRealEigenvalues(), 0);
		double[] e = es.getEigenvector(k).toArray();
		
		double A = e[0];
		double B = e[1];
		double C = -(A * Sx + B * Sy) / n;
		
		return new double[] {A, B, C};
	}
		
}
