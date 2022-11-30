/**
 * A collection of ImageJ plugins referenced in various chapters of the
 * associated image processing books as well as additional demos and tools.
 */
module imagingbook.plugins_book {
	exports Ch02_Histograms_Statistics;
	exports Ch03_PointOperations;
	exports Ch04_Filters;
	exports Ch05_Edges_Contours;
	exports Ch06_CornerDetection;
	exports Ch07_MorphologicalFilters;
	exports Ch08_BinaryRegions;
	exports Ch09_AutomaticThresholding;
	exports Ch10_LineFitting;
	exports Ch11_Circles_Ellipses;
	exports Ch12_Ransac_Hough.settings;
	exports Ch12_Ransac_Hough;
	exports Ch13_ColorImages;
	exports Ch14_ColorimetricColor;
	exports Ch15_ColorFilters;
	exports Ch16_ColorEdges;
	exports Ch17_EdgePreservingSmoothing;
	exports Ch19_DiscreteFourierTransform;
	exports Ch20_DiscreteCosineTransform;
	exports Ch21_GeometricOperations;
	exports Ch22_PixelInterpolation;
	exports Ch23_ImageMatching;
	exports Ch24_NonRigidMatching;
	exports Ch25_SIFT;
	exports Ch26_MSER;
	exports More_;
	exports Tools_;

	requires commons.math3;
	requires transitive ij;
	requires transitive imagingbook.common;
	requires imagingbook.core;
	requires imagingbook.sampleimages;
	requires imagingbook.spectral;
	requires transitive java.desktop;
	requires imagingbook.pdf;
	requires imagingbook.bookimages;
}