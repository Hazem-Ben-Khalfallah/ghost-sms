package com.blacknebula.ghostsms.encryption;

/**
 * @author hazem
 */

public class DecryptionResult {
    private String message;
    private String secretKey;

    public DecryptionResult(String message, String encryptedSecretKey) {
        this.secretKey = encryptedSecretKey;
        this.message = message;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
