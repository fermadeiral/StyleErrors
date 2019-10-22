package ai.idylnlp.custom.encryption;

import static java.lang.Character.digit;

import ai.idylnlp.opennlp.custom.encryption.OpenNLPEncryption;

/**
 * Base class for OpenNLP model encryption implementations of {@link OpenNLPEncryption}.
 *
 * @author Mountain Fog, Inc.
 *
 */
public abstract class AbstractEncryption implements OpenNLPEncryption {

  /**
   * Converts a string to a byte array.
   * @param input The string to convert to bytes.
   * @return A byte array of the string.
   */
  protected byte[] stringToBytes(String input) {

    /*
  * When you call String.getBytes() (JDK documentation) you encodes characters
    * of the given string into a sequence of bytes using the platform's default charset.
    * What you are actually need to do is to convert each hexadecimal (also base 16)
    * number (represented by two characters from 0 to 9 and A to F e.g. 1A, 99, etc.)
    * into its corresponding numerical (byte) value e.g. "FF" -> -1 byte.
   * http://stackoverflow.com/questions/14368374/how-to-turn-64-character-string-into-key-for-256-aes-encryption
   */

      int length = input.length();
      byte[] output = new byte[length / 2];

      for (int i = 0; i < length; i += 2) {
          output[i / 2] = (byte) ((digit(input.charAt(i), 16) << 4) | digit(input.charAt(i+1), 16));
      }

      return output;

  }

}
