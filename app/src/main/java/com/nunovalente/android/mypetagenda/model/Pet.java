package com.nunovalente.android.mypetagenda.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "pet_database")
public class Pet implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "birthday")
    private String birthday;

    @ColumnInfo(name = "type")
    private String type;

    @ColumnInfo(name = "weight")
    private double weight;

    public Pet(int id, String name, String birthday, String type, double weight) {
        this.id = id;
        this.name = name;
        this.birthday = birthday;
        this.type = type;
        this.weight = weight;
    }

    @Ignore
    public Pet(String name, String birthday, String type, double weight) {
        this.name = name;
        this.birthday = birthday;
        this.type = type;
        this.weight = weight;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
