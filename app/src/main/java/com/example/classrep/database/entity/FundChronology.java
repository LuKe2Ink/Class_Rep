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


@Entity(tableName = "fund_chronology",
        primaryKeys = {"id_funcChron", "foreign_fund"},
        foreignKeys = @ForeignKey(entity = Fund.class, parentColumns = "id_fund", childColumns = "foreign_fund", onDelete = CASCADE),
        indices = {@Index(value = "foreign_fund"), @Index(value = "id_funcChron")})
public class FundChronology {

    @ColumnInfo(name = "id_funcChron")
    private int id_funcChron;

    @ColumnInfo(name = "foreign_fund")
    private int foreign_fund;

    @ColumnInfo(name = "quantity")
    private double quantity;

    @ColumnInfo(name = "action")
    private String action;

    @ColumnInfo(name = "date")
    @TypeConverters({DataConverter.class})
    private Date date;

    @ColumnInfo(name = "causal")
    private String causal;

    public FundChronology(int id_funcChron, int foreign_fund, double quantity, String action, Date date, String causal) {
        this.id_funcChron = id_funcChron;
        this.foreign_fund = foreign_fund;
        this.quantity = quantity;
        this.action = action;
        this.date = date;
        this.causal = causal;
    }

    public int getId_funcChron() {
        return id_funcChron;
    }

    public void setId_funcChron(int id_funcChron) {
        this.id_funcChron = id_funcChron;
    }

    public int getForeign_fund() {
        return foreign_fund;
    }

    public void setForeign_fund(int foreign_fund) {
        this.foreign_fund = foreign_fund;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getCausal() {
        return causal;
    }

    public void setCausal(String causal) {
        this.causal = causal;
    }
}
