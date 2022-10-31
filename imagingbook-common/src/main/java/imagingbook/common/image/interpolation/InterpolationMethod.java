package imagingbook.common.image.interpolation;

/**
 * <p>
 * Enumeration type listing the most common interpolation methods.
 * See Ch. 22 of [1] for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>,
 * 3rd ed, Springer (2022).
 * </p>
 */
public enum InterpolationMethod {
	NearestNeighbor,
	Bilinear,
	Bicubic,
	BicubicSmooth,
	BicubicSharp,
	CatmullRom,
	CubicBSpline,
	MitchellNetravali,
	Lanzcos2,
	Lanzcos3,
	Lanzcos4;
}