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
    public static String encrypt(String message) throws IOException, GeneralSecurityException {
        final EncryptionResult encryptedResult = Encryptor.encrypt(message);
        final String json = new Gson().toJson(encryptedResult);
        final byte[] messageBase64 = StringUtils.encodeBase64(StringUtils.fromStringToBytes(json));
        return StringUtils.fromBytesToString(messageBase64);
    }

    public static String decrypt(String encryptedMessage) throws Exception {
        if (StringUtils.isEmpty(encryptedMessage, true) || !StringUtils.isBase64Encoded(encryptedMessage)) {
            Logger.error(Logger.Type.GHOST_SMS, "value not base64 encoded");
            return encryptedMessage;
        }
        final byte[] jsonBase64 = StringUtils.decodeBase64(encryptedMessage);
        final String json = StringUtils.fromBytesToString(jsonBase64);
        final EncryptionResult encryptedResult = new Gson().fromJson(json, EncryptionResult.class);
        final DecryptionResult decryptionResult = Decryptor.decrypt(encryptedResult.getEncryptedMessage(), encryptedResult.getEncryptedSecretKey());
        return decryptionResult.getMessage();
    }
}
