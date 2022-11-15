package imagingbook.common.color.colorspace;

/**
 * This interface specifies methods for retrieving XYZ coordinates of white
 * point and RGB primaries from the implementing color space.
 * 
 * @author WB
 * @version 2022/11/15
 */
public interface RgbPrimaries {

//	public double[][] getRgbToXyzMatrix();
//	public double[][] getXyzToRgbMatrix();
//	public double[] getWhitePoint();
//	public double[] getPrimary(int idx);
	
	// methods required by all implementations of ColorSpace:
	public float[] toCIEXYZ(float[] value);
	public float[] fromCIEXYZ(float[] value);
	public float[] toRGB(float[] value);
	public float[] fromRGB(float[] value);
	
	public default float[] getWhitePoint() {
		float[] rgb = {1, 1, 1};
		return this.toCIEXYZ(rgb);
	}
	
	public default float[] getPrimary(int idx) {
		float[] rgb = new float[3];
		rgb[idx] = 1;
		return this.toCIEXYZ(rgb);
	}
	
}
