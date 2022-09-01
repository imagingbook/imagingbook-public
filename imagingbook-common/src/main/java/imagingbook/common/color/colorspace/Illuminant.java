package imagingbook.common.color.colorspace;

public interface Illuminant {
	
	/**
	 * Returns the illuminant's coordinate in CIE-XYZ color space.
	 * @return the XYZ-coordinate
	 */
	public double[] getXYZ();

}
