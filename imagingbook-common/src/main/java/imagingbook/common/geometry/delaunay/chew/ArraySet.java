package imagingbook.common.geometry.delaunay.chew;

/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * An ArrayList implementation of Set. An ArraySet is good for small sets; it
 * has less overhead than a HashSet or a TreeSet.
 *
 * @author Paul Chew
 *
 * Created December 2007.  For use with Voronoi/Delaunay applet.
 *
 */
public class ArraySet<E> extends AbstractSet<E> {

    private ArrayList<E> items;            // Items of the set

    /**
     * Create an empty set (default initial capacity is 3).
     */
    public ArraySet () {
        this(3);
    }

    /**
     * Create an empty set with the specified initial capacity.
     * @param initialCapacity the initial capacity
     */
    public ArraySet (int initialCapacity) {
        items  = new ArrayList<E>(initialCapacity);
    }

    /**
     * Create a set containing the items of the collection.  Any duplicate
     * items are discarded.
     * @param collection the source for the items of the small set
     */
    public ArraySet (Collection<? extends E> collection) {
        items = new ArrayList<E>(collection.size());
        for (E item: collection)
            if (!items.contains(item)) items.add(item);
    }

    /**
     * Get the item at the specified index.
     * @param index where the item is located in the ListSet
     * @return the item at the specified index
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    public E get (int index) throws IndexOutOfBoundsException {
        return items.get(index);
    }

    /**
     * True iff any member of the collection is also in the ArraySet.
     * @param collection the Collection to check
     * @return true iff any member of collection appears in this ArraySet
     */
    public boolean containsAny (Collection<?> collection) {
        for (Object item: collection)
            if (this.contains(item)) return true;
        return false;
    }

    @Override
    public boolean add(E item) {
        if (items.contains(item)) return false;
        return items.add(item);
    }

    @Override
    public Iterator<E> iterator() {
        return items.iterator();
    }

    @Override
    public int size() {
        return items.size();
    }

}
