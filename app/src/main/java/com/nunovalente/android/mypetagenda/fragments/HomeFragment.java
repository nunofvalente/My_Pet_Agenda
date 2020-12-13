package com.nunovalente.android.mypetagenda.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.transition.MaterialFadeThrough;
import com.nunovalente.android.mypetagenda.R;
import com.nunovalente.android.mypetagenda.adapters.RecyclerHomeAdapter;
import com.nunovalente.android.mypetagenda.databinding.FragmentHomeBinding;
import com.nunovalente.android.mypetagenda.model.Reminder;
import com.nunovalente.android.mypetagenda.util.NetworkUtils;
import com.nunovalente.android.mypetagenda.viewmodel.FirebaseViewModel;
import com.nunovalente.android.mypetagenda.viewmodel.RoomViewModel;

import java.util.List;

public class HomeFragment extends Fragment {

    private RoomViewModel roomViewModel;

    private FragmentHomeBinding mBinding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        roomViewModel = new ViewModelProvider(this).get(RoomViewModel.class);

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        View root = mBinding.getRoot();

        loadReminders();


        this.setExitTransition(new MaterialFadeThrough().setDuration(getResources().getInteger(R.integer.reply_motion_duration_large)));
        return root;
    }

    private void loadReminders() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        String accountId = sharedPreferences.getString(getString(R.string.pref_account_id), "");

        roomViewModel.getAllReminders(accountId).observe(requireActivity(), this::setRecyclerView);
    }

    private void setRecyclerView(List<Reminder> reminders) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        RecyclerHomeAdapter adapter = new RecyclerHomeAdapter(reminders);
        mBinding.recyclerHome.setLayoutManager(linearLayoutManager);
        mBinding.recyclerHome.setAdapter(adapter);
        mBinding.recyclerHome.setHasFixedSize(true);
    }


}