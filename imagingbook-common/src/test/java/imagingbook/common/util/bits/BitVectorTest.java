
package imagingbook.common.util.bits;

import org.junit.Assert;
import org.junit.Test;

public class BitVectorTest {

    @Test
    public void createTest32() {
        BitVector bv;
        for (int n : new int[]{1, 10, 32}) {
            bv = BitVector.create(n);
            Assert.assertEquals(n, bv.length());
            Assert.assertTrue(bv instanceof BitVector32);
        }
    }

    @Test
    public void createTest64() {
        BitVector bv;
        for (int n : new int[]{33, 64, 3017}) {
            bv = BitVector.create(n);
            Assert.assertEquals(n, bv.length());
            Assert.assertTrue(bv instanceof BitVector64);
        }
    }

    @Test
    public void fromStringTest() {
        String str = "0111010111";
        BitVector bv = BitVector.from(str);
        Assert.assertEquals(10, bv.length());
        Assert.assertEquals(str, bv.asString());
    }

    @Test
    public void setFromStringTest() {
        String str = "01110000100010100001011111010001110110111110000010110111";
        BitVector bv = BitVector.create(str.length());
        bv.set(str);
        Assert.assertEquals(str, bv.asString());
    }

    @Test
    public void cardinalityTest0() {
        int n = 23;
        BitVector bv = BitVector.create(n);
        int c = bv.cardinality();
        Assert.assertEquals(0, bv.cardinality());
        bv.setAll();
        Assert.assertEquals(n, bv.cardinality());
    }

    @Test
    public void cardinalityTest1() {
        String str = "01110000100010100001011111010001110110111110000010110111";
        long count = str.chars().filter(c -> c == '1').count(); // count '0's in str
        BitVector bv = BitVector.from(str);
        Assert.assertEquals(count, bv.cardinality());
    }
}