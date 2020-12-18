package com.nunovalente.android.mypetagenda.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.nunovalente.android.mypetagenda.R;
import com.nunovalente.android.mypetagenda.databinding.HomeListAdapterBinding;
import com.nunovalente.android.mypetagenda.model.Reminder;

import java.util.List;

public class RecyclerHomeAdapter extends RecyclerView.Adapter<RecyclerHomeAdapter.MyHomeViewHolder> {

    private HomeListAdapterBinding mBinding;

    private final List<Reminder> mReminderList;

    public RecyclerHomeAdapter(List<Reminder> mReminderList) {
        this.mReminderList = mReminderList;
    }

    @NonNull
    @Override
    public MyHomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        mBinding = DataBindingUtil.inflate(inflater, R.layout.home_list_adapter, parent, false);

        return new MyHomeViewHolder(mBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull MyHomeViewHolder holder, int position) {
        Reminder reminder = mReminderList.get(position);
        mBinding.setReminder(reminder);
        if (!reminder.isRecurring()) {
            mBinding.reminderDays.setText(R.string.not_recurring);
        }
    }

    @Override
    public int getItemCount() {
        return mReminderList.size();
    }

    public class MyHomeViewHolder extends RecyclerView.ViewHolder {

        public MyHomeViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
