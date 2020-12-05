package com.nunovalente.android.mypetagenda.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nunovalente.android.mypetagenda.data.repository.FirebaseHelper;
import com.nunovalente.android.mypetagenda.util.Constants;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Entity(tableName = "owner_database")
public class Owner implements Serializable {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "email")
    private String email;

    @ColumnInfo(name = "password")
    @Ignore
    private String password;

    @ColumnInfo(name = "imagePath")
    private String imagePath;

    @ColumnInfo(name = "accountId")
    private String accountId;

    @Ignore
    public Owner() {
        id = null;
    }

    @Ignore
    public Owner(String id, String name, String email, String password, String imagePath, String accountId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.imagePath = imagePath;
        this.accountId = accountId;
    }

    public Owner(String id, String name, String email, String imagePath, String accountId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.imagePath = imagePath;
        this.accountId = accountId;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    @Ignore
    public String getPassword() {
        return password;
    }

    @Ignore
    public void setPassword(String password) {
        this.password = password;
    }

    public String getAccountId() {return accountId;}

    public void setAccountId(String accountId){this.accountId = accountId;}

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }


    public void updateUser() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        String userId = FirebaseHelper.getUserId();
        Map<String, Object> userValues = convertToMap();

        DatabaseReference path = database.child(Constants.USERS).child(userId);
        path.updateChildren(userValues);

        path.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Owner owner = snapshot.getValue(Owner.class);
                assert owner != null;
                DatabaseReference accountPath = database.child(Constants.ACCOUNT).child(owner.getAccountId()).child(userId);
                accountPath.updateChildren(userValues);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                error.getMessage();
            }
        });
    }

    @Exclude
    private Map<String, Object> convertToMap() {
        HashMap<String, Object> ownerMap = new HashMap<>();

        ownerMap.put("email", this.email);
        ownerMap.put("name", this.name);
        ownerMap.put("imagePath", this.imagePath);

        return ownerMap;
    }
}
