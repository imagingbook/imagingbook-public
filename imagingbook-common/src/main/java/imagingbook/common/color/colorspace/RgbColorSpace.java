package imagingbook.common.color.colorspace;

public interface RgbColorSpace {

//	public double[][] getRgbToXyzMatrix();
//	public double[][] getXyzToRgbMatrix();
	public double[] getWhitePoint();
	public double[] getPrimary(int idx);
	
//	public default double[] getPrimary(int idx) {
//		double[][] Mrgbi = getXyzToRgbMatrix();
//		return Matrix.getColumn(Mrgbi, idx);
//	}
	
	
}
