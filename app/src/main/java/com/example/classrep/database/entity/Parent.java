package com.example.classrep.database.entity;

import static androidx.room.ForeignKey.CASCADE;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.classrep.database.DataConverter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity(tableName = "parent")

public class Parent {

    @PrimaryKey
    @ColumnInfo(name = "id_parent")
    private int id_parent;

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

    public Parent(int id_parent, int foreign_event, int foreign_pta, String name, String surname, Date time) {
        this.id_parent = id_parent;
        this.foreign_event = foreign_event;
        this.foreign_pta = foreign_pta;
        this.name = name;
        this.surname = surname;
        this.time = time;
    }

    public int getId_parent() {
        return id_parent;
    }

    public void setId_parent(int id_parent) {
        this.id_parent = id_parent;
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


    public String toTesto(){
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String testo = "Genitore: "+this.getName()+" "+this.getSurname()+" "+"Ora: "+dateFormat.format(this.getTime());
        return testo;
    }
}
