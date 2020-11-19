package com.nunovalente.android.mypetagenda.activities.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.nunovalente.android.mypetagenda.R;
import com.nunovalente.android.mypetagenda.viewmodel.FirebaseViewModel;

import static com.nunovalente.android.mypetagenda.R.id.button_reset_password;
import static com.nunovalente.android.mypetagenda.R.id.tv_cancel_reset_password;

public class DialogResetPassword extends DialogFragment implements View.OnClickListener {

    private FirebaseViewModel firebaseViewModel;
    private AlertDialog.Builder builder;

    private Button mResetButton;
    private TextView mCancel;
    private EditText mEditEmail;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        if(getActivity() != null) {

            builder = new AlertDialog.Builder(getActivity());

            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_reset_password, null);

            firebaseViewModel = new ViewModelProvider(this).get(FirebaseViewModel.class);

            mResetButton = view.findViewById(button_reset_password);
            mEditEmail = view.findViewById(R.id.edit_reset_email);
            mCancel = view.findViewById(tv_cancel_reset_password);

            builder.setView(view)
                    .setTitle(R.string.reset_password);


            setListeners();
        }

        return builder.create();
    }

    private void setListeners() {
        mResetButton.setOnClickListener(this);
        mCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case button_reset_password:
                sendResetEmail();
                break;
            case tv_cancel_reset_password:
                getDialog().dismiss();
                break;
        }
    }

    private void sendResetEmail() {
        Dialog dialog = getDialog();
        String email = mEditEmail.getText().toString();
        firebaseViewModel.resetPassword(getActivity(), email, dialog);
    }
}
