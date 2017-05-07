package com.blacknebula.ghostsms.activity;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

/**
 * @author hazem
 */
@Parcel
public class SmsDto {
    private String id;
    private String phone;
    private String message;
    private Long date;
    private boolean read;
    private boolean encrypted;

    public SmsDto() {
    }


    @ParcelConstructor
    public SmsDto(String id, String phone, String message, Long date, boolean read, boolean encrypted) {
        this.id = id;
        this.phone = phone;
        this.message = message;
        this.date = date;
        this.read = read;
        this.encrypted = encrypted;
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
}
