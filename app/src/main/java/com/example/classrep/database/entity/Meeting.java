package com.example.classrep.database.entity;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.classrep.database.DataConverter;

import java.util.Date;


@Entity(tableName = "meeting",
        primaryKeys = {"id_meeting", "foreign_institute"},
        foreignKeys = @ForeignKey(entity = Institute.class, parentColumns = "id_institute", childColumns = "foreign_institute", onDelete = CASCADE),
        indices = {@Index(value = "foreign_institute"), @Index(value = "id_meeting", unique = true)})
public class Meeting {

    @ColumnInfo(name = "id_meeting")
    private int id_meeting;

    @ColumnInfo(name = "foreign_institute")
    private int foreign_institute;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "type")
    private String type;

    @ColumnInfo(name = "date")
    @TypeConverters({DataConverter.class})
    private Date date;

    @ColumnInfo(name = "place")
    private String place;

    @ColumnInfo(name = "note")
    private String note;

    @ColumnInfo(name = "report")
    private String report;

    public Meeting(int id_meeting, int foreign_institute, String title, String type, Date date, String place, String note) {
        this.id_meeting = id_meeting;
        this.foreign_institute = foreign_institute;
        this.title = title;
        this.type = type;
        this.date = date;
        this.place = place;
        this.note=note;
    }

    public int getForeign_institute() {
        return foreign_institute;
    }

    public void setForeign_institute(int foreign_institute) {
        this.foreign_institute = foreign_institute;
    }

    public int getId_meeting() {
        return id_meeting;
    }

    public void setId_meeting(int id_meeting) {
        this.id_meeting = id_meeting;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }
}
