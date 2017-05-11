package com.blacknebula.ghostsms.encryption;

import com.blacknebula.ghostsms.utils.Logger;
import com.blacknebula.ghostsms.utils.StringUtils;
import com.google.gson.Gson;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * @author hazem
 */

public class SmsEncryptionWrapper {
    public static String encrypt(String message, byte[] publicKey) throws IOException, GeneralSecurityException {
        final EncryptionResult encryptedResult = Encryptor.encrypt(message, publicKey);
        final String json = new Gson().toJson(encryptedResult);
        final byte[] messageBase64 = StringUtils.encodeBase64(StringUtils.fromStringToBytes(json));
        return StringUtils.fromBytesToString(messageBase64);
    }

    public static String decrypt(String receivedMessage) throws Exception {
        if (StringUtils.isEmpty(receivedMessage, true) || !StringUtils.isBase64Encoded(receivedMessage)) {
            Logger.error(Logger.Type.GHOST_SMS, "value not base64 encoded");
            return receivedMessage;
        }
        final byte[] jsonBase64 = StringUtils.decodeBase64(receivedMessage);
        final String json = StringUtils.fromBytesToString(jsonBase64);
        final EncryptionResult encryptedResult = new Gson().fromJson(json, EncryptionResult.class);
        final DecryptionResult decryptionResult = Decryptor.decrypt(encryptedResult.getEncryptedMessage(), encryptedResult.getEncryptedSecretKey());
        return decryptionResult.getMessage();
    }

    public static boolean isEncrypted(String receivedMessage) {
        try {
            if (StringUtils.isEmpty(receivedMessage, true) || !StringUtils.isBase64Encoded(receivedMessage)) {
                return false;
            }

            final byte[] jsonBase64 = StringUtils.decodeBase64(receivedMessage);
            final String json = StringUtils.fromBytesToString(jsonBase64);
            final EncryptionResult encryptedResult = new Gson().fromJson(json, EncryptionResult.class);
            return encryptedResult != null;
        } catch (Exception e) {
        }
        return false;
    }
}
