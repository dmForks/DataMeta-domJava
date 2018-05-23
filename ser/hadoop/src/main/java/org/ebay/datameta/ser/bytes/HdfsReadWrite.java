package org.ebay.datameta.ser.bytes;

import org.ebay.datameta.util.jdk.Api;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Implementation via delegation - can reuse the instance on the same thread by swapping the value in and out.
 * <p>No subclass of this class will be thread-safe, always create a copy for a thread.</p>
 *
 * @deprecated use InOutable instead.
 * @author Michael Bergens
 */
public abstract class HdfsReadWrite<T> implements Writable {
    private T val;

    public HdfsReadWrite(T value) {
        val = value;
    }

    private HdfsReadWrite() {
        //throw new UnsupportedOperationException("Empty constructor should never be called");
    }
    
    /**
     * Writes the {@link #getVal()} value.
     * Method overridden only to provide the JavaDocs.
     */
    @Override abstract public void write(DataOutput out) throws IOException;

    /**
     * Reads into the {@link #getVal()} value, must create an instance with a new value to use this method.
     * Method overridden only to provide the JavaDocs.
     */
    @Override abstract public void readFields(DataInput in) throws IOException;

    public T getVal() { return val; }
    public void setVal(final T newVal) { val = newVal; }

    protected void assertValue() {
        if(val == null) throw new IllegalStateException("Writable delegate value not set");
    }

    /**
     * Returns staple exception to throw when a required field is not set.
     * @deprecated Use {@link org.ebay.datameta.dom.Verifiable#verify()} instead which should be present on each DataMeta DOM POJO, preferably
     * on the caller side.
     */
    protected IllegalStateException noReqFld(final String name) {
        return new IllegalStateException("Required field \"" + name + "\" is not set");
    }

    @Override public String toString() {
        return ToStringBuilder.reflectionToString(val == null ? this : val); // subclasses are free to provide their own
    }
}
