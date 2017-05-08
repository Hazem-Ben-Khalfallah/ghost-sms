package com.blacknebula.ghostsms.activity;

/**
 * @author hazem
 */

public class ContactDto {
    String displayName;
    String photoUri;

    public ContactDto() {
    }

    public ContactDto(String displayName, String photoUri) {
        this.displayName = displayName;
        this.photoUri = photoUri;
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
