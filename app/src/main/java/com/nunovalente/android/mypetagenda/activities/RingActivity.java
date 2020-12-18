package com.nunovalente.android.mypetagenda.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.nunovalente.android.mypetagenda.R;
import com.nunovalente.android.mypetagenda.databinding.ActivityRingtoneBinding;
import com.nunovalente.android.mypetagenda.notif.AlarmService;

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
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == mBinding.buttonRingtoneDismiss.getId()) {
            Intent intentService = new Intent(getApplicationContext(), AlarmService.class);
            getApplicationContext().stopService(intentService);
            finish();
        }
    }
}
