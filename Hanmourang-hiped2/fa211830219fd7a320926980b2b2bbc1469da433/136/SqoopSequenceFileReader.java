package hip.ch3.db;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;

import java.io.IOException;

public class SqoopSequenceFileReader {
  public static void main(String... args) throws IOException {
    read(new Path(args[0]));
  }

  public static void read(Path inputPath) throws IOException {
    Configuration conf = new Configuration();
    FileSystem fs = FileSystem.get(conf);

    SequenceFile.Reader reader =   //<co id="ch03_comment_seqfile_read1"/>
        new SequenceFile.Reader(fs, inputPath, conf);

    try {
      System.out.println(
          "Is block compressed = " + reader.isBlockCompressed());

      Text key = new Text();
      IntWritable value = new IntWritable();

      while (reader.next(key, value)) {   //<co id="ch03_comment_seqfile_read2"/>
        System.out.println(key + "," + value);
      }
    } finally {
      reader.close();
    }
  }
}
