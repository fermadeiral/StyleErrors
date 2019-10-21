package hip.ch3.avro;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.hadoop.io.Writable;

import java.io.*;
import java.nio.ByteBuffer;

public class AvroBytesRecord {

  public static final String BYTES_FIELD = "b";
  private static final String SCHEMA_JSON =
          "{\"type\": \"record\", \"name\": \"Bytes\", "
          + "\"fields\": ["
          + "{\"name\":\"" + BYTES_FIELD
          + "\", \"type\":\"bytes\"}]}";
  public static final Schema SCHEMA = Schema.parse(SCHEMA_JSON);

  public static GenericRecord toGenericRecord(byte[] bytes) {
    GenericRecord record = new GenericData.Record(SCHEMA);
    record.put(BYTES_FIELD, ByteBuffer.wrap(bytes));
    return record;
  }

  public static GenericRecord toGenericRecord(Writable writable)
      throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dao = new DataOutputStream(baos);
    writable.write(dao);
    dao.close();
    return toGenericRecord(baos.toByteArray());
  }

  public static void fromGenericRecord(GenericRecord r, Writable w)
      throws IOException {
    ByteArrayInputStream bais = new ByteArrayInputStream(fromGenericRecord(r));
    DataInputStream dis = new DataInputStream(bais);
    w.readFields(dis);
  }

  public static byte[] fromGenericRecord(GenericRecord record) {
    return ((ByteBuffer)record.get(BYTES_FIELD)).array();
  }


}
