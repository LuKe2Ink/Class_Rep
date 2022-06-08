package com.example.classrep.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "institute")
public class Institute {

    @PrimaryKey
    private int id;

    @ColumnInfo(name="institute_nome")
    private String istituto;

    @ColumnInfo(name="institute_classe")
    private String classe;

    @ColumnInfo(name="institute_image")
    private String image;

    public Institute(int id, String institute, String grade, String image) {
        this.id = id;
        this.istituto = institute;
        this.classe = grade;
        this.image = image;
    }

    public Institute() {

    }

    public int getId() {
        return id;
    }

    public String getImage() {
        return image;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIstituto() {
        return istituto;
    }

    public void setIstituto(String istituto) {
        this.istituto = istituto;
    }

    public String getClasse() {
        return classe;
    }

    public void setClasse(String classe) {
        this.classe = classe;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
