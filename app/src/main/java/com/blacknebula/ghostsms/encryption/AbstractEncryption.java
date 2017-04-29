package com.blacknebula.ghostsms.encryption;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;

import javax.crypto.Cipher;

/**
 * @author hazem
 */

abstract class AbstractEncryption {
    protected static byte[] encrypt(byte[] input, Key key, Cipher cipher)
            throws IOException, GeneralSecurityException {

        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(input);

    }
}
