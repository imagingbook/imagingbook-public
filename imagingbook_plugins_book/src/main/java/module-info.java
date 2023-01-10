import imagingbook.core.jdoc.JavaDocBaseUrl;

/**
 * A collection of ImageJ plugins referenced in various chapters of the associated image processing books as well as
 * additional demos and tools.
 */
module imagingbook_plugins_book {
	exports Ch02_Histograms_Statistics;
	exports Ch03_Point_Operations;
	exports Ch04_Filters;
	exports Ch05_Edges_Contours;
	exports Ch06_Corner_Detection;
	exports Ch07_Morphological_Filters;
	exports Ch08_Binary_Regions;
	exports Ch09_Automatic_Thresholding;
	exports Ch10_Line_Fitting;
	exports Ch11_Circle_Ellipse_Fitting;
	exports Ch12_Ransac_Hough.settings;
	exports Ch12_Ransac_Hough;
	exports Ch13_Color_Images;
	exports Ch14_Colorimetric_Color;
	exports Ch15_Color_Filters;
	exports Ch16_Color_Edges;
	exports Ch17_EdgePreserving_Smoothing;
	exports Ch19_Discrete_Fourier_Transform;
	exports Ch20_Discrete_Cosine_Transform;
	exports Ch21_Geometric_Operations;
	exports Ch22_Pixel_Interpolation;
	exports Ch23_Image_Matching;
	exports Ch24_NonRigid_Matching;
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
}