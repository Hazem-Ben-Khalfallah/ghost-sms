package com.blacknebula.ghostsms.utils;

import java.nio.charset.Charset;

/**
 * @author hazem
 */

public class StringUtils {

    public static byte[] fromStringToBytes(String message) {
        // String => byte []
        return message.getBytes(Charset.forName("ISO-8859-1"));
    }

    public static String fromBytesToString(byte[] bytes) {
        // byte [] => String
        return new String(bytes, Charset.forName("ISO-8859-1"));
    }
}
