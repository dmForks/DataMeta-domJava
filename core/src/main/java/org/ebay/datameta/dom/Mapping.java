package org.ebay.datameta.dom;

/**
 * Mapping of 2 keys of any type.
 *
 * @author Michael Bergens
 */
public interface Mapping<K, V> extends DataMetaEntity {
    void setKey(K key);
    K getKey();
    V getValue();
}
