module imagingbook.core {
	exports imagingbook.core.resource;
	exports imagingbook.core.plugin;
	exports imagingbook.core;

	requires transitive ij;
	requires java.desktop;
}