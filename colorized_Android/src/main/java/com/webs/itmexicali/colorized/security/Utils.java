package com.webs.itmexicali.colorized.security;

public class Utils {
  /**
   * For HEXStrings <-> ByteArrays conversions
   */
  private final static char[] hexChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

  /**
   * Return a HEX String generated from the byte array
   */
  public static String byteArrayToHexString(byte[] toHex) {
    StringBuilder hexString = new StringBuilder();
    for (byte b : toHex) {
      hexString.append(byteToHexString(b));
    }
    return hexString.toString();
  }

  /**
   * Append string representation of a byte
   */
  private static String byteToHexString(byte b) {
    int unsigned_byte = b < 0 ? b + 256 : b;
    int hi = unsigned_byte / 16;
    int lo = unsigned_byte % 16;
    return String.format("%c%c", hexChars[hi], hexChars[lo]);
  }

  /**
   * Return a new String from the byte array
   */
  public static byte[] HexStringToByte(String in) {
    int len = in.length();
    if (len < 2)
      return null;

    byte[] ret = new byte[len / 2];

    for (int i = 0; i < len; i += 2) {
      ret[i / 2] = (byte) (charToByte(in.charAt(i)) << 4 | charToByte(in.charAt(i + 1)));
    }

    return ret;
  }

  /**
   * Return half byte (4bits) value from a HEXADECIMAL char
   */
  private static byte charToByte(char c) {
    switch (c) {
      case '0':
      case '1':
      case '2':
      case '3':
      case '4':
      case '5':
      case '6':
      case '7':
      case '8':
      case '9':
        return (byte) (c - '0');
      case 'A':
        return 10;
      case 'B':
        return 11;
      case 'C':
        return 12;
      case 'D':
        return 13;
      case 'E':
        return 14;
      case 'F':
        return 15;
      default:
        return 0;
    }
  }
}
