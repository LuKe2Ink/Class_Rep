package com.example.classrep.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.classrep.database.DataConverter;

import java.util.Date;

@Entity(tableName = "institute",
        indices = @Index(value = "id_institute", unique = true))
public class Institute {

    @PrimaryKey
    private int id_institute;

    @ColumnInfo(name="institute_nome")
    private String institute;

    @ColumnInfo(name="institute_grade")
    private String grade;

    @ColumnInfo(name="institute_image")
    private String image;

    @ColumnInfo(name = "creation_date")
    @TypeConverters({DataConverter.class})
    private Date creation_date;

    public Institute(int id_institute, String institute, String grade, String image, Date creation_date) {
        this.id_institute = id_institute;
        this.institute = institute;
        this.grade = grade;
        this.image = image;
        this.creation_date = creation_date;
    }

    public int getId_institute() {
        return id_institute;
    }

    public String getImage() {
        return image;
    }

    public void setId_institute(int id_institute) {
        this.id_institute = id_institute;
    }

    public String getInstitute() {
        return institute;
    }

    public void setInstitute(String institute) {
        this.institute = institute;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Date getCreation_date() {
        return creation_date;
    }

    public void setCreation_date(Date creation_date) {
        this.creation_date = creation_date;
    }
}
