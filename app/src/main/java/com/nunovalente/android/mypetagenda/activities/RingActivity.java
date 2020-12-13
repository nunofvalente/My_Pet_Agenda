package com.nunovalente.android.mypetagenda.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.nunovalente.android.mypetagenda.R;
import com.nunovalente.android.mypetagenda.databinding.ActivityRingtoneBinding;
import com.nunovalente.android.mypetagenda.model.Reminder;
import com.nunovalente.android.mypetagenda.notif.AlarmService;

import java.util.Calendar;
import java.util.Random;

public class RingActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityRingtoneBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_ringtone);


        setListeners();
    }

    private void setListeners() {
        mBinding.buttonRingtoneDismiss.setOnClickListener(this);
        mBinding.buttonRingtoneSnooze.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == mBinding.buttonRingtoneDismiss.getId()) {
            Intent intentService = new Intent(getApplicationContext(), AlarmService.class);
            getApplicationContext().stopService(intentService);
            finish();
        } else if (v.getId() == mBinding.buttonRingtoneSnooze.getId()) {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(System.currentTimeMillis());
            c.add(Calendar.MINUTE, 10);

/*
            //TODO need pet and account info
            Reminder reminder = new Reminder(new Random().nextInt(Integer.MAX_VALUE),
                    petId,
                    accountId,
                    petName,
                    "Snooze",
                    c.get(Calendar.HOUR_OF_DAY),
                    c.get(Calendar.MINUTE),
                    true,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false);

            reminder.schedule(getApplicationContext());

            Intent intentService = new Intent(getApplicationContext(), AlarmService.class);
            getApplicationContext().stopService(intentService);
            finish();
        }*/
        }

        //TODO add animation and better layout
    }
}
