module imagingbook.plugins_extras {
	exports Delaunay_Triangulation;
	exports Fourier_Shape_Descriptors;
	exports Synthetic_Noise;

	requires transitive ij;
	requires imagingbook.common;
	requires imagingbook.core;
	requires java.desktop;
}