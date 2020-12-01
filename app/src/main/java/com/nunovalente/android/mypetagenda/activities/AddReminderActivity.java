package com.nunovalente.android.mypetagenda.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.nunovalente.android.mypetagenda.R;
import com.nunovalente.android.mypetagenda.data.repository.FirebaseHelper;
import com.nunovalente.android.mypetagenda.databinding.ActivityAddReminderBinding;
import com.nunovalente.android.mypetagenda.fragments.PetProfileFragment;
import com.nunovalente.android.mypetagenda.model.Owner;
import com.nunovalente.android.mypetagenda.model.Pet;
import com.nunovalente.android.mypetagenda.model.Reminder;
import com.nunovalente.android.mypetagenda.util.Constants;
import com.nunovalente.android.mypetagenda.viewmodel.FirebaseViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.nunovalente.android.mypetagenda.fragments.MyPetsFragment.PET;

public class AddReminderActivity extends AppCompatActivity {

    private ActivityAddReminderBinding mBinding;
    private FirebaseViewModel firebaseViewModel;

    private final Calendar mCalendar = Calendar.getInstance();
    private final Reminder reminder = new Reminder();
    private final List<String> mDaysList = new ArrayList<>();

    private Pet pet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_reminder);

        firebaseViewModel = new ViewModelProvider(this).get(FirebaseViewModel.class);
        AddReminderClickHandler mHandler = new AddReminderClickHandler();
        mBinding.setClickHandler(mHandler);

        Bundle bundle = getIntent().getExtras();
        if (bundle.containsKey(PetProfileFragment.PET)) {
            pet = (Pet) bundle.getSerializable(PetProfileFragment.PET);
        }

        mBinding.reminderTimePicker.setIs24HourView(true);
        mBinding.toolbarReminder.setNavigationOnClickListener(v -> {
            finish();
        });
    }

    public class AddReminderClickHandler {

        public AddReminderClickHandler() {
        }

        public void validateReminder(View view) {
            if (!mBinding.editReminderTitle.getText().toString().isEmpty()) {
                if (!mBinding.buttonDate.getText().toString().equals(getString(R.string.select_date))) {
                    validateCheckboxes();
                    validateTime();

                    reminder.setTitle(mBinding.editReminderTitle.getText().toString());
                    reminder.setDate(mBinding.buttonDate.getText().toString());
                    reminder.setIsActive(true);

                    saveReminder(reminder);

                } else {
                    Toast.makeText(AddReminderActivity.this, "Please select a date!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(AddReminderActivity.this, "Add a reminder title.", Toast.LENGTH_LONG).show();
            }
        }

        private void saveReminder(Reminder reminder) {
            DatabaseReference databaseReference = firebaseViewModel.getDatabase();
            databaseReference.child(Constants.USERS).child(FirebaseHelper.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Owner owner = snapshot.getValue(Owner.class);
                    firebaseViewModel.saveReminder(reminder, owner.getAccountId(), pet.getId());
                    finish();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        public void chooseReminderDate(View view) {

            DatePickerDialog.OnDateSetListener date = (view1, year, month, dayOfMonth) -> {
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH, month);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            };

            new DatePickerDialog(AddReminderActivity.this, date, mCalendar
                    .get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                    mCalendar.get(Calendar.DAY_OF_MONTH)).show();
        }

        private void updateLabel() {
            String format = "dd/MM/yyyy";
            SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.UK);

            String date = sdf.format(mCalendar.getTime());
            mBinding.buttonDate.setText(date);
            reminder.setDate(date);
        }

        private void validateCheckboxes() {
            CheckBox mondayCheckBox = findViewById(R.id.checkbox_monday);
            CheckBox tuesdayCheckBox = findViewById(R.id.checkbox_tuesday);
            CheckBox wednesdayCheckBox = findViewById(R.id.checkbox_wednesday);
            CheckBox thursdayCheckBox = findViewById(R.id.checkbox_thursday);
            CheckBox fridayCheckBox = findViewById(R.id.checkbox_friday);
            CheckBox saturdayCheckBox = findViewById(R.id.checkbox_saturday);
            CheckBox sundayCheckBox = findViewById(R.id.checkbox_sunday);

            if (mondayCheckBox.isChecked()) {
                mDaysList.add(mondayCheckBox.getText().toString());
            }

            if (tuesdayCheckBox.isChecked()) {
                mDaysList.add(tuesdayCheckBox.getText().toString());
            }

            if (wednesdayCheckBox.isChecked()) {
                mDaysList.add(wednesdayCheckBox.getText().toString());
            }

            if (thursdayCheckBox.isChecked()) {
                mDaysList.add(thursdayCheckBox.getText().toString());
            }

            if (fridayCheckBox.isChecked()) {
                mDaysList.add(fridayCheckBox.getText().toString());
            }

            if (saturdayCheckBox.isChecked()) {
                mDaysList.add(saturdayCheckBox.getText().toString());
            }

            if (sundayCheckBox.isChecked()) {
                mDaysList.add(sundayCheckBox.getText().toString());
            }

            reminder.setDays(mDaysList);
        }

        private void validateTime() {
            int hour, minute;
            if (Build.VERSION.SDK_INT >= 23) {
                hour = mBinding.reminderTimePicker.getHour();
                minute = mBinding.reminderTimePicker.getMinute();
            } else {
                hour = mBinding.reminderTimePicker.getCurrentHour();
                minute = mBinding.reminderTimePicker.getCurrentMinute();
            }

            reminder.setHour(String.valueOf(hour));
            reminder.setMinutes(String.valueOf(minute));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}