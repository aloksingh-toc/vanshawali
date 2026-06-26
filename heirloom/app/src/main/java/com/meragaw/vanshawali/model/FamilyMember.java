package com.meragaw.vanshawali.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.meragaw.vanshawali.data.Converters;

import java.util.List;

/**
 * Represents a single family member in the Heirloom tree.
 */
@Entity(tableName = "family_members")
@TypeConverters(Converters.class)
public class FamilyMember {

    @PrimaryKey
    @NonNull
    public String id = "";
    public String firstName;
    public String lastName;
    public String maidenName;       // nullable
    public String birthDate;        // "YYYY-MM-DD", nullable
    public String deathDate;        // "YYYY-MM-DD", nullable
    public String birthPlace;       // nullable
    public String spouseId;         // nullable
    public String parentId;         // nullable — primary parent node for tree drawing
    public List<String> childIds;
    public String profilePhotoUri;  // nullable — local URI or remote URL
    public String bio;              // nullable
    public int generation;          // 1 = great-grandparents, 2 = grandparents, etc.
    public boolean isCurrentUser;
    public long createdAt;          // System.currentTimeMillis() when added — used for "recently added"

    // Avatar color index (0–4), maps to @color/avatar_* palette
    public int avatarColorIndex;

    public FamilyMember() {}

    /** Returns initials for avatar fallback (e.g. "EH" from Eleanor Hartwell) */
    public String getInitial() {
        if (firstName != null && !firstName.isEmpty()) {
            return String.valueOf(firstName.charAt(0)).toUpperCase();
        }
        return "?";
    }

    /** Full display name */
    public String getFullName() {
        return (firstName != null ? firstName : "") +
               (lastName != null ? " " + lastName : "");
    }

    /** Age string, e.g. "b. 1944" or "1938–2015" */
    public String getLifespanLabel() {
        String birthYear = birthDate != null && birthDate.length() >= 4
                ? birthDate.substring(0, 4) : null;
        String deathYear = deathDate != null && deathDate.length() >= 4
                ? deathDate.substring(0, 4) : null;

        if (birthYear != null && deathYear != null) {
            return birthYear + "–" + deathYear;
        } else if (birthYear != null) {
            return "b. " + birthYear;
        }
        return "";
    }

    /**
     * Returns a deterministic avatar color resource ID based on member ID hash.
     * Usage: int colorRes = member.getAvatarColorRes();
     *        view.setBackgroundTintList(ColorStateList.valueOf(
     *            ContextCompat.getColor(context, colorRes)));
     */
    public int getAvatarColorRes() {
        int[] colorResIds = {
            com.meragaw.vanshawali.R.color.avatar_slate,
            com.meragaw.vanshawali.R.color.avatar_terracotta,
            com.meragaw.vanshawali.R.color.avatar_rust,
            com.meragaw.vanshawali.R.color.avatar_olive,
            com.meragaw.vanshawali.R.color.avatar_amber
        };
        if (id == null) return colorResIds[0];
        return colorResIds[Math.abs(id.hashCode()) % colorResIds.length];
    }
}
