package com.example.classrep.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Institute.class}, version = 1, exportSchema = false)
public abstract class ClassRepDB extends RoomDatabase {

    public abstract ClassRepDAO ClassRepDAO();

    private static volatile ClassRepDB INSTANCE;

    static final ExecutorService executor = Executors.newFixedThreadPool(4);

    public static synchronized ClassRepDB getDatabase(final Context context) {
        if (INSTANCE == null) {

            synchronized (ClassRepDB.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            ClassRepDB.class, "ClassRep_database")
                            .build();

                }
            }

        }
        return INSTANCE;
    }
}
