/**
 * Contains a set of general sample images (as named resources)
 * used for demonstrations and testing.
 */
module imagingbook.sampleimages {
	exports imagingbook.sampleimages;
    exports imagingbook.sampleimages.kimia;

    requires imagingbook.core;
	requires transitive ij;
	requires java.desktop;
}