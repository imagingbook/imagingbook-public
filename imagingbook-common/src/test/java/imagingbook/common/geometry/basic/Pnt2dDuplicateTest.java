package imagingbook.common.geometry.basic;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import imagingbook.common.geometry.basic.Pnt2d.PntDouble;
import imagingbook.common.geometry.basic.Pnt2d.PntInt;

// Testing point duplication
public class Pnt2dDuplicateTest {
	
	@Test
	public void testDuplicateClass() {
		// duplicates are always of the same class as the original
		
		Pnt2d p1 = PntInt.from(3, 8);
		Assert.assertTrue(p1.duplicate() instanceof PntInt);
		Assert.assertTrue(p1.duplicate().getClass() == p1.getClass());
		
		Pnt2d p2 = PntDouble.from(3, 8);
		Assert.assertTrue(p2.duplicate() instanceof PntDouble);
		Assert.assertTrue(p2.duplicate().getClass() == p2.getClass());
	}

	@Test
	public void testDuplicateEquality() {

		Pnt2d p1i = PntInt.from(3, 8);
		Pnt2d p1d = PntDouble.from(p1i);
		
		Pnt2d.PntInt p2i = PntInt.from(-2, 7);
		Pnt2d.PntDouble p2d = PntDouble.from(p2i);
		
		// points of different type but with same coordinates satisfy equals()
		Assert.assertTrue(p1i.equals(p1d));
		Assert.assertTrue(p1d.equals(p1i));
		
		// all direct duplicates must be satisfy equals()
		Assert.assertTrue(p1i.equals(p1i.duplicate()));
		Assert.assertTrue(p1d.equals(p1d.duplicate()));
		
		// these points are not equal!
		Assert.assertFalse(p1i.equals(p2d));
		Assert.assertFalse(p2d.equals(p1i));
		
	}
	
	@Test
	public void testDuplicateSetInsertion() {
		/*
		 * Note: This only works for duplicate points of the same type,
		 * since set add() of HashSet only compares the hashcodes of existing elements 
		 * but does NOT check for Objects.equals(e, e2), as claimed in 
		 * the Java documentation! Thus objects are only refused insertion
		 * if they have the same hashcode.
		 */
		
		Set<Pnt2d> set = new HashSet<>();
		assertEquals(0, set.size());

		Pnt2d p1 = PntInt.from(3, 8);
		Pnt2d p2 = PntInt.from(3, 8);
		
//		Assert.assertTrue(p1.equals(p2));
//		Assert.assertTrue(p2.equals(p1));
//		
//		Assert.assertTrue(Objects.equals(p1, p2));
//		Assert.assertTrue(Objects.equals(p2, p1));
		
		Assert.assertTrue(set.add(p1));		// returns true
		assertEquals(1, set.size());
		
		Assert.assertFalse(set.add(p2));	// returns false (duplicate)
		assertEquals(1, set.size());
		
	}
}
