
/**
 * PDF-related code, separated from the main imagingbook library to minimize
 * third-party dependencies.
 */
module imagingbook.pdf {
	exports imagingbook.pdf;

	requires transitive com.github.librepdf.openpdf;
	requires transitive ij;
	requires imagingbook.common;
	requires imagingbook.core;
	requires transitive java.desktop;
}