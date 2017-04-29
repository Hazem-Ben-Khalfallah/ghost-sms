package com.blacknebula.ghostsms.encryption;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class MessageDecryption extends AbstractDecryption {

    public static byte[] decrypt(byte[] encryptedMessage, SecretKeySpec secretKey, String algorithm)
            throws IOException, GeneralSecurityException {
        final Cipher cipher = Cipher.getInstance(algorithm);
        return decrypt(encryptedMessage, secretKey, cipher);

    }

}