package test.ebay.datameta.ser.jackson.fasterxml;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import org.apache.logging.log4j.core.util.JsonUtils;
import org.ebay.datameta.ser.jackson.fasterxml.JacksonUtil;
import org.ebay.datameta.ser.jackson.fasterxml.VerAndDataType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.ebay.datameta.ser.jackson.fasterxml.gen.v3_2_14.Colors;
import test.ebay.datameta.ser.jackson.fasterxml.gen.v3_2_14.DmTesting;
import test.ebay.datameta.ser.jackson.fasterxml.gen.v3_2_14.DmTesting_DmSameFull;
import test.ebay.datameta.ser.jackson.fasterxml.gen.v3_2_14.DmTesting_JSONable;
import test.ebay.datameta.ser.jackson.fasterxml.gen.v3_2_14.TestingDm;
import test.ebay.datameta.ser.jackson.fasterxml.gen.v3_2_14.TestingDm_DmSameFull;
import test.ebay.datameta.ser.jackson.fasterxml.gen.v3_2_14.TestingDm_JSONable;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertTrue;

/**
  * Tests for the [[JacksonUtil]], some useful examples.
  *
  * @author Michael Bergens
  */
public class TestJacksonUtil {
  private static Logger L = LoggerFactory.getLogger(TestJacksonUtil.class);
  private static JacksonUtil JU = JacksonUtil.getInstance();

  @Test public void testJsonRoundtrip() throws Exception {

    final JsonFactory jf = new JsonFactory();
    final TestingDm tdm1 = new TestingDm();
    tdm1.setId("first");
    tdm1.setColor(Colors.Blue);
    Set<Long> longs = new HashSet<>();
    longs.add(1111L);
    longs.add(222L);
    longs.add(33L);
    tdm1.setLongs(longs);
    List<ZonedDateTime> zdtms1 = new ArrayList<>();
    zdtms1.add(ZonedDateTime.of(1993, Month.FEBRUARY.getValue(), 28, 23, 58, 45, 0, ZoneId.systemDefault()));
    zdtms1.add(ZonedDateTime.of(1995, Month.MAY.getValue(), 15, 20, 40, 35, 0, ZoneId.of("America/Phoenix")));
    tdm1.setWhens(zdtms1);
    String json = JU.writeObject(jf, TestingDm_JSONable.getInstance(), tdm1);
/*    jg.writeStartObject();
    TestingDm_JSONable.getInstance().write(jf.createGenerator(w), tdm1);
    jg.writeEndObject(); */
    L.info("TestingDm 1:\n{}", json);
    TestingDm reTdm1 = TestingDm_JSONable.getInstance().read(jf.createParser(json), false);
    VerAndDataType vdt = VerAndDataType.fromJson(json);
    L.info("TestingDm VDT: {}", vdt);

    assertTrue("Round trip of tdm1 via JSON failed", TestingDm_DmSameFull.I.isSame(tdm1, reTdm1));
    final TestingDm tdm2 = new TestingDm();
    tdm2.setId("second");
    tdm2.setColor(Colors.Red);
    longs = new HashSet<>();
    longs.add(99999L);
    longs.add(8888L);
    longs.add(777L);
    longs.add(66L);
    longs.add(5L);
    tdm2.setLongs(longs);
    zdtms1 = new ArrayList<>();
    zdtms1.add(ZonedDateTime.of(2001, Month.AUGUST.getValue(), 18, 13, 38, 15, 0, ZoneId.of("America/Chicago")));
    zdtms1.add(ZonedDateTime.of(2003, Month.SEPTEMBER.getValue(), 25,17, 39, 35, 0, ZoneId.of("America/Indiana/Indianapolis")));
    zdtms1.add(ZonedDateTime.of(2015, Month.JULY.getValue(), 4, 18, 43, 35, 0, ZoneId.of("America/New_York")));
    tdm2.setWhens(zdtms1);

    List<TestingDm> embs = new ArrayList<>(); embs.add(tdm1); embs.add(tdm2);

    final DmTesting dmt = new DmTesting();
    dmt.setIntVal(45678);
    dmt.setLongVal(12345678901234L);
    dmt.setName("\"DataMeta\" testing\ninstance\t#1");
    dmt.setEmail("someone@somewhere.com");
    dmt.setCreated(ZonedDateTime.now());
//                           max long: 9223372036854775807
    dmt.setSalary(new BigDecimal("12345678909876543210123456789.56"));
    dmt.setColor(Colors.White);
    dmt.setEmbedded(tdm1);
    dmt.setEmbs(embs);
    json = JU.writeObject(jf, DmTesting_JSONable.getInstance(), dmt);

    L.info("DmTesting 1:\n{}", json);

    DmTesting reDmt = DmTesting_JSONable.getInstance().read(jf.createParser(json), false);
    vdt = VerAndDataType.fromJson(json);
    L.info("DmTesting VDT: {}", vdt);
    assertTrue("Round trip of DmTesting via JSON failed", DmTesting_DmSameFull.I.isSame(dmt, reDmt));
  }
}
