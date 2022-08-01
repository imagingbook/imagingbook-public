package imagingbook.common.threshold;

import ij.process.ByteProcessor;
import ij.process.FloatProcessor;

public abstract class Utils {

	// compute the sum of a histogram array
	public static int sum(int[] h) {
		int cnt = 0;
		for (int i = 0; i < h.length; i++) {
			cnt += h[i];
		}
		return cnt;
	}

	public static int count(int[] h) {
		return count(h, 0, h.length - 1);
	}

	// compute the population of a histogram from index lo...hi
	public static int count(int[] h, int lo, int hi) {
		if (lo < 0) lo = 0;
		if (hi >= h.length) hi = h.length-1;
		int cnt = 0;
		for (int i = lo; i <= hi; i++) {
			cnt += h[i];
		}
		return cnt;
	}

	public static double mean(int[] h) {
		return mean(h,0,h.length-1);
	}

	public static double mean(int[] h, int lo, int hi) {
		if (lo < 0)
			lo = 0;
		if (hi >= h.length)
			hi = h.length - 1;
		long cnt = 0;
		long sum = 0;
		for (int i = lo; i <= hi; i++) {
			cnt = cnt + h[i];
			sum = sum + i * h[i];
		}
		if (cnt > 0)
			return ((double) sum) / cnt;
		else
			return 0;
	}

	public static double sigma2(int[] h) {
		return sigma2(h, 0, h.length-1);
	}

	// fast version
	public static double sigma2(int[] h, int lo, int hi) {
		if (lo < 0)
			lo = 0;
		if (hi >= h.length)
			hi = h.length - 1;
		long A = 0;
		long B = 0;
		long N = 0;
		for (int i = lo; i <= hi; i++) {
			int ni = h[i];
			A = A + i * ni;
			B = B + i * i * ni;
			N = N + ni;
		}
		if (N > 0)
			return ((double) B - (double) A * A / N) / N;
		else
			return 0;
	}
	
	// this is a slow version, only for testing:
//	protected double sigma2(int[] h, int lo, int hi) {
//		if (lo < 0) lo = 0;
//		if (hi >= h.length) hi = h.length-1;
//		double mu = mean(h,lo,hi);
//		long cnt = 0;
//		double sum = 0;
//		for (int i=lo; i<=hi; i++) {
//			cnt = cnt + h[i];
//			sum = sum + (i - mu) * (i - mu) * h[i];
//		}
//		if (cnt > 0)
//			return ((double)sum) / cnt;
//		else 
//			return 0;
//	}

	// compute the median of a histogram 
	public static int median(int[] h) {
		int K = h.length;
		int N = sum(h);
		int m = N / 2;
		int i = 0;
		int sum = h[0];
		while (sum <= m && i < K) {
			i++;
			sum += h[i];
		}
		return i;
	}

	public static double[] normalize(int[] h) {
		int K = h.length;
		int N = count(h);
		double[] nh = new double[K];
		for (int i = 0; i < K; i++) {
			nh[i] = ((double) h[i]) / N;
		}
		return nh;
	}

	public static double[] normalize(double[] h) {
		double[] nh = new double[h.length];
		double hmax = max(h);
		for (int i = 0; i < h.length; i++) {
			nh[i] = (double) h[i] / hmax;
		}
		return nh;
	}

	public static double max(double[] h) {
		double hmax = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < h.length; i++) {
			double n = h[i];
			if (n > hmax)
				hmax = n;
		}
		return hmax;
	}

	// TODO: change to use an ImageAccessor!
	public static int getPaddedPixel(ByteProcessor bp, int u, int v) {
		final int w = bp.getWidth();
		final int h = bp.getHeight();
		if (u < 0)
			u = 0;
		else if (u >= w)
			u = w - 1;
		if (v < 0)
			v = 0;
		else if (v >= h)
			v = h - 1;
		return bp.get(u, v);
	}

	// used for logging/testing only
	public static double[] getLine(ByteProcessor bp, int v) {
		double[] line = new double[bp.getWidth()];
		for (int u = 0; u < line.length; u++) {
			line[u] = bp.get(u, v);
		}
		return line;
	}

	// used for logging/testing only
	public static double[] getLine(FloatProcessor fp, int v) {
		double[] line = new double[fp.getWidth()];
		for (int u = 0; u < line.length; u++) {
			line[u] = fp.getf(u, v);
		}
		return line;
	}

}
