module imagingbook.plugins_demos {
	exports ImageJ_Demos;
	exports ImageAccessorDemos;
	exports GenericFilterDemos;

	requires ij;
	requires imagingbook.common;
	requires imagingbook.core;
	requires imagingbook.sampleimages;
	requires java.desktop;
}