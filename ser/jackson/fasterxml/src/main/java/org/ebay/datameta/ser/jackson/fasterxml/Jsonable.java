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
  public abstract void write(final JsonGenerator target, final T value);

  /**
    * Reads into the given object
    *
    * Defaults to ignoring unknown fields.
    */
  public abstract T readInto(final JsonParser source, final T target, boolean ignoreUnknown);

  public T readInto(final JsonParser source, final T target) {return readInto(source, target, true);}

  /**
    * Creates a new instance of `T` and delegates to `read(source: JsonParser, into: T): T`.
    *
    * Defaults to ignoring unknown fields.
    */
  public abstract T read(final JsonParser source, boolean ignoreUnknown);

  public T read(final JsonParser source) {return read (source, true);}

}

