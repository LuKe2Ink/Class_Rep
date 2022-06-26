package com.example.classrep.database.entity;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "adhesion",
        foreignKeys = @ForeignKey(entity = Event.class, parentColumns = "id_event", childColumns = "foreign_event", onDelete = CASCADE),
        indices = @Index(value = "foreign_event"))
public class Adhesion {

    @PrimaryKey
    @ColumnInfo(name = "foreign_event")
    private int foreign_event;

    @ColumnInfo(name = "money")
    private double money;

    public Adhesion(int foreign_event, double money) {
        this.foreign_event = foreign_event;
        this.money = money;
    }

    public int getForeign_event() {
        return foreign_event;
    }

    public void setForeign_event(int foreign_event) {
        this.foreign_event = foreign_event;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public String toTesto(){
        String testo= "Soldi: " + String.valueOf(this.getMoney());
        return testo;
    }
}
