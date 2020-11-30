package com.nunovalente.android.mypetagenda.model;

import java.io.Serializable;

public class Pet implements Serializable {

    private String id;
    private String name;
    private String birthday;
    private String type;
    private String breed;
    private String weight;
    private String notes;
    private String imagePath;

    public Pet() {

    }

    public Pet(String id, String name, String birthday, String type, String breed, String weight, String notes, String imagePath) {
        this.id = id;
        this.name = name;
        this.birthday = birthday;
        this.type = type;
        this.breed = breed;
        this.weight = weight;
        this.notes = notes;
        this.imagePath = imagePath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
