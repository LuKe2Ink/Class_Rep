package com.example.classrep.database.entity;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.TypeConverters;

import com.example.classrep.database.DataConverter;

import java.util.Date;


@Entity(tableName = "pta_meeting",
        primaryKeys = {"id_pta", "foreign_institute"},
        foreignKeys = @ForeignKey(entity = Institute.class, parentColumns = "id_institute", childColumns = "foreign_institute", onDelete = CASCADE),
        indices = {@Index(value = "foreign_institute"), @Index(value = "id_pta", unique = true)})

public class PTAmeeting {

    @ColumnInfo(name = "id_pta")
    int id_pta;

    @ColumnInfo(name = "foreign_institute")
    int foreign_institute;

    @ColumnInfo(name = "name")
    String name;

    @ColumnInfo(name = "surname")
    String surname;

    @ColumnInfo(name = "start_date")
    @TypeConverters({DataConverter.class})
    Date start_date;

    @ColumnInfo(name = "finish_date")
    @TypeConverters({DataConverter.class})
    Date finish_date;

    @ColumnInfo(name = "subject")
    String subject; //da vedere bene come fare

    public PTAmeeting(int id_pta, int foreign_institute, String name, String surname, Date start_date, Date finish_date, String subject) {
        this.id_pta = id_pta;
        this.foreign_institute = foreign_institute;
        this.name = name;
        this.surname = surname;
        this.start_date = start_date;
        this.finish_date = finish_date;
        this.subject = subject;
    }

    public int getId_pta() {
        return id_pta;
    }

    public void setId_pta(int id_pta) {
        this.id_pta = id_pta;
    }

    public int getForeign_institute() {
        return foreign_institute;
    }

    public void setForeign_institute(int foreign_institute) {
        this.foreign_institute = foreign_institute;
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

    public Date getStart_date() {
        return start_date;
    }

    public void setStart_date(Date start_date) {
        this.start_date = start_date;
    }

    public Date getFinish_date() {
        return finish_date;
    }

    public void setFinish_date(Date finish_date) {
        this.finish_date = finish_date;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
