package com.nunovalente.android.mypetagenda.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.nunovalente.android.mypetagenda.R;
import com.nunovalente.android.mypetagenda.databinding.ReminderListAdapterBinding;
import com.nunovalente.android.mypetagenda.model.Reminder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecyclerAddReminderAdapter extends RecyclerView.Adapter<RecyclerAddReminderAdapter.AddReminderViewHolder> {

    private ReminderListAdapterBinding mBinding;

    private final List<Reminder> mReminderList;

    public RecyclerAddReminderAdapter(List<Reminder> mReminderList) {
        this.mReminderList = mReminderList;
    }

    @NonNull
    @Override
    public AddReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        mBinding = DataBindingUtil.inflate(inflater, R.layout.reminder_list_adapter, parent, false);

        return new RecyclerAddReminderAdapter.AddReminderViewHolder(mBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull AddReminderViewHolder holder, int position) {
        Reminder reminder = mReminderList.get(position);
        mBinding.setReminder(reminder);

    }

    @Override
    public int getItemCount() {
        return mReminderList.size();
    }

    public class AddReminderViewHolder extends RecyclerView.ViewHolder {

        public AddReminderViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
