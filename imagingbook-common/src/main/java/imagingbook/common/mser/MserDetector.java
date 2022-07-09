/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.mser;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import ij.process.ByteProcessor;
import imagingbook.common.mser.components.Component;
import imagingbook.common.mser.components.ComponentTree;
import imagingbook.common.mser.components.ComponentTree.Method;
import imagingbook.common.mser.components.PixelMap.Pixel;
import imagingbook.common.util.ParameterBundle;

/**
 * <p>
 * Performs "Maximally Stable Extremal Region" (MSER) detection [1] on gray-scale images.
 * See Chapter 26 of [2] for a detailed description.
 * </p>
 * <p>
 * [1 J. Matas, O. Chum, M. Urban, and T. Pajdla. Robust widebaseline
 * stereo from maximally stable extremal regions. Image and Vision Computing 22(10), 
 * 761–767 (2004).
 * <br>
 * [2] W. Burger, M.J. Burge, <em>Digital Image Processing - An Algorithmic Approach</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 */
public class MserDetector {

	/**
	 * Inner class defining parameters for MSER detection.
	 */
	public static class Parameters implements ParameterBundle {
		
		@DialogLabel("Component tree method")
		public Method method = Method.LinearTime;
		
		@DialogLabel("Delta")
		public int delta = 5;							// = \Delta
		
		@DialogLabel("Min component size (pixels)")
		public int minAbsComponentArea = 3;
		
		@DialogLabel("Min rel. component size")
		public double minRelCompSize = 0.0001;		// = \alpha_{\min}
		
		@DialogLabel("Max rel. component size")
		public double maxRelCompSize = 0.25;		// = \alpha_{\max}
		
		@DialogLabel("Max component size variation")
		public double maxSizeVariation = 0.25;
		
		@DialogLabel("Min component diversity")
		public double minDiversity = 0.50;
		
		@DialogLabel("Constrain ellipse size")
		public boolean constrainEllipseSize = true;
		
		@DialogLabel("Min region compactness")
		public double minCompactness = 0.2;
		
		@DialogLabel("Validate component tree")@DialogHide
		public boolean validateComponentTree = false;
	}

	// --------------------------------------------------------------------

	private final Parameters params;
	private ComponentTree<MserData> compTree = null;
	private List<Component<MserData>> msers = null;
	private double minSizeAbs;
	private double maxSizeAbs;
	private double maxVar;
	private double minDiv;	
	private double elapsedTimeMs = Double.NaN;
	
	// --------------------------------------------------------------------
	
	/**
	 * Constructor using default parameters.
	 */
	public MserDetector() {
		this(new Parameters());
	}

	/**
	 * Constructor using explicit parameters.
	 * @see MserDetector.Parameters
	 */
	public MserDetector(Parameters params) {
		this.params = params;
	}

	// -----------------------------------------------------------------------

	/**
	 * Applies this {@link MserDetector} to the specified gray-scale image
	 * and returns a list of detected MSER components.
	 * 
	 * @param ip a gray-scale image
	 * @return a list of detected MSER components
	 */
	public List<Component<MserData>> applyTo(ByteProcessor ip) {
		long startTime = System.nanoTime();
		compTree = ComponentTree.from(ip, params.method);
		
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
		
		this.minSizeAbs = Math.max((int) (imgSize * params.minRelCompSize), params.minAbsComponentArea);
		this.maxSizeAbs = (int) (imgSize * params.maxRelCompSize);
		this.maxVar = params.maxSizeVariation;
		this.minDiv = params.minDiversity;
		
		this.msers = new LinkedList<>();
		collectMsers(compTree.getRoot(), Integer.MAX_VALUE);
		
		this.elapsedTimeMs = (System.nanoTime() - startTime) / 1000000.0;
		return this.msers;
	}

	// --------------------------------------------------------------------------------

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
	 * Calculates point (coordinate) statistics for all tree components.
	 * We start at the forest root nodes and walk down recursively
	 * toward the leaf nodes. Each component carries a 5-element
	 * {@code long} vector of sums, where each element is the sum 
	 * of the corresponding elements over all child components.
	 * @param c the current component
	 * @return a 5-element vector of coordinate sums
	 */
	private long[] updateStatistics(Component<MserData> c) {
		if (c.getProperties().stats != null) {
			throw new RuntimeException(this.getClass().getSimpleName() + ": sums array should not exist yet!");
		}
		long[] m = new long[5];
		//		if (c.sums == null) {
		//			c.sums = new long[5];
		//		}
		//		else {
		//			Arrays.fill(c.sums, 0);
		//		}
		// add local point statistics
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
	 * Checks and collects component 'c' into {@link #msers}'.
	 * Started from a root component, recursively walks toward the leaf
	 * components.
	 * Diversity is the size ratio between collected components on the same
	 * path. I.e., to be eligible, the current component must be significantly smaller than
	 * the previously collected component. This starts with the largest component.
	 * @param c the current component
	 * @param ap the size of the closest ancestor component marked as MSER
	 */
	private void collectMsers(Component<MserData> c, int ap) {
		//IJ.log("collecting " + c.ID + " / " + msers.size());
		int ac = c.getSize();		// the current component's size
		//double diversity = (ap - ac) / (double) ap;

		MserData props = c.getProperties();
		if (props.isStable && 
				minSizeAbs <= ac && ac <= maxSizeAbs && 
				props.variation <= maxVar && 
				((ap - ac) / (double) ap) >= minDiv) {
			
			//Mser mser = new Mser(c);			// create a candidate MSER (with ellipse)
			props.init();
			double ellipseArea = props.getEllipse().getArea();
			double compactness = ac / ellipseArea;
			// ignore MSERs whose ellipse is too big (if turned on)
			// ignore MSERs whose size is much smaller than the associated ellipse (i.e., not compact at all)
			if ((!params.constrainEllipseSize || ellipseArea <= maxSizeAbs) && 
					compactness > params.minCompactness) {
				this.msers.add(c); //this.msers.add(mser);
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
	 * Returns the list of collected MSERs.
	 * @return the list of collected MSERs
	 */
	public Collection<Component<MserData>> getMser() {
		if (msers == null) {
			throw new IllegalStateException("no MSERs available yet, call applyTo() first!");
		}
		return this.msers;
	}

	/**
	 * Returns the tree of detected MSER components.
	 * An exception is thrown if method {@link #applyTo(ByteProcessor)} 
	 * was not previously called.
	 * 
	 * @return the tree of detected MSER components
	 */
	public ComponentTree<MserData> getComponentTree() {
		if (compTree == null) {
			throw new IllegalStateException("no component tree yet, call applyTo() first!");
		}
		return compTree;
	}

	/**
	 * Returns the time required for the
	 * most recent invocation of {@link #applyTo(ByteProcessor)}) in milliseconds.
	 * {@code NaN} is returned if method {@link #applyTo(ByteProcessor)} 
	 * was not previously called.
	 * @return time required for MSER detection (ms)
	 */
	public double getElapsedTime() {
		return elapsedTimeMs;
	}

}