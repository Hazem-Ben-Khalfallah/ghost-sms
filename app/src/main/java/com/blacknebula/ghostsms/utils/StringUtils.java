package com.blacknebula.ghostsms.utils;

import org.apache.commons.codec.binary.Base64;

import java.nio.charset.Charset;

/**
 * @author hazem
 */

public class StringUtils{

    public static int warn_emoji = 0x26A0;
    public static int bookmark_emoji = 0x1F516;
    public static int locked_emoji = 0x1F510;

    private static final Charset CHARSET = Charset.forName("ISO-8859-1");

    public static byte[] fromStringToBytes(String message) {
        // String => byte []Charset.defaultCharset(); //
        return message.getBytes(CHARSET);
    }

    public static String fromBytesToString(byte[] bytes) {
        // byte [] => String
        return new String(bytes, CHARSET);
    }

    public static boolean isBase64Encoded(String value) {
        return Base64.isArrayByteBase64(fromStringToBytes(value));
    }

    public static byte[] encodeBase64(byte[] bytes) {
        return Base64.encodeBase64(bytes);
    }

    public static byte[] decodeBase64(String value) {
        return Base64.decodeBase64(fromStringToBytes(value));
    }

    public static boolean isEmpty(String value) {
        return value == null || value.length() == 0;
    }

    public static boolean isNotEmpty(String value) {
        return !isEmpty(value);
    }

    public static boolean isEmpty(String value, boolean trim) {
        return value == null || (trim ? value.trim().length() == 0 : value.length() == 0);
    }

    public static boolean isNotEmpty(String value, boolean trim) {
        return !isEmpty(value, trim);
    }

    public static String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }

}
