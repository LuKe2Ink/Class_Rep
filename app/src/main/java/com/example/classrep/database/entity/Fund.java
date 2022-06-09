package com.example.classrep.database.entity;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(tableName = "fund",
        primaryKeys = {"id_fund", "foreign_institute"},
        foreignKeys = @ForeignKey(entity = Institute.class, parentColumns = "id_institute", childColumns = "foreign_institute", onDelete = CASCADE),
        indices = {@Index(value = "foreign_institute"), @Index(value = "id_fund", unique = true)})
public class Fund {

    @ColumnInfo(name = "id_fund")
    private int id_fund;

    @ColumnInfo(name = "foreign_institute")
    private int foreign_institute;

    @ColumnInfo(name = "money")
    private double money;

    public Fund(int id_fund, int foreign_institute, double money) {
        this.id_fund = id_fund;
        this.foreign_institute = foreign_institute;
        this.money = money;
    }

    public int getId_fund() {
        return id_fund;
    }

    public void setId_fund(int id_fund) {
        this.id_fund = id_fund;
    }

    public int getForeign_institute() {
        return foreign_institute;
    }

    public void setForeign_institute(int foreign_institute) {
        this.foreign_institute = foreign_institute;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }
}
