module imagingbook.plugins_book {
	exports Ch17_EdgePreservingSmoothing;
	exports Ch02_Histograms_Statistics;
	exports Ch13_ColorImages;
	exports Ch24_NonRigidMatching;
	exports Ch04_Filters;
	exports Ch10_LineFitting;
	exports Ch09_AutomaticThresholding;
	exports Ch15_ColorFilters;
	exports Ch14_ColorimetricColor;
	exports Ch11_Circles_Ellipses;
	exports Ch25_SIFT;
	exports Ch26_MSER;
	exports Ch05_Edges_Contours;
	exports Ch12_Ransac_Hough.settings;
	exports Ch12_Ransac_Hough;
	exports Ch07_MorphologicalFilters;
	exports Ch08_BinaryRegions;
	exports Ch20_DiscreteCosineTransform;
	exports Ch03_PointOperations;
	exports Ch19_DiscreteFourierTransform;
	exports Tools_;
	exports Ch16_ColorEdges;
	exports Ch22_PixelInterpolation;
	exports Ch23_ImageMatching;
	exports Ch06_CornerDetection;
	exports Ch21_GeometricOperations;
	exports More_;

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