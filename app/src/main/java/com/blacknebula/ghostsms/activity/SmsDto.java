package com.blacknebula.ghostsms.activity;

import android.support.annotation.NonNull;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

/**
 * @author hazem
 */
@Parcel
public class SmsDto implements Comparable<SmsDto> {
    String id;
    String phone;
    String message;
    Long date;
    boolean read;
    boolean encrypted;
    String threadId;
    int type;
    String displayName;
    String photoUri;

    public SmsDto() {
    }

    @ParcelConstructor
    public SmsDto(String id, String phone, String message, Long date, boolean read, boolean encrypted, String threadId, int type, String displayName, String photoUri) {
        this.id = id;
        this.phone = phone;
        this.message = message;
        this.date = date;
        this.read = read;
        this.encrypted = encrypted;
        this.threadId = threadId;
        this.type = type;
        this.displayName = displayName;
        this.photoUri = photoUri;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isEncrypted() {
        return encrypted;
    }

    public void setEncrypted(boolean encrypted) {
        this.encrypted = encrypted;
    }

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int compareTo(@NonNull SmsDto otherSms) {
        if (this.getDate() > otherSms.getDate()) {
            return 1;
        } else if (this.getDate().equals(otherSms.getDate())) {
            return 0;
        }
        return -1;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }
}
