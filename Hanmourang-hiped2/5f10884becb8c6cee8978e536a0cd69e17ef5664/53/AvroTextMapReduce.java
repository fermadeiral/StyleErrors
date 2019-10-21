package hip.ch4.avro;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileStream;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.mapred.AvroAsTextInputFormat;
import org.apache.avro.mapred.AvroTextOutputFormat;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Iterator;

public class AvroTextMapReduce {

  public static final String[] LINES = new String[]{
      "the quick brown fox jumps over the lazy dog",
      "the cow jumps over the moon",
      "the rain in spain falls mainly on the plains"
  };

  public static void writeLinesBytesFile(OutputStream os)
      throws IOException {
    DatumWriter<ByteBuffer>
        writer = new GenericDatumWriter<ByteBuffer>();
    DataFileWriter<ByteBuffer> out =
        new DataFileWriter<ByteBuffer>(writer);
    out.create(Schema.create(Schema.Type.BYTES), os);
    for (String line : LINES) {
      out.append(ByteBuffer.wrap(line.getBytes("UTF-8")));
    }
    out.close();
  }

  /**
   * Uses default mapper with no reduces for a map-only identity job.
   */
  public static void main(String... args) throws Exception {
    JobConf job = new JobConf();
    job.setJarByClass(AvroTextMapReduce.class);
    Path input = new Path(args[0]);
    Path output = new Path(args[1]);

    output.getFileSystem(job).delete(output, true);

    FileSystem hdfs = FileSystem.get(job);
    OutputStream os = hdfs.create(input);
    writeLinesBytesFile(os);

    FileInputFormat.setInputPaths(job, input);
    FileOutputFormat.setOutputPath(job, output);

    job.setInputFormat(AvroAsTextInputFormat.class);
    job.setOutputFormat(AvroTextOutputFormat.class);
    job.setOutputKeyClass(Text.class);

    job.setMapperClass(Mapper.class);
    job.setReducerClass(Reducer.class);

    JobClient.runJob(job);

    validateSortedFile(output.getFileSystem(job)
        .open(new Path(output, "part-00000.avro")));
  }

  public static class Mapper
      extends MapReduceBase implements
      org.apache.hadoop.mapred.Mapper<Text, Text, Text, Text> {
    @Override
    public void map(Text key, Text value,
                    OutputCollector<Text, Text> output,
                    Reporter reporter) throws IOException {
      output.collect(key, value);
    }
  }

  public static class Reducer
      extends MapReduceBase implements
      org.apache.hadoop.mapred.Reducer<Text, Text, Text, Text> {
    public void reduce(Text key, Iterator<Text> values,
                       OutputCollector<Text, Text> output,
                       Reporter reporter)
        throws IOException {
      while (values.hasNext()) {
        output.collect(key, values.next());
      }
    }
  }


  public static void validateSortedFile(InputStream is)
      throws Exception {
    DatumReader<ByteBuffer>
        reader = new GenericDatumReader<ByteBuffer>();
    DataFileStream<ByteBuffer> lines =
        new DataFileStream<ByteBuffer>(is, reader);

    for (ByteBuffer line : lines) {
      byte[] b = new byte[line.remaining()];
      line.get(b);
      System.out.println(new String(b, "UTF-8").trim());
    }

    is.close();
  }
}
