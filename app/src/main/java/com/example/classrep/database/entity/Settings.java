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

    @ColumnInfo(name = "theme_mode")
    private boolean theme_mode;

    @ColumnInfo(name = "notification")
    private boolean notification;

    @ColumnInfo(name = "last_notification")
    private boolean last_notification;

    public Settings(int foreign_institute, boolean theme_mode, boolean notification, boolean last_notification) {
        this.foreign_institute = foreign_institute;
        this.theme_mode = theme_mode;
        this.notification = notification;
        this.last_notification = last_notification;
    }

    public int getForeign_institute() {
        return foreign_institute;
    }

    public void setForeign_institute(int foreign_institute) {
        this.foreign_institute = foreign_institute;
    }

    public boolean isTheme_mode() {
        return theme_mode;
    }

    public void setTheme_mode(boolean theme_mode) {
        this.theme_mode = theme_mode;
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
