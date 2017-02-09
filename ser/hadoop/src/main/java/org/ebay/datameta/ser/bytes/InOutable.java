package org.ebay.datameta.ser.bytes;

import org.ebay.datameta.util.jdk.SemanticVersion;
import org.ebay.datameta.dom.DataMetaEntity;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import static org.apache.hadoop.io.Text.writeString;
import static org.apache.hadoop.io.Text.readString;

/**
 * Common ancestor for serializers to/from {@link DataOutput}/{@link DataInput}.
 *
 * @author Michael Bergens
 */
public abstract class InOutable<T extends DataMetaEntity> {

    abstract public T read(DataInput in) throws IOException;
    abstract public T read(DataInput in, T val) throws IOException;

    abstract public void write(DataOutput out, T val) throws IOException;

  /**
   * Write a semantic version - semantic parts only to save space.
   */
    public static void writeVersion(DataOutput out, final SemanticVersion version) throws IOException {
        writeString(out, version.getSemanticPartsOnly());
    }

    public static SemanticVersion readVersion(DataInput in) throws IOException {
        return SemanticVersion.parse(readString(in));
    }

}
