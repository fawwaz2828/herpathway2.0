package com.herpathway.model;

import javafx.scene.image.Image;

/** A chat partner: id, name and (optional) picture. */
public class ContactSummary {

    private final long userId;
    private final String displayName;
    private final Image picture;

    public ContactSummary(long userId, String displayName, Image picture) {
        this.userId = userId;
        this.displayName = displayName;
        this.picture = picture;
    }

    public long getUserId() { return userId; }
    public String getDisplayName() { return displayName; }
    public Image getPicture() { return picture; }

    @Override
    public String toString() {
        return displayName;
    }
}
