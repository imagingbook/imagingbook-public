package imagingbook.common.geometry.moments;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.mappings.linear.Rotation2D;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class FlusserMomentsTest {

    private static double TOL = 1e-6;

    private static List<Pnt2d> points = Arrays.asList(
            Pnt2d.from(10, 15),
            Pnt2d.from(3, 7),
            Pnt2d.from(-1, 5),
            Pnt2d.from(5, -5),
            Pnt2d.from(-7, 3)
    );

    @Test
    public void testFlusser1() {
        FlusserMoments fm = new FlusserMoments(points);
        System.out.println(" c00 = " + fm.getComplexMoment(0, 0));
        System.out.println(" c10 = " + fm.getComplexMoment(1, 0));
        System.out.println(" c01 = " + fm.getComplexMoment(0, 1));
        System.out.println(" c11 = " + fm.getComplexMoment(1, 1));
        System.out.println(" c20 = " + fm.getComplexMoment(2, 0));
        System.out.println(" c02 = " + fm.getComplexMoment(0, 2));
    }
       /*
        c00 = (5.000000000, 0.000000000)
        c10 = (0.000000000, 0.000000000)
        c01 = (0.000000000, 0.000000000)
        c11 = (372.000000000, 0.000000000)
        c20 = (-44.000000000, 140.000000000)
        c02 = (-44.000000000, -140.000000000)
     */

    @Test
    public void testFlusser2() {
        FlusserMoments fm = new FlusserMoments(points);
        System.out.println(" m00 = " + fm.getScaleNormalizedMoment(0, 0));
        System.out.println(" m10 = " + fm.getScaleNormalizedMoment(1, 0));
        System.out.println(" m01 = " + fm.getScaleNormalizedMoment(0, 1));
        System.out.println(" m11 = " + fm.getScaleNormalizedMoment(1, 1));
        System.out.println(" m20 = " + fm.getScaleNormalizedMoment(2, 0));
        System.out.println(" m02 = " + fm.getScaleNormalizedMoment(0, 2));

    }
    /*
        m00 = (1.000000000, 0.000000000)
        m10 = (0.000000000, 0.000000000)
        m01 = (0.000000000, 0.000000000)
        m11 = (14.880000000, 0.000000000)
        m20 = (-1.760000000, 5.600000000)
        m02 = (-1.760000000, -5.600000000)
     */

    @Test
    public void testFlusser3() {
        FlusserMoments fm = new FlusserMoments(points);
        double[] im = fm.getInvariantMoments();
        System.out.println(Arrays.toString(im));
    }

    /*
    [14.88, 280.96128000000004, 867.7251072000003, 1402.5369600000006, 14203.272830976013, 304180.56570470415,
    368.86400000000003, 25223.15354112001, 49134.545510400014, 4447152.773082437, 8866036.876121216]
     */

    @Test
    public void testFlusser4() {
        Rotation2D rot = new Rotation2D(0.5);
        Pnt2d[] pointA = rot.applyTo(points.toArray(new Pnt2d[0]));
        FlusserMoments fm = new FlusserMoments(Arrays.asList(pointA));
        double[] im = fm.getInvariantMoments();
        System.out.println(Arrays.toString(im));
    }

    /*
    [14.880000000000003, 280.9612800000003, 867.7251072000015, 1402.5369600000006, 14203.272830976246, 304180.56570470444,
    368.8640000000001, 25223.153541120042, 49134.54551040004, 4447152.773082451, 8866036.876121225]
     */
}
