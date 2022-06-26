package com.example.classrep.database.entity;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.TypeConverters;

import com.example.classrep.database.DataConverter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity(tableName = "event",
        primaryKeys = {"id_event", "foreign_institute"},
        foreignKeys = @ForeignKey(entity = Institute.class, parentColumns = "id_institute", childColumns = "foreign_institute", onDelete = CASCADE),
        indices = {@Index(value = "foreign_institute"), @Index(value = "id_event", unique = true)})
public class Event {

    @ColumnInfo(name = "id_event")
    private int id_event;

    @ColumnInfo(name = "foreign_institute")
    private int foreign_institute;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "children")
    private boolean children;

    @ColumnInfo(name = "adhesions")
    private boolean adhesions;

    @ColumnInfo(name = "date")
    @TypeConverters({DataConverter.class})
    private Date date;

    @ColumnInfo(name = "note")
    private String note;

    @ColumnInfo(name = "place")
    private String place;

    public Event(int id_event, int foreign_institute, String title, boolean children, boolean adhesions, Date date, String note, String place) {
        this.id_event = id_event;
        this.foreign_institute = foreign_institute;
        this.title = title;
        this.children = children;
        this.adhesions = adhesions;
        this.date = date;
        this.note = note;
        this.place = place;
    }

    public int getId_event() {
        return id_event;
    }

    public void setId_event(int id_event) {
        this.id_event = id_event;
    }

    public int getForeign_institute() {
        return foreign_institute;
    }

    public void setForeign_institute(int foreign_institute) {
        this.foreign_institute = foreign_institute;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isChildren() {
        return children;
    }

    public void setChildren(boolean children) {
        this.children = children;
    }

    public boolean isAdhesions() {
        return adhesions;
    }

    public void setAdhesions(boolean adhesions) {
        this.adhesions = adhesions;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String toTesto(){
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String testo=
                this.getTitle()+"\n"
                +dateFormat.format(this.getDate())+"\n"
                +this.getPlace()+"\n"
                +this.getNote();
        return testo;
    }
}
