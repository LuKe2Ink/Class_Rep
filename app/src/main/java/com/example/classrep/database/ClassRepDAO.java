package com.example.classrep.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ClassRepDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertIstituti(Istitute ...institute);


    @Query("SELECT * FROM Istitute")
    List<Istitute> getAllIstituti();
}
