package ai.idylnlp.test.opennlp.custom.encryption;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ai.idylnlp.opennlp.custom.encryption.OpenNLP183Encryption;

public class OpenNLP183EncryptionTest {

  @Test
  public void encrypt() throws Exception {

    OpenNLP183Encryption encryption = new OpenNLP183Encryption();

    encryption.setKey("enc");
    String enc = encryption.encrypt("test data");
    String dec = encryption.decrypt(enc);

    assertEquals("test data", dec);

  }

}
