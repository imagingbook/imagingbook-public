package imagingbook.common.color.colorspace;

import java.awt.color.ColorSpace;

import imagingbook.common.math.Matrix;

public abstract class CustomColorSpace extends ColorSpace {
	static final long serialVersionUID = 1L;
	
	protected CustomColorSpace(int type, int numcomponents) {
		super(type, numcomponents);
	}

	// new ColorSpace methods using double values only
	public abstract double[] toCIEXYZ(double[] value);
	public abstract double[] fromCIEXYZ(double[] value);
	public abstract double[] toRGB(double[] value);
	public abstract double[] fromRGB(double[] value);
	
	public float[] toCIEXYZ(float[] value) {
		return Matrix.toFloat(toCIEXYZ(Matrix.toDouble(value)));
	}
	
	public float[] fromCIEXYZ(float[] value) {
		return Matrix.toFloat(fromCIEXYZ(Matrix.toDouble(value)));
	}
	
	public float[] toRGB(float[] value) {
		return Matrix.toFloat(toRGB(Matrix.toDouble(value)));
	}
	
	public float[] fromRGB(float[] value) {
		return Matrix.toFloat(fromRGB(Matrix.toDouble(value)));
	}

}
