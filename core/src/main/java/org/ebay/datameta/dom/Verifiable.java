package org.ebay.datameta.dom;

/**
 * Contract for a verifiable entity. All <code>record</code> entities are verifiable.
 * @author Michael Bergens
 */
public interface Verifiable extends DataMetaEntity {
    /**
     * If completes successfully, there are no errors; otherwise throws.
     * @throws IllegalArgumentException with error messages if the entity fails verification.
     */
    void verify();
}
