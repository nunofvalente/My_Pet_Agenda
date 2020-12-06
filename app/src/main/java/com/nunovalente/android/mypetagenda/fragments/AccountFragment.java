package com.nunovalente.android.mypetagenda.fragments;


import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.ClipboardManager;
import android.content.Context;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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
import com.nunovalente.android.mypetagenda.util.NetworkUtils;
import com.nunovalente.android.mypetagenda.viewmodel.FirebaseViewModel;
import com.nunovalente.android.mypetagenda.viewmodel.RoomViewModel;

import java.util.ArrayList;

public class AccountFragment extends Fragment {

    private static final String TAG = "account_fragment";

    private FragmentAccountBinding mBinding;
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;
    private RoomViewModel roomViewModel;

    private final ArrayList<Owner> mAccountHoldersList = new ArrayList<>();


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_account, container, false);
        View root = mBinding.getRoot();

        FirebaseViewModel firebaseViewModel = new ViewModelProvider(this).get(FirebaseViewModel.class);
        roomViewModel = new ViewModelProvider(this).get(RoomViewModel.class);

        AccountFragmentClickHandler mHandlers = new AccountFragmentClickHandler(getContext());
        mBinding.setClickHandler(mHandlers);

       if(NetworkUtils.checkConnectivity(requireActivity().getApplication()) && FirebaseHelper.getCurrentOwner() != null) {
           databaseReference = firebaseViewModel.getDatabase();
           getUserInformation();
       }
       else {
           getOfflineUserInformation();
       }


        this.setExitTransition(new MaterialFadeThrough().setDuration(getResources().getInteger(R.integer.reply_motion_duration_large)));
        return root;
    }

    @SuppressLint("StaticFieldLeak")
    private void getOfflineUserInformation() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString(getString(R.string.pref_user_id), "");

        AsyncTask<String , Void, Owner> task = new AsyncTask<String, Void, Owner>() {
            @Override
            protected Owner doInBackground(String... strings) {
                return roomViewModel.getOwner(strings[0]);
            }

            @Override
            protected void onPostExecute(Owner owner) {
                super.onPostExecute(owner);
                mBinding.setOwner(owner);
            }
        };

        task.execute(userId);
    }

    private void getUserInformation() {
        valueEventListener = databaseReference.child(Constants.USERS).child(FirebaseHelper.getUserId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Owner owner = snapshot.getValue(Owner.class);
                mBinding.setOwner(owner);

                assert owner != null;
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
        databaseReference.child(Constants.ACCOUNT).child(accountId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mAccountHoldersList.clear();

                for (DataSnapshot data : snapshot.getChildren()) {
                    Owner owner = data.getValue(Owner.class);

                    mAccountHoldersList.add(owner);
                }

                if (mAccountHoldersList.size() <= 1) {
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

        private final Context context;

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
        if (NetworkUtils.checkConnectivity(requireActivity().getApplication()) && FirebaseHelper.getCurrentOwner() != null) {
            configureRecyclerView();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (valueEventListener != null) {
            databaseReference.removeEventListener(valueEventListener);
        }
    }
}