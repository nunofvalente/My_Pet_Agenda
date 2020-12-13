package com.nunovalente.android.mypetagenda.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.nunovalente.android.mypetagenda.util.Constants;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Entity(tableName = "pet_table")
public class Pet implements Serializable {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "accountId")
    @Exclude
    private String accountId;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "birthday")
    private String birthday;

    @ColumnInfo(name = "type")
    private String type;

    @ColumnInfo(name = "breed")
    private String breed;

    @ColumnInfo(name = "weight")
    private String weight;

    @ColumnInfo(name = "imagePath")
    private String imagePath;

    @Ignore
    public Pet() {
        id = "";
    }

    @Ignore
    public Pet(@NotNull String id, String name, String birthday, String type, String breed, String weight, String imagePath) {
        this.id = id;
        this.name = name;
        this.birthday = birthday;
        this.type = type;
        this.breed = breed;
        this.weight = weight;
        this.imagePath = imagePath;
    }

    public Pet(@NotNull String id, String accountId, String name, String birthday, String type, String breed, String weight, String imagePath) {
        this.id = id;
        this.accountId = accountId;
        this.name = name;
        this.birthday = birthday;
        this.type = type;
        this.breed = breed;
        this.weight = weight;
        this.imagePath = imagePath;
    }

    @NotNull
    public String getId() {
        return id;
    }

    public void setId(@NotNull String id) {
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

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public void updatePet(String accountId, String petId) {
        Map<String, Object> petValues = convertToMap();

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference path = database.child(Constants.PETS).child(accountId).child(petId);
        path.updateChildren(petValues);
    }

    @Exclude
    private Map<String, Object> convertToMap() {
        HashMap<String, Object> petMap = new HashMap<>();

        petMap.put("name", this.name);
        petMap.put("birthday", this.birthday);
        petMap.put("type", this.type);
        petMap.put("breed", this.breed);
        petMap.put("weight", this.weight);
        petMap.put("imagePath", this.imagePath);

        return petMap;
    }
}
