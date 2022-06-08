package com.example.classrep.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ClassRepDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertIstituti(Institute...institute);


    @Query("SELECT * FROM institute")
    List<Institute> getAllIstituti();
}
