package com.blacknebula.ghostsms.encryption;

import com.blacknebula.ghostsms.GhostSmsApplication;
import com.blacknebula.ghostsms.utils.FileUtils;
import com.blacknebula.ghostsms.utils.StringUtils;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.spec.SecretKeySpec;

public class Decryptor {

    public static DecryptionResult decrypt(String encryptedMessage, String encryptedSecretKey) throws Exception {
        final byte[] secretKey = KeyDecryption.decrypt(StringUtils.fromStringToBytes(encryptedSecretKey),
                getPrivate(KeyGenerator.PRIVATE_KEY_PATH, "RSA"), "RSA");
        final byte[] message = MessageDecryption.decrypt(StringUtils.fromStringToBytes(encryptedMessage),
                new SecretKeySpec(secretKey, "AES"), "AES");
        return new DecryptionResult(message, secretKey);
    }

    public static PrivateKey getPrivate(String filename, String algorithm) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = FileUtils.readFromFile(filename, GhostSmsApplication.getAppContext());
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance(algorithm);
        return kf.generatePrivate(spec);
    }

}