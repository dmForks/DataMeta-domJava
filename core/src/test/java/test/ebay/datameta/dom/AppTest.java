package test.ebay.datameta.dom;

import org.ebay.datameta.dom.BitSet;
import org.ebay.datameta.dom.CannedRegexUtil;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Formatter;
import java.util.regex.Pattern;

import static org.ebay.datameta.dom.CannedRegexUtil.EMAIL_KEY;
import static org.ebay.datameta.dom.CannedRegexUtil.getCannedRegEx;
import static org.ebay.datameta.dom.DataMetaSame.EQ;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Michael Bergens
 */
public class AppTest {

    private static final Logger L = LoggerFactory.getLogger(AppTest.class);

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
