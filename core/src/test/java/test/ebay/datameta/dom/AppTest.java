package test.ebay.datameta.dom;

import org.ebay.datameta.dom.BitSet;
import org.ebay.datameta.dom.DateTimeUtil;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.util.Formatter;
import java.util.regex.Pattern;

import static org.ebay.datameta.dom.CannedRegexUtil.EMAIL_KEY;
import static org.ebay.datameta.dom.CannedRegexUtil.getCannedRegEx;
import static org.ebay.datameta.dom.DataMetaSame.EQ;
import static org.ebay.datameta.dom.DateTimeUtil.UTC_FMT;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author Michael Bergens
 */
public class AppTest {

    private static final Logger L = LoggerFactory.getLogger(AppTest.class);

    private static final DateTimeUtil DTU = DateTimeUtil.getInstance();

  /**
   * This test reflects an attempt to save some space by stripping the <tt>[UTC]</tt> ending from the ISO format.
   * It didn't work because, without the <tt>[UTC]</tt> ending, parsing the string ending with <tt>Z</tt>,
   * sets timezone ID to "Z" not to the "UTC" as otherwise. Therefore, comparison of the datetime with the
   * timezone set to <tt>UTC</tt> with the timezone set to <tt>Z</tt> fails. Hence, at the expense of extra
   * 5 characters we get predictable (de)serialization to/from textual including but not limited to JSON.
   */
  @Test public void testUtcEndStrip() {
      final String preUtcPart = "2016-12-30T12:12:54.719Z";
      final String fullIsoSpec = preUtcPart + UTC_FMT;
      final ZonedDateTime dttm = DTU.parse(fullIsoSpec);
      final String str = DTU.toString(dttm);
      L.info("{} round trip parsed into {}", fullIsoSpec, str);
      assertThat(str, is(fullIsoSpec)); // should NOT be stripped from the "[UTC]" ending.
      final ZonedDateTime reDttm = DTU.parse(str);
      assertThat(dttm, is(reDttm)); // make sure it's parsed back into the right value.
    }

    @Test public void testBitSetImage() throws Exception {
        final int bCount = 67;
        BitSet bs = new BitSet(bCount);
        for(int i = 0; i < bCount; i++) {
            bs.set(i);
            long[] image = bs.getTrimmedImage();
            StringBuilder sb = new StringBuilder(1024);
            Formatter fm  = new Formatter(sb);

            for (long l : image) {
                fm.format("%016X.", l);
            }
            L.info("Bit {}, image: {}", i, sb);
        }

    }

    @Before public void init() {}

    @Test public void testNaturalDataMetaSame() {
        Integer one = 1, another = 1, different = 2;

        assertTrue(EQ.isSame(one, another));
        assertTrue(EQ.isSame(null, null));
        assertFalse(EQ.isSame(one, null));
        assertFalse(EQ.isSame(null, another));
        assertFalse(EQ.isSame(one, different));
    }

    @Test public void testEmailRegex() {
      final String[] emails = new String[]{
        "johndoe@gmail.com",
        "johndoe123@gmail.com",
        "johnDoe123@gmail.com",
        "Johndoe123@gmail.com",
        "JohnDoe@gmail.com"
      };

      final Pattern emailPattern = getCannedRegEx(EMAIL_KEY);
      for (final String email : emails) {
        L.info("Testing email \"{}\"", email);
        assertTrue("Email check failed for \"" + email + '\"', emailPattern.matcher(email).matches());
      }
    }
}
