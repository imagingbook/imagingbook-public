package imagingbook.common.filter.examples;

import imagingbook.common.filter.GenericFilterVector;
import imagingbook.common.image.data.PixelPack;

public class ExampleFilter3x3Vector extends GenericFilterVector {

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
	protected float[] doPixel(PixelPack pack, int u, int v) {
		int depth = pack.getDepth();
		float[] sum = new float[depth];
		for (int j = 0; j < height; j++) {
			int vj = v + j - yc;
			for (int i = 0; i < width; i++) {
				int ui = u + i - xc;
				float[] p = pack.getVec(ui, vj);
				for (int k = 0; k < depth; k++) {
					sum[k] = sum[k] + (p[k] * H[i][j]) / s;
				}
			}
		}
		return sum;
	}

}
