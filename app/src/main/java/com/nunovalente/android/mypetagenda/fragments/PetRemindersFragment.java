package com.nunovalente.android.mypetagenda.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nunovalente.android.mypetagenda.R;
import com.nunovalente.android.mypetagenda.activities.AddReminderActivity;
import com.nunovalente.android.mypetagenda.adapters.OnToggleAlarmListener;
import com.nunovalente.android.mypetagenda.adapters.RecyclerAddReminderAdapter;
import com.nunovalente.android.mypetagenda.adapters.RecyclerItemClickListener;
import com.nunovalente.android.mypetagenda.databinding.FragmentPetRemindersBinding;
import com.nunovalente.android.mypetagenda.model.Pet;
import com.nunovalente.android.mypetagenda.model.Reminder;
import com.nunovalente.android.mypetagenda.viewmodel.FragmentShareViewModel;
import com.nunovalente.android.mypetagenda.viewmodel.RoomViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PetRemindersFragment extends Fragment implements RecyclerItemClickListener, OnToggleAlarmListener {

    public static final String REMINDER = "reminder_passed";

    private FragmentPetRemindersBinding mBinding;
    private RoomViewModel roomViewModel;

    private List<Reminder> mReminderList = new ArrayList<>();

    public PetRemindersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentShareViewModel fragmentShareViewModel = new ViewModelProvider(requireActivity()).get(FragmentShareViewModel.class);
        fragmentShareViewModel.getSelectedPet().observe(getViewLifecycleOwner(), this::retrieveReminders);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_pet_reminders, container, false);
        roomViewModel = new ViewModelProvider(this).get(RoomViewModel.class);

        return mBinding.getRoot();
    }

    private void retrieveReminders(Pet pet) {
        roomViewModel.getPetReminders(pet.getId()).observe(this, reminders -> {
            mReminderList = reminders;
            if(mReminderList.isEmpty()) {
                mBinding.tvNoReminders.setVisibility(View.VISIBLE);
            } else {
                mBinding.tvNoReminders.setVisibility(View.INVISIBLE);
                setRecyclerView(mReminderList);
            }
        });
    }

    private void setRecyclerView(List<Reminder> reminderList) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mBinding.recyclerReminders.setLayoutManager(linearLayoutManager);
        mBinding.recyclerReminders.setHasFixedSize(true);
        RecyclerAddReminderAdapter adapter = new RecyclerAddReminderAdapter(reminderList, this, this);
        mBinding.recyclerReminders.setAdapter(adapter);
    }

    private void showDialog(Reminder reminder) {
        AlertDialog.Builder alert = new AlertDialog.Builder(
                requireActivity());
        alert.setTitle(R.string.delete);
        alert.setMessage(R.string.are_you_sure_delete_reminder);
        alert.setPositiveButton(R.string.confirm, (dialog, which) -> {
            roomViewModel.deleteReminder(reminder);
            if (mBinding.recyclerReminders.getAdapter() != null) {
                mReminderList.remove(reminder);
                mBinding.recyclerReminders.getAdapter().notifyDataSetChanged();
            }
            dialog.dismiss();
        });
        alert.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss());
        alert.show();
    }

    @Override
    public void onToggleAlarm(Reminder reminder) {
        if (reminder.isStarted() && !reminder.isRecurring()) {
            reminder.cancelAlarm(requireActivity());
            reminder.setStarted(false);
        } else if(reminder.isStarted() && reminder.isRecurring()) {
            reminder.cancelRecurringAlarm(requireActivity());
        } else {
            reminder.schedule(requireActivity());
        }
        roomViewModel.updateReminder(reminder);
    }

    @Override
    public void onItemClickListener(int id) {
        Reminder reminder = mReminderList.get(id);
        Intent intent = new Intent(requireActivity(), AddReminderActivity.class);
        intent.putExtra(REMINDER, reminder);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onLongClicked(int id) {
        Reminder reminder = mReminderList.get(id);
        showDialog(reminder);
    }


}