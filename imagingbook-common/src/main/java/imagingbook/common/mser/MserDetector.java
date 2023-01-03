/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.mser;

import ij.process.ByteProcessor;
import imagingbook.common.mser.components.Component;
import imagingbook.common.mser.components.ComponentTree;
import imagingbook.common.mser.components.PixelMap.Pixel;

import java.util.LinkedList;
import java.util.List;

/**
 * <p>
 * Performs "Maximally Stable Extremal Region" (MSER) detection [1] on gray-scale images. See Chapter 26 of [2] for more
 * details. The constructor sets up the complete component tree for the specified image but does not perform feature
 * detection itself, which is done by calling {@link #getMserFeatures()}.
 * </p>
 * <p>
 * [1] J. Matas, O. Chum, M. Urban, and T. Pajdla. Robust widebaseline stereo from maximally stable extremal regions.
 * Image and Vision Computing 22(10), 761–767 (2004). <br> [2] W. Burger, M.J. Burge, <em>Digital Image Processing
 * &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 *
 * @author WB
 * @version 2022/11/23
 */
public class MserDetector {

	private final MserParameters params;
	private final ComponentTree<MserData> compTree;
	private final double minSizeAbs;
	private final double maxSizeAbs;
	private final double maxVar;
	private final double minDiv;	
	private final double elapsedTimeMs;
	
	private List<Component<MserData>> msers = null;	// used to collect MSERs
	
	// --------------------------------------------------------------------
	
	/**
	 * Constructor using default parameters. 
	 * 
	 * @param ip the input image
	 * @see #MserDetector(ByteProcessor, MserParameters)
	 */
	public MserDetector(ByteProcessor ip) {
		this(ip, new MserParameters());
	}

	/**
	 * Constructor using explicit parameters. A set of parameters can be specified (see {@link MserParameters}). The
	 * constructor sets up the complete component tree but does not perform feature detection itself, which is done by
	 * calling {@link #getMserFeatures()}.
	 *
	 * @param ip the input image
	 * @param params a {@link MserParameters} instance
	 * @see MserParameters
	 */
	public MserDetector(ByteProcessor ip, MserParameters params) {
		this.params = params;
		long startTime = System.nanoTime();
		
		// set up the component tree for ip
		this.compTree = ComponentTree.from(ip, params.method);
		
		if (params.validateComponentTree) {
			if(!compTree.validate()) {
				throw new RuntimeException("component tree not valid");
			}
		}

		attachMserProperties(compTree);
		calcVariations(compTree, params.delta);
		markMaximallyStable(compTree);
		updateStatistics(compTree.getRoot());	// calcPointStatistics(compTree);

		int imgSize = ip.getWidth() * ip.getHeight();
		
		this.minSizeAbs = 
				Math.max((int) (imgSize * params.minRelCompSize), params.minAbsComponentArea);
		this.maxSizeAbs = (int) (imgSize * params.maxRelCompSize);
		this.maxVar = params.maxSizeVariation;
		this.minDiv = params.minDiversity;
		
		this.elapsedTimeMs = (System.nanoTime() - startTime) / 1000000.0;
	}

	// -----------------------------------------------------------------------

	/**
	 * Extracts and returns a list of MSER components. Features are extracted only once and cached for subsequent
	 * calls.
	 *
	 * @return a list of extracted MSER components
	 */
	public List<Component<MserData>> getMserFeatures() {
		if (this.msers == null) {
			this.msers = new LinkedList<>();
			collectMsers(compTree.getRoot(), Integer.MAX_VALUE);
		}
		return this.msers;
	}
	
	// -----------------------------------------------------------------------

	/**
	 * Attach Mser data to all region tree components.
	 * @param tree
	 */
	private void attachMserProperties(ComponentTree<MserData> tree) {
		for (Component<MserData> c : tree) {
			c.setProperties(new MserData(c));
		}
	}
	
	/**
	 * New version, works on components in any order.
	 * @param compTree
	 * @param delta
	 */
	private void calcVariations(ComponentTree<MserData> compTree, int delta) {
		for (Component<MserData> c : compTree) {		
			c.getProperties().variation = Float.POSITIVE_INFINITY;
			Component<?> p = c.getParent();
			if (p != null) {	// c is not root
				final int ld = c.getLevel() + delta;
				Component<?> cc = c;
				
				// find the next ancestor component (p) whose level val(p) >= vd :
				while (p != null && p.getLevel() < ld) {
					// IJ.log("        hiC=" + hiC.ID + " hiC.parent.level=" + hiC.getParent().getLevel());
					cc = p;			// climb upward
					p = cc.getParent();
				}
				if (p != null) {	// cc is not the root -->  pp.getLevel() >= vd
					// calculate growth and assign to c
					float ac  = c.getSize();		// size of the current component (c)
					float acc = cc.getSize();		// size of the upper component (cc)
					c.getProperties().variation = (acc - ac) / ac;
				}
			}
		}
	}
	
	/**
	 * We start from the root and walk down recursively.
	 * Every node is only visited once.
	 */
	private void markMaximallyStable(ComponentTree<MserData> compTree) {
		// initially assume all components are max. stable
		for (Component<MserData> c : compTree) {
			Component<MserData> p = c.getParent();
			if (p == null) {					// the root is always unstable
				c.getProperties().isStable = false;
			}
			else if (c.getLevel() + 1 == p.getLevel()) {
				float vp = p.getProperties().variation;
				float vc = c.getProperties().variation;
				
				if (vc < vp) {
					p.getProperties().isStable = false;
				}
				else if (vc > vp) {
					c.getProperties().isStable = false;
				}
			}
		}
	}

	/**
	 * Calculates point (coordinate) statistics for all tree components. We start at the forest root nodes and walk down
	 * recursively toward the leaf nodes. Each component carries a 5-element {@code long} vector of sums, where each
	 * element is the sum of the corresponding elements over all child components.
	 *
	 * @param c the current component
	 * @return a 5-element vector of coordinate sums
	 */
	private long[] updateStatistics(Component<MserData> c) {
		if (c.getProperties().stats != null) {
			throw new RuntimeException(this.getClass().getSimpleName() + ": sums array should not exist yet!");
		}
		long[] m = new long[5];
		for (Pixel p : c.getLocalPixels()) {
			final long x = p.x;
			final long y = p.y;
			m[0] += x;
			m[1] += y;
			m[2] += x * x;
			m[3] += y * y;
			m[4] += x * y;
		}

		for (Component<MserData> child : c.getChildren()) {
			long[] sc = updateStatistics(child);
			for (int i = 0; i < sc.length; i++) {
				m[i] += sc[i];
			}
		}
		c.getProperties().stats = m;
		//		IJ.log("updateStatistics: " + c.toString() + " " + Arrays.toString(c.sums));
		return m;
	}

	// --------------------------------------------------------------------------------

	/**
	 * Checks and collects component 'c' into {@link #msers}'. Started from a root component, recursively walks toward
	 * the leaf components. Diversity is the size ratio between collected components on the same path. I.e., to be
	 * eligible, the current component must be significantly smaller than the previously collected component. This
	 * starts with the largest component.
	 *
	 * @param c the current component
	 * @param ap the size of the closest ancestor component marked as MSER
	 */
	private void collectMsers(Component<MserData> c, int ap) {
		int ac = c.getSize();		// the current component's size

		MserData props = c.getProperties();
		if (props.isStable && 
				minSizeAbs <= ac && ac <= maxSizeAbs && 
				props.variation <= maxVar && 
				((ap - ac) / (double) ap) >= minDiv) {

			props.init();
			double ellipseArea = props.getEllipse().getArea();
			double compactness = ac / ellipseArea;
			// ignore MSERs whose ellipse is too big (if turned on)
			// ignore MSERs whose size is much smaller than the associated ellipse (i.e., not compact at all)
			if ((!params.constrainEllipseSize || ellipseArea <= maxSizeAbs) && 
					compactness > params.minCompactness) {
				this.msers.add(c);
				ap = ac;
				props.isMserP = true;	// mark component c as a selected MSER
			}
		}
		
		if (ac > minSizeAbs) { // continue unless size is too small
			// recursively collect components from all children:
			for (Component<MserData> child : c.getChildren()) {
				collectMsers(child, ap);
			}
		}
	}

	/**
	 * Returns the component tree for this MSER detector.
	 * 
	 * @return the component tree
	 */
	public ComponentTree<MserData> getComponentTree() {
		return compTree;
	}

	/**
	 * Returns the time required for the MSER detector to process the
	 * associated image (in milliseconds).
	 * 
	 * @return time required for MSER detection (ms)
	 */
	public double getElapsedTime() {
		return elapsedTimeMs;
	}

}