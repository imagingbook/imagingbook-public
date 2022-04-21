package imagingbook.common.mser.components;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public abstract class Util {
	
//	public static int WAIT_TIME_MS = 500;
	static int INDENT = 4;
	
	
	//--------------------------------------------------------------------------------------
	
	// print forest with multiple roots
	public static String toStringRecursive(ComponentTree<?> rt) { 
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(os);
		
		for (Component<?> c : rt) {
			if (c.getParent() == null || c.getParent() == c) {	// start at any root
				printToStreamRecursive(c, ps, 0);
			}	
		}	
		return os.toString();
	}
	
	// print tree with a single root
	public static String toStringRecursive(Component<?> rt) { 
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(os);
		printToStreamRecursive(rt, ps, 0);	
		return os.toString();
	}
	
	
	private static void printToStreamRecursive(Component<?> c, PrintStream ps, int indent) {
		for (int i=0; i < indent; i++) {
			ps.append('|');
			for (int j = 0; j < INDENT; j++) {
				ps.append(' ');
			}
		}
		c.printToStream(ps);
		ps.println();
		for (Component<?> child : c.getChildren()) {
			printToStreamRecursive(child, ps, indent + 1);
		}
	}
	
	//--------------------------------------------------------------------------------------
	
//	public static Roi makeEllipseRoi(Ellipse e) {
//		Ellipse2D oval = new Ellipse2D.Double(-e.ra, -e.rb, 2*e.ra, 2*e.rb);
//		AffineTransform trans = new AffineTransform();
//		
//		trans.translate(e.xc + 0.5, e.yc + 0.5);
//		trans.rotate(e.theta);
//		
//		Shape s = trans.createTransformedShape(oval);
//		Roi roi = new ShapeRoi(s);	
//		return roi;
//	}

}
