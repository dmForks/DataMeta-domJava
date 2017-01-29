package org.ebay.datameta.dom;

/** Interface for a migrator from one DataMeta DOM Entity to another, most commonly because of a schema version difference.
 *
 * @author Michael Bergens
 */
public interface Migrator<S extends DataMetaEntity, T extends DataMetaEntity> {

    T migrate(S src, Object... xtras);
}
