package imagingbook.common.color.colorspace;

import java.awt.color.ColorSpace;

import imagingbook.common.math.Matrix;

@SuppressWarnings("serial")
public abstract class CustomColorSpace extends ColorSpace {
	
	protected CustomColorSpace(int type, int numcomponents) {
		super(type, numcomponents);
	}

	// additional ColorSpace methods using double values only
	public abstract double[] toCIEXYZ(double[] value);
	public abstract double[] fromCIEXYZ(double[] value);
	public abstract double[] toRGB(double[] value);
	public abstract double[] fromRGB(double[] value);
	
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

}
