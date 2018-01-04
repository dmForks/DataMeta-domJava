package org.ebay.datameta.dom;

import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;
import static org.apache.commons.lang3.StringUtils.removeEnd;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

/**
 * Datetime serialization/deserialization idioms. These are purely convenience methods to save on typing
 * and slowing down to remember the exact idiom.
 *
 * @author mubergens Michael Bergens
 */
public class DateTimeUtil {
  /**
    * Standard clock to use for the `now` methods. This implementation is immutable and thread-safe.
    */
  public static final Clock CLOCK = Clock.systemUTC();

  /**
   * UTC timezone ID.
   */
  public static final String UTC = "UTC";

  /**
   * UTC timezone ID enclosed in square brackets as it is appended by the standard JDK ISO datetime format,
   * also containing the <tt>z</tt> at the end - this is unnecessary and better be removed.
   */
  public static final String UTC_FMT = "[" + UTC + ']';

  /**
   * UTC zone ID. It is available in some 3rd party libs, but not in the JDK, keep it here for convenience
   * and to avoid unneeded dependencies.
   */
  public static final ZoneId UTC_ID = ZoneId.of(UTC);

  private DateTimeUtil() {}

  private final static DateTimeUtil INSTANCE = new DateTimeUtil();

  public static DateTimeUtil getInstance() { return INSTANCE;}

  /**
    * Parses the argument using {@link DateTimeFormatter#ISO_DATE_TIME}.
    */
  public ZonedDateTime parse(final String text) {
    return ZonedDateTime.from(ISO_DATE_TIME.parse(text));
  }

  /**
   * Serializes the {@link ZonedDateTime} argument into the ISO format, using {@link DateTimeFormatter#ISO_DATE_TIME}.
   *
   * @param dateTime the method does not check if the argument is in UTC, leaves this to the caller
   * @return textual representation of the argument per {@link DateTimeFormatter#ISO_DATE_TIME}.
   */
  public String toString(final ZonedDateTime dateTime) {
    //return removeEnd(ISO_DATE_TIME.format(dateTime), UTC_FMT);
    // removing the [UTC] ending will set the timezone id to "Z" not to "UTC", which means, the datetime comparison
    // with UTC datetime will fail.
    return ISO_DATE_TIME.format(dateTime);
  }

  /**
   * Serializes the {@link OffsetDateTime} argument into the ISO format, using {@link DateTimeFormatter#ISO_DATE_TIME}
   *
   * @param dateTime the method does not check if the argument is in UTC, leaves this to the caller
   * @return textual representation of the argument per {@link DateTimeFormatter#ISO_DATE_TIME}
   */
  public String toString(final OffsetDateTime dateTime) {
    return ISO_DATE_TIME.format(dateTime);
  }

  public ZonedDateTime now() { return ZonedDateTime.now(CLOCK); }
}
