package com.example.classrep.database.entity;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.TypeConverters;

import com.example.classrep.database.DataConverter;

import java.util.Date;

@Entity(tableName = "parent",
        primaryKeys = {"foreign_event", "foreign_pta"},
        foreignKeys = {
            @ForeignKey(entity = Event.class, parentColumns = "id_event", childColumns = "foreign_event", onDelete = CASCADE),
            @ForeignKey(entity = PTAmeeting.class, parentColumns = "id_pta", childColumns = "foreign_pta", onDelete = CASCADE)},
        indices = {@Index(value = "foreign_event"), @Index(value = "foreign_pta")})

public class Parent {

    @ColumnInfo(name = "foreign_event")
    private int foreign_event;

    @ColumnInfo(name = "foreign_pta")
    private int foreign_pta;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "surname")
    private String surname;

    @ColumnInfo(name = "time")
    @TypeConverters({DataConverter.class})
    private Date time;

    public Parent(int foreign_event, int foreign_pta, String name, String surname, Date time) {
        this.foreign_event = foreign_event;
        this.foreign_pta = foreign_pta;
        this.name = name;
        this.surname = surname;
        this.time = time;
    }

    public int getForeign_event() {
        return foreign_event;
    }

    public void setForeign_event(int foreign_event) {
        this.foreign_event = foreign_event;
    }

    public int getForeign_pta() {
        return foreign_pta;
    }

    public void setForeign_pta(int foreign_pta) {
        this.foreign_pta = foreign_pta;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
