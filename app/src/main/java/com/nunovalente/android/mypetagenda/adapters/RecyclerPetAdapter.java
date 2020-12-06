package com.nunovalente.android.mypetagenda.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nunovalente.android.mypetagenda.R;
import com.nunovalente.android.mypetagenda.databinding.PetListAdapterBinding;
import com.nunovalente.android.mypetagenda.model.Pet;

import java.util.List;

public class RecyclerPetAdapter extends RecyclerView.Adapter<RecyclerPetAdapter.MyPetViewHolder> {

    private PetListAdapterBinding mBinding;

    private final Context context;
    private final List<Pet> mPetList;

    public RecyclerPetAdapter(Context context, List<Pet> mPetList) {
        this.context = context;
        this.mPetList = mPetList;
    }

    @NonNull
    @Override
    public MyPetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        mBinding = DataBindingUtil.inflate(inflater, R.layout.pet_list_adapter, parent, false);

        return new MyPetViewHolder(mBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyPetViewHolder holder, int position) {
        Pet pet = mPetList.get(position);
        mBinding.setPet(pet);
        Glide.with(context).load(pet.getImagePath()).into(mBinding.recyclerPetImage);
    }

    @Override
    public int getItemCount() {
        return mPetList.size();
    }

    public static class MyPetViewHolder extends RecyclerView.ViewHolder {


        public MyPetViewHolder(@NonNull PetListAdapterBinding binding) {
            super(binding.getRoot());
        }
    }
}

