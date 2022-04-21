package imagingbook.common.mser.components;

import ij.process.ImageProcessor;
import imagingbook.common.geometry.basic.Pnt2d.PntInt;

/**
 * Basically a 2D array of pixels.
 * This class holds all necessary information about the image geometry.
 * It keeps track of which pixels have been visited and knows how
 * to access neighboring pixels (currently 4-neighborhood only).
 * TODO: Bring in line with binary region neighborhoods (type).
 * 
 * @author WB
 */
public class PixelMap {
	
	public final int width, height;
	
	private final Pixel[][] pixels;
	
	public PixelMap(ImageProcessor ip) {
		this.width = ip.getWidth();
		this.height = ip.getHeight();
		this.pixels = makeImagePoints(ip);
	}
	
	private Pixel[][] makeImagePoints(ImageProcessor ip) {
		Pixel[][] ipts = new Pixel[width][height];
		for (int u = 0; u < width; u++) {
			for (int v = 0; v < height; v++) {
				ipts[u][v] = new Pixel(u, v, ip.get(u, v));
			}
		}
		return ipts;
	}

	public Pixel getPixel(int u, int v) {
		return pixels[u][v];
	}
	
	/**
	 * Returns a new 1D array (i.e., a "flattened" vector) of {@link Pixel} elements,
	 * e.g., for sorting pixels by value.
	 * 
	 * @return a 1D array of pixels
	 */
	public Pixel[] getPixelVector() {
		final Pixel[] pix = new Pixel[width * height];
		int i = 0;
		for (int v = 0; v < height; v++) {
			for (int u = 0; u < width; u++) {
				pix[i] = pixels[u][v];
				i++;
			}
		}
		return pix;
	}
	
	/**
	 * Sets all pixels to unvisited and resets next-neighbor search directions.
	 */
	public void reset() {
		//this.visited.unsetAll();
		for (int u = 0; u < width; u++) {
			for (int v = 0; v < height; v++) {
				pixels[u][v].reset();
			}
		}
	}
	
	// ------------------------------------
	
	
//	public PointNeighborhood getNeighbors(PntInt p) {
//		return new PointNeighborhood(p);
//	}
//	
//	public class PointNeighborhood implements Iterable<ImagePoint> {
//		private final int x, y;	// the center of this neighborhood
//		private int xn, yn; 	// the coordinates of the next neighbor
//		private int dir=  -1;	// the direction of the next neighbor (valid if dir=0,..,3)
//		
//		protected PointNeighborhood(PntInt p) {
//			this.x = p.x;
//			this.y = p.y;
//			findNextNeighbor();
//		}
//		
//		private void findNextNeighbor() {
//			dir = dir + 1;
//			while (dir < 4) {
//				xn = x + dX[dir];
//				yn = y + dY[dir];
//				if (xn >= 0 && xn < width && yn >= 0 && yn < height) {
//					break;	// found neighbor, don't advance dir
//				}
//				dir = dir + 1;
//			}
//		}
//
//		@Override
//		public Iterator<ImagePoint> iterator() {
//			return new Iterator<ImagePoint>() {
//				@Override
//				public boolean hasNext() {
//					return dir < 4;
//				}
//				@Override
//				public ImagePoint next() {
//					ImagePoint nextPnt = imagePoints[xn][yn];
//					findNextNeighbor();
//					return nextPnt;
//				}
//			};
//		}
//		
//	}
	
	// --------------------------------------------------------
	
	private static final int[] dX = {1, 0, -1, 0};
	private static final int[] dY = {0, -1, 0, 1};
	
	/**
	 * A pixel value which knows its coordinates.
	 * This is a non-static class, i.e.,  {@link Pixel} instances can only
	 * exist in the context of a {@link PixelMap} instance.
	 */
	public class Pixel extends PntInt implements Comparable<Pixel> {
		
		public final int val;		// the pixel value
		private byte dir = 0;		// next-neighbor search direction

		// only the enclosing PixelMap can instantiate pixels
		public Pixel(int x, int y, int val) {
			super(x, y);	// Pnt2d.PntInt
			this.val = val;
		}
		
		/**
		 * Sets this pixel to unvisited and resets its next-neighbor search direction.
		 */
		public void reset() {
			this.dir = 0;
		}
		
		/**
		 * Gets the next neighbor of this pixel that is inside
		 * the containing image.
		 * @return the next neighboring {@link Pixel} or {@code null} if no more neighbors
		 */
		public Pixel getNextNeighbor() {
			int u = -1, v = -1;
			boolean found = false;
			while (this.dir < 4 && !found) {
				// try direction dir
				u = this.x + dX[this.dir];	// coordinates of neighbor in direction n
				v = this.y + dY[this.dir];
				found = (u >= 0 && u < width && v >= 0 && v < height);
				this.dir++;
			}
			if (found) {
				return PixelMap.this.getPixel(u, v);
			}
			return null;
		}

		@Override	// sorts by increasing val
		public int compareTo(Pixel other) {
			//return val - other.val;
			return Integer.compare(this.val, other.val);
		}

		@Override
		public String toString() {
			return String.format("%s[x=%d, y=%d, val=%d]", Pixel.class.getSimpleName(), x, y, val);
		}

	}
}
