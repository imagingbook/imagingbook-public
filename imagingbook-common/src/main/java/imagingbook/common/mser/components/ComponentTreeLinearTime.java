package imagingbook.common.mser.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import imagingbook.common.mser.components.PixelMap.Pixel;
import imagingbook.common.util.bits.BitMap;

/**
 * This class implements the "linear-time" component tree algorithm 
 * described by D. Nister and H. Stewenius
 * in "Linear Time Maximally Stable Extremal Regions" (ECCV 2008).
 * 
 * @author WB
 *
 * @param <T> the type of properties to be attached to components
 */
public class ComponentTreeLinearTime<T> extends ComponentTree<T> {
	
	private final List<Component<T>> components;
	private final Component<T> root;
	
	/**
	 * Constructor. 
	 * Creates a new component tree from a {@link PixelMap} representing
	 * a gray-level image.
	 * @param P a {@link PixelMap}
	 */
	public ComponentTreeLinearTime(PixelMap P) {
		components = new ArrayList<Component<T>>();
		root = buildTree(P);
	}
	
//	public ComponentTreeLinearTime(ByteProcessor I) {
//		components = new ArrayList<Component<T>>();
//		root = buildTree(I);
//	}
	
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
	 * Creates the component set from a {@link PixelMap}.
	 * Modifies {@link #components}.
	 * The last item in {@link #components} is the root component.
	 * @param P the input image
	 */
	private Component<T> buildTree(PixelMap P) {
		Component.NEXT_ID = 0;		// reset component IDs (for debugging only)
		//List<Component<T>> components = new ArrayList<Component<T>>();
		
		final BitMap V = new BitMap(P.width, P.height);		// "visited", all set to false by default
		final PriorityQueue<Pixel> B = new PriorityQueue<>();	// heap of boundary points that is sorted by 'val'
		final Deque<Component<T>> C = new LinkedList<>();		// stack of region components
		
		Pixel p = P.getPixel(0, 0); 				// start point (seed), could be any pixel
		V.set(p); //p.setVisited();								// p is the current point, mark as accessible
		C.push(new Component<>(p.val)); 			// push an empty component onto stack				
		
		while (p != null) {							// repeat until all pixels are done
			Pixel n = p.getNextNeighbor();			// get the next neighbor of p (pixels keep track of visited neighbors)
			while (n != null) {
				if (!V.get(n)) {	// (!n.isVisited())
					V.set(n); //n.setVisited();
					if (n.val >= p.val) {		
						B.add(n);					// add neighbor to the boundary heap
					}
					else {
						B.add(p);							// move current pixel back to boundary heap 
						C.push(new Component<>(n.val));		// create an empty component for the neighbor pixel
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
	 * This method is called whenever the current pixel value is raised.
	 * Processes the top items on the component stack.
	 * Modifies {@link #components}.
	 * @param v	new "target" level
	 * @param C component stack
	 */
	private void processStack(int v, Deque<Component<T>> C) {
		while (!C.isEmpty() && v > C.peek().getLevel()) {		
			Component<T> c1 = C.poll();	// retrieve the top-of-stack component
			components.add(c1);				// "emit" component c1 (note: c1.parent == null)

			if (v < Integer.MAX_VALUE) {
				Component<T> c2 = (C.isEmpty() || v < C.peek().getLevel()) ?
						new Component<>(v) : 	// c2 = artificial component with level v
						C.pop();				// c2 = next component on the stack 
				
				// merge c1 into c2 (c2 becomes parent of c1)
				c2.addChild(c1);
				c2.addToSize(c1.getSize());
				c1.setParent(c2);
				C.push(c2);	// insert c2 into the stack C (again)
			}
		}
	}

}
