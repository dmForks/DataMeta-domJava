package test.ebay.datameta.ser.bytes;

import org.ebay.datameta.test.util.DataInOutMock;
//import com.google.common.io.ByteArrayDataInput;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Michael Bergens
 */
public class AppTest {

    private static final Logger L = LoggerFactory.getLogger(AppTest.class);

    @Before public void init() {
    }

    // test the DateTime serialization/deserialization with a timezone
    @Test public void testDttmTzSerDeser() throws Exception {
        final DataInOutMock io = new DataInOutMock("DttmTzSerDeser");
        io.flipOutToIn();
    }


}
