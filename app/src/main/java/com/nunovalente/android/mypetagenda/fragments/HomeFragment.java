package com.nunovalente.android.mypetagenda.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.transition.MaterialFadeThrough;
import com.nunovalente.android.mypetagenda.R;
import com.nunovalente.android.mypetagenda.databinding.FragmentHomeBinding;
import com.nunovalente.android.mypetagenda.util.NetworkUtils;
import com.nunovalente.android.mypetagenda.viewmodel.FirebaseViewModel;

public class HomeFragment extends Fragment {

    private FirebaseViewModel firebaseViewModel;

    private FragmentHomeBinding mBinding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        firebaseViewModel = new ViewModelProvider(this).get(FirebaseViewModel.class);

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        View root = mBinding.getRoot();

        if(!NetworkUtils.checkConnectivity(requireActivity().getApplication())) {
            mBinding.textNoNetwork.setVisibility(View.VISIBLE);
        }


        this.setExitTransition(new MaterialFadeThrough().setDuration(getResources().getInteger(R.integer.reply_motion_duration_large)));
        return root;
    }
}