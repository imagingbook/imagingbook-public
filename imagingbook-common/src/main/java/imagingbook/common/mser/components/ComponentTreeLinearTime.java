/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.mser.components;

import imagingbook.common.mser.components.PixelMap.Pixel;
import imagingbook.common.util.bits.BitMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * <p>
 * This class implements the "linear-time" ("local flooding") component tree algorithm described in [1]. See Section
 * 26.2.3 of [2] for a detailed description (Algs. 26.3 - 26.4).
 * </p>
 * <p>
 * [1] D. Nister and H. Stewenius, "Linear Time Maximally Stable Extremal Regions", Computer Vision - ECCV 2008. pp.
 * 183-196, Springer Berlin/Heidelberg (2008).
 * <br>
 * [2] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @param <T> the type of properties to be attached to components
 * @author WB
 * @version 2022/11/19
 */
public class ComponentTreeLinearTime<T> extends ComponentTree<T> {
	
	private final List<Component<T>> components;
	private final Component<T> root;

	/**
	 * Constructor, creates a new component tree from a {@link PixelMap} representing a gray-level image.
	 *
	 * @param P a {@link PixelMap}
	 */
	public ComponentTreeLinearTime(PixelMap P) {
		components = new ArrayList<Component<T>>();
		root = buildTree(P);
	}
	
	// -----------------------------------------------------------------------------------
	
	@Override
	public Component<T> getRoot() {
//		return components.get(components.size() - 1);	// root is the last component
		return root;
	}

	@Override
	public Collection<Component<T>> getComponents() {
		return components;
	}
	
	// -----------------------------------------------------------------------------------

	/**
	 * Creates the component set from a {@link PixelMap}. Modifies {@link #components}. The last item in
	 * {@link #components} is the root component.
	 *
	 * @param P the input image
	 */
	private Component<T> buildTree(PixelMap P) {		
		final BitMap V = new BitMap(P.width, P.height);		// "visited", all set to false by default
		final PriorityQueue<Pixel> B = new PriorityQueue<>();	// heap of boundary points that is sorted by 'val'
		final Deque<Component<T>> C = new LinkedList<>();		// stack of region components
		
		Pixel p = P.getPixel(0, 0); 				// start point (seed), could be any pixel
		V.set(p); //p.setVisited();					// p is the current point, mark as accessible
		C.push(makeComponent(p.val));				// push an empty component onto stack
		
		while (p != null) {							// repeat until all pixels are done
			Pixel n = p.getNextNeighbor();			// get the next neighbor of p (pixels keep track of visited neighbors)
			while (n != null) {
				if (!V.get(n)) {					// (!n.isVisited())
					V.set(n); 						//n.setVisited();
					if (n.val >= p.val) {		
						B.add(n);					// add neighbor to the boundary heap
					}
					else {
						B.add(p);							// move current pixel back to boundary heap 
						C.push(makeComponent(n.val));	// create an empty component for the neighbor pixel
						p = n;								// make neighbor the current pixel
					}
				}
				n = p.getNextNeighbor();
			}
			
			C.peek().addPoint(p);					// add the current pixel to the top-of-stack component
			
			Pixel q = B.poll();						// remove the first point from the heap of boundary pixels
			if (q != null && q.val > p.val) {		// new pixel value (q) is greater than current value (p)
				processStack(q.val, C);				// see below
			}
			p = q;									// update the current pixel
		}
		
		if (C.size() != 1) {	// just to make sure!
			throw new RuntimeException("component stack size must be 1 but is " + C.size());
		}
		Component<T> r = C.peek();					// the root component is still on the stack
		processStack(Integer.MAX_VALUE, C); 		// process the root component
		
		return r;
	}
	

	
	// -------------------------------------------------------------------

	/**
	 * This method is called whenever the current pixel value is raised. Processes the top items on the component stack.
	 * Modifies {@link #components}.
	 *
	 * @param v new "target" level
	 * @param C component stack
	 */
	private void processStack(int v, Deque<Component<T>> C) {
		while (!C.isEmpty() && v > C.peek().getLevel()) {		
			Component<T> c1 = C.poll();	// retrieve the top-of-stack component
			components.add(c1);				// "emit" component c1 (note: c1.parent == null)

			if (v < Integer.MAX_VALUE) {
				Component<T> c2 = (C.isEmpty() || v < C.peek().getLevel()) ?
						makeComponent(v) :		// c2 = artificial component with level v
						C.pop();				// c2 = next component on the stack 
				
				// merge c1 into c2 (c2 becomes parent of c1)
				c2.addChild(c1);
				c2.addToSize(c1.getSize());
				c1.setParent(c2);
				C.push(c2);	// insert c2 into the stack C (again)
			}
		}
	}

	private int nextComponentIndex = 0;
	
	private Component<T> makeComponent(int level) {
		return new Component<>(level, nextComponentIndex++);
	}

}
