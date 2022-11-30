/**
 * Defines utility methods for JUnit testing (intended to be used
 * only in test scope).
 */
module imagingbook.testing {
	exports imagingbook.testutils;

	requires transitive ij;
	requires transitive imagingbook.core;
	requires java.desktop;
	requires junit;
}