package org.ebay.datameta.ser.bytes;

import org.ebay.datameta.dom.DataMetaEntity;

import com.google.common.io.ByteArrayDataOutput;

import java.io.DataInput;
import java.io.IOException;

import static com.google.common.io.ByteStreams.newDataInput;
import static com.google.common.io.ByteStreams.newDataOutput;

/**
 * Read/Write to/from byte arrays
 * @author Michael Bergens
 */
public class BytesDataIoUtil {

    private BytesDataIoUtil() { }

    private static final int DEFAULT_SIZE = 16 * 1024;

    public static <T extends DataMetaEntity> T read(byte[] bytes, InOutable<T> io) throws IOException {
        return io.read(newDataInput(bytes));
    }

    public static <T extends DataMetaEntity> byte[] write(InOutable<T> io, T value) throws IOException {
        final ByteArrayDataOutput bo = newDataOutput(DEFAULT_SIZE);
        io.write(bo, value);
        return bo.toByteArray();
    }

    /**
     * Same as {@link #read(byte[], InOutable)}, but reads the version first and discards it.
     */
    public static <T extends DataMetaEntity> T readVersioned(byte[] bytes, InOutable<T> io) throws IOException {
        final DataInput in = newDataInput(bytes);
        InOutable.readVersion(in);
        return io.read(in);
    }

    /**
     * Same as {@link #write(InOutable, DataMetaEntity)}, but writes the version first and discards it.
     */
    public static <T extends DataMetaEntity> byte[] writeVersioned(InOutable<T> io, T value) throws IOException {
        final ByteArrayDataOutput bo = newDataOutput(DEFAULT_SIZE);
        InOutable.writeVersion(bo, value.getVersion());
        io.write(bo, value);
        return bo.toByteArray();
    }
}
