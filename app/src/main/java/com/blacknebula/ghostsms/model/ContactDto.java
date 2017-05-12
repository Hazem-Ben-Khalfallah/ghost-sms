package com.blacknebula.ghostsms.model;

import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.pchmn.materialchips.model.ChipInterface;

/**
 * @author hazem
 */

public class ContactDto implements ChipInterface {
    Object id;
    Uri avatarUri;
    String avatarUriString;
    Drawable avatarDrawable;
    String label;
    String info;

    public ContactDto() {
    }

    public ContactDto(String label) {
        this.label = label;
    }

    public ContactDto(Object id, String label) {
        this.id = id;
        this.label = label;
    }

    public ContactDto(String label, Uri avatarUri) {
        this.avatarUri = avatarUri;
        this.label = label;
    }

    public Object getId() {
        return this.id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public Uri getAvatarUri() {
        return this.avatarUri;
    }

    public void setAvatarUri(Uri avatarUri) {
        this.avatarUri = avatarUri;
    }

    public Drawable getAvatarDrawable() {
        return this.avatarDrawable;
    }

    public void setAvatarDrawable(Drawable avatarDrawable) {
        this.avatarDrawable = avatarDrawable;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getInfo() {
        return this.info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getAvatarUriString() {

        return avatarUriString;
    }

    public void setAvatarUriString(String avatarUriString) {
        this.avatarUriString = avatarUriString;
    }
}
