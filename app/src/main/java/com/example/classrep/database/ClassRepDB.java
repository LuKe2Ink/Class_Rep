package com.example.classrep.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.classrep.database.entity.Adhesion;
import com.example.classrep.database.entity.Child;
import com.example.classrep.database.entity.Event;
import com.example.classrep.database.entity.Fund;
import com.example.classrep.database.entity.FundChronology;
import com.example.classrep.database.entity.Institute;
import com.example.classrep.database.entity.Meeting;
import com.example.classrep.database.entity.PTAmeeting;
import com.example.classrep.database.entity.Parent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities =
            {Institute.class, Adhesion.class, Child.class, Event.class, Fund.class, FundChronology.class, Meeting.class, Parent.class, PTAmeeting.class}
             , version = 5, exportSchema = false)
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
                            .fallbackToDestructiveMigration()
                            .build();

                }
            }

        }
        return INSTANCE;
    }
}
