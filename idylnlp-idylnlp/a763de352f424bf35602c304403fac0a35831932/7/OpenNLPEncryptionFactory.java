package ai.idylnlp.opennlp.custom.encryption;

public class OpenNLPEncryptionFactory {

  private static OpenNLPEncryption openNLPEncryption;

  private OpenNLPEncryptionFactory() {
    // This is a factory class.
  }

  public static OpenNLPEncryption getDefault() {

    if(openNLPEncryption == null) {
      openNLPEncryption = new OpenNLP183Encryption();
    }

    return openNLPEncryption;

  }

}
