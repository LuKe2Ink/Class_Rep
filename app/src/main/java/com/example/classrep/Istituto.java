package com.example.classrep;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Istituto {

    @PrimaryKey
    private int id;

    @ColumnInfo(name="istituto_nome")
    private String istituto;

    @ColumnInfo(name="istituto_classe")
    private String classe;

    @ColumnInfo(name="istituto_image")
    private String image;

    public Istituto() {
    }

    public Istituto(int id, String istituto, String classe, String image) {
        this.id = id;
        this.istituto = istituto;
        this.classe = classe;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public String getIstituto() {
        return istituto;
    }

    public String getClasse() {
        return classe;
    }

    public String getImage() {
        return image;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setIstituto(String istituto) {
        this.istituto = istituto;
    }

    public void setClasse(String classe) {
        this.classe = classe;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
