package org.ebay.datameta.dom;

import java.util.function.Consumer;

/**
 * @author Michael Bergens
 */
public class Util {
    public static final Object[] NO_XTRAS = new Object[0];
    private Util() {
    }

    /* attempt to generalize the list function; but Java generics are just not powerful enough for this
    public static <T, L> boolean areListsSame(List<T> one, List<T> another, DataMetaSame<L> dataMetaSame) {
        if(one == another) return true;
        if(one == null || another == null ) return false; // one of them is null but not both -- not equal short-circuit
        java.util.ListIterator<?> it1 = one.listIterator();
        java.util.ListIterator<?> it2 = another.listIterator();
        while(it1.hasNext() && it2.hasNext()) {
            final T o1 = it1.next(), o2 = it2.next();
            if(!(o1 == null ? o2 == null : dataMetaSame.isSame(o1, o2))) return false; // shortcircuit to false
        }
        return !(it1.hasNext() || it2.hasNext());
    }
    */
    public static <T, X extends Exception> Consumer<T> consumerReRte(ThrowingConsumer<T, X> consumer) {
        return t -> {
            try {
                consumer.accept(t);
            }
            catch (Exception x) {
                reAsRte(x);
            }
        };
    }

    @SuppressWarnings("unchecked") private static <E extends Throwable> void reAsRte(E x) {
        throw new RuntimeException(x);
    }
}
