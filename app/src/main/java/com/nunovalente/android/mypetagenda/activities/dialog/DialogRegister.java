package com.nunovalente.android.mypetagenda.activities.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.transition.MaterialFadeThrough;
import com.nunovalente.android.mypetagenda.R;
import com.nunovalente.android.mypetagenda.model.Owner;
import com.nunovalente.android.mypetagenda.util.Base64Custom;
import com.nunovalente.android.mypetagenda.util.StringGenerator;
import com.nunovalente.android.mypetagenda.viewmodel.FirebaseViewModel;
import com.nunovalente.android.mypetagenda.viewmodel.RoomViewModel;

import static com.nunovalente.android.mypetagenda.R.id.button_register;
import static com.nunovalente.android.mypetagenda.R.id.tv_cancel_register;


public class DialogRegister extends AppCompatDialogFragment implements View.OnClickListener {

    private TextInputEditText mName, mEmail, mPassword, mConfirmPassword;
    private Button mRegisterButton;
    private TextView mCancel, mTermsAndConditions;
    private ProgressBar mProgressBar;
    private CheckBox mCheckBox;

    private FirebaseViewModel firebaseViewModel;
    private RoomViewModel roomViewModel;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_register, null);

        builder.setView(view)
                .setTitle(R.string.register_Account);


        firebaseViewModel = new ViewModelProvider(this).get(FirebaseViewModel.class);
        roomViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(this.requireActivity().getApplication())).get(RoomViewModel.class);


        mName = view.findViewById(R.id.owner_name);
        mEmail = view.findViewById(R.id.owner_email);
        mPassword = view.findViewById(R.id.owner_password);
        mConfirmPassword = view.findViewById(R.id.owner_confirm_password);
        mRegisterButton = view.findViewById(button_register);
        mCancel = view.findViewById(tv_cancel_register);
        mCheckBox = view.findViewById(R.id.checkbox_terms_conditions);
        mTermsAndConditions = view.findViewById(R.id.tv_terms_conditions);
        mProgressBar = view.findViewById(R.id.progress_dialog_register);

        setListeners();

        this.setEnterTransition(new MaterialFadeThrough().setDuration(getResources().getInteger(R.integer.reply_motion_duration_large)));

        return builder.create();
    }

    private void setListeners() {
        mRegisterButton.setOnClickListener(this);
        mCancel.setOnClickListener(this);
    }

    private void register() {
        String name = mName.getText().toString();
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();
        String confirmPassword = mConfirmPassword.getText().toString();


        if (!name.isEmpty()) {
            if (!email.isEmpty()) {
                if (!password.isEmpty() && password.length() >= 6) {
                    if (password.equals(confirmPassword)) {
                        if (mCheckBox.isChecked()) {

                            Owner owner = new Owner("", name, email, password, "", "");
                            String accountId = StringGenerator.getRandomString();
                            owner.setAccountId(accountId);
                            owner.setId(Base64Custom.encodeString(email));
                            firebaseViewModel.registerOwner(owner, getActivity(), mProgressBar);
                            roomViewModel.insertOwner(owner);

                            SharedPreferences sharedpreferences = requireActivity().getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString(getString(R.string.pref_account_id), owner.getAccountId());
                            editor.putString(getString(R.string.pref_user_id), owner.getId());
                            editor.apply();

                        } else {
                            Toast.makeText(requireActivity().getApplicationContext(), "Please confirm Terms and Conditions", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(requireActivity().getApplicationContext(), "Passwords must match!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireActivity().getApplicationContext(), "Password must contain at least 6 characters!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(requireActivity().getApplicationContext(), "Please fill your email!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(requireActivity().getApplicationContext(), "Please fill your name!", Toast.LENGTH_SHORT).show();
        }

    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case button_register:
                register();
                break;
            case tv_cancel_register:
                if (getDialog() != null) {
                    getDialog().dismiss();
                }
                break;
        }
    }

}

