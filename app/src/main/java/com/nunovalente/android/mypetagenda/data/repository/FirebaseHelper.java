package com.nunovalente.android.mypetagenda.data.repository;

import android.net.Uri;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.nunovalente.android.mypetagenda.model.Owner;
import com.nunovalente.android.mypetagenda.util.Base64Custom;

public class FirebaseHelper {

    private static final String TAG = FirebaseHelper.class.getSimpleName();

    private static final FirebaseAuth authentication = FirebaseAuth.getInstance();

  public static String getUserId() {
      String email = null;
      if (authentication.getCurrentUser() != null) {
          email = authentication.getCurrentUser().getEmail();
          assert email != null;
      }
      assert email != null;
      return Base64Custom.encodeString(email);
  }

    public static FirebaseUser getCurrentOwner() {
        return authentication.getCurrentUser();
    }

    public static boolean updateOwnerName(String name) {
        try {
            FirebaseUser user = authentication.getCurrentUser();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder().setDisplayName(name).build();

            assert user != null;
            user.updateProfile(profile).addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.d(TAG, "Error updating the profile name");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static Boolean updateUserPhoto(Uri url) {

        try {
            FirebaseUser owner = authentication.getCurrentUser();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder().setPhotoUri(url).build();

            assert owner != null;
            owner.updateProfile(profile).addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.d(TAG, "Error updating the profile photo");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static Owner getLoggedUserData() {
        FirebaseUser firebaseUser = authentication.getCurrentUser();

        Owner owner = new Owner("", "", "", "", "", "");
        if(firebaseUser != null) {
            owner.setEmail(firebaseUser.getEmail());
            owner.setName(firebaseUser.getDisplayName());
            if(firebaseUser.getEmail() != null) {
                owner.setId(Base64Custom.encodeString(firebaseUser.getEmail()));
            }


            if (firebaseUser.getPhotoUrl() == null) {
                owner.setImagePath("");
            } else {
                owner.setImagePath(firebaseUser.getPhotoUrl().toString());
            }
        }

        return owner;
    }

}
