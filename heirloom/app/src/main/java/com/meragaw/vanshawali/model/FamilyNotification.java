package com.meragaw.vanshawali.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Represents a notification in the Heirloom app.
 */
@Entity(tableName = "notifications")
public class FamilyNotification {

    public enum Type {
        BIRTHDAY,
        ANNIVERSARY,
        PHOTO_ADDED,
        MEMBER_ADDED,
        MEMORY_ADDED
    }

    @PrimaryKey
    @NonNull
    public String id = "";
    public Type type;
    public String memberId;       // member this notification is about
    public String actorId;        // member who triggered the action (nullable)
    public String message;        // pre-formatted display message (HTML SpannedString)
    public long timestamp;        // Unix millis
    public boolean isRead;
    public boolean isHighlighted; // true = shows as featured card (birthday/anniversary)

    public FamilyNotification() {}

    /**
     * Returns a human-readable relative time string.
     * For production, use DateUtils.getRelativeTimeSpanString().
     */
    public String getRelativeTime() {
        long now = System.currentTimeMillis();
        long diff = now - timestamp;
        long hours = diff / (1000 * 60 * 60);
        long days = hours / 24;

        if (hours < 1) return "Just now";
        if (hours < 24) return hours + "h ago";
        if (days == 1) return "Yesterday";
        return days + " days ago";
    }

    /** Icon drawable resource for this notification type */
    public int getIconRes() {
        switch (type) {
            case BIRTHDAY:    return com.meragaw.vanshawali.R.drawable.ic_gift;
            case ANNIVERSARY: return com.meragaw.vanshawali.R.drawable.ic_heart;
            case PHOTO_ADDED: return com.meragaw.vanshawali.R.drawable.ic_tab_photos;
            case MEMBER_ADDED:return com.meragaw.vanshawali.R.drawable.ic_tab_profile;
            case MEMORY_ADDED:return com.meragaw.vanshawali.R.drawable.ic_memory;
            default:          return com.meragaw.vanshawali.R.drawable.ic_bell;
        }
    }

    /** Icon background color resource for this notification type */
    public int getIconBgColorRes() {
        switch (type) {
            case BIRTHDAY:    return com.meragaw.vanshawali.R.color.heirloom_primary;
            case ANNIVERSARY: return com.meragaw.vanshawali.R.color.avatar_olive;
            default:          return com.meragaw.vanshawali.R.color.avatar_slate;
        }
    }
}
