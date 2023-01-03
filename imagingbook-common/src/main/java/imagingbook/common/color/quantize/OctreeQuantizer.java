/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.common.color.quantize;

import imagingbook.common.color.RgbUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * <p>
 * This class implements color quantization based on the octree method. It is a re-factored version of an implementation
 * ({@code OctTreeOpImage.java}) used in Sun's JAI (Java Advanced Imaging) framework, which &ndash; in turn &ndash; is
 * based on a C implementation ({@code quantize.c}) by John Cristy in 1992 as part of <a
 * href="http://www.imagemagick.org/">ImageMagick</a>. The associated source code can be found <a href=
 * "https://github.com/ImageMagick/ImageMagick/blob/main/MagickCore/quantize.c"> here</a>, the original license note is
 * provided at the bottom of this source file. This implementation is similar but not identical to the original octree
 * quantization algorithm described in [1].
 * </p>
 * <p>
 * This implementation uses a modified strategy for pruning the octree for better efficiency. "Quick quantization" means
 * that original colors are mapped to their color index by simply finding the containing octree node. Otherwise, the
 * closest quantized color (in terms of Euclidean distance) is searched for, which is naturally slower. See Sec. 13.4 of
 * [2] for more details.
 * </p>
 * <p>
 * [1] M. Gervautz and W. Purgathofer, "A simple method for color quantization: octree quantization", in A. Glassner
 * (editor), Graphics Gems I, pp. 287–293. Academic Press, New York (1990).<br> [2] W. Burger, M.J. Burge, <em>Digital
 * Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 *
 * @author WB
 * @version 2022/11/05
 */
public class OctreeQuantizer implements ColorQuantizer {

//	private final static int MAX_RGB = 255;
	private final static int MAX_NODES = 262144 - 1; // = 2^18 - 1, was 266817;
	private final static int MAX_TREE_DEPTH = 8;	// check, 7 enough?

	private final int maxColors;	// max. number of distinct colors after quantization
	private final TreeNode root;	// root node of the tree
	private final float[][] colorMap;
	private final boolean quickQuantization;
	@SuppressWarnings("unused")
	private final int nColors;		// final number of colors
	
	private int depth;				// current depth of the tree
	private int nodeCnt = 0;		// counts the number of nodes in the tree
	private int colorCnt = 0; 		// counts the number of colors in the cube (used for temp. counting)

	// -------------------------------------------------------------------------

	/**
	 * Constructor, creates a new {@link OctreeQuantizer} with up to K colors, but never more than the number of colors
	 * found in the supplied image. Quick quantization is turned off by default.
	 *
	 * @param pixels an image as a aRGB-encoded int array
	 * @param K the desired number of colors (1 or more)
	 */
	public OctreeQuantizer(int[] pixels, int K) {
		this(pixels, K, false);
	}

	/**
	 * Constructor, creates a new {@link OctreeQuantizer} with up to K colors, but never more than the number of colors
	 * found in the supplied image. "Quick" quantization can be selected, which means that original colors are mapped to
	 * their color index by simply finding the containing octree node. Otherwise, the closest quantized color (in terms
	 * of Euclidean distance) is searched for, which is naturally slower but usually gives better results. This setting
	 * only affects the final assignment of input colors to the nearest reference colors, the reference colors
	 * themselves are not changed.
	 *
	 * @param pixels an image as a aRGB-encoded int array
	 * @param K the desired number of colors (1 or more)
	 * @param quick turns "quick quantization" on or off
	 */
	public OctreeQuantizer(int[] pixels, int K, boolean quick) {
		this.maxColors = K;
		this.root = new TreeNode(null, 0);
		this.depth = Math.min(Math.max(log2(maxColors) - 1, 2), MAX_TREE_DEPTH);// 2 <= depth <= maxTreeDepth		
		int initColorCnt = addPixels(pixels);
		this.nColors = reduceTree(initColorCnt, pixels.length);
		this.colorMap = makeColorMap();
		this.quickQuantization = quick;
	}

	
	// -------------------------------------------------------------------------
	
	private int addPixels(int[] pixels) {
		for (int p : pixels) {
			addPixel(p);
			if (nodeCnt > MAX_NODES) { // a hard limit on the number of nodes in the tree
				root.pruneTree();
				depth--;
			}	
		}
		return colorCnt;
	}

	// walk the tree to depth, increasing the 'npixels' count in each node
	private void addPixel(int p) {
		TreeNode node = root;
		node.nPixels++;
		int[] rgb = RgbUtils.intToRgb(p);
		for (int level = 1; level <= depth; level++) {
			int id = node.getChildId(rgb);
			if (node.childs[id] == null) {
				node = new TreeNode(node, id);	// create a new node at next level
			} 
			else {	// step to next level
				node = node.childs[id];
			}
			node.nPixels++;
		}

		// at level 'depth': update color statistics of this node
		node.nUnique++;
		
		node.totalR += rgb[0];
		node.totalG += rgb[1];
		node.totalB += rgb[2];
	}

	/**
	 * Repeatedly prunes the tree until the number of nodes with unique &gt; 0 is less than or equal to the maximum
	 * number of colors allowed in the output image. When a node to be pruned has offspring, the pruning procedure
	 * invokes itself recursively in order to prune the tree from the leaves upward. The statistics of the node being
	 * pruned are always added to the corresponding data in that node's parent. This retains the pruned node's color
	 * characteristics for later averaging.
	 *
	 * @param initColorCnt The initial number of colors (leaves) in the octree.
	 * @param nSamples The total number of color samples used for creating the tree.
	 * @return the number of colors
	 */
	private int reduceTree(int initColorCnt, int nSamples) {
		int minPixelCnt = Math.max(1,  nSamples / (maxColors * 8));
		colorCnt = initColorCnt;
		while (colorCnt > maxColors) {
			colorCnt = 0;	// for recounting the number of colors (leaves)
			minPixelCnt = root.reduceSparseNodes(minPixelCnt, Integer.MAX_VALUE);
		}
		return colorCnt;
	}

	private float[][] makeColorMap() {
		List<float[]> colList = new LinkedList<>();
		colorCnt = 0;	// used to store the color index in each node
		root.collectColors(colList);
		return colList.toArray(new float[0][]);
	}
	
	/**
	 * Lists the octree nodes to System.out (for debugging only).
	 */
	public void listNodes() {
		root.listNodes();
	}

	// ------------------------------------------------------------------------------------
	
	/**
	 * Represents a node of the octree.
	 */
	private class TreeNode {
		private final TreeNode parent;		// reference to the parent node (null for the root node)
		private final TreeNode[] childs;	// references to child nodes
		private final int id;				// branch index of this node at the parent node (0,...,7)
		private final int level;			// level of this node within the tree (root has level 0)

		private final int midRed;			// RGB color midpoint (center of this node in color space)
		private final int midGrn;
		private final int midBlu;
		
		private int nChilds = 0;			// number of child nodes
		private int nPixels = 0;			// number of pixels represented by this node and all child nodes
		private int nUnique = 0;			// number of pixels represented by this node but none of the children
		
		private int totalR = 0;				// sum of all pixel component values represented by this node
		private int totalG = 0;
		private int totalB = 0;
		
		int colorIdx = -1; 					// the index of the associated color in the final color table

		private TreeNode(TreeNode parent, int id) {
			this.parent = parent;
			this.id = id;
			this.childs = new TreeNode[8];
			nodeCnt++;

			if (parent == null) {	// this is the root node
				this.level = 0;
				// set this node's center
				int mid = (MAX_RGB + 1) / 2;
				midRed = mid;
				midGrn = mid;
				midBlu = mid;
			}
			else {
				this.level = parent.level + 1;
				// attach this node to its parent
				parent.nChilds++;
				parent.childs[id] = this;
				// set this node's midpoint
				//int bi = (1 << (MAX_TREE_DEPTH - level)) >> 1;
				int bi = 256 >> (level + 1);
				midRed = parent.midRed + ((id & 1) > 0 ? bi : -bi);
				midGrn = parent.midGrn + ((id & 2) > 0 ? bi : -bi);
				midBlu = parent.midBlu + ((id & 4) > 0 ? bi : -bi);
				if (level == depth) {	// this is a leaf node
					colorCnt++;
				}
			}
		}

		/**
		 * Prune all nodes at the lowest level (= depth) of the tree.
		 */
		private void pruneTree() {
			if (nChilds != 0) {	
				// move recursively to the lowest level
				for (int i = 0; i < childs.length; i++) {
					if (childs[i] != null) {
						childs[i].pruneTree();
					}
				}
			}
			// work on the actual node now
			if (level == depth) {	// we are at a node on the lowest level
				delete();			// remove THIS node!
			}
		}

		/**
		 * Removes any nodes that hold fewer than 'minPixelCount' pixels and finds the minimum population of all
		 * remaining nodes.
		 *
		 * @param minPixelCount The minimum population required for surviving nodes.
		 * @param newMinPixelCnt The minimum population found so far (during recursive tree walk).
		 * @return
		 */
		private int reduceSparseNodes(int minPixelCount, int newMinPixelCnt) {
			// visit all child nodes first
			if (nChilds > 0) {
				for (TreeNode ch : childs) {
					if (ch != null) {
						newMinPixelCnt = ch.reduceSparseNodes(minPixelCount, newMinPixelCnt);
					}
				}
			}
			// work on the actual node now
			if (nPixels <= minPixelCount) {
				this.delete();
			} 
			else {
				if (nUnique > 0) {
					colorCnt++;
				}
				newMinPixelCnt = Math.min(nPixels, newMinPixelCnt); // find smallest population
			}
			return newMinPixelCnt;
		}
		
		/**
		 * Remove this node, and push all pixel statistics to its parent.
		 */
		private void delete() {
			parent.nChilds--;
			parent.nUnique += nUnique;
			parent.totalR += totalR;
			parent.totalG += totalG;
			parent.totalB += totalB;
			parent.childs[id] = null;	// unlink
			nodeCnt--;
		}

		/**
		 * Calculates the branch index [0,...,7] out of this node for the specified color.
		 */
		private int getChildId(int[] rgb) {
			int idx = 0;
			if (rgb[0] > this.midRed) idx = idx | 0X01;
			if (rgb[1] > this.midGrn) idx = idx | 0X02;
			if (rgb[2] > this.midBlu) idx = idx | 0X04;
			return idx;
		}

		/**
		 * Collects the color entries for the color map. Any node with a non-zero number of unique colors creates a
		 * color map entry. The representative color for the node is calculated as the average color vector over all
		 * contributing pixels.
		 *
		 * @param colList List of colors to add to.
		 */
		private void collectColors(List<float[]> colList) {
			// visit all children first
			if (nChilds > 0) {
				for (TreeNode ch : childs) {
					if (ch != null) {
						ch.collectColors(colList);
					}
				}
			}		
			// process this node
			if (nUnique > 0) {			
				float avgRed = (float) totalR / nUnique;
				float avgGrn = (float) totalG / nUnique;
				float avgBlu = (float) totalB / nUnique;
				colList.add(new float[] {avgRed, avgGrn, avgBlu});
				this.colorIdx = colorCnt;	// store the color table index for this node
				colorCnt++;
			}
		}

		private void listNodes() {
			if (nChilds > 0) {	
				for (TreeNode ch : childs) {
					if (ch != null) {
						ch.listNodes();
					}
				}
			}
			// bottom of recursion
		}
		
	}
	
	// ------- methods required by abstract super class -----------------------
	
	@Override
	public float[][] getColorMap() {
		return this.colorMap;
	}
	
	@Override
	public int findColorIndex(int p, float[][] colormap) {
		if (quickQuantization) {
			return getNodeIndex(p);
		}
		else {
			return ColorQuantizer.super.findColorIndex(p, colormap);
		}
	}

	/**
	 * Finds the associated color table index for the supplied RGB color by traversing the octree.
	 */
	private int getNodeIndex(int p) {
		int[] rgb = RgbUtils.intToRgb(p);
		TreeNode node = root;
		while(true) {
			int id = node.getChildId(rgb);	// which of the child nodes?
			if (node.childs[id] == null) {	// there is no finer-grained child node, so current 'node' is the one
				break;
			}
			node = node.childs[id];
		}
		// 'node' is associated with color p
		if (node.colorIdx < 0) {
			throw new RuntimeException("cannot assign color " + p);
		}
		return node.colorIdx;
	}
	
	private int log2(int n){
		if (n <= 0) throw new IllegalArgumentException();
		return 31 - Integer.numberOfLeadingZeros(n);
	}

}

