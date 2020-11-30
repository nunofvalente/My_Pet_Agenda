package com.nunovalente.android.mypetagenda.data.repository;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.renderscript.Sampler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nunovalente.android.mypetagenda.R;
import com.nunovalente.android.mypetagenda.activities.SlideLoginActivity;
import com.nunovalente.android.mypetagenda.activities.MainActivity;
import com.nunovalente.android.mypetagenda.model.Owner;
import com.nunovalente.android.mypetagenda.model.Pet;
import com.nunovalente.android.mypetagenda.util.Base64Custom;
import com.nunovalente.android.mypetagenda.util.Constants;
import com.nunovalente.android.mypetagenda.util.StringGenerator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

public class FirebaseRepository {

    private static final String TAG = FirebaseRepository.class.getSimpleName();
    private static final Object LOCK = new Object();

    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private final StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private final FirebaseAuth authentication = FirebaseAuth.getInstance();
    private static FirebaseRepository repository;

    public static FirebaseRepository getRepository() {
        if (repository == null) {
            synchronized (LOCK) {
                repository = new FirebaseRepository();
            }
        }
        return repository;
    }

    public void sendConfirmationEmail(Context context) {

        FirebaseUser owner = authentication.getCurrentUser();
        owner.sendEmailVerification().addOnCompleteListener((Activity) context, task -> {
            if (task.isSuccessful()) {
                Toast.makeText(context, context.getResources().getString(R.string.verification_email), Toast.LENGTH_SHORT).show();
                context.startActivity(new Intent(context, SlideLoginActivity.class));
            }
        });
    }

    public void registerOwner(Owner authenticatedOwner, Context context, View view) {
        ProgressBar progressBar = view.findViewById(R.id.progress_dialog_register);
        progressBar.setVisibility(View.VISIBLE);
        authentication.createUserWithEmailAndPassword(authenticatedOwner.getEmail(), authenticatedOwner.getPassword()).addOnCompleteListener(authTask -> {
            if (authTask.isSuccessful()) {
                Log.d(TAG, "User created with success");
                FirebaseHelper.updateOwnerName(authenticatedOwner.getName());

                authenticatedOwner.setId(Base64Custom.encodeString(authenticatedOwner.getEmail()));

                saveOwner(authenticatedOwner);
                sendConfirmationEmail(context);

            } else {

                String exception;
                try {
                    throw authTask.getException();
                } catch (FirebaseAuthWeakPasswordException e) {
                    exception = "Please enter a stronger password!";
                } catch (FirebaseAuthInvalidCredentialsException f) {
                    exception = "Please enter a valid email!";
                    Log.d(TAG, f.getMessage());
                } catch (FirebaseAuthUserCollisionException g) {
                    exception = "Account already registered!";
                } catch (Exception e) {
                    exception = "Error registering user: " + e.getMessage();
                    e.printStackTrace();
                }

                Toast.makeText(context, exception, Toast.LENGTH_SHORT).show();
            }
            progressBar.setVisibility(View.GONE);
        });
    }

    private boolean isEmailVerified() {
        return authentication.getCurrentUser().isEmailVerified();
    }

    public void signInOwner(String email, String password, Context context, View view) {
        ProgressBar progressBar = view.findViewById(R.id.progress_login_activity);
        progressBar.setVisibility(View.VISIBLE);
        if (!email.equals("")) {
            if (!password.equals("")) {
                authentication.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (isEmailVerified()) {
                                progressBar.setVisibility(View.GONE);

                                SharedPreferences sharedpreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
                                if (!sharedpreferences.getBoolean(context.getString(R.string.pref_previously_started), false)) {
                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    editor.putBoolean(context.getString(R.string.pref_previously_started), Boolean.TRUE);
                                    editor.apply();
                                }

                                Intent i = new Intent(context, MainActivity.class);
                                context.startActivity(i);
                            } else if (!isEmailVerified()) {
                                Toast.makeText(context, "Please verify your email!", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        } else {
                            String exception;
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidUserException e) {
                                exception = "Email address does not exist!";
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                exception = "Password does not match with email!";
                            } catch (Exception e) {
                                exception = "Error logging in: " + e.getMessage();
                                e.printStackTrace();
                            }
                            Toast.makeText(context, exception, Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            } else {
                Toast.makeText(context, "Please enter a password!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "Please enter an email address!", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveOwner(Owner owner) {
        FirebaseUser user = authentication.getCurrentUser();
        if (user != null) {
            String accountId = StringGenerator.getRandomString();
            Owner newOwner = new Owner(owner.getId(), owner.getName(), owner.getEmail(), owner.getPassword(), owner.getImagePath(), accountId);

            //Saves account to account branch
            DatabaseReference pathAccount = databaseReference.child(Constants.ACCOUNT).child(accountId);
            pathAccount.child(newOwner.getId()).setValue(newOwner);

            //Saves owner to the owner branch
            DatabaseReference path = databaseReference.child(Constants.USERS);
            path.child(newOwner.getId()).setValue(newOwner);
        }
    }

    public void savePet(Pet pet) {
        //Saves Pet to the pet branch
        databaseReference.child(Constants.USERS).child(FirebaseHelper.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Owner owner = snapshot.getValue(Owner.class);
                DatabaseReference path = databaseReference.child(Constants.PETS).child(owner.getAccountId()).child(pet.getId());
                path.setValue(pet);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void resetPassword(Context context, String email, Dialog dialog) {
        if (!email.equals("")) {
            authentication.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(context, context.getResources().getString(R.string.emil_sent), Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    String exception;
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e) {
                        exception = "Email address does not exist!";
                    } catch (FirebaseAuthInvalidCredentialsException f) {
                        exception = "Please enter a valid email!";
                    } catch (Exception e) {
                        exception = "Error logging in: " + e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(context, exception, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void storeImage(Context context, String userId, String storagePath, String fileName, byte[] imageData) {
        StorageReference imageRef = storageReference
                .child(Constants.IMAGES)
                .child(storagePath)
                .child(userId)
                .child(fileName);

        UploadTask uploadTask = imageRef.putBytes(imageData);

        Task<Uri> urlTask = uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }
            return imageRef.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                if (downloadUri == null) {
                } else {
                    updateUserImage(downloadUri, context);
                }
            }
        });
    }

    public void storePetImage(Context context, String userId, String storagePath, String fileName, String imageUrl, Pet pet) {
        Bitmap image = null;
        try {
            Uri imagePath = Uri.parse(imageUrl);
            if (Build.VERSION.SDK_INT < 28) {
                image = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imagePath);
            } else {
                image = ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.getContentResolver(), imagePath));
            }

            if (image != null) {

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageData = baos.toByteArray();

                StorageReference imageRef = storageReference
                        .child(Constants.IMAGES)
                        .child(storagePath)
                        .child(userId)
                        .child(fileName);

                UploadTask uploadTask = imageRef.putBytes(imageData);

                Task<Uri> urlTask = uploadTask.continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return imageRef.getDownloadUrl();
                }).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String url = downloadUri.toString();
                        pet.setImagePath(url);
                        savePet(pet);
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateUserImage(Uri url, Context context) {
        boolean updated = FirebaseHelper.updateUserPhoto(url);
        Owner loggedOwner = FirebaseHelper.getLoggedUserData();
        databaseReference.child(Constants.USERS).child(loggedOwner.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Owner owner = snapshot.getValue(Owner.class);
                if (updated) {
                    loggedOwner.setImagePath(url.toString());
                    loggedOwner.setAccountId(owner.getAccountId());
                    loggedOwner.updateUser();
                    Toast.makeText(context, "Photo Updated!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

  /*  public void updatePetImage(Uri url, Context context) {
        boolean updated = FirebaseHelper.updateUserPhoto(url);
        Owner loggedOwner = FirebaseHelper.getLoggedUserData();
        databaseReference.child(Constants.USERS).child(loggedOwner.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Owner owner = snapshot.getValue(Owner.class);
                if (updated) {
                    loggedOwner.setImagePath(url.toString());
                    loggedOwner.setAccountId(owner.getAccountId());
                    loggedOwner.updateUser();
                    Toast.makeText(context, "Photo Updated!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }*/

    public void addPet(Pet pet, Context context, View view) {
        ProgressBar progressBar = view.findViewById(R.id.progress_add_pet);
        progressBar.setVisibility(View.VISIBLE);
        FirebaseUser user = authentication.getCurrentUser();

    /*    if (user != null) {
            //Saves owner to the pet branch. Pet id is his name in Base64
            DatabaseReference path = databaseReference.child(Constants.PETS).child(FirebaseHelper.getAccountId());
            String petId = Base64Custom.encodeString(pet.getName());
            path.child(petId).setValue(pet);
            progressBar.setVisibility(View.INVISIBLE);
        }*/
    }

    public FirebaseAuth getAuthentication() {
        return authentication;
    }

    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }
}

