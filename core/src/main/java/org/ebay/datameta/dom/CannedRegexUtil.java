package org.ebay.datameta.dom;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utilities for "canned" regexes, most common ones like an email, a phone number etc. 
 *
 * @author Michael Bergens
 */
public class CannedRegexUtil {
    /**
     * <a href="http://www.ietf.org/rfc/rfc5322.txt">RFC 5322</a> - inspired email regex .
     */
    private final static Pattern EMAIL = java.util.regex.Pattern.compile("(?:[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");

    /**
     * Idea <a href="http://www.regextester.com/17">borrowed from here</a>, improved to support any country code.
     */
    private final static Pattern PHONE = Pattern.compile("^(?:(?:\\+?\\d+\\s*(?:[.-]\\s*)?)?(?:\\(\\s*([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9])\\s*\\)|([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9]))\\s*(?:[.-]\\s*)?)?([2-9]1[02-9]|[2-9][02-9]1|[2-9][02-9]{2})\\s*(?:[.-]\\s*)?([0-9]{4})(?:\\s*(?:#|x\\.?|ext\\.?|extension)\\s*(\\d+))?$");

    /**
     *
     */
    private final static Pattern UUID = Pattern.compile("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}");

    public final static String EMAIL_KEY = "email";
    public final static String PHONE_KEY = "phone";
    public final static String UUID_KEY_LOWER = "uuid";
    public final static String UUID_KEY_UPPER = "UUID";

    private static final Map<String, Pattern> RX = Collections.unmodifiableMap(
        Stream.of(
            new AbstractMap.SimpleEntry<>(EMAIL_KEY, EMAIL),
            new AbstractMap.SimpleEntry<>(PHONE_KEY, PHONE),
            new AbstractMap.SimpleEntry<>(UUID_KEY_LOWER, UUID),
            new AbstractMap.SimpleEntry<>(UUID_KEY_UPPER, UUID)
        ).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue))
    );

    /**
     * Retrieves the {@link Pattern} for the given key.
     * @param key canned expression name
     * @return the instance of the {@link Pattern} that matches the argument
     * @throws IllegalArgumentException if the key is invalid (undefined).
     */
    public static Pattern getCannedRegEx(final String key) {

        if(!RX.containsKey(key)) throw new IllegalArgumentException("There is no canned regex with the name \"" +
            key + "\" defined");

        return RX.get(key);
    }
}
