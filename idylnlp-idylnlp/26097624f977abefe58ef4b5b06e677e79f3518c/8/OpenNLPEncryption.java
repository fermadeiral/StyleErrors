package ai.idylnlp.opennlp.custom.encryption;

/**
 * Interface for encryption methods used by OpenNLP model encryption.
 *
 * @author Mountain Fog, Inc.
 *
 */
public interface OpenNLPEncryption {

  /**
   * Encrypt the input text.
   * @param text The text to encrypt.
   * @return The encrypted text.
   * @throws Exception Thrown if the text cannot be encrypted.
   */
  public String encrypt(String text) throws Exception;

  /**
   * Decrypt the input text.
   * @param text The text to decrypt.
   * @return The decrypted text.
   * @throws Exception Thrown if the text cannot be decrypted.
   */
  public String decrypt(String text) throws Exception;

  public String decrypt(String text, String encryptionKey) throws Exception;

  /**
   * Sets the encryption key.
   * @param encryptionKey The encryption key.
   */
  public void setKey(String encryptionKey);

  public String getKey();

  /**
   * Clears the encryption key.
   */
  public void clearKey();

}