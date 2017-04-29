package com.blacknebula.ghostsms.encryption;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PublicKey;

import javax.crypto.Cipher;

public class KeyEncryption extends AbstractEncryption {

    public static byte[] Encrypt(byte[] originalKey, PublicKey key, String cipherAlgorithm)
            throws IOException, GeneralSecurityException {
        final Cipher cipher = Cipher.getInstance(cipherAlgorithm);
        return encrypt(originalKey, key, cipher);

    }

}