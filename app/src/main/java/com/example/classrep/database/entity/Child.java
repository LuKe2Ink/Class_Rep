package com.example.classrep.database.entity;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "child",
        primaryKeys = {"foreign_event", "id_child"},
        foreignKeys = @ForeignKey(entity = Event.class, parentColumns = "id_event", childColumns = "foreign_event", onDelete = CASCADE),
        indices = {@Index(value = "foreign_event"), @Index(value = "id_child")})

public class Child {

    @ColumnInfo(name = "id_child")
    private int id_child;

    @ColumnInfo(name = "foreign_event")
    private int foreign_event;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "surname")
    private String surname;

    public Child(int id_child, int foreign_event, String name, String surname) {
        this.id_child = id_child;
        this.foreign_event = foreign_event;
        this.name = name;
        this.surname = surname;
    }

    public int getId_child() {
        return id_child;
    }

    public void setId_child(int id_child) {
        this.id_child = id_child;
    }

    public int getForeign_event() {
        return foreign_event;
    }

    public void setForeign_event(int foreign_event) {
        this.foreign_event = foreign_event;
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
}
