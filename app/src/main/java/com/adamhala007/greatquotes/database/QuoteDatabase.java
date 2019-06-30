package com.adamhala007.greatquotes.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Quote.class}, version = 2, exportSchema = false)
public abstract class QuoteDatabase extends RoomDatabase {
    private static QuoteDatabase INSTANCE;
    private static final String DATABASE_NAME = "quotes.db";

    public static QuoteDatabase getDatabase(final Context context){
        if (INSTANCE == null){
            synchronized (QuoteDatabase.class){
                if (INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), QuoteDatabase.class, DATABASE_NAME)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract DaoAccess daoAccess();
}
