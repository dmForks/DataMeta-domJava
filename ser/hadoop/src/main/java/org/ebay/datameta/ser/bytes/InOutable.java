package org.ebay.datameta.ser.bytes;

import org.ebay.datameta.util.jdk.SemanticVersion;
import org.ebay.datameta.dom.DataMetaEntity;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import static org.apache.hadoop.io.Text.writeString;
import static org.apache.hadoop.io.Text.readString;

/**
 * Version of Writable that takes a value for each call. Creating a Writable for each object written is
 * a waste and it's unnecessary. Setting a value for each call is not thread-safe.
 *
 * @author Michael Bergens
 */
public abstract class InOutable<T extends DataMetaEntity> {

    abstract public T read(DataInput in) throws IOException;
    abstract public T read(DataInput in, T val) throws IOException;

    abstract public void write(DataOutput out, T val) throws IOException;

    public static void writeVersion(DataOutput out, final SemanticVersion version) throws IOException {
        writeString(out, version.getSemanticPartsOnly());
    }

    public static SemanticVersion readVersion(DataInput in) throws IOException {
        return SemanticVersion.parse(readString(in));
    }

}
