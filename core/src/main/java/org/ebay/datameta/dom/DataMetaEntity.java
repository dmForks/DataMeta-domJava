package org.ebay.datameta.dom;
import org.ebay.datameta.util.jdk.SemanticVersion;

/**
 * DataMeta DOM entities have a semantic version attached.
 * @author Michael Bergens
 */
public interface DataMetaEntity {

    SemanticVersion getVersion();
}
