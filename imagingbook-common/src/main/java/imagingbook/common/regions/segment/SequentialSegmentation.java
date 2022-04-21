/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.regions.segment;

import static imagingbook.common.geometry.basic.NeighborhoodType2D.N4;

import java.util.HashSet;

import ij.process.ByteProcessor;
import imagingbook.common.geometry.basic.NeighborhoodType2D;

/**
 * Binary region labeler based on a sequential labeling
 * algorithm. 
 * 
 * @author WB
 * @version 2020/12/20
 */
public class SequentialSegmentation extends BinaryRegionSegmentation {

	private HashSet<LabelCollision> collisions;

	/**
	 * Constructor. Creates a new sequential binary region segmenter.
	 * @param ip a binary image with 0 values for background pixels and values &gt; 0
	 * for foreground pixels.
	 */
	public SequentialSegmentation(ByteProcessor ip) {
		this(ip, DEFAULT_NEIGHBORHOOD);
	}
	
	public SequentialSegmentation(ByteProcessor ip, NeighborhoodType2D nh) {
		super(ip, nh);
	}

	@Override
	protected boolean applySegmentation() {
		collisions = new HashSet<>();
		int[] nh = null;
		
		// Step 1: assign initial labels:
		for (int v = 0; v < height; v++) {
			for (int u = 0; u < width; u++) {
				if (getLabel(u, v) == FOREGROUND) {
					nh = getNeighborhood(nh, u, v);
					int a = max(nh);
					if (!isLabel(a)) { // a = 0 or 1,  i.e., (u,v) is isolated and not connected to any labeled pixel
						setLabel(u, v, getNextLabel()); // new region with a new label
					}
					else {					// at least one label in nh[] is an assigned label		
						setLabel(u, v, a);	// connect to the existing region with the largest label among neighbors
						for (int b : nh) {	// register label collisions between a and all b
							if (isLabel(b) && a != b) {
								registerCollision(a, b);	// we know that a <= b
							}
						}
					}
				}
			}
		}
		
		int[] replacementTable = resolveCollisions(); 	// Step 2: resolve label collisions
		relabelImage(replacementTable); 				// Step 3: relabel the image
		return true;
	}
	
	private int[] getNeighborhood(int[] nh, int u, int v) {
		if (nh == null) {
			nh = new int[(NT == N4) ? 4 : 8];
		}
		//assemble the neighborhood nh (x is current position u,v):
				// 4-neighborhood:
				//       [1]
				//    [0][x]
				// 8-neighborhood:
				//    [1][2][3]
				//    [0][x]
		if (NT == N4) {
			nh[0] = getLabel(u - 1, v);
			nh[1] = getLabel(u, v - 1);
		}
		else {	// neighborhood == NeighborhoodType.N8
			nh[0] = getLabel(u - 1, v);
			nh[1] = getLabel(u - 1, v - 1);
			nh[2] = getLabel(u, v - 1);
			nh[3] = getLabel(u + 1, v - 1);
		}
		return nh;
	}
	
	private int max(int[] nh) {
		int nMax = Integer.MIN_VALUE;
		for (int n : nh) {
			if (n > nMax) {
				nMax = n;
			}
		}
		return nMax;
	}

	private void registerCollision(int a, int b) {
		if (a != b) {
			LabelCollision c = (a < b) ? new LabelCollision(a, b) : new LabelCollision(b, a);
			//if (!collisionMap.contains(c))	// not needed, add() does check for existing instance
			collisions.add(c);
		}
	}
	
	//---------------------------------------------------------------------------
	
	/**
	 *  This is the core of the algorithm: The set of collisions (stored in map) 
	 *  is used to merge connected regions. Transitivity of collisions makes this 
	 *  a nontrivial task. The algorithm used here is a basic "Connected-Components 
	 *  Algorithm" as used for finding connected parts in undirected graphs 
	 *  (e.g. see Corman, Leiserson, Rivest: "Introduction to Algorithms", MIT Press, 
	 *  1995, p. 441). Here, the regions represent the nodes of the graph and the 
	 *  collisions are equivalent to the edges of the graph. The implementation is 
	 *  not particularly efficient, since the merging of sets is done by relabeling 
	 *  the entire replacement table for each pair of nodes. Still fast enough even 
	 *  for large and complex images.
	 *  
	 *  @param size size of the label set
	 *  @return replacement table
	 */
	private int[] resolveCollisions() {
		final int N = getMaxLabel() + 1;
		// R[k] is the index of the set in which element k is contained:
		int[] R = new int[N];

		// Initially, each element i is in its own (one-element) set:
		for (int i = 0; i < N; i++) {
			R[i] = i;
		}

		// Inspect all collisions <a,b> one by one (note that a < b):
		for (LabelCollision c : collisions) {
			int A = R[c.a];	// set Ra contains label a
			int B = R[c.b]; 	// set Rb contains label b
			// Merge sets Ra and Rb (unless they are the same) by
			// moving all elements of set Rb into set Ra:
			if (A != B) {	// a,b are in different sets
				for (int i = 0; i < N; i++) {
					if (R[i] == B) {
						R[i] = A;
					}
				}
			}
			// otherwise a,b are already in the same set (i.e., known to be equivalent)
		}
		
		cleanup(R);
		return R;
	}

	private void cleanup(int[] table) {
		if (table.length <= getMinLabel()) {	// case of empty image, nothing to clean
			//return table; 
			return; 
		}
		// Assume the replacement table looks the following:
		// table = [0 1 4 4 4 6 6 8 3 3 ]
		//     i =  0 1 2 3 4 5 6 7 8 9
		// meaning that label 2 should be replaced by 4 etc.
		
		// First, we figure out which of the original labels
		// are still used. For this we use an intermediate array "mark":
		int[] mark = new int[table.length];	// initialized to 0
		for (int i = 0; i < table.length; i++) {
			int k = table[i];
			if (k < 0 || k >= table.length) {
				throw new RuntimeException("illegal segmentation label: " + k);
			}
			mark[k] = 1;	// mark label k as being used
		}
		// Result:
		// mark = [1 1 0 1 1 0 1 0 1 0 ]
		//    i =  0 1 2 3 4 5 6 7 8 9
		
		// Now we assign new, contiguous labels in mark array:
		int newLabel = getMinLabel();
		for (int i = 0; i < getMinLabel(); i++) {
			mark[i] = i;	// keep labels 0,..., minLabel-1
		}
		for (int i = getMinLabel(); i < table.length; i++) {
			if (mark[i] > 0) {	// this one's marked
				mark[i] = newLabel;
				newLabel = newLabel + 1;
			}
		}
		// Result:
		// mark = [0 1 0 2 3 0 4 0 5 0 ]
		//    i =  0 1 2 3 4 5 6 7 8 9
		
		// Update the actual replacement table to reflect the new labels:
		for (int i = 0; i < table.length; i++) {
			table[i] = mark[table[i]];
		}
        // table = [0 1 4 4 4 6 6 8 3 3 ]
        //              |             |
        //              V             V
		// table = [0 1 3 3 3 4 4 5 2 2 ]
		//     i =  0 1 2 3 4 5 6 7 8 9
		
		//return table;
	}

	// Replace image labels in labelArray
	private void relabelImage(int[] replacementTable){
		for (int v = 0; v < height; v++) {
			for (int u = 0; u < width; u++) {
				int oldLb = getLabel(u, v);
				setLabel(u, v, replacementTable[oldLb]);
			}
		}
	}
	
	/**
	 * This class represents a collision between two pixel labels a, b
	 */
	private class LabelCollision { 
		private final int a, b;

		LabelCollision(int a, int b) {
			this.a = a;
			this.b = b;
		}

		@Override
		public int hashCode() {
			return (17 + a) * 37 + b;	
		}

		@Override
		public boolean equals(Object other) {
			if (other instanceof LabelCollision) {
				LabelCollision coll = (LabelCollision) other;
				return (this.a == coll.a && this.b == coll.b);
			} 
			else {
				return false;
			}
		}
	}

}


