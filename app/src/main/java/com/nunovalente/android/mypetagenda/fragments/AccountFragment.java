package com.nunovalente.android.mypetagenda.fragments;


import android.app.ActivityOptions;
import android.content.ClipboardManager;
import android.content.Context;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.transition.MaterialFadeThrough;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.nunovalente.android.mypetagenda.R;
import com.nunovalente.android.mypetagenda.activities.ProfileActivity;
import com.nunovalente.android.mypetagenda.adapters.RecyclerAccountAdapter;
import com.nunovalente.android.mypetagenda.data.repository.FirebaseHelper;
import com.nunovalente.android.mypetagenda.databinding.FragmentAccountBinding;
import com.nunovalente.android.mypetagenda.model.Owner;
import com.nunovalente.android.mypetagenda.util.Constants;
import com.nunovalente.android.mypetagenda.util.Permission;
import com.nunovalente.android.mypetagenda.viewmodel.FirebaseViewModel;

import java.util.ArrayList;
import java.util.Arrays;

public class AccountFragment extends Fragment {

    private static final String TAG = "account_fragment";

    private FragmentAccountBinding mBinding;
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;

    private final ArrayList<Owner> mAccountHoldersList = new ArrayList<>();


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        FirebaseViewModel firebaseViewModel = new ViewModelProvider(this).get(FirebaseViewModel.class);
        databaseReference = firebaseViewModel.getDatabase();

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_account, container, false);
        View root = mBinding.getRoot();

        AccountFragmentClickHandler mHandlers = new AccountFragmentClickHandler(getContext());
        mBinding.setClickHandler(mHandlers);

        getUserInformation();

        this.setExitTransition(new MaterialFadeThrough().setDuration(getResources().getInteger(R.integer.reply_motion_duration_large)));
        return root;
    }

    private void getUserInformation() {
        databaseReference.child(Constants.USERS).child(FirebaseHelper.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Owner owner = snapshot.getValue(Owner.class);
                mBinding.setOwner(owner);

                String accountId = owner.getAccountId();
                showAccountHoldersIfMoreThanOne(accountId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void configureRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mBinding.recyclerAccountHolders.setLayoutManager(layoutManager);
        mBinding.recyclerAccountHolders.setHasFixedSize(true);
    }

    public void showAccountHoldersIfMoreThanOne(String accountId) {
        valueEventListener = databaseReference.child(Constants.ACCOUNT).child(accountId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mAccountHoldersList.clear();

                for(DataSnapshot data: snapshot.getChildren()) {
                    Owner owner = data.getValue(Owner.class);

                    mAccountHoldersList.add(owner);
                }

                if(mAccountHoldersList.size() <= 1) {
                    mBinding.accountHoldersLinear.setVisibility(View.GONE);
                } else {
                    mBinding.accountHoldersLinear.setVisibility(View.VISIBLE);
                    setRecyclerView(mAccountHoldersList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, error.getMessage());
            }
        });
    }

    private void setRecyclerView(ArrayList<Owner> mList) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mBinding.recyclerAccountHolders.setLayoutManager(layoutManager);
        mBinding.recyclerAccountHolders.setHasFixedSize(true);
        RecyclerAccountAdapter adapter = new RecyclerAccountAdapter(mList);
        mBinding.recyclerAccountHolders.setAdapter(adapter);
    }

    public class AccountFragmentClickHandler {

        Context context;

        public AccountFragmentClickHandler(Context context) {
            this.context = context;
        }

        public void copyText(View view) {
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", mBinding.accountUniqueId.getText().toString().substring(14));
            clipboard.setPrimaryClip(clip);
            Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show();
        }

        public void openEditProfileActivity(View view) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle();
                Intent intent = new Intent(getContext(), ProfileActivity.class);
                startActivity(intent, bundle);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        configureRecyclerView();
    }

    @Override
    public void onStop() {
        super.onStop();
        databaseReference.removeEventListener(valueEventListener);
    }
}