package com.blacknebula.ghostsms.encryption;

import com.blacknebula.ghostsms.GhostSmsApplication;
import com.blacknebula.ghostsms.utils.FileUtils;
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

    /**
     * todo should select which public key will be used in accordance with SMS destination
     */
    public static EncryptionResult encrypt(String message, byte[]  publicKey) throws IOException, GeneralSecurityException {
        //encrypt secret key
        final SecretKeySpec secretKey = KeyGenerator.generateSecretKey();
        final byte[] encryptedSecretKey = KeyEncryption.Encrypt(secretKey.getEncoded(), getDestinationPublic(publicKey), "RSA");

        //encrypt message
        final byte[] encryptedMessage = MessageEncryption.Encrypt(message, secretKey, "AES");

        return new EncryptionResult(encryptedSecretKey, encryptedMessage);
    }

    public static String getSenderPublic() throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = FileUtils.readFromFile(KeyGenerator.PUBLIC_KEY_PATH, GhostSmsApplication.getAppContext());
        return StringUtils.fromBytesToString(StringUtils.encodeBase64(keyBytes));
    }

    public static PublicKey getDestinationPublic(byte[]  publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKey);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

}