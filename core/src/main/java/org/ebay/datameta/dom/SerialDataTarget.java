package org.ebay.datameta.dom;

/**
 * Generic serial data target (destination, sink) for structured strong-typed table-oriented data,
 * accepts one record after another.
 * No reposition (rewind), no random access.
 * Should work the same for dynamic or partial dynamic schemas if the implementation supports it.
 *
 * @author Michael Bergens
 * @param <T> the data type to save into this data target
 */
public interface SerialDataTarget<T> {
    void save(T what);
}
