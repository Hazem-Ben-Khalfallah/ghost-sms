package com.blacknebula.ghostsms.encryption;

import android.content.Context;

import com.blacknebula.ghostsms.utils.FileUtils;

import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class KeyGenerator {
    public static String PUBLIC_KEY_PATH = "publicKey";
    public static String PRIVATE_KEY_PATH = "privateKey";

    public static void generateRSAKeys(Context context) throws NoSuchAlgorithmException, NoSuchProviderException, IOException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(1024);
        KeyPair pair = keyGen.generateKeyPair();
        FileUtils.writeToFile(PUBLIC_KEY_PATH, pair.getPublic().getEncoded(), context);
        FileUtils.writeToFile(PRIVATE_KEY_PATH, pair.getPrivate().getEncoded(), context);
    }

    public static SecretKeySpec generateSecretKey()
            throws IOException, NoSuchAlgorithmException, NoSuchPaddingException {
        SecureRandom rnd = new SecureRandom();
        byte[] key = new byte[16];
        rnd.nextBytes(key);
        final SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
        return secretKey;

    }


}