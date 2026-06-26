package com.meragaw.vanshawali.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.meragaw.vanshawali.model.FamilyMember;
import com.meragaw.vanshawali.model.FamilyNotification;
import com.meragaw.vanshawali.model.Memory;

@Database(
        entities = {FamilyMember.class, FamilyNotification.class, Memory.class},
        version = 1,
        exportSchema = false)
@TypeConverters(Converters.class)
public abstract class AppDatabase extends RoomDatabase {

    public abstract FamilyMemberDao familyMemberDao();

    public abstract FamilyNotificationDao familyNotificationDao();

    public abstract MemoryDao memoryDao();

    private static volatile AppDatabase instance;

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "heirloom.db")
                            .build();
                }
            }
        }
        return instance;
    }
}
