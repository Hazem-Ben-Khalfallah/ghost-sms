package com.blacknebula.ghostsms.encryption;

import com.blacknebula.ghostsms.utils.StringUtils;

/**
 * @author hazem
 */

public class DecryptionResult {
    private String message;
    private String secretKey;

    public DecryptionResult(byte[] message, byte[] encryptedSecretKey) {
        this.secretKey = StringUtils.fromBytesToString(encryptedSecretKey);
        this.message = StringUtils.fromBytesToString(message);
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
