package com.example.classrep.database.entity;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(tableName = "setting",
        primaryKeys = "foreign_institute",
        foreignKeys = @ForeignKey(entity = Institute.class, parentColumns = "id_institute", childColumns = "foreign_institute", onDelete = CASCADE),
        indices = @Index(value = "foreign_institute"))
public class Settings {

    @ColumnInfo(name = "foreign_institute")
    private int foreign_institute;

    @ColumnInfo(name = "color_App_bar")
    private int color_App_bar;

    @ColumnInfo(name = "notification")
    private boolean notification;

    @ColumnInfo(name = "last_notification")
    private boolean last_notification;

    @ColumnInfo(name="background_image")
    private String image;


    public Settings(int foreign_institute, int color_App_bar, boolean notification, boolean last_notification, String image) {
        this.foreign_institute = foreign_institute;
        this.color_App_bar = color_App_bar;
        this.notification = notification;
        this.last_notification = last_notification;
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getColor_App_bar() {
        return color_App_bar;
    }

    public void setColor_App_bar(int color_App_bar) {
        this.color_App_bar = color_App_bar;
    }

    public int getForeign_institute() {
        return foreign_institute;
    }

    public void setForeign_institute(int foreign_institute) {
        this.foreign_institute = foreign_institute;
    }


    public boolean isNotification() {
        return notification;
    }

    public void setNotification(boolean notification) {
        this.notification = notification;
    }

    public boolean isLast_notification() {
        return last_notification;
    }

    public void setLast_notification(boolean last_notification) {
        this.last_notification = last_notification;
    }
}
