package com.blacknebula.ghostsms.encryption;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class MessageEncryption extends AbstractEncryption {

    public static byte[] Encrypt(String message, SecretKeySpec secretKey, String cipherAlgorithm)
            throws IOException, GeneralSecurityException {
        final Cipher cipher = Cipher.getInstance(cipherAlgorithm);
        return encrypt(message.getBytes(), secretKey, cipher);
    }

}