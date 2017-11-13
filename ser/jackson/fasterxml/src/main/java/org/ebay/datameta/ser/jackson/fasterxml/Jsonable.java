package org.ebay.datameta.ser.jackson.fasterxml;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.ebay.datameta.dom.DataMetaEntity;

import java.io.IOException;

/** Contract for generated JSON serializers.
  *
  * @author mubergens Michael Bergens
  */
public abstract class Jsonable<T extends DataMetaEntity> {

  /**
    * Wraps the `write(target: JsonGenerator, value: T)` method to add the object field start and the end in
    * proper places.
    */
  public void writeField(final String fieldName, final JsonGenerator target, final T value) throws IOException {
    target.writeObjectFieldStart(fieldName);
    write(target, value);
    target.writeEndObject();
  }

  /**
    * Implementation to write just the object's insides.
    */
  public abstract void write(final JsonGenerator target, final T value) throws IOException;

  /**
   * Reads into the given object.
   */
  public abstract T readInto(final JsonParser source, final T target, boolean ignoreUnknown) throws IOException;

  /**
   * Delegates to {@link #readInto(JsonParser, DataMetaEntity, boolean)} with the last parameter <tt>true</tt>.
   */
  public T readInto(final JsonParser source, final T target)  throws IOException {
    return readInto(source, target, true);
  }

  /**
    * Creates a new instance of `T` and delegates to `read(source: JsonParser, into: T): T`.
    *
    * Defaults to ignoring unknown fields.
    */
  public abstract T read(final JsonParser source, boolean ignoreUnknown) throws IOException ;

  /**
   * Delegates to {@link #read(JsonParser, boolean)} with the last parameter set to <tt>true</tt>.
   */
  public T read(final JsonParser source) throws IOException {return read (source, true);}

}

