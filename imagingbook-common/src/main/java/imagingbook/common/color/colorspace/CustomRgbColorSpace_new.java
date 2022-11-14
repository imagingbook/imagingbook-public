package imagingbook.common.color.colorspace;

import imagingbook.common.math.Matrix;

public interface CustomRgbColorSpace_new {
	
	public float[] toCIEXYZ(float[] value) ;

//	public double[][] getRgbToXyzMatrix();
//	public double[][] getXyzToRgbMatrix();
//	public double[] getWhitePoint();
//	public double[] getPrimary(int idx);
	
	public default float[] getWhitePoint() {
		float[] rgb = {1, 1, 1};
		return this.toCIEXYZ(rgb);
	}
	
	public default float[] getPrimary(int idx) {
		float[] rgb = new float[3];
		rgb[idx] = 1;
		return this.toCIEXYZ(rgb);
	}
	
//	public default double[] getPrimary(int idx) {
//		double[][] Mrgbi = getXyzToRgbMatrix();
//		return Matrix.getColumn(Mrgbi, idx);
//	}
	
	
}
