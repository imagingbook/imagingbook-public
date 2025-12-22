/**
 * Library code related to spectral image processing (Fourier
 * transforms etc.), separated from the main {@literal imagingbook} library to
 * minimize third-party dependencies.
 */
module imagingbook.spectral {
	requires imagingbook.common;
    requires JTransforms;
    exports imagingbook.spectral.dft;
	exports imagingbook.spectral.dct;
}