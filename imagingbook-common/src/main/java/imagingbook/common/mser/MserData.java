/*
 *  This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge.
 * All rights reserved. Visit https://imagingbook.com for additional details.
 */
package imagingbook.common.mser;

import java.util.Arrays;

import imagingbook.common.geometry.basic.Pnt2d.PntDouble;
import imagingbook.common.geometry.ellipse.GeometricEllipse;
import imagingbook.common.math.eigen.Eigensolver2x2;
import imagingbook.common.mser.components.Component;

/**
 * Defines a container holding the data for calculating MSER properties.
 * Instances of this type are attached to {@link Component} objects during
 * MSER calculation in {@link MserDetector}.
 * 
 * @author WB
 * @version 2022/11/19
 */
public class MserData {
	
	private final Component<MserData> component;	// reference to associated component
	
	/** True iff the associated component is a MSER. */
	protected boolean isMserP = false;
	
	/** Component size variation (wrt. to the size at delta levels higher), undefined = -1 */
	protected float variation = -1;
	
	/** Component stability status */
	protected boolean isStable = true;
	
	/** Component coordinate ordinary moments (m10, m01, m20, m02, m11) */
	protected long[] stats = null;
	
	/** Central moments of pixel coordinates (mu10, mu01, mu20, mu02, mu11) */
	private double[] moments = null;
	
	/** Reference to the equivalent ellipse */
	private GeometricEllipse ellipse = null;
	
	
	/**
	 * Constructor.
	 * @param c a reference to the associated {@link Component}
	 */
	public MserData(Component<MserData> c) {
		this.component = c;
	}
		
	/**
	 * Returns true iff the associated component is a MSER.
	 * @return true iff a MSER
	 */
	public boolean isMser() {
		return isMserP;
	}
	
	/**
	 * Returns the vector of central moments (mu10, mu01, mu20, mu02, mu11)
	 * for the pixel coordinates contained in the associated component.
	 * @return the vector of central moments
	 */
	public double[] getCentralMoments() {
		return this.moments;
	}
	
	/**
	 * Returns the covariance matrix for the pixel coordinates contained
	 * in the associated component.
	 * @return the covariance matrix 
	 */
	public double[][] getCovarianceMatrix() {
		double[] mu = getCentralMoments(); // = (mu10, mu01, mu20, mu02, mu11)
		final int size = component.getSize();
		double[][] S = {
				{mu[2]/size, mu[4]/size},
				{mu[4]/size, mu[3]/size}};
		return S;
	}
	
	/**
	 * Returns the center point for the associated component.
	 * @return the center point
	 */
	public PntDouble getCenter() {
		double mu10 = moments[0];
		double mu01 = moments[1];
		return PntDouble.from(mu10, mu01);
	}
	
	/**
	 * Returns the equivalent ellipse for the associated component.
	 * @return the equivalent ellipse
	 */
	public GeometricEllipse getEllipse() {
		return this.ellipse;
	}
	
	/**
	 * Calculates the central moments and the equivalent ellipse
	 * for the associated component.
	 */
	protected void init() {
		calculateMoments();
		makeEllipse();
	}
	
	private void calculateMoments() {
		double n = component.getSize();
		double mu10 = stats[0] / n;
		double mu01 = stats[1] / n;
		double mu20 = stats[2] - stats[0] * stats[0] / n; // = size times the covariance, see Book p.750
		double mu02 = stats[3] - stats[1] * stats[1] / n;
		double mu11 = stats[4] - stats[0] * stats[1] / n;
		this.moments = new double[] {mu10, mu01, mu20, mu02, mu11};
	}
	
	private void makeEllipse() {
		final double n = component.getSize();
		final double mu10 = moments[0];
		final double mu01 = moments[1];
		final double mu20 = moments[2];
		final double mu02 = moments[3];
		final double mu11 = moments[4];
		Eigensolver2x2 es = new Eigensolver2x2(mu20, mu11, mu11, mu02);
		double ra = 2 * Math.sqrt(es.getRealEigenvalue(0) / n);	// correct (see Book p.238)
		double rb = 2 * Math.sqrt(es.getRealEigenvalue(1) / n);
		double[] x1 = es.getEigenvector(0).toArray();
		double theta = Math.atan2(x1[1], x1[0]);
		this.ellipse = new GeometricEllipse(ra, rb, mu10, mu01, theta);
	}
	
//	/**
//	 * Sorts a list of MSERs by (decreasing) component size, i.e.,
//	 * the largest MSER (with the most pixels) becomes the first.
//	 * 
//	 * @param msers a list of {@link MSER} instances
//	 */
//	public static void sortBySize(List<Component<MserData>> msers) {
//		Comparator<Component<MserData>> cmp = new Comparator<Component<MserData>>() {
//			@Override
//			public int compare(Component<MserData> mser1, Component<MserData> mser2) {
//				return Integer.compare(mser2.getSize(), mser1.getSize());
//			}
//		};
//		Collections.sort(msers, cmp);
//	}

	@Override
	public String toString() {
		return String.format("variation=%.2f stability=%d stats=%s",
				variation, isStable, (stats == null) ? "x" : Arrays.toString(stats));
	}
	

}
