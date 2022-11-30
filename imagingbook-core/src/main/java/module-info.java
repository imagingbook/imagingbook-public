/**
 * Provides minimal infrastructure required for building other imagingbook library
 * packages. This includes code for handling resources and automatically compiling
 * plugins.config files for ImageJ plugin sets.
 */
module imagingbook.core {
	exports imagingbook.core.resource;
	exports imagingbook.core.plugin;
	exports imagingbook.core;

	requires transitive ij;
	requires java.desktop;
}