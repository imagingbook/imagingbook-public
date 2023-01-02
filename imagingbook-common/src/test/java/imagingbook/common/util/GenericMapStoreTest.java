package imagingbook.common.util;

import org.junit.Test;

import static imagingbook.testutils.NumericTestUtils.assert2dArrayEquals;
import static org.junit.Assert.assertEquals;

public class GenericMapStoreTest {

    private static double[][] D = {{3}, {4, 5}};

    @Test
    public void test1() {
        GenericMapStore gms = new GenericMapStore();

        GenericMapStore.MapKey<Double> doubleKey1 = gms.newKey();
        GenericMapStore.MapKey<Double> doubleKey2 = gms.newKey();
        GenericMapStore.MapKey<double[][]> doubleArrayKey = gms.newKey();
        GenericMapStore.MapKey<String> stringKey = gms.newKey();

        gms.put(doubleKey1, 9.9);
        gms.put(doubleKey2, Math.PI);
        gms.put(doubleArrayKey, D);
        gms.put(stringKey, "Foo");
        gms.put(stringKey, "Bar");   // overwrite existing item

        // System.out.println(gms.get(doubleKey1));
        // System.out.println(gms.get(doubleKey2));
        // System.out.println(Matrix.toString(gms.get(doubleArrayKey)));
        // System.out.println(gms.get(stringKey));

        assertEquals(9.9, gms.get(doubleKey1), 0);
        assertEquals(Math.PI, gms.get(doubleKey2), 0);
        assert2dArrayEquals(D, gms.get(doubleArrayKey), 0);
        assertEquals("Bar", gms.get(stringKey));
    }

}