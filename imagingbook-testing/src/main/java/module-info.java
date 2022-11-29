module imagingbook.testing {
	exports imagingbook.testutils;

	requires transitive ij;
	requires transitive imagingbook.core;
	requires java.desktop;
	requires junit;
}