package com.nunovalente.android.mypetagenda.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.nunovalente.android.mypetagenda.R;
import com.nunovalente.android.mypetagenda.databinding.ReminderListAdapterBinding;
import com.nunovalente.android.mypetagenda.model.Reminder;

import java.util.List;

public class RecyclerAddReminderAdapter extends RecyclerView.Adapter<RecyclerAddReminderAdapter.AddReminderViewHolder> {

    private ReminderListAdapterBinding mBinding;

    private final List<Reminder> mReminderList;
    private final RecyclerItemClickListener listener;
    private final OnToggleAlarmListener alarmListener;

    public RecyclerAddReminderAdapter(List<Reminder> mReminderList, RecyclerItemClickListener listener, OnToggleAlarmListener alarmListener) {
        this.mReminderList = mReminderList;
        this.listener = listener;
        this.alarmListener = alarmListener;
    }

    @NonNull
    @Override
    public AddReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        mBinding = DataBindingUtil.inflate(inflater, R.layout.reminder_list_adapter, parent, false);

        return new AddReminderViewHolder(mBinding.getRoot(), alarmListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AddReminderViewHolder holder, int position) {
        Reminder reminder = mReminderList.get(position);
        mBinding.setReminder(reminder);
        if(reminder.isStarted()) {
            mBinding.switchReminder.setChecked(true);
        }
        if(reminder.getRecurringDays() != null) {
            mBinding.reminderDays.setText(reminder.getRecurringDays());
        } else {
            mBinding.reminderDays.setText(R.string.not_recurring);
        }
        holder.bind(reminder);
    }

    @Override
    public int getItemCount() {
        return mReminderList.size();
    }



    public class AddReminderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        SwitchMaterial reminderSwitch;
        private final OnToggleAlarmListener alarmListener;

        public AddReminderViewHolder(@NonNull View itemView, OnToggleAlarmListener alarmListener) {
            super(itemView);

            reminderSwitch = itemView.findViewById(R.id.switch_reminder);
            itemView.setOnLongClickListener(this);
            itemView.setOnClickListener(this);

            this.alarmListener = alarmListener;
        }

        public void bind(Reminder reminder) {
            reminderSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> alarmListener.onToggleAlarm(reminder));

        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            listener.onItemClickListener(adapterPosition);
        }

        @Override
        public boolean onLongClick(View v) {
            int adapterPosition = getAdapterPosition();
            listener.onLongClicked(adapterPosition);
            return true;
        }
    }
}
