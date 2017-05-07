package com.blacknebula.ghostsms.encryption;

import com.blacknebula.ghostsms.utils.StringUtils;
import com.google.gson.annotations.SerializedName;

/**
 * @author hazem
 */

public class EncryptionResult {
    @SerializedName("s")
    private String encryptedSecretKey;
    @SerializedName("m")
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
