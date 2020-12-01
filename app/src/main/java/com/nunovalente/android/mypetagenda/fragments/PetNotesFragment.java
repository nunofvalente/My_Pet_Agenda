package com.nunovalente.android.mypetagenda.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nunovalente.android.mypetagenda.R;

public class PetNotesFragment extends Fragment {


    public PetNotesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pet_notes, container, false);

        setRecyclerView(view);

        return view;
    }

    private void setRecyclerView(View view) {
        RecyclerView mRecyclerActivities = view.findViewById(R.id.recycler_activities);
        //TODO will inflate recycler view with activity objects, use progressbar as well
    }
}