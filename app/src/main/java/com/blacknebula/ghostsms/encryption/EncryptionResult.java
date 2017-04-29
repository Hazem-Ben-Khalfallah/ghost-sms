package com.blacknebula.ghostsms.encryption;

/**
 * @author hazem
 */

public class EncryptionResult {
    private String encryptedSecretKey;
    private String encryptedMessage;

    public EncryptionResult(String encryptedSecretKey, String encryptedMessage) {
        this.encryptedSecretKey = encryptedSecretKey;
        this.encryptedMessage = encryptedMessage;
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
