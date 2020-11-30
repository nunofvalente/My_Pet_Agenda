package com.nunovalente.android.mypetagenda.fragments;

import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.transition.MaterialFadeThrough;
import com.nunovalente.android.mypetagenda.R;
import com.nunovalente.android.mypetagenda.viewmodel.FirebaseViewModel;

public class HomeFragment extends Fragment {

    private FirebaseViewModel firebaseViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        firebaseViewModel = new ViewModelProvider(this).get(FirebaseViewModel.class);

        View root = inflater.inflate(R.layout.fragment_home, container, false);


        this.setExitTransition(new MaterialFadeThrough().setDuration(getResources().getInteger(R.integer.reply_motion_duration_large)));
        return root;
    }
}