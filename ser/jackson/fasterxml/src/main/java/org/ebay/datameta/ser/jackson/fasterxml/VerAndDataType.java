package org.ebay.datameta.ser.jackson.fasterxml;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.ebay.datameta.util.jdk.SemanticVersion;

import java.io.IOException;

import static org.ebay.datameta.ser.jackson.fasterxml.JacksonUtil.DT_KEY;
import static org.ebay.datameta.ser.jackson.fasterxml.JacksonUtil.VER_KEY;
import static com.fasterxml.jackson.core.JsonToken.END_OBJECT;

/**
 * Instances of {@link SemanticVersion} and the data class with the full qualified data name, usually
 * retrieved from serialization service.
 * @author mubergens Michael Bergens
 */
public class VerAndDataType {
  private SemanticVersion version;
  private String dataType;
  private Class dataClass;

  public static VerAndDataType fromJson(final String json) throws IOException {

    final VerAndDataType result = new VerAndDataType();
    final JsonFactory jf = new JsonFactory();
    final JsonParser jp = jf.createParser(json);

    JsonToken t = null;
    while ( (t = jp.nextToken()) != END_OBJECT) {
      if(t == null) throw new IllegalArgumentException("NULL token at " + jp.getParsingContext());
      final String fldName = jp.getCurrentName();
      if (fldName != null) {
        jp.nextToken();
        switch (fldName) {
          case VER_KEY:
            result.version = SemanticVersion.parse(jp.getText());
            break; // skip the version field

          case DT_KEY:
            result.dataType = jp.getText();
            try {
              result.dataClass = Class.forName(result.dataType);
            }
            catch (ClassNotFoundException e) {
              throw new IllegalArgumentException("For the source:\n" + json + "\n - the class " + result.dataType +
                " is not found in the current classloader");
            }
            break;  // skip the data type field

          default:
            // nothing to do
        }
      }
      if(result.version != null && result.dataClass != null) break;
    }
    return result;
  }
  // use the factory methods to get an instance.
  private VerAndDataType() {}

  public SemanticVersion getVersion() { return version; }

  public String getDataType() { return dataType; }

  public Class getDataClass() { return dataClass; }

  @Override public String toString() {
    return "VerAndDataType{" + dataClass.getName() + '#' + version + '}';
  }
}
