/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.color.quantize;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.color.RgbUtils;

/**
 * This class implements color quantization based on the octree method. It is a
 * re-factored version of an implementation (OctTreeOpImage.java) used in Sun's JAI 
 * (Java Advanced Imaging) framework, which &ndash; in turn &ndash; is based on a 
 * C implementation (quantize.c) authored by John Cristy in 1992 as part of 
 * <a href="http://www.imagemagick.org/">ImageMagick</a>.
 * The associated source code can be found 
 * <a href="http://git.imagemagick.org/repos/ImageMagick/blob/master/MagickCore/quantize.c">
 * here</a>, the original copyright note is provided at the bottom of this source file.
 * 
 * See also the abovementioned JAI implementation in
 * <a href="https://java.net/projects/jai-core/sources/svn/content/trunk/src/share/classes/com/sun/media/jai/opimage/OctTreeOpImage.java">
 * OctTreeOpImage.java</a>.
 * 
 * This implementation is similar but not identical to the original octree quantization algorithm proposed
 * by Gervautz and Purgathofer [M. Gervautz and W. Purgathofer. "A simple method for color
 * quantization: octree quantization", in A. Glassner (editor), Graphics Gems I, 
 * pp. 287–293. Academic Press, New York (1990)].
 * This implementation uses a modified strategy for pruning the octree for better efficiency.
 * 
 * "Quick quantization" means that original colors are mapped to their color index by simply
 * finding the containing octree node. Otherwise, the closest quantized color (in terms of 
 * Euclidean distance) is searched for, which is naturally slower.
 * 
 * @author WB
 * @version 2017/01/03
 */
public class OctreeQuantizer implements ColorQuantizer {

//	private final static int MAX_RGB = 255;
	private final static int MAX_NODES = 262144 - 1; // = 2^18 - 1, was 266817;
	private final static int MAX_TREE_DEPTH = 8;	// check, 7 enough?

	private final int maxColors;	// max. number of distinct colors after quantization
	
	@SuppressWarnings("unused")
	private final int nColors;		// final number of colors
	private final TreeNode root;	// root node of the tree
	
	private int depth;				// current depth of the tree
	private int nodeCnt = 0;		// counts the number of nodes in the tree
	private int colorCnt = 0; 		// counts the number of colors in the cube (used for temp. counting)

//	private final float[][] colormap;
	private boolean quickQuantization = false;
	
	
//	public static class Parameters {
//		/** Maximum number of quantized colors. */
//		public int maxColors = 16;
//		/** Enable/disable quick quantization */
//		public boolean quickQuantization = false;
//		
//		void check() {
//			if (maxColors < 2 || maxColors > 256) {
//				throw new IllegalArgumentException();
//			}
//		}
//	}

	// -------------------------------------------------------------------------
	
	public OctreeQuantizer(ColorProcessor ip, int K) {
		this((int[]) ip.getPixels(), K);
	}
	
	public OctreeQuantizer(int[] pixels, int K) {
		this.maxColors = K;
		this.root = new TreeNode(null, 0);
		this.depth = Math.min(Math.max(log2(maxColors) - 1, 2), MAX_TREE_DEPTH);// 2 <= depth <= maxTreeDepth
		
		int initColorCnt = addPixels(pixels);
		
		this.nColors = reduceTree(initColorCnt, pixels.length);
	}
	
//	public OctreeQuantizer(int[] pixels) {
//		this(pixels, new Parameters());
//	}
	
	public void setQuickQuantization(boolean quickQuantization) {
		this.quickQuantization = quickQuantization;
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
		
		node.totalRed += rgb[0];
		node.totalGrn += rgb[1];
		node.totalBlu += rgb[2];
	}

	/**
	 * reduceTree() repeatedly prunes the tree until the number of
	 * nodes with unique > 0 is less than or equal to the maximum
	 * number of colors allowed in the output image.
	 * When a node to be pruned has offspring, the pruning
	 * procedure invokes itself recursively in order to prune the
	 * tree from the leaves upward.  The statistics of the node
	 * being pruned are always added to the corresponding data in
	 * that node's parent.  This retains the pruned node's color
	 * characteristics for later averaging.
	 * 
	 * @param initColorCnt 
	 * 		The initial number of colors (leaves) in the octree.
	 * @param nSamples 
	 * 		The total number of color samples used for creating the tree.
	 * @return
	 */
	@SuppressWarnings("unused")
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
	 * Lists the octree nodes to System.out (intended for debugging only).
	 */
	public void listNodes() {
		root.listNodes();
	}

	// ------------------------------------------------------------------------------------
	
	/**
	 * Represents a node in the octree.
	 */
	private class TreeNode {
		final TreeNode parent;		// reference to the parent node (null for the root node)
		final TreeNode[] childs;	// references to child nodes
		final int id;			// branch index of this node at the parent node (0,...,7)
		final int level;		// level of this node within the tree (root has level 0)
		
		int nChilds = 0;	// number of child nodes
		int nPixels = 0;	// number of pixels represented by this node and all child nodes
		int nUnique = 0;	// number of pixels represented by this node but none of the children

		final int midRed;	// RGB color midpoint (center of this node in color space)
		final int midGrn;
		final int midBlu;
		
		int totalRed = 0;	// sum of all pixel component values represented by this node
		int totalGrn = 0;
		int totalBlu = 0;
		
		int colorIdx = -1; 	// the index of the associated color in the final color table

		TreeNode(TreeNode parent, int id) {
			this.parent = parent;
			this.id = id;
			this.childs = new TreeNode[8];
			nodeCnt++;

			if (parent == null) {	// this is the root node
				this.level = 0;
				// set this node's midpoint
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
		void pruneTree() {
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
		 * Removes any nodes that hold fewer than 'minPixelCount' pixels
		 * and finds the minimum population of all remaining nodes. 
		 * 
		 * @param minPixelCount The minimum population required for surviving nodes.
		 * @param newMinPixelCnt The minimum population found so far (during recursive tree walk).
		 * @return
		 */
		int reduceSparseNodes(int minPixelCount, int newMinPixelCnt) {
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
		void delete() {
			parent.nChilds--;
			parent.nUnique += nUnique;
			parent.totalRed += totalRed;
			parent.totalGrn += totalGrn;
			parent.totalBlu += totalBlu;
			parent.childs[id] = null;	// unlink
			nodeCnt--;
		}
		
		/**
		 * Calculates the branch index [0,...,7] out of this node
		 * for the specified color.
		 * @param red
		 * @param grn
		 * @param blu
		 * @return The branch index [0,...,7].
		 */
		int getChildId(int[] rgb) {
			int idx = 0;
			if (rgb[0] > this.midRed) idx = idx | 0X01;
			if (rgb[1] > this.midGrn) idx = idx | 0X02;
			if (rgb[2] > this.midBlu) idx = idx | 0X04;
			return idx;
		}
		
//		int getChildId(int[] rgb) {
//			int idx = ((rgb[0] > this.midRed ? 1 : 0) << 0) 
//					| ((rgb[1] > this.midGrn ? 1 : 0) << 1)
//					| ((rgb[2] > this.midBlu ? 1 : 0) << 2);
//			return idx;
//		}
		
		/**
		 * Collects the color entries for the color map. Any node
		 * with a non-zero number of unique colors creates a color map
		 * entry. The representative color for the node is calculated
		 * as the average color vector over all contributing pixels.
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
//			System.out.println("collectColors " + this.id + " unique=" + this.nUnique);
			if (nUnique > 0) {			
				float avgRed = (float) totalRed / nUnique;
				float avgGrn = (float) totalGrn / nUnique;
				float avgBlu = (float) totalBlu / nUnique;
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
//			System.out.format(makeBlanks(level) + "node level=%d unique=%d total=%d: %3d %3d %3d, \n", 
//					level, nUnique, this.nPixels, 0xFF & midRed, 0xFF & midGrn, 0xFF & midBlu); 
		}
		
		@SuppressWarnings("unused")
		private String makeBlanks(int level) {
			char[] blanks = new char[3 * level];
			Arrays.fill(blanks, '-');
			return new String(blanks);
		}
	}
	
	// ------- methods required by abstract super class -----------------------
	
	@Override
	public float[][] getColorMap() {
		return makeColorMap();
	}
	
	@Override
	public int findColorIndex(int p, float[][] colormap) {
		if (this.quickQuantization) {
			return getNodeIndex(p);
		}
		else {
			return ColorQuantizer.super.findColorIndex(p, colormap);
		}
	}

	/**
	 * Finds the associated color table index for the supplied RGB color
	 * by traversing the octree. 
	 * @param p
	 * @return
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
		if(n <= 0) throw new IllegalArgumentException();
		return 31 - Integer.numberOfLeadingZeros(n);
	}
	
	// -----------------------------------------------------------------------------------

	public static void main(String[] args) {
//		String path = "D:/svn-book/Book/img/ch-color-images/alps-01s.png";
		String path = "D:/svn-book/Book/img/ch-color-images/desaturation-hsv/balls.jpg";
//		String path = "D:/svn-book/Book/img/ch-color-images/single-color.png";
//		String path = "D:/svn-book/Book/img/ch-color-images/two-colors.png";
//		String path = "D:/svn-book/Book/img/ch-color-images/random-colors.png";
//		String path = "D:/svn-book/Book/img/ch-color-images/ramp-fire.png";
		
		int K = 16; 
		System.out.println("image = " + path);
		System.out.println("K = " + K);

		ImagePlus im = IJ.openImage(path);
		if (im == null) {
			System.out.println("could not open: " + path);
			return;
		}
		
		ImageProcessor ip = im.getProcessor();
		ColorProcessor cp = ip.convertToColorProcessor();
		
		// MedianCutQuantizer quantizer = new MedianCutQuantizer(cp, K);

		OctreeQuantizer quantizer = new OctreeQuantizer(cp, K);
		quantizer.setQuickQuantization(false);
	
		quantizer.listColorMap();
		
		System.out.println("quantizing image");
		ByteProcessor qi = quantizer.quantize(cp);
		System.out.println("showing image");
		(new ImagePlus("quantizez", qi)).show();
		
	}
}

/*
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                                             %
%                                                                             %
%                                                                             %
%           QQQ   U   U   AAA   N   N  TTTTT  IIIII   ZZZZZ  EEEEE            %
%          Q   Q  U   U  A   A  NN  N    T      I        ZZ  E                %
%          Q   Q  U   U  AAAAA  N N N    T      I      ZZZ   EEEEE            %
%          Q  QQ  U   U  A   A  N  NN    T      I     ZZ     E                %
%           QQQQ   UUU   A   A  N   N    T    IIIII   ZZZZZ  EEEEE            %
%                                                                             %
%                                                                             %
%              Reduce the Number of Unique Colors in an Image                 %
%                                                                             %
%                                                                             %
%                           Software Design                                   %
%                             John Cristy                                     %
%                              July 1992                                      %
%                                                                             %
%                                                                             %
%  Copyright 1998 E. I. du Pont de Nemours and Company                        %
%                                                                             %
%  Permission is hereby granted, free of charge, to any person obtaining a    %
%  copy of this software and associated documentation files ("ImageMagick"),  %
%  to deal in ImageMagick without restriction, including without limitation   %
%  the rights to use, copy, modify, merge, publish, distribute, sublicense,   %
%  and/or sell copies of ImageMagick, and to permit persons to whom the       %
%  ImageMagick is furnished to do so, subject to the following conditions:    %
%                                                                             %
%  The above copyright notice and this permission notice shall be included in %
%  all copies or substantial portions of ImageMagick.                         %
%                                                                             %
%  The software is provided "as is", without warranty of any kind, express or %
%  implied, including but not limited to the warranties of merchantability,   %
%  fitness for a particular purpose and noninfringement.  In no event shall   %
%  E. I. du Pont de Nemours and Company be liable for any claim, damages or   %
%  other liability, whether in an action of contract, tort or otherwise,      %
%  arising from, out of or in connection with ImageMagick or the use or other %
%  dealings in ImageMagick.                                                   %
%                                                                             %
%  Except as contained in this notice, the name of the E. I. du Pont de       %
%  Nemours and Company shall not be used in advertising or otherwise to       %
%  promote the sale, use or other dealings in ImageMagick without prior       %
%  written authorization from the E. I. du Pont de Nemours and Company.       %
%                                                                             %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
%  Realism in computer graphics typically requires using 24 bits/pixel to
%  generate an image. Yet many graphic display devices do not contain
%  the amount of memory necessary to match the spatial and color
%  resolution of the human eye. The QUANTIZE program takes a 24 bit
%  image and reduces the number of colors so it can be displayed on
%  raster device with less bits per pixel. In most instances, the
%  quantized image closely resembles the original reference image.
%
%  A reduction of colors in an image is also desirable for image
%  transmission and real-time animation.
%
%  Function Quantize takes a standard RGB or monochrome images and quantizes
%  them down to some fixed number of colors.
%
%  For purposes of color allocation, an image is a set of n pixels, where
%  each pixel is a point in RGB space. RGB space is a 3-dimensional
%  vector space, and each pixel, pi, is defined by an ordered triple of
%  red, green, and blue coordinates, (ri, gi, bi).
%
%  Each primary color component (red, green, or blue) represents an
%  intensity which varies linearly from 0 to a maximum value, cmax, which
%  corresponds to full saturation of that color. Color allocation is
%  defined over a domain consisting of the cube in RGB space with
%  opposite vertices at (0,0,0) and (cmax,cmax,cmax). QUANTIZE requires
%  cmax = 255.
%
%  The algorithm maps this domain onto a tree in which each node
%  represents a cube within that domain. In the following discussion
%  these cubes are defined by the coordinate of two opposite vertices:
%  The vertex nearest the origin in RGB space and the vertex farthest
%  from the origin.
%
%  The tree's root node represents the the entire domain, (0,0,0) through
%  (cmax,cmax,cmax). Each lower level in the tree is generated by
%  subdividing one node's cube into eight smaller cubes of equal size.
%  This corresponds to bisecting the parent cube with planes passing
%  through the midpoints of each edge.
%
%  The basic algorithm operates in three phases: Classification,
%  Reduction, and Assignment. Classification builds a color
%  description tree for the image. Reduction collapses the tree until
%  the number it represents, at most, the number of colors desired in the
%  output image. Assignment defines the output image's color map and
%  sets each pixel's color by reclassification in the reduced tree.
%  Our goal is to minimize the numerical discrepancies between the original
%  colors and quantized colors (quantization error).
%
%  Classification begins by initializing a color description tree of
%  sufficient depth to represent each possible input color in a leaf.
%  However, it is impractical to generate a fully-formed color
%  description tree in the classification phase for realistic values of
%  cmax. If colors components in the input image are quantized to k-bit
%  precision, so that cmax= 2k-1, the tree would need k levels below the
%  root node to allow representing each possible input color in a leaf.
%  This becomes prohibitive because the tree's total number of nodes is
%  1 + sum(i=1,k,8k).
%
%  A complete tree would require 19,173,961 nodes for k = 8, cmax = 255.
%  Therefore, to avoid building a fully populated tree, QUANTIZE: (1)
%  Initializes data structures for nodes only as they are needed;  (2)
%  Chooses a maximum depth for the tree as a function of the desired
%  number of colors in the output image (currently log2(colormap size)).
%
%  For each pixel in the input image, classification scans downward from
%  the root of the color description tree. At each level of the tree it
%  identifies the single node which represents a cube in RGB space
%  containing the pixel's color. It updates the following data for each
%  such node:
%
%    n1: Number of pixels whose color is contained in the RGB cube
%    which this node represents;
%
%    n2: Number of pixels whose color is not represented in a node at
%    lower depth in the tree;  initially,  n2 = 0 for all nodes except
%    leaves of the tree.
%
%    Sr, Sg, Sb: Sums of the red, green, and blue component values for
%    all pixels not classified at a lower depth. The combination of
%    these sums and n2  will ultimately characterize the mean color of a
%    set of pixels represented by this node.
%
%    E: The distance squared in RGB space between each pixel contained
%    within a node and the nodes' center. This represents the quantization
%    error for a node.
%
%  Reduction repeatedly prunes the tree until the number of nodes with
%  n2 > 0 is less than or equal to the maximum number of colors allowed
%  in the output image. On any given iteration over the tree, it selects
%  those nodes whose E count is minimal for pruning and merges their
%  color statistics upward. It uses a pruning threshold, Ep, to govern
%  node selection as follows:
%
%    Ep = 0
%    while number of nodes with (n2 > 0) > required maximum number of colors
%      prune all nodes such that E <= Ep
%      Set Ep to minimum E in remaining nodes
%
%  This has the effect of minimizing any quantization error when merging
%  two nodes together.
%
%  When a node to be pruned has offspring, the pruning procedure invokes
%  itself recursively in order to prune the tree from the leaves upward.
%  n2,  Sr, Sg,  and  Sb in a node being pruned are always added to the
%  corresponding data in that node's parent. This retains the pruned
%  node's color characteristics for later averaging.
%
%  For each node, n2 pixels exist for which that node represents the
%  smallest volume in RGB space containing those pixel's colors. When n2
%  > 0 the node will uniquely define a color in the output image. At the
%  beginning of reduction,  n2 = 0  for all nodes except a the leaves of
%  the tree which represent colors present in the input image.
%
%  The other pixel count, n1, indicates the total number of colors
%  within the cubic volume which the node represents. This includes n1 -
%  n2  pixels whose colors should be defined by nodes at a lower level in
%  the tree.
%
%  Assignment generates the output image from the pruned tree. The
%  output image consists of two parts: (1)  A color map, which is an
%  array of color descriptions (RGB triples) for each color present in
%  the output image;  (2)  A pixel array, which represents each pixel as
%  an index into the color map array.
%
%  First, the assignment phase makes one pass over the pruned color
%  description tree to establish the image's color map. For each node
%  with n2  > 0, it divides Sr, Sg, and Sb by n2 . This produces the
%  mean color of all pixels that classify no lower than this node. Each
%  of these colors becomes an entry in the color map.
%
%  Finally,  the assignment phase reclassifies each pixel in the pruned
%  tree to identify the deepest node containing the pixel's color. The
%  pixel's value in the pixel array becomes the index of this node's mean
%  color in the color map.
%
%  With the permission of USC Information Sciences Institute, 4676 Admiralty
%  Way, Marina del Rey, California  90292, this code was adapted from module
%  ALCOLS written by Paul Raveling.
%
%  The names of ISI and USC are not used in advertising or publicity
%  pertaining to distribution of the software without prior specific
%  written permission from ISI.
%
*/
