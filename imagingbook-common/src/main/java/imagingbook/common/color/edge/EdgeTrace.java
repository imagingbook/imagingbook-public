package imagingbook.common.color.edge;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.Pnt2d.PntInt;

/**
 * Represents a chain of connected edge points.
 * @author WB
 *
 */
public class EdgeTrace implements Iterable<PntInt> {
	
	private final ArrayList<PntInt> edgePoints;
	
	public EdgeTrace() {
		this.edgePoints = new ArrayList<>();
	}
	
	public int size() {
		return edgePoints.size();
	}
	
	public void addPoint(PntInt p) {
		edgePoints.add(p);
	}
	
	public PntInt getPoint(int idx) {
		return (idx >= 0 && idx < edgePoints.size()) ? 
				edgePoints.get(idx) : null;
	}
	
	public PntInt[] getPointArray() {
		return edgePoints.toArray(new PntInt[0]);
	}

	@Override
	public Iterator<PntInt> iterator() {
		return edgePoints.iterator();
	}
	
	
	/**
	 * For backward compatibility only.
	 * @return a list of AWT {@link Point} instances.
	 */
	protected List<Point> getAwtPoints() {
		List<Point> awtPoints = new ArrayList<>();
		for (PntInt p : edgePoints) {
			awtPoints.add(new Point(p.x, p.y));
		}
		return awtPoints;
	}
	
	@Override
	public String toString() {
		return String.format("%s: start=%s length=%d", 
				this.getClass().getSimpleName(), getPoint(0).toString(), size());
	}
	
	// ---------------------------------------------------------
	
	public static void main(String[] args) {
		EdgeTrace trace = new EdgeTrace();
		trace.addPoint(PntInt.from(1,2));
		trace.addPoint(PntInt.from(3,4));
		trace.addPoint(PntInt.from(5,6));
		
		for (Pnt2d p : trace) {
			System.out.println(p);
		}
		
		System.out.println();
		trace.addPoint(PntInt.from(7,8));
		
		for (PntInt p : trace.getPointArray()) {
			System.out.println(p);
		}
		
		System.out.println();
		for (Point p : trace.getAwtPoints()) {
			System.out.println(p);
		}
		
		System.out.println(trace.getPoint(3));
		System.out.println(trace.getPoint(4));
		
		PntInt[] copy = Arrays.copyOfRange(trace.getPointArray(),0, 7);
		System.out.println(copy.length);
		for (PntInt p : copy) {
			System.out.println(p);
		}
	}

}
