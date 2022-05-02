package imagingbook.common.geometry.basic;

import java.awt.Shape;
import java.awt.geom.PathIterator;
import java.util.Iterator;

import imagingbook.common.geometry.circle.GeometricCircle;

public class ShapePointIterator implements Iterator<Pnt2d> {
	
	private final PathIterator pi;
	private final double coords[] = new double[6];
	
	public ShapePointIterator(Shape s, double flatness) {
		this.pi = s.getPathIterator(null, flatness);
		System.out.println(this.pi);
	}

	@Override
	public boolean hasNext() {
		return !pi.isDone();
	}

	@Override
	public Pnt2d next() {
		if (pi.isDone()) {
			return null;
		}
		else {
			pi.currentSegment(coords);
			pi.next();
			return Pnt2d.from(coords[0], coords[1]);
		}
	}

	
	public static void main(String[] args) {
		Pnt2d ctr = Pnt2d.from(20, 30);
		GeometricCircle gc = new GeometricCircle(20, 30, 100);
		Shape circle = gc.getShape();
		System.out.println("circle = " + circle);
		
		ShapePointIterator iter = new ShapePointIterator(circle, 0.01);
		while(iter.hasNext()) {
			Pnt2d p = iter.next();
			System.out.println(p + " dist = " + p.distance(ctr));
		}
		
	}
}
