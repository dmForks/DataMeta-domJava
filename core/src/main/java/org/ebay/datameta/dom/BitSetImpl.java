package org.ebay.datameta.dom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Michael Bergens
 */
abstract public class BitSetImpl<T> extends BitSet {

    public static int  getLongArrayLength(int bitLength) {
       return  bitLength / 64  + ((bitLength & 0x3F) == 0 ? 0 : 1);
    }

    public BitSetImpl() {
    }

    public BitSetImpl(long[] image) {
        super(image, true);
    }

    abstract public int getCount();
    abstract public T[] getMap();
    /**
     * Sorted by keys, immutable list.
     */
    public List<T> getValList() {
        final T[] map = getMap();
        final int len = getCount();
        final List<T> result = new ArrayList<>(len);
        for(int ix = 0; ix < len; ix++) {
            if(get(ix)) result.add(map[ix]);
        }
        return Collections.unmodifiableList(result);
    }

    /**
     * Unsorted set of all the values mapped to in the immutable set.
     * Make sure that {@link T} implementes {@link #equals(Object)} and {@link #hashCode()} to use this feature.
     */
    public Set<T> getValSet() {
        final T[] map = getMap();
        final int len = getCount();
        final Set<T> result = new HashSet<T>(len);
        for(int ix = 0; ix < len; ix++) {
            if(get(ix)) result.add(map[ix]);
        }
        return Collections.unmodifiableSet(result);
    }

    /**
     * Single mapped value for the key.
     */
    public T getVal(int key) {
        return getMap()[key];
    }
}
