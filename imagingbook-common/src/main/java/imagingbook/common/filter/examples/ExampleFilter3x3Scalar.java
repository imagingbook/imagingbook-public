package imagingbook.common.filter.examples;

import imagingbook.common.filter.GenericFilterScalar;
import imagingbook.common.image.data.PixelPack.PixelSlice;

public class ExampleFilter3x3Scalar extends GenericFilterScalar {
	
	private final static float[][] H = {
			{1, 2, 1},
			{2, 4, 2},
			{1, 2, 1}};
	
	private final static int width = 3;
	private final static int height = 3;
	private final static int xc = 1;
	private final static int yc = 1;
	private final static float s = 16;
	
	@Override
	protected float doPixel(PixelSlice plane, int u, int v) {
		float sum = 0;
		for (int j = 0; j < height; j++) {
			int vj = v + j - yc;
			for (int i = 0; i < width; i++) {
				int ui = u + i - xc;
				sum = sum + plane.getVal(ui, vj) * H[i][j];
			}
		}
		return sum / s;
	}

}
