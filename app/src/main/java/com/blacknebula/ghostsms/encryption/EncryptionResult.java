package com.blacknebula.ghostsms.encryption;

import com.blacknebula.ghostsms.utils.StringUtils;

/**
 * @author hazem
 */

public class EncryptionResult {
    private String encryptedSecretKey;
    private String encryptedMessage;

    public EncryptionResult(byte[] encryptedSecretKey, byte[] encryptedMessage) {
        this.encryptedSecretKey = StringUtils.fromBytesToString(encryptedSecretKey);
        this.encryptedMessage = StringUtils.fromBytesToString(encryptedMessage);
    }

    public String getEncryptedSecretKey() {
        return encryptedSecretKey;
    }

    public void setEncryptedSecretKey(String encryptedSecretKey) {
        this.encryptedSecretKey = encryptedSecretKey;
    }

    public String getEncryptedMessage() {
        return encryptedMessage;
    }

    public void setEncryptedMessage(String encryptedMessage) {
        this.encryptedMessage = encryptedMessage;
    }
}
