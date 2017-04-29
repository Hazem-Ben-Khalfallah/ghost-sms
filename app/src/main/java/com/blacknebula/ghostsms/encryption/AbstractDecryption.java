package com.blacknebula.ghostsms.encryption;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;

import javax.crypto.Cipher;

/**
 * @author hazem
 */

class AbstractDecryption {
    protected static byte[] decrypt(byte[] input, Key key, Cipher cipher)
            throws IOException, GeneralSecurityException {
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(input);
    }
}
