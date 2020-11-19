package com.nunovalente.android.mypetagenda.data.repository;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nunovalente.android.mypetagenda.R;
import com.nunovalente.android.mypetagenda.activities.SlideLoginActivity;
import com.nunovalente.android.mypetagenda.activities.MainActivity;
import com.nunovalente.android.mypetagenda.model.Owner;
import com.nunovalente.android.mypetagenda.util.Base64Custom;
import com.nunovalente.android.mypetagenda.util.Constants;

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
                Toast.makeText(context, context.getResources().getString(R.string.verifiation_email), Toast.LENGTH_SHORT).show();
                context.startActivity(new Intent(context, SlideLoginActivity.class));
            }
        });
    }

    public MutableLiveData<Owner> registerOwner(Owner authenticatedOwner, String accountId, Context context, View view) {
        ProgressBar progressBar = view.findViewById(R.id.progress_dialog_register);
        progressBar.setVisibility(View.VISIBLE);
        MutableLiveData<Owner> authenticatedOwnerMutableLiveData = new MutableLiveData<>();
        authentication.createUserWithEmailAndPassword(authenticatedOwner.getEmail(), authenticatedOwner.getPassword()).addOnCompleteListener(authTask -> {
            if (authTask.isSuccessful()) {
                Log.d(TAG, "User created with success");
                updateOwnerName(authenticatedOwner.getName());
                authenticatedOwner.setId(Base64Custom.encodeString(authenticatedOwner.getEmail()));
                saveOwner(authenticatedOwner, accountId);
                sendConfirmationEmail(context);
                progressBar.setVisibility(View.GONE);

            } else {

                String exception;
                try {
                    throw authTask.getException();
                } catch (FirebaseAuthWeakPasswordException e) {
                    exception = "Please enter a stronger password!";
                } catch (FirebaseAuthInvalidCredentialsException f) {
                    exception = "Please enter a valid email!";
                } catch (FirebaseAuthUserCollisionException g) {
                    exception = "Account already registered!";
                } catch (Exception e) {
                    exception = "Error registering user: " + e.getMessage();
                    e.printStackTrace();
                }

                Toast.makeText(context, exception, Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
        return authenticatedOwnerMutableLiveData;
    }

    private boolean isEmailVerified() {
        if (authentication.getCurrentUser().isEmailVerified()) {
            return true;
        } else {
            return false;
        }
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

    private void saveOwner(Owner authenticatedOwner, String accountId) {
        DatabaseReference path;
        if (accountId.equals("")) {
            path = databaseReference.child(Constants.USERS).push();
            accountId = path.getKey();
            authenticatedOwner.setAccountId(accountId);
        } else {
            path = databaseReference.child(Constants.USERS).child(accountId);
        }
        String email = Base64Custom.encodeString(authenticatedOwner.getEmail());
        path.child(email).setValue(authenticatedOwner);
    }

    public void updateOwnerName(String name) {
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
        }
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

    public FirebaseAuth getAuthentication() {
        return authentication;
    }
}

