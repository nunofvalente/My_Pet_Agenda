package com.nunovalente.android.mypetagenda.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.CheckBox;

import com.nunovalente.android.mypetagenda.R;
import com.nunovalente.android.mypetagenda.databinding.ActivityAddReminderBinding;
import com.nunovalente.android.mypetagenda.fragments.PetProfileFragment;
import com.nunovalente.android.mypetagenda.fragments.PetRemindersFragment;
import com.nunovalente.android.mypetagenda.model.Pet;
import com.nunovalente.android.mypetagenda.model.Reminder;
import com.nunovalente.android.mypetagenda.viewmodel.RoomViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddReminderActivity extends AppCompatActivity {

    private static final String HOUR = "hour";
    private static final String MINUTE = "minute";

    private ActivityAddReminderBinding mBinding;

    private final Calendar mCalendar = Calendar.getInstance();
    private Reminder reminder = new Reminder();
    private CheckBox mon, tue, wed, thu, fri, sat, sun;
    private int hour;
    private int minute;

    private RoomViewModel roomViewModel;

    private Pet pet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_reminder);

        roomViewModel = new ViewModelProvider(this).get(RoomViewModel.class);

        AddReminderClickHandler mHandler = new AddReminderClickHandler();
        mBinding.setClickHandler(mHandler);

        initializeComponents();

        Bundle bundle = getIntent().getExtras();
        if (bundle.containsKey(PetProfileFragment.PET)) {
            pet = (Pet) bundle.getSerializable(PetProfileFragment.PET);
        } else if (bundle.containsKey(PetRemindersFragment.REMINDER)) {
            reminder = (Reminder) bundle.getSerializable(PetRemindersFragment.REMINDER);
            loadReminderData(reminder);
        }

        mBinding.reminderTimePicker.setIs24HourView(true);
        mBinding.toolbarReminder.setNavigationOnClickListener(v -> finish());
    }

    private void initializeComponents() {
        mon = findViewById(R.id.checkbox_monday);
        tue = findViewById(R.id.checkbox_tuesday);
        wed = findViewById(R.id.checkbox_wednesday);
        thu = findViewById(R.id.checkbox_thursday);
        fri = findViewById(R.id.checkbox_friday);
        sat = findViewById(R.id.checkbox_saturday);
        sun = findViewById(R.id.checkbox_sunday);
    }

    private void loadReminderData(Reminder reminder) {
        if (Build.VERSION.SDK_INT < 23) {
            mBinding.reminderTimePicker.setCurrentHour(reminder.getHour());
            mBinding.reminderTimePicker.setCurrentMinute(reminder.getMinutes());
        } else {
            mBinding.reminderTimePicker.setHour(reminder.getHour());
            mBinding.reminderTimePicker.setMinute(reminder.getMinutes());
        }

        mBinding.editReminderTitle.setText(reminder.getTitle());

        addCheckboxes(reminder);
    }

    private void addCheckboxes(Reminder reminder) {
        String checkedDays = reminder.getRecurringDays();

        if (checkedDays != null) {
            String[] daysArray = checkedDays.split(" ");
            for (String day : daysArray) {
                switch (day) {
                    case "Monday":
                        mon.setChecked(true);
                        break;
                    case "Tuesday":
                        tue.setChecked(true);
                        break;
                    case "Wednesday":
                        wed.setChecked(true);
                        break;
                    case "Thursday":
                        thu.setChecked(true);
                        break;
                    case "Friday":
                        fri.setChecked(true);
                        break;
                    case "Saturday":
                        sat.setChecked(true);
                        break;
                    case "Sunday":
                        sun.setChecked(true);
                        break;
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        return false;
    }

    public class AddReminderClickHandler {

        public AddReminderClickHandler() {
        }

        public void saveReminder(View view) {
            validateCheckboxes();
            validateTime();

            SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
            String accountId = sharedPreferences.getString(getString(R.string.pref_account_id), "");

            reminder.setTitle(mBinding.editReminderTitle.getText().toString());
            reminder.setStarted(true);

            Bundle bundle = getIntent().getExtras();
            if(bundle.containsKey(PetRemindersFragment.REMINDER)) {
                reminder.cancelAlarm(AddReminderActivity.this);
                reminder.setStarted(true);
                roomViewModel.updateReminder(reminder);
            } else {
                String petId = pet.getId();
                String petName = pet.getName();
                reminder.setPetName(petName);
                reminder.setAccountId(accountId);
                reminder.setPetId(petId);
                roomViewModel.insertReminder(reminder);
            }

            reminder.schedule(AddReminderActivity.this);
            finish();
        }

        private void validateCheckboxes() {
            reminder.setRecurring(false);
            reminder.setMonday(mon.isChecked());
            reminder.setTuesday(tue.isChecked());
            reminder.setWednesday(wed.isChecked());
            reminder.setThursday(thu.isChecked());
            reminder.setFriday(fri.isChecked());
            reminder.setSaturday(sat.isChecked());
            reminder.setSunday(sun.isChecked());

            reminder.setRecurring(mon.isChecked() || tue.isChecked() || wed.isChecked() || thu.isChecked() || fri.isChecked() || sat.isChecked() || sun.isChecked());
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

            reminder.setHour(hour);
            reminder.setMinutes(minute);
        }
    }
}