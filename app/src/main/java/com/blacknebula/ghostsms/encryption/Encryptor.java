package com.blacknebula.ghostsms.encryption;

import com.blacknebula.ghostsms.GhostSmsApplication;
import com.blacknebula.ghostsms.utils.FileUtils;
import com.blacknebula.ghostsms.utils.Logger;
import com.blacknebula.ghostsms.utils.StringUtils;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.spec.SecretKeySpec;

public class Encryptor {

    public static EncryptionResult encrypt(String message) throws IOException, GeneralSecurityException {
        //encrypt secret key
        final SecretKeySpec secretKey = KeyGenerator.generateSecretKey();
        final byte[] encryptedSecretKey = KeyEncryption.Encrypt(secretKey.getEncoded(), getPublic(KeyGenerator.PUBLIC_KEY_PATH, "RSA"), "RSA");

        //encrypt message
        final byte[] encryptedMessage = MessageEncryption.Encrypt(message, secretKey, "AES");

        return new EncryptionResult(encryptedSecretKey, encryptedMessage);
    }

    public static PublicKey getPublic(String filename, String algorithm) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = FileUtils.readFromFile(filename, GhostSmsApplication.getAppContext());
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance(algorithm);
        return kf.generatePublic(spec);

    }

}