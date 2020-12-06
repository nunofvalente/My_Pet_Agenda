package com.nunovalente.android.mypetagenda.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.nunovalente.android.mypetagenda.R;
import com.nunovalente.android.mypetagenda.databinding.AccountListAdapterBinding;
import com.nunovalente.android.mypetagenda.model.Owner;

import java.util.List;

public class RecyclerAccountAdapter extends RecyclerView.Adapter<RecyclerAccountAdapter.MyAccountViewHolder> {

    private AccountListAdapterBinding mBinding;

    private final List<Owner> mOwnerList;

    public RecyclerAccountAdapter(List<Owner> mOwnerList) {
        this.mOwnerList = mOwnerList;
    }

    @NonNull
    @Override
    public MyAccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        mBinding = DataBindingUtil.inflate(inflater, R.layout.account_list_adapter, parent, false);

        return new MyAccountViewHolder(mBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAccountViewHolder holder, int position) {
            Owner owner = mOwnerList.get(position);
            mBinding.setOwner(owner);
            mBinding.setImageUrl(owner.getImagePath());
    }

    @Override
    public int getItemCount() {
        return mOwnerList.size();
    }

    public static class MyAccountViewHolder extends RecyclerView.ViewHolder {

        public MyAccountViewHolder(@NonNull AccountListAdapterBinding binding) {
            super(binding.getRoot());
        }
    }
}
