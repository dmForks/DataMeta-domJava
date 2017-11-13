package org.ebay.datameta.ser.jackson.fasterxml;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import static com.fasterxml.jackson.core.JsonToken.END_ARRAY;
import static com.fasterxml.jackson.core.JsonToken.END_OBJECT;
import static org.apache.commons.lang3.ArrayUtils.toObject;

import org.apache.commons.lang3.ArrayUtils;
import org.ebay.datameta.dom.BitSet;
import org.ebay.datameta.dom.DataMetaEntity;
import org.ebay.datameta.dom.DateTimeUtil;


/** Jackson Utilities for FasterXML: provide methods similar to the Byte Array Serializers, can be used also
  * as an example/a reference. Some are very superficial, but some, such as with DateTime, less so.
  *
  * @author Michael Bergens
  */
//noinspection TypeAnnotation
public class JacksonUtil {

  private final static JacksonUtil INSTANCE = new JacksonUtil();

  public static JacksonUtil getInstance() {return INSTANCE;}

  private final static DateTimeUtil DTU = DateTimeUtil.getInstance();

  /**
   * The JSON key for the version of the record, namely {@link DataMetaEntity#getVersion()}.
   */
  public final static String VER_KEY = "*v*";

  /**
   * They JSON key for the datatype, i.e. the <tt>record</tt> full name including the namespace, i.e.
   * the full Java/Scala class specification for the data type.
   */
  public final static String DT_KEY = "*dt*";

  private JacksonUtil() {}

  public <T extends DataMetaEntity> String writeObject(final JsonFactory jf, final Jsonable<T> out,
                                                       final T v) throws IOException {
    final StringWriter w = new StringWriter(8000);
    final JsonGenerator generator = jf.createGenerator(w);
    generator.writeStartObject();
    generator.writeStringField(VER_KEY, v.getVersion().toString());
    generator.writeStringField(DT_KEY, v.getClass().getName());
    out.write(generator, v);
    generator.close();
    return w.toString();
  }

  public <T extends DataMetaEntity> T readObject(final JsonFactory jf, Jsonable<T> in, final String source) throws IOException {
    return in.read(jf.createParser(source));
  }

  public void writeTextFldIfAny(final String fieldName, final JsonGenerator out, final String source) throws IOException {
    out.writeStringField(fieldName, source);
  } 

  public String readText(final JsonParser in) throws IOException { return in.getText(); }

  public void writeDttmFld(final String fieldName, final JsonGenerator out, final ZonedDateTime source) throws IOException {
    out.writeStringField(fieldName, DTU.toString(source));
  }

  public ZonedDateTime readDttm(final JsonParser in) throws IOException {
    return DateTimeUtil.getInstance().parse(in.getText());
  }

  public void writeBigDecimalFld(final String fieldName, final JsonGenerator out,
                                 final BigDecimal source) throws IOException {
    out.writeNumberField(fieldName, source);
  }

  public BigDecimal readBigDecimal(final JsonParser in) throws IOException { return in.getDecimalValue(); }

  public void writeByteArrayFld(final String fieldName, final JsonGenerator out,
                                final Byte[] source) throws IOException {// FIXME -- need to test it with bytes > 0x7F
    out.writeArrayFieldStart(fieldName);
    for(final byte b: source) out.writeNumber(b);
    out.writeEndArray();
  }

  public Byte[] readByteArray(final JsonParser in) throws IOException {// FIXME -- need to test it with bytes > 0x7F
    final List<Byte> accumulator = new ArrayList<>(4096);
    while(in.nextToken() != END_ARRAY) accumulator.add(in.getByteValue());
    return accumulator.toArray(new Byte[accumulator.size()]);
  }

  public void writeLongArrayFld(final String fieldName, final JsonGenerator out, final Long[] source) throws IOException {
    out.writeArrayFieldStart(fieldName);
    for(final Long v: source) out.writeNumber(v);
    out.writeEndArray();
  }

  public Long[] readLongArray(final JsonParser in) throws IOException {
    final List<Long> accumulator = new ArrayList<>(4096);
    while(in.nextToken() != END_ARRAY) accumulator.add(in.getLongValue());
    return accumulator.toArray(new Long[accumulator.size()]);
  }

  public void writeBitSetFld(final String fieldName, final JsonGenerator out, final BitSet source) throws IOException {
    writeLongArrayFld(fieldName, out, toObject(source.getTrimmedImage()));
  }

  public <T extends DataMetaEntity> void writeCollection(final JsonGenerator out, final Collection<T> source,
                                                         Jsonable<T> js) throws IOException {
    if(source == null) out.writeNull();
    else {
      out.writeStartArray(source.size());
      for(final T e: source) {
        out.writeStartObject();
        js.write(out, e);
        out.writeEndObject();
      }
      out.writeEndArray();
    }
  }

  public <T extends DataMetaEntity> void writeCollectionFld(final String fieldName, final JsonGenerator out,
                                                            final Collection<T> source,
                                                            final Jsonable<T> js) throws IOException {
    if(source == null) out.writeNull();
    else {
      out.writeFieldName(fieldName);
      writeCollection(out, source, js);
    }
  }
  
  public <T extends DataMetaEntity> List<T> readList(final JsonParser in, final Jsonable<T> js) throws IOException {
    final List<T> accumulator = new ArrayList<>(4096);
    while(in.nextToken() != END_ARRAY) accumulator.add(js.read(in));
    return accumulator;
  }

  public <T extends DataMetaEntity> Set<T> readSet(final JsonParser in, final Jsonable<T> js) throws IOException {
    final Set<T> accumulator = new HashSet<>(4096);
    while(in.nextToken() != END_ARRAY) accumulator.add(js.read(in));
    return accumulator;
  }

  public <T extends DataMetaEntity> Deque<T> readDeque(final JsonParser in, final Jsonable<T> js) throws IOException {
    final Deque<T> accumulator = new java.util.LinkedList<>();
    while(in.nextToken() != END_ARRAY) accumulator.add(js.read(in));
    return accumulator;
  }

  public <T extends DataMetaEntity> List<Integer> readListInteger(final JsonParser in) throws IOException {
    final List<Integer> accumulator = new ArrayList<>(4096);
    while(in.nextToken() != END_ARRAY) accumulator.add(in.getIntValue());
    return accumulator;
  }

  public List<Long> readListLong(final JsonParser in) throws IOException {
    final List<Long> accumulator = new ArrayList<>(4096);
    while(in.nextToken() != END_ARRAY) accumulator.add(in.getLongValue());
    return accumulator;
  }
  
  public List<Float> readListFloat(final JsonParser in) throws IOException {
    final List<Float> accumulator = new ArrayList<>(4096);
    while(in.nextToken() != END_ARRAY) accumulator.add(in.getFloatValue());
    return accumulator;
  }
  
  public List<Double> readListDouble(final JsonParser in) throws IOException {
    final List<Double> accumulator = new ArrayList<>(4096);
    while(in.nextToken() != END_ARRAY) accumulator.add(in.getDoubleValue());
    return accumulator;
  }

  public <T extends DataMetaEntity>List<String> readListString(final JsonParser in) throws IOException {
    final List<String> accumulator = new java.util.ArrayList<>();
    while(in.nextToken() != END_ARRAY) accumulator.add(in.getText());
    return accumulator;
  }

  public <T extends DataMetaEntity> List<ZonedDateTime> readListZonedDateTime(final JsonParser in) throws IOException {
    final List<ZonedDateTime> accumulator = new java.util.ArrayList<>();
    while (in.nextToken() != END_ARRAY) accumulator.add(readDttm(in));
    return accumulator;
  }

  public <T extends DataMetaEntity> List<BigDecimal> readListBigDecimal(final JsonParser in) throws IOException {
    final List<BigDecimal> accumulator = new java.util.ArrayList<>();
    while(in.nextToken() != END_ARRAY) accumulator.add(readBigDecimal(in));
    return accumulator;
  }
  
  public <T extends DataMetaEntity> void writeListInteger(final String fieldName, final JsonGenerator out,
                                                          final List<Integer> source) throws IOException {
    out.writeArrayFieldStart(fieldName);
    for(final Integer i: source) out.writeNumber(i);
    out.writeEndArray();
  }

  public <T extends DataMetaEntity> void writeListLong(final String fieldName, final JsonGenerator out,
                                                       final List<Long> source) throws IOException {
    out.writeArrayFieldStart(fieldName);
    for(final Long v: source) out.writeNumber(v);
    out.writeEndArray();
  }

  public <T extends DataMetaEntity> void writeListFloat(final String fieldName, final JsonGenerator out,
                                                        final List<Float> source) throws IOException {
    out.writeArrayFieldStart(fieldName);
    for (final Float f : source) out.writeNumber(f);
    out.writeEndArray();
  }
  
  public <T extends DataMetaEntity> void writeListDouble(final String fieldName, final JsonGenerator out,
                                                         final List<Double> source) throws IOException {
    out.writeArrayFieldStart(fieldName);
    for(final Double d: source) out.writeNumber(d);
    out.writeEndArray();
  }
  
  public <T extends DataMetaEntity> void writeListString(final String fieldName, final JsonGenerator out,
                                                         final List<String> source) throws IOException {
    out.writeArrayFieldStart(fieldName);
    for(final String s: source) out.writeString(s);
    out.writeEndArray();
  }
  
  public <T extends DataMetaEntity> void writeListZonedDateTime(final String fieldName, final JsonGenerator out,
                                                                final List<ZonedDateTime> source) throws IOException {
    out.writeArrayFieldStart(fieldName);
    for(final ZonedDateTime d: source) out.writeString(DateTimeUtil.getInstance().toString(d));
    out.writeEndArray();
  }

  public <T extends DataMetaEntity> void writeListBigDecimal(final String fieldName, final JsonGenerator out,
                                                             final List<BigDecimal> source) throws IOException {
    out.writeArrayFieldStart(fieldName);
    for(final BigDecimal n: source) out.writeNumber(n);
    out.writeEndArray();
  }
  
  // ************* Deques:
  
  public <T extends DataMetaEntity> List<Integer> readLinkedListInteger(final JsonParser in) throws IOException {
    final List<Integer> accumulator = new java.util.LinkedList<>();
    while(in.nextToken() != END_ARRAY) accumulator.add(in.getIntValue());
    return accumulator;
  }

  public <T extends DataMetaEntity> List<Long> readLinkedListLong(final JsonParser in) throws IOException {
    final List<Long> accumulator = new java.util.LinkedList<>();
    while(in.nextToken() != END_ARRAY) accumulator.add(in.getLongValue());
    return accumulator;
  }

  public <T extends DataMetaEntity> List<Float> readLinkedListFloat(final JsonParser in) throws IOException {
    final List<Float> accumulator = new java.util.LinkedList<>();
    while(in.nextToken() != END_ARRAY) accumulator.add(in.getFloatValue());
    return accumulator;
  }

  public <T extends DataMetaEntity> List<Double> readLinkedListDouble(final JsonParser in) throws IOException {
    final List<Double> accumulator = new java.util.LinkedList<>();
    while(in.nextToken() != END_ARRAY) accumulator.add(in.getDoubleValue());
    return accumulator;
  }

  public <T extends DataMetaEntity> List<String> readLinkedListString(final JsonParser in) throws IOException {
    final List<String> accumulator = new java.util.LinkedList<>();
    while(in.nextToken() != END_ARRAY) accumulator.add(in.getText());
    return accumulator;
  }

  public <T extends DataMetaEntity> List<ZonedDateTime> readLinkedListZonedDateTime(final JsonParser in) throws IOException {
    final List<ZonedDateTime> accumulator = new LinkedList<>();
    while(in.nextToken() != END_ARRAY) accumulator.add(readDttm(in));
    return accumulator;
  }

  public <T extends DataMetaEntity> List<BigDecimal> readLinkedListBigDecimal(final JsonParser in) throws IOException {
    final List<BigDecimal> accumulator = new LinkedList<>();
    while(in.nextToken() != END_ARRAY) accumulator.add(readBigDecimal(in));
    return accumulator;
  }

  public <T extends DataMetaEntity> void writeLinkedListInteger(final String fieldName, final JsonGenerator out,
                                         final java.util.LinkedList<Integer> source) throws IOException {
    out.writeArrayFieldStart(fieldName);
    for(final Integer i: source) out.writeNumber(i);
    out.writeEndArray();
  }

  public <T extends DataMetaEntity> void writeLinkedListLong(final String fieldName, final JsonGenerator out,
                                         final java.util.LinkedList<Long> source) throws IOException {
    out.writeArrayFieldStart(fieldName);
    for(final Long v: source) out.writeNumber(v);
    out.writeEndArray();
  }

  public <T extends DataMetaEntity> void writeLinkedListFloat(final String fieldName, final JsonGenerator out,
                                         final java.util.LinkedList<Float> source) throws IOException {
    out.writeArrayFieldStart(fieldName);
    for(final Float f: source) out.writeNumber(f);
    out.writeEndArray();
  }

  public <T extends DataMetaEntity> void writeLinkedListDouble(final String fieldName, final JsonGenerator out,
                                         final java.util.LinkedList<Double> source) throws IOException {
    out.writeArrayFieldStart(fieldName);
    for(final Double d: source) out.writeNumber(d);
    out.writeEndArray();
  }

  public <T extends DataMetaEntity> void writeLinkedListString(final String fieldName, final JsonGenerator out,
                                                               final LinkedList<String> source) throws IOException {
    out.writeArrayFieldStart(fieldName);
    for(final String s: source) out.writeString(s);
    out.writeEndArray();
  }

  public <T extends DataMetaEntity> void writeLinkedListZonedDateTime(final String fieldName, final JsonGenerator out,
                                         final LinkedList<ZonedDateTime> source) throws IOException {
    out.writeArrayFieldStart(fieldName);
    for(final ZonedDateTime d: source) out.writeString(DateTimeUtil.getInstance().toString(d));
    out.writeEndArray();
  }

  public <T extends DataMetaEntity> void writeLinkedListBigDecimal(final String fieldName, final JsonGenerator out,
                                         final LinkedList<BigDecimal> source) throws IOException {
    out.writeArrayFieldStart(fieldName);
    for(final BigDecimal n: source) out.writeNumber(n);
    out.writeEndArray();
  }

  // ************* Sets:

  public <T extends DataMetaEntity> Set<Integer> readSetInteger(final JsonParser in) throws IOException {
    final Set<Integer> accumulator = new java.util.HashSet<>();
    while(in.nextToken() != END_ARRAY) accumulator.add(in.getIntValue());
    return accumulator;
  }

  public <T extends DataMetaEntity> Set<Long> readSetLong(final JsonParser in) throws IOException {
    final Set<Long> accumulator = new java.util.HashSet<>();
    while(in.nextToken() != END_ARRAY) accumulator.add(in.getLongValue());
    return accumulator;
  }

  public <T extends DataMetaEntity> Set<Float> readSetFloat(final JsonParser in) throws IOException {
    final Set<Float> accumulator = new java.util.HashSet<>();
    while(in.nextToken() != END_ARRAY) accumulator.add(in.getFloatValue());
    return accumulator;
  }

  public <T extends DataMetaEntity> Set<Double> readSetDouble(final JsonParser in) throws IOException {
    final Set<Double> accumulator = new java.util.HashSet<>();
    while(in.nextToken() != END_ARRAY) accumulator.add(in.getDoubleValue());
    return accumulator;
  }

  public <T extends DataMetaEntity> Set<String> readSetString(final JsonParser in) throws IOException {
    final Set<String> accumulator = new java.util.HashSet<>();
    while(in.nextToken() != END_ARRAY) accumulator.add(in.getText());
    return accumulator;
  }

  public <T extends DataMetaEntity> Set<ZonedDateTime> readSetZonedDateTime(final JsonParser in) throws IOException {
    final Set<ZonedDateTime> accumulator = new java.util.HashSet<>();
    while(in.nextToken() != END_ARRAY) accumulator.add(readDttm(in));
    return accumulator;
  }

  public <T extends DataMetaEntity> Set<BigDecimal> readSetBigDecimal(final JsonParser in) throws IOException {
    final Set<BigDecimal> accumulator = new java.util.HashSet<>();
    while(in.nextToken() != END_ARRAY) accumulator.add(readBigDecimal(in));
    return accumulator;
  }

  public <T extends DataMetaEntity> void writeSetInteger(final String fieldName, final JsonGenerator out,
                                                         final Set<Integer> source) throws IOException {

    out.writeArrayFieldStart(fieldName);
    for (final Integer i : source) out.writeNumber(i);
    out.writeEndArray();
  }

  public <T extends DataMetaEntity> void writeSetLong(final String fieldName, final JsonGenerator out,
                                                      final Set<Long> source) throws IOException {
    out.writeArrayFieldStart(fieldName);
    for(final Long v: source) out.writeNumber(v);
    out.writeEndArray();
  }

  public <T extends DataMetaEntity> void writeSetFloat(final String fieldName, final JsonGenerator out,
                                                       final Set<Float> source) throws IOException {
    out.writeArrayFieldStart(fieldName);
    for(final Float f: source) out.writeNumber(f);
    out.writeEndArray();
  }

  public <T extends DataMetaEntity> void writeSetDouble(final String fieldName, final JsonGenerator out,
                                                        final Set<Double> source) throws IOException {
    out.writeArrayFieldStart(fieldName);
    for(final Double d: source) out.writeNumber(d);
    out.writeEndArray();
  }

  public <T extends DataMetaEntity> void writeSetString(final String fieldName, final JsonGenerator out,
                                                        final Set<String> source) throws IOException {
    out.writeArrayFieldStart(fieldName);
    for(final String s: source) out.writeString(s);
    out.writeEndArray();
  }

  public <T extends DataMetaEntity> void writeSetZonedDateTime(final String fieldName, final JsonGenerator out,
                                                               final Set<ZonedDateTime> source) throws IOException {
    out.writeArrayFieldStart(fieldName);
    for(final ZonedDateTime d: source) out.writeString(DateTimeUtil.getInstance().toString(d));
    out.writeEndArray();
  }

  public <T extends DataMetaEntity> void writeSetBigDecimal(final String fieldName, final JsonGenerator out,
                                                            final Set<BigDecimal> source) throws IOException {
    out.writeArrayFieldStart(fieldName);
    for(final BigDecimal n: source) out.writeNumber(n);
    out.writeEndArray();
  }

}
