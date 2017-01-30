package org.ebay.datameta.ser.bytes;

import org.ebay.datameta.util.jdk.Api;
import org.ebay.datameta.dom.BitSet;
import org.ebay.datameta.dom.DataMetaEntity;
import org.apache.hadoop.io.Text;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.time.Instant.ofEpochMilli;
import static java.time.ZoneOffset.UTC; // immutable, thread-safe
import static java.time.ZonedDateTime.ofInstant;
import static org.apache.hadoop.io.WritableUtils.readVInt;
import static org.apache.hadoop.io.WritableUtils.readVLong;
import static org.apache.hadoop.io.WritableUtils.writeVInt;
import static org.apache.hadoop.io.WritableUtils.writeVLong;

public class DataMetaHadoopUtil {

    private final static Map<String, Integer> TZ_ID_TO_KEY;
    private final static String[] KEY_TO_TZ_ID;

    static private int registerPair(final String zoneId, final int zoneKey, final Map<String, Integer> tzIdToKeyMap
        , final Map<Integer, String> keyToTzIdMap, int count) {
        count++;
        @SuppressWarnings("unused") final ZoneId tz = ZoneId.of(zoneId); // verify that the ID is still valid
        tzIdToKeyMap.put(zoneId, zoneKey);
        keyToTzIdMap.put(zoneKey, zoneId);
        return count;
    }

    static {
        int count = 0;
        final int initialMapSize = 100;
        final Map<String, Integer> tzIdToKey = new HashMap<>(initialMapSize);
        final Map<Integer, String> keyToTzId = new HashMap<>(initialMapSize);
        count = registerPair("Z", count, tzIdToKey, keyToTzId, count);
        count = registerPair("UTC", count, tzIdToKey, keyToTzId, count);
        count = registerPair("GMT", count, tzIdToKey, keyToTzId, count);
        count = registerPair("America/New_York", count, tzIdToKey, keyToTzId, count);
        count = registerPair("America/Chicago", count, tzIdToKey, keyToTzId, count);
        count = registerPair("America/Denver", count, tzIdToKey, keyToTzId, count);
        count = registerPair("America/Phoenix", count, tzIdToKey, keyToTzId, count);
        count = registerPair("America/Los_Angeles", count, tzIdToKey, keyToTzId, count);
        count = registerPair("America/Anchorage", count, tzIdToKey, keyToTzId, count);
        count = registerPair("Pacific/Honolulu", count, tzIdToKey, keyToTzId, count);
/*        count = registerPair("Asia/Kolkata", count, tzIdToKey, keyToTzId, count);
        count = registerPair("Asia/Shanghai", count, tzIdToKey, keyToTzId, count);
        count = registerPair("Europe/London", count, tzIdToKey, keyToTzId, count);
        count = registerPair("Europe/Paris", count, tzIdToKey, keyToTzId, count);
        count = registerPair("Europe/Berlin", count, tzIdToKey, keyToTzId, count);
        count = registerPair("Europe/Istanbul", count, tzIdToKey, keyToTzId, count); */
        KEY_TO_TZ_ID = new String[count];
        for (int i = 0; i < count; i++) {
            final String tzId = keyToTzId.get(i);
            if(tzId != null) KEY_TO_TZ_ID[i] = tzId;
        }
        TZ_ID_TO_KEY = Collections.unmodifiableMap(tzIdToKey);
    }

    @Api @Nonnull public static Set<String> getAllTzIds() {
        return new HashSet<>(Arrays.asList(KEY_TO_TZ_ID));
    }

    private static String getTzId(final int key) {
        return KEY_TO_TZ_ID[key];
    }

    private static int getTzKey(final String tzId) {
        final Integer key = TZ_ID_TO_KEY.get(tzId);
        if(key == null) throw new UnsupportedOperationException("Time Zone ID not supported: " + tzId);
        return key;
    }
    /**
     * Safe set on the target: if the source is null, sets to an empty string, otherwise to the source.
     * @param target on which instance to set the text.
     * @param source if it's
     */
    @Api public static void setTextualIfAny(final Text target, @Nullable final CharSequence source) {
        target.set(source == null ? "" : source.toString());
    }

    /**
     * Pair to {@link #readText(DataInput)}, writes either the argument, or an empty string if null.
     */
    public static void writeTextIfAny(final DataOutput out, @Nullable final String source) throws IOException {
        Text.writeString(out, source == null ? "" : source);
    }

    /**
     * Simple pair to {@link #writeTextIfAny(DataOutput, String)}, simple wrapper around {@link Text#readString(DataInput)}.
     */
    public static String readText(final DataInput in) throws IOException {
        return Text.readString(in);
    }

    public static void writeDttm(final DataOutput out, final ZonedDateTime dttm) throws IOException {
        // Text.writeString writes more compact string than WritableUtils.writeString, it uses VInt instead of VInt for length
        // for short strings such as Time Zone Id, that's marginally better (why waste even 3 bytes)
        writeVInt(out, getTzKey(dttm.getZone().getId()));
        writeVLong(out, dttm.toInstant().toEpochMilli()); // for 2012, the millis already occcupy 6 bytes out of 8 in a long
        // but this occupation will last for another 7K years; for 7K years we'd be saving 1 byte per datetime instance
        // by using VLong. After that, we'd be using all 8 bytes for another 2M years.
        // so it makes no sense bothering with VLong format, adding 1 byte for length (total 7 vs 8)
        // Java millis format overflow in some 292 million years.
    }
    public static ZonedDateTime readDttm(final DataInput in) throws IOException {
        final String timeZoneId = getTzId(readVInt(in));
        final long millis = readVLong(in);
        final ZoneId tz = ZoneId.of(timeZoneId);
        return ofInstant(ofEpochMilli(millis), tz);
    }

    /**
     * Saving with UTC saves one byte of a time zone and relieves the headache of maintaining one.
     * Since all the dates are UTC and there is no TZ key, it makes easy to sort them.
     */
    public static void writeDttmUtc(final DataOutput out, final ZonedDateTime dttm) throws IOException {
        writeVLong(out, dttm.withZoneSameLocal(UTC).toInstant().toEpochMilli());
    }

    @Nonnull public static ZonedDateTime readDttmUtc(final DataInput in) throws IOException {
        return ofInstant(ofEpochMilli(readVLong(in)), UTC);
    }

    /**
     * Uses the binary format -- likely to be incompatible cross-platform, use with caution if ever.
     */
    public static void writeBigDecimalBin(final DataOutput out, final BigDecimal bigDecimal) throws IOException {
        final BigInteger bi = bigDecimal.unscaledValue();
        final int scale = bigDecimal.scale();
        writeVInt(out, scale);
        final byte[] bigIntImage = bi.toByteArray();
        writeVInt(out, bigIntImage.length);
        out.write(bigIntImage);
    }

    /**
     * Uses the binary format -- likely to be incompatible cross-platform, use with caution if ever.
     */
    public static BigDecimal readBigDecimalBin(final DataInput in) throws IOException {
        final int scale = readVInt(in);
        final int len = readVInt(in);
        final byte[] bigIntImage = new byte[len];
        in.readFully(bigIntImage);
        final BigInteger bi = new BigInteger(bigIntImage);
        return new BigDecimal(bi, scale);
    }

    /**
     *  Uses string representation - most likely to be compatible cross-platform although may take more space than
     *  the binary version.
     */
    public static void writeBigDecimal(final DataOutput out, final BigDecimal bigDecimal) throws IOException {
        writeTextIfAny(out, bigDecimal.toString());
    }
    /**
     *  Uses string representation - most likely to be compatible cross-platform although may take more space than
     *  the binary version.
     */
    public static BigDecimal readBigDecimal(final DataInput in) throws IOException {
        return new BigDecimal(readText(in));
    }

    /**
     * Writes uncompressed byte array with the length first as VInt.
     * @see #readByteArray(DataInput)
     */
    public static void writeByteArray(final DataOutput out, final byte[] bytes) throws IOException {
        writeVInt(out, bytes.length);
        if(bytes.length > 0) out.write(bytes);
    }

    /**
     * Pair to {@link #writeByteArray(DataOutput, byte[])}.
     */
    public static byte[] readByteArray(final DataInput in) throws IOException {
        final int len = readVInt(in);
        if(len < 1) return new byte[0];
        final byte[] result = new byte[len];
        in.readFully(result);
        return result;
    }

    public static void writeLongArray(final DataOutput out, final long[] array) throws IOException {
        writeVInt(out, array.length);
        if(array.length > 0) {//noinspection ForLoopReplaceableByForEach
            for (int ix = 0; ix < array.length; ix++) writeVLong(out, array[ix]);
        }
    }

    public static long[] readLongArray(final DataInput in) throws IOException {
        final int len = readVInt(in);
        if(len < 1) return new long[0];
        final long[] result = new long[len];
        for (int ix = 0; ix < len; ix++) result[ix] = readVLong(in);
        return result;
    }

    public static void writeBitSet(final DataOutput out, final BitSet bitSet) throws IOException {
        writeLongArray(out, bitSet.getTrimmedImage());
    }

    public static <T extends DataMetaEntity> void writeCollection(final Collection<T> val, final DataOutput out, final InOutable<T> io) throws IOException {
        if(val != null) { /* if it is null, then the nullFlags had been set, don't need to do anything
               scalar implementation goes like this:
               if(val.getEmbo() != null) Embodiment_InOutable.getInstance().write(out, val.getEmbo()); */
           writeVInt(out, val.size());
           for(final T e: val)  {
              io.write(out, e);
           }
        }
    }

    public static <T extends DataMetaEntity> List<T> readList(final DataInput in, final InOutable<T> io) throws IOException {
        final int size = readVInt(in);
        final List<T> result = new ArrayList<>(size);
        for(int i = 0; i < size; i++) {
            result.add(io.read(in));
        }
        return result;
    }

    public static <T extends DataMetaEntity> Set<T> readSet(final DataInput in, final InOutable<T> io) throws IOException {
        final int size = readVInt(in);
        final Set<T> result = new HashSet<>(size * 3 /4 + 1);
        for(int i = 0; i < size; i++) {
            result.add(io.read(in));
        }
        return result;
    }

    public static <T extends DataMetaEntity> Deque<T> readDeque(final DataInput in, final InOutable<T> io) throws IOException {
        final int size = readVInt(in);
        final Deque<T> result = new LinkedList<>();
        for(int i = 0; i < size; i++) {
            result.add(io.read(in));
        }
        return result;
    }
    
    public static List<Integer> readListInteger(final DataInput in) throws IOException {
        final int size = readVInt(in);
        final List<Integer> result = new ArrayList<>(size);
        for(int i = 0; i < size; i++) {
            result.add(readVInt(in));
        }
        return result;
    }
    public static List<Long> readListLong(final DataInput in) throws IOException {
        final int size = readVInt(in);
        final List<Long> result = new ArrayList<>(size);
        for(int i = 0; i < size; i++) {
            result.add(readVLong(in));
        }
        return result;
    }
    public static List<Float> readListFloat(final DataInput in) throws IOException {
        final int size = readVInt(in);
        final List<Float> result = new ArrayList<>(size);
        for(int i = 0; i < size; i++) {
            result.add(in.readFloat());
        }
        return result;
    }
    public static List<Double> readListDouble(final DataInput in) throws IOException {
        final int size = readVInt(in);
        final List<Double> result = new ArrayList<>(size);
        for(int i = 0; i < size; i++) {
            result.add(in.readDouble());
        }
        return result;
    }

    public static List<String> readListString(final DataInput in) throws IOException {
        final int size = readVInt(in);
        final List<String> result = new ArrayList<>(size);
        for(int i = 0; i < size; i++) {
            result.add(readText(in));
        }
        return result;
    }

    public static List<ZonedDateTime> readListZonedDateTime(final DataInput in) throws IOException {
        final int size = readVInt(in);
        final List<ZonedDateTime> result = new ArrayList<>(size);
        for(int i = 0; i < size; i++) {
            result.add(readDttm(in));
        }
        return result;
    }
    
    public static List<BigDecimal> readListBigDecimal(final DataInput in) throws IOException {
        final int size = readVInt(in);
        final List<BigDecimal> result = new ArrayList<>(size);
        for(int i = 0; i < size; i++) {
            result.add(readBigDecimal(in));
        }
        return result;
    }
    
    public static void writeListInteger(final DataOutput out, List<Integer> vals) throws IOException {
        if(vals != null) { 
            writeVInt(out, vals.size());
            for(final Integer e: vals)  {
                writeVInt(out, e);
            }
        }
    }
    public static void writeListLong(final DataOutput out, List<Long> vals) throws IOException {
        if(vals != null) {
            writeVInt(out, vals.size());
            for(final Long e: vals)  {
                writeVLong(out, e);
            }
        }
    }
    public static void writeListFloat(final DataOutput out, List<Float> vals) throws IOException {
        if(vals != null) {
            writeVInt(out, vals.size());
            for(final Float e: vals)  {
                out.writeFloat(e);
            }
        }
    }
    public static void writeListDouble(final DataOutput out, List<Double> vals) throws IOException {
        if(vals != null) {
            writeVInt(out, vals.size());
            for(final Double e: vals)  {
                out.writeDouble(e);
            }
        }
    }

    public static void writeListString(final DataOutput out, List<String> vals) throws IOException {
        if(vals != null) {
            writeVInt(out, vals.size());
            for(final String e: vals)  {
                writeTextIfAny(out, e);
            }
        }
    }
    public static void writeListZonedDateTime(final DataOutput out, List<ZonedDateTime> vals) throws IOException {
        if(vals != null) {
            writeVInt(out, vals.size());
            for(final ZonedDateTime e: vals)  {
                writeDttm(out, e);
            }
        }
    }
    public static void writeListBigDecimal(final DataOutput out, List<BigDecimal> vals) throws IOException {
        if(vals != null) {
            writeVInt(out, vals.size());
            for(final BigDecimal e: vals)  {
                writeBigDecimal(out, e);
            }
        }
    }
    /// Deques
    public static LinkedList<Integer> readLinkedListInteger(final DataInput in) throws IOException {
        final int size = readVInt(in);
        final LinkedList<Integer> result = new LinkedList<>();
        for(int i = 0; i < size; i++) {
            result.add(readVInt(in));
        }
        return result;
    }
    public static LinkedList<Long> readLinkedListLong(final DataInput in) throws IOException {
        final int size = readVInt(in);
        final LinkedList<Long> result = new LinkedList<>();
        for(int i = 0; i < size; i++) {
            result.add(readVLong(in));
        }
        return result;
    }
    public static LinkedList<Float> readLinkedListFloat(final DataInput in) throws IOException {
        final int size = readVInt(in);
        final LinkedList<Float> result = new LinkedList<>();
        for(int i = 0; i < size; i++) {
            result.add(in.readFloat());
        }
        return result;
    }
    public static LinkedList<Double> readLinkedListDouble(final DataInput in) throws IOException {
        final int size = readVInt(in);
        final LinkedList<Double> result = new LinkedList<>();
        for(int i = 0; i < size; i++) {
            result.add(in.readDouble());
        }
        return result;
    }

    public static LinkedList<ZonedDateTime> readLinkedListZonedDateTime(final DataInput in) throws IOException {
        final int size = readVInt(in);
        final LinkedList<ZonedDateTime> result = new LinkedList<>();
        for(int i = 0; i < size; i++) {
            result.add(readDttm(in));
        }
        return result;
    }

    public static LinkedList<BigDecimal> readLinkedListBigDecimal(final DataInput in) throws IOException {
        final int size = readVInt(in);
        final LinkedList<BigDecimal> result = new LinkedList<>();
        for(int i = 0; i < size; i++) {
            result.add(readBigDecimal(in));
        }
        return result;
    }

    public static void writeLinkedListInteger(final DataOutput out, LinkedList<Integer> vals) throws IOException {
        if(vals != null) {
            writeVInt(out, vals.size());
            for(final Integer e: vals)  {
                writeVInt(out, e);
            }
        }
    }
    public static void writeLinkedListLong(final DataOutput out, LinkedList<Long> vals) throws IOException {
        if(vals != null) {
            writeVInt(out, vals.size());
            for(final Long e: vals)  {
                writeVLong(out, e);
            }
        }
    }
    public static void writeLinkedListFloat(final DataOutput out, LinkedList<Float> vals) throws IOException {
        if(vals != null) {
            writeVInt(out, vals.size());
            for(final Float e: vals)  {
                out.writeFloat(e);
            }
        }
    }
    public static void writeLinkedListDouble(final DataOutput out, LinkedList<Double> vals) throws IOException {
        if(vals != null) {
            writeVInt(out, vals.size());
            for(final Double e: vals)  {
                out.writeDouble(e);
            }
        }
    }
    public static void writeLinkedListZonedDateTime(final DataOutput out, LinkedList<ZonedDateTime> vals) throws IOException {
        if(vals != null) {
            writeVInt(out, vals.size());
            for(final ZonedDateTime e: vals)  {
                writeDttm(out, e);
            }
        }
    }
    public static void writeLinkedListBigDecimal(final DataOutput out, LinkedList<BigDecimal> vals) throws IOException {
        if(vals != null) {
            writeVInt(out, vals.size());
            for(final BigDecimal e: vals)  {
                writeBigDecimal(out, e);
            }
        }
    }
    
    // Sets
    public static Set<Integer> readSetInteger(final DataInput in) throws IOException {
        final int size = readVInt(in);
        final Set<Integer> result = new HashSet<>(size * 3 /4 +1);
        for(int i = 0; i < size; i++) {
            result.add(readVInt(in));
        }
        return result;
    }
    public static Set<Long> readSetLong(final DataInput in) throws IOException {
        final int size = readVInt(in);
        final Set<Long> result = new HashSet<>(size * 3 /4 +1);
        for(int i = 0; i < size; i++) {
            result.add(readVLong(in));
        }
        return result;
    }
    public static Set<Float> readSetFloat(final DataInput in) throws IOException {
        final int size = readVInt(in);
        final Set<Float> result = new HashSet<>(size * 3 /4 +1);
        for(int i = 0; i < size; i++) {
            result.add(in.readFloat());
        }
        return result;
    }
    public static Set<Double> readSetDouble(final DataInput in) throws IOException {
        final int size = readVInt(in);
        final Set<Double> result = new HashSet<>(size * 3 /4 +1);
        for(int i = 0; i < size; i++) {
            result.add(in.readDouble());
        }
        return result;
    }

    public static Set<ZonedDateTime> readSetZonedDateTime(final DataInput in) throws IOException {
        final int size = readVInt(in);
        final Set<ZonedDateTime> result = new HashSet<>(size * 3 /4 +1);
        for(int i = 0; i < size; i++) {
            result.add(readDttm(in));
        }
        return result;
    }

    public static Set<BigDecimal> readSetBigDecimal(final DataInput in) throws IOException {
        final int size = readVInt(in);
        final Set<BigDecimal> result = new HashSet<>(size * 3 /4 +1);
        for(int i = 0; i < size; i++) {
            result.add(readBigDecimal(in));
        }
        return result;
    }

    public static Set<String> readSetString(final DataInput in) throws IOException {
        final int size = readVInt(in);
        final Set<String> result = new HashSet<>(size * 3 /4 +1);
        for(int i = 0; i < size; i++) {
            result.add(readText(in));
        }
        return result;
    }
    public static void writeSetInteger(final DataOutput out, Set<Integer> vals) throws IOException {
        if(vals != null) {
            writeVInt(out, vals.size());
            for(final Integer e: vals)  {
                writeVInt(out, e);
            }
        }
    }
    public static void writeSetString(final DataOutput out, Set<String> vals) throws IOException {
        if(vals != null) {
            writeVInt(out, vals.size());
            for(final String e: vals)  {
                writeTextIfAny(out, e);
            }
        }
    }
    public static void writeSetLong(final DataOutput out, Set<Long> vals) throws IOException {
        if(vals != null) {
            writeVInt(out, vals.size());
            for(final Long e: vals)  {
                writeVLong(out, e);
            }
        }
    }
    public static void writeSetFloat(final DataOutput out, Set<Float> vals) throws IOException {
        if(vals != null) {
            writeVInt(out, vals.size());
            for(final Float e: vals)  {
                out.writeFloat(e);
            }
        }
    }
    public static void writeSetDouble(final DataOutput out, Set<Double> vals) throws IOException {
        if(vals != null) {
            writeVInt(out, vals.size());
            for(final Double e: vals)  {
                out.writeDouble(e);
            }
        }
    }
    public static void writeSetZonedDateTime(final DataOutput out, Set<ZonedDateTime> vals) throws IOException {
        if(vals != null) {
            writeVInt(out, vals.size());
            for(final ZonedDateTime e: vals)  {
                writeDttm(out, e);
            }
        }
    }

    public static void writeSetBigDecimal(final DataOutput out, Set<BigDecimal> vals) throws IOException {
        if(vals != null) {
            writeVInt(out, vals.size());
            for(final BigDecimal e: vals)  {
                writeBigDecimal(out, e);
            }
        }
    }
}
