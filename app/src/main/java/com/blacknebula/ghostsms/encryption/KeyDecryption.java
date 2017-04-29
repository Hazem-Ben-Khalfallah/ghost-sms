package com.blacknebula.ghostsms.encryption;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;

import javax.crypto.Cipher;

public class KeyDecryption extends AbstractDecryption {

    public static byte[] decrypt(byte[] encryptedKey, PrivateKey privateKey, String algorithm)
            throws IOException, GeneralSecurityException {

        final Cipher cipher = Cipher.getInstance(algorithm);
        return decrypt(encryptedKey, privateKey, cipher);

    }
}