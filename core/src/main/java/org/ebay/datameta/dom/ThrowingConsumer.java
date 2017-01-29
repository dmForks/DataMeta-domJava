package org.ebay.datameta.dom;

/**
 * To enable our lambdas run methods with an {@link Exception} thrown, even a checked one.
 * @author Michael Bergens
 */
@FunctionalInterface
public interface ThrowingConsumer<T, X extends Exception> {
    void accept(T v) throws X;
}
