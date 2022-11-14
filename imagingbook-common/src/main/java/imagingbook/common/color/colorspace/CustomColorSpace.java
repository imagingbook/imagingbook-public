package imagingbook.common.color.colorspace;

import java.awt.color.ColorSpace;

import imagingbook.common.math.Matrix;

@SuppressWarnings("serial")
public abstract class CustomColorSpace extends ColorSpace {
	
	protected CustomColorSpace(int type, int numcomponents) {
		super(type, numcomponents);
	}

	// additional ColorSpace methods using double values only
	/**
	 * Double version of {@link #toCIEXYZ(float[]).
	 * @param value color value in this color space
	 * @return D50-based XYZ color value
	 */
	public abstract double[] toCIEXYZ(double[] value);
	
	/**
	 * Double version of {@link #fromCIEXYZ(float[]).
	 * @param value D50-based XYZ color value
	 * @return color value in this color space
	 */
	public abstract double[] fromCIEXYZ(double[] value);
	
	/**
	 * Double version of {@link #toRGB(float[]).
	 * @param value color value in this color space
	 * @return sRGB color value
	 */
	public abstract double[] toRGB(double[] value);
	
	/**
	 * Double version of {@link #fromRGB(float[]).
	 * @param value sRGB color value
	 * @return color value in this color space
	 */
	public abstract double[] fromRGB(double[] value);
	
	// -----------------------------------------------------------------
	
	@Override
	public float[] toCIEXYZ(float[] value) {
		return Matrix.toFloat(toCIEXYZ(Matrix.toDouble(value)));
	}
	
	@Override
	public float[] fromCIEXYZ(float[] value) {
		return Matrix.toFloat(fromCIEXYZ(Matrix.toDouble(value)));
	}
	
	@Override
	public float[] toRGB(float[] value) {
		return Matrix.toFloat(toRGB(Matrix.toDouble(value)));
	}
	
	@Override
	public float[] fromRGB(float[] value) {
		return Matrix.toFloat(fromRGB(Matrix.toDouble(value)));
	}
	
	// -----------------------------------------------------------------
	
	/**
	 * Analogous to {@link #fromCIEXYZ(double[])} but accepts
	 * D65-based instead of D50-based PCS color values.
	 * 
	 * @param xyz65 D65-based XYZ color values
	 * @return color values in this color space
	 */
	public double[] fromCIEXYZ65(double[] xyz65) {
		throw new UnsupportedOperationException();
	};
	
	/**
	 * Analogous to {@link #toCIEXYZ(double[])} but returns
	 * D65-based instead of D50-based PCS color values.
	 * @param value color value in this color space
	 * @return D65-based XYZ color values
	 */
	public double[] toCIEXYZ65(double[] value) {
		throw new UnsupportedOperationException();
	}

}
