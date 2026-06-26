package com.meragaw.vanshawali.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * A single photo/caption memory attached to a family member.
 */
@Entity(tableName = "memories")
public class Memory {

    @PrimaryKey
    @NonNull
    public String id = "";
    public String memberId;
    public String caption;
    public String date;       // "YYYY-MM-DD", nullable
    public String photoUri;   // nullable — local URI or remote URL

    public Memory() {}
}
