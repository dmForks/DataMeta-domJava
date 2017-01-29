package org.ebay.datameta.dom;

import java.util.Comparator;
import java.util.List;

/**
 * Adds the flexible equality trait to DataMeta classes. This interface is made different from:
 * <ul>
 * <li>{@link Object#equals(Object)} - not hard-wired to the class, you can have as many
 * equality definition as you need and it is generically typed. You don't need to redefine
 * the {@link Object#equals(Object)}, but you can make it use this interface if needed</li>
 * <li>{@link Comparable} - not hard-wired to the class, can be decoupled and diversified, not burdened with
 * greater-than nor lesser-than logic.</li>
 * <li>{@link Comparator} not burdened with greater-than nor lesser-than logic.</li>
 * </ul>
 * @author Michael Bergens
 */
public interface DataMetaSame<T> {

    /**
     * Leverages the {@link Object#equals(Object)} (re)defined on the class.
     * The {@link #isSame(Object, Object)} method returns <tt>true</tt> if
     * both instances are <tt>null</tt> or <tt>one</tt> is not null and it <tt>equals</tt> the
     * <tt>another</tt>.
     */
    DataMetaSame<Object> EQ = (one, another) -> one == another || (one != null && one.equals(another));

    /**
     * Equality of {@link Mapping} instances, by {@link Mapping#getKey()}.
     * As usual, nulls are equals, reference to the same object equals too.
     */
    DataMetaSame<Mapping<?, ?>> MAP_EQ = (one, another) -> one == another ||
        ( one != null && another != null && one.getKey().equals(another.getKey()));

    /**
     * Evaluates if both of the arguments are the same in certain applicable sense.
     */
    boolean isSame(T one, T another);
}
