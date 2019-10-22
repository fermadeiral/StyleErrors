package ai.idylnlp.opennlp.custom.encryption;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

import ai.idylnlp.custom.encryption.AbstractEncryption;

/**
 * Model encryption for OpenNLP 1.8.3. This encryption is a different implementation but
 * it is 100% compatible with the {@link OpenNLP160Encryption} implementation. There is
 * a test in OpenNLP170EncryptionTest to ensure the compatibility.
 * <p>
 * IMPORTANT NOTE:
 * <p>
 * If you get an error like "java.security.InvalidKeyException: Illegal key size or default parameters" when trying
 * to load and decrypt a model make sure you have the Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files
 * for your JDK. Refer to: http://stackoverflow.com/a/6481658 and http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html
 * <p>
 * You will NOT get this error when using OpenJDK!
 *
 * @author Mountain Fog, Inc.
 *
 */
public class OpenNLP183Encryption extends AbstractEncryption implements OpenNLPEncryption {

  private String encryptionKey;

  @Override
  public String encrypt(String strToEncrypt) throws Exception {

    SecretKeySpec key = generateKey(encryptionKey);

    Cipher cipher = Cipher.getInstance("AES");

    cipher.init(Cipher.ENCRYPT_MODE, key);

    return Base64.encodeBase64String(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));

  }

  @Override
  public String decrypt(String strToDecrypt) throws Exception {

    return decrypt(strToDecrypt, encryptionKey);

  }

  private SecretKeySpec generateKey(String key) throws Exception {

    String encryptionKey = DigestUtils.sha256Hex(key + "uGrClE0GW1Sm7DRsiavg");

    // Generate the encryption key based on the string value.
    return new SecretKeySpec(stringToBytes(encryptionKey), "AES");

  }

  @Override
  public void setKey(String encryptionKey) {
    this.encryptionKey = encryptionKey;
  }

  @Override
  public String getKey() {
    return encryptionKey;
  }

  @Override
  public void clearKey() {
    this.encryptionKey = null;
  }

  @Override
  public String decrypt(String text, String encryptionKey) throws Exception {

    SecretKeySpec key = generateKey(encryptionKey);

    Cipher cipher = Cipher.getInstance("AES");

    cipher.init(Cipher.DECRYPT_MODE, key);

    return new String(cipher.doFinal(Base64.decodeBase64(text)));

  }

}