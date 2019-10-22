package ai.idylnlp.opennlp.custom;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.lang3.StringUtils;

import ai.idylnlp.opennlp.custom.encryption.OpenNLPEncryptionFactory;

public class EncryptedDataOutputStream extends DataOutputStream {

  public EncryptedDataOutputStream(OutputStream out) {

    super(out);

  }

  public void writeEncryptedUTF(String s) throws IOException {

    if(StringUtils.isNotEmpty(OpenNLPEncryptionFactory.getDefault().getKey())) {

      try {
        // Encrypt the input.
        s = OpenNLPEncryptionFactory.getDefault().encrypt(s);
      } catch (Exception ex) {
        throw new RuntimeException("Unable to write encrypted model.", ex);
      }

    }

    writeUTF(s);

  }

}