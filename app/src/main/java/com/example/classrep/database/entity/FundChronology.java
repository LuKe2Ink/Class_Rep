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
        foreignKeys = @ForeignKey(entity = Fund.class, parentColumns = "id_fund", childColumns = "foreign_fund", onDelete = CASCADE),
        indices = @Index(value = "foreign_fund"))
public class FundChronology {

    @PrimaryKey
    @ColumnInfo(name = "foreign_fund")
    private int foreign_fund;

    @ColumnInfo(name = "quantity")
    private int quantity;

    @ColumnInfo(name = "action")
    private String action;

    @ColumnInfo(name = "date")
    @TypeConverters({DataConverter.class})
    private Date date;

    @ColumnInfo(name = "causal")
    private String causal;

    public FundChronology(int foreign_fund, int quantity, String action, Date date, String causal) {
        this.foreign_fund = foreign_fund;
        this.quantity = quantity;
        this.action = action;
        this.date = date;
        this.causal = causal;
    }

    public int getForeign_fund() {
        return foreign_fund;
    }

    public void setForeign_fund(int foreign_fund) {
        this.foreign_fund = foreign_fund;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
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
