/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.hough;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ij.IJ;
import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.hough.lines.HoughLine;
import imagingbook.common.util.ParameterBundle;
import imagingbook.common.util.progress.ProgressReporter;

/**
 * This class implements the "classic" Hough Transform for straight lines.
 * This implementation improves the algorithm described in the textbook in
 * the way the accumulator is updated. Here, the quantity 1 is not added
 * to a single accumulator cell but gets distributed over two neighboring
 * (radial) cells to reduce aliasing effects. Thus we accumulate non-integer
 * values and therefore the various accumulators are of type {@code float[][]}.
 *
 * TODO: revise constructors and parameters (remove IJ progress reporting)
 * TODO: add bias correction
 * 
 * @author W. Burger
 * @version 2022/04/01
 */
public class HoughTransformLines implements ProgressReporter {

	public static class Parameters implements ParameterBundle {
		
		/** Number of angular steps over [0, pi] */
		public int nAng = 256;
		
		/** Number of radial steps in each pos/neg direction (accum. size = 2 * nRad + 1) */
		public int nRad = 128;
		
		public boolean showProgress = true;
		public boolean showCheckImage = true;
		public boolean debug = false;
	}

	private final Parameters params;

	private final int nAng; // number of angular steps over [0, pi]
	private final int nRad; // number of radial steps in each pos/neg direction

	private final int width, height; // size of the reference frame (image)
	private final double xRef, yRef; // reference point (x/y-coordinate of image center)

	private final double dAng; // increment of angle
	private final double dRad; // increment of radius
	private final int cRad; // array index representing the zero radius

	private final int accWidth; // width of the accumulator array (angular direction)
	private final int accHeight; // height of the accumulator array (radial direction)
	private final float[][] accumulator; // accumulator array
	private final float[][] accumulatorExt; // extended accumulator array
	private final float[][] accumulatorMax; // accumulator, with local maxima only

	private final double[] cosTable; // tabulated cosine values
	private final double[] sinTable; // tabulated sine values

	// -------------- public constructor(s) ------------------------

	/**
	 * Creates a new Hough transform from the binary image I.
	 * 
	 * @param I      input image, relevant (edge) points have pixel values greater 0.
	 * @param params parameter object.
	 */
	public HoughTransformLines(ByteProcessor I, Parameters params) {
		this(I.getWidth(), I.getHeight(), params);
		this.process(I, accumulator);
	}
	
	public HoughTransformLines(ByteProcessor I) {
		this(I, new Parameters());
	}

	/**
	 * Creates a new Hough transform from a sequence of 2D points. Parameters M, N
	 * are only used to specify the reference point (usually at the center of the
	 * image). Use this constructor if the relevant image points are collected
	 * separately.
	 * 
	 * @param points an array of 2D points.
	 * @param width      width of the corresponding image plane.
	 * @param height      height of the corresponding image plane.
	 * @param params parameter object.
	 */
	public HoughTransformLines(Pnt2d[] points, int width, int height, Parameters params) {
		this(width, height, params);
		this.process(points, accumulator);
	}

	// Non-public constructor used by public constructors (to initialize all final
	// members variables).
	private HoughTransformLines(int width, int height, Parameters params) {
		this.params = (params == null) ? new Parameters() : params;
		this.width = width;
		this.height = height;
		this.xRef = width / 2; // integer value
		this.yRef = height / 2; // integer value
		this.nAng = params.nAng;
		this.nRad = params.nRad;
		this.dAng = Math.PI / nAng;
		this.dRad = 0.5 * Math.hypot(width, height) / nRad; // nRad radial steps over half the diagonal length
		this.cRad = nRad;
		this.accWidth = nAng;
		this.accHeight = nRad + 1 + nRad;
		this.accumulator = new float[accWidth][accHeight]; // cells are initialized to zero!
		this.accumulatorExt = new float[2 * accWidth][accHeight];
		this.accumulatorMax = new float[accWidth][accHeight];
		this.cosTable = makeCosTable();
		this.sinTable = makeSinTable();
	}

	// -------------- public methods ------------------------

	/**
	 * Finds and returns the parameters of the strongest lines with a specified min.
	 * pixel count. All objects in the returned array are valid, but the array may
	 * be empty. Note: Could perhaps be implemented more efficiently with
	 * insert-sort.
	 * 
	 * @param amin the minimum accumulator value for each line.
	 * @param maxLines maximum number of (strongest) lines to extract.
	 * @return a possibly empty array of {@link HoughLine} objects.
	 */
	public HoughLine[] getLines(int amin, int maxLines) {
		makeAccumulatorExt();	// TODO: should not be here
		findLocalMaxima();
		// collect all lines corresponding to accumulator maxima
		List<HoughLine> lines = new ArrayList<>();
		for (int ri = 0; ri < accHeight; ri++) {
			for (int ai = 0; ai < accWidth; ai++) {
				int hcount = (int) accumulatorMax[ai][ri];
				if (hcount >= amin) {
					double angle = angleFromIndex(ai);
					double radius = radiusFromIndex(ri);
					lines.add(new HoughLine(angle, radius, xRef, yRef, hcount));
				}
			}
		}
		// sort 'lines' by count (highest counts first)
		Collections.sort(lines);
		List<HoughLine> slines = lines.subList(0, Math.min(maxLines, lines.size()));
		// convert the list to an array and return:
		return slines.toArray(new HoughLine[0]);
	}
	
	/**
	 * Returns the reference points x-coordinate.
	 * @return as described.
	 */
	public double getXref() {
		return xRef;
	}
	
	/**
	 * Returns the reference points y-coordinate.
	 * @return as described.
	 */
	public double getYref() {
		return yRef;
	}

	/**
	 * Calculates the actual angle (in radians) for angle index {@code ai}
	 * 
	 * @param ai angle index [0,...,nAng-1]
	 * @return Angle [0,...,PI] for angle index ai
	 */
	public double angleFromIndex(int ai) {
		return ai * dAng;
	}

	/**
	 * Calculates the actual radius for radius index ri.
	 * 
	 * @param ri radius index [0,...,nRad-1].
	 * @return Radius [-maxRad,...,maxRad] with respect to reference point (xc, yc).
	 */
	public double radiusFromIndex(int ri) {
		return (ri - cRad) * dRad;
	}

	public double radiusToIndex(double rad) {
		return cRad + rad / dRad;
	}
	
	// ---------------------------------------------------------------

	/**
	 * Returns the accumulator as a 2D float-array.
	 * @return the contents of the accumulator
	 */
	public float[][] getAccumulator() {
		return this.accumulator;
	}

	/**
	 * Returns the local maximum values of the accumulator as a 2D float-array
	 * (all non-maximum elements are set to zero).
	 * @return the local maximum values of the accumulator 
	 */
	public float[][] getAccumulatorMax() {
		return this.accumulatorMax;
	}
	
	/**
	 * Returns the extended accumulator as a 2D float-array.
	 * @return the extended accumulator
	 */
	public float[][] getAccumulatorExtended() {
		return this.accumulatorExt;
	}
	

	/**
	 * Creates and returns an image of the 2D accumulator array.
	 * @return a {@link FloatProcessor} image of the accumulator
	 */
	public FloatProcessor getAccumulatorImage() {
		return new FloatProcessor(accumulator);
	}
	

	/**
	 * Creates and returns an image of the extended 2D accumulator array, produced
	 * by adding a vertically mirrored copy of the accumulator to its right end. 
	 * @return a {@link FloatProcessor} image of the extended accumulator
	 */
	public FloatProcessor getAccumulatorImageExtended() {
		FloatProcessor fp = new FloatProcessor(accumulatorExt);
		return fp;
	}

	/**
	 * Creates and returns an image of the local maxima of the 2D accumulator array.
	 * @return a {@link FloatProcessor} image of the accumulator maxima
	 */
	public FloatProcessor getAccumulatorMaxImage() {
		return new FloatProcessor(accumulatorMax);
	}

	// -------------- nonpublic methods ------------------------

	private double[] makeCosTable() {
		double[] cosTab = new double[nAng];
		for (int ai = 0; ai < nAng; ai++) {
			double angle = dAng * ai;
			cosTab[ai] = Math.cos(angle);
		}
		return cosTab;
	}

	private double[] makeSinTable() {
		double[] sinTab = new double[nAng];
		for (int ai = 0; ai < nAng; ai++) {
			double angle = dAng * ai;
			sinTab[ai] = Math.sin(angle);
		}
		return sinTab;
	}

	private void process(ByteProcessor ip, float[][] acc) {
		if (params.showProgress)
			IJ.showStatus("filling accumulator ...");
		for (int v = 0; v < height; v++) {
			if (params.showProgress)
				IJ.showProgress(v, height);
			for (int u = 0; u < width; u++) {
				if (ip.get(u, v) != 0) { // this is a foreground (edge) pixel - use ImageAccessor??
					processPoint(u, v, acc);
				}
			}
		}
		if (params.showProgress)
			IJ.showProgress(1, 1);
	}

	private void process(Pnt2d[] points, float[][] acc) {
		if (params.showProgress)
			IJ.showStatus("filling accumulator ...");
		for (int i = 0; i < points.length; i++) {
			if (params.showProgress && i % 50 == 0)
				IJ.showProgress(i, points.length);
			Pnt2d p = points[i];
			if (p != null) {
				processPoint(p.getX(), p.getY(), acc);
			}
		}
		if (params.showProgress)
			IJ.showProgress(1, 1);
	}

	private void processPoint(double u, double v, float[][] acc) {
		final double x = u - xRef;
		final double y = v - yRef;
		for (int ai = 0; ai < accWidth; ai++) {
//			double theta = dAng * ai;
//			double r = x * Math.cos(theta) + y * Math.sin(theta);
			double r = x * cosTable[ai] + y * sinTable[ai]; // sin/cos tables improve speed!
			double ri = radiusToIndex(r);
			// accumulated quantity (1.0) is distributed to 2 neighboring bins:
			int r0 = (int) Math.floor(ri);	// lower radial bin index
			int r1 = r0 + 1;				// upper radial bin index
			if (r0 >= 0 && r1 < accHeight) {
				double alpha = ri - r0;
				acc[ai][r0] += (1.0 - alpha);
				acc[ai][r1] += alpha;
			}
		}
	}
	
	/**
	 * Searches for local maxima in the extended accumulator but enters their
	 * positions in 'accumulatorMax'.
	 */
	private void findLocalMaxima() {
		if (params.showProgress)
			IJ.showStatus("finding local maxima");
		int count = 0;
		for (int ai = 1; ai <= accWidth; ai++) {	// note the range!
			for (int ri = 1; ri < accHeight - 1; ri++) {
				float vC = accumulatorExt[ai][ri];	// center value
				boolean ismax = 
					vC > accumulatorExt[ai + 1][ri]     && // 0
					vC > accumulatorExt[ai + 1][ri - 1] && // 1
					vC > accumulatorExt[ai]    [ri - 1] && // 2
					vC > accumulatorExt[ai - 1][ri - 1] && // 3
					vC > accumulatorExt[ai - 1][ri]     && // 4
					vC > accumulatorExt[ai - 1][ri + 1] && // 5
					vC > accumulatorExt[ai]    [ri + 1] && // 6
					vC > accumulatorExt[ai + 1][ri + 1];   // 7
				if (ismax) {
					if (ai < accWidth)
						accumulatorMax[ai % accWidth][ri] = vC;	// take care of ai == accWidth
					else
						accumulatorMax[ai % accWidth][accHeight - ri - 1] = vC;
					count++;
				}
			}
		}
		if (params.debug)
			IJ.log("found maxima: " + count);
	}
	
	/**
	 * Creates the extended 2D accumulator array 
	 */
	private void makeAccumulatorExt() {
		if (params.showProgress)
			IJ.showStatus("making extended accumulator");
		for (int ai = 0; ai < accWidth; ai++) {
			for (int ri = 0; ri < accHeight; ri++) {
				// insert original accumulator into the left side
				accumulatorExt[ai][ri] = accumulator[ai][ri];
				// insert a vertically flipped copy of accumulator into the right side
				accumulatorExt[accWidth + ai][ri] = accumulator[ai][accHeight - ri - 1];
			}
		}
	}

	@Override
	public double getProgress() {
		// TODO report progress state
		return 0;
	}
	
}


