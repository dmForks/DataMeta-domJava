package org.ebay.datameta.dom;

import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;
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
   * @return textual representation of the argument per {@link DateTimeFormatter#ISO_DATE_TIME}
   */
  public String toString(final ZonedDateTime dateTime) {
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

}
