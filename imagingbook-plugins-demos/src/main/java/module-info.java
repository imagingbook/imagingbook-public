/**
 * A collection of ImageJ plugins demonstrating various technical 
 * concepts of the imagingbook library.
 */
module imagingbook.plugins_demos {
	exports ImageJ_Demos;
	exports ImageAccessorDemos;
	exports GenericFilterDemos;

	requires transitive ij;
	requires imagingbook.common;
	requires imagingbook.core;
	requires imagingbook.sampleimages;
	requires java.desktop;
}