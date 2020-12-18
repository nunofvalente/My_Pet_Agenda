package com.nunovalente.android.mypetagenda.fragments;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.android.material.transition.MaterialFadeThrough;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.nunovalente.android.mypetagenda.R;
import com.nunovalente.android.mypetagenda.activities.AddPetActivity;
import com.nunovalente.android.mypetagenda.activities.PetProfileActivity;
import com.nunovalente.android.mypetagenda.adapters.RecyclerClickListener;
import com.nunovalente.android.mypetagenda.adapters.RecyclerPetAdapter;
import com.nunovalente.android.mypetagenda.data.repository.FirebaseHelper;
import com.nunovalente.android.mypetagenda.databinding.FragmentMyPetsBinding;
import com.nunovalente.android.mypetagenda.model.Owner;
import com.nunovalente.android.mypetagenda.model.Pet;
import com.nunovalente.android.mypetagenda.util.Constants;
import com.nunovalente.android.mypetagenda.util.NetworkUtils;
import com.nunovalente.android.mypetagenda.viewmodel.FirebaseViewModel;
import com.nunovalente.android.mypetagenda.viewmodel.RoomViewModel;
import com.nunovalente.android.mypetagenda.widget.NoteService;

import java.util.ArrayList;
import java.util.List;

public class MyPetsFragment extends Fragment {

    public static final String PET = "pet";

    private RoomViewModel roomViewModel;

    private FirebaseAuth auth;
    private Pet pet;

    private DatabaseReference databaseReference;
    private FragmentMyPetsBinding mBinding;
    private ValueEventListener valueEventListener;
    private List<Pet> mPetList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_pets, container, false);
        roomViewModel = new ViewModelProvider(this).get(RoomViewModel.class);
        FirebaseViewModel firebaseViewModel1 = new ViewModelProvider(this).get(FirebaseViewModel.class);
        auth = firebaseViewModel1.getAuth();

        if (!NetworkUtils.checkConnectivity(requireActivity().getApplication()) || auth.getCurrentUser() == null) {
            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
            String accountId = sharedPreferences.getString(getString(R.string.pref_account_id), "");
            loadRecyclerView(accountId);
        } else {
            FirebaseViewModel firebaseViewModel = new ViewModelProvider(this).get(FirebaseViewModel.class);
            databaseReference = firebaseViewModel.getDatabase();
            MyPetFragmentClickHandler mHandlers = new MyPetFragmentClickHandler(getContext());
            mBinding.setClickHandler(mHandlers);
        }

        setListeners();


        this.setExitTransition(new MaterialFadeThrough().setDuration(getResources().getInteger(R.integer.reply_motion_duration_large)));
        return mBinding.getRoot();
    }

    @SuppressLint("StaticFieldLeak")
    private void loadRecyclerView(String accountId) {
        roomViewModel.getAllPets(accountId).observe(requireActivity(), pets -> {
            mPetList = pets;
            setRecyclerView(mPetList);
        });
    }

    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float dpWidth = metrics.widthPixels / metrics.density;
        int scalingFactor = 200;
        int noOfColumns = (int) (dpWidth / scalingFactor);
        if (noOfColumns < 2)
            noOfColumns = 2;
        return noOfColumns;
    }

    private void setListeners() {
        mBinding.recyclerMyPets.addOnItemTouchListener(new RecyclerClickListener(getContext(), mBinding.recyclerMyPets, new RecyclerClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Pet pet = mPetList.get(position);
                Intent intent = new Intent(getContext(), PetProfileActivity.class);
                intent.putExtra(PET, pet);

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireActivity());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(getString(R.string.selected_pet_id), pet.getId());
                editor.apply();

                ImageView petImage = view.findViewById(R.id.recycler_pet_image);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(getActivity(), petImage, petImage.getTransitionName()).toBundle();
                    startActivity(intent, bundle);
                } else {
                    startActivity(intent);
                }
            }

            @Override
            public void onLongItemClick(View view, int position) {
                pet = mPetList.get(position);
                registerForContextMenu(view);
            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));
    }

    private void showDeleteDialog(Pet pet) {
        String accountId = pet.getAccountId();
        String petId = pet.getId();

        AlertDialog.Builder alert = new AlertDialog.Builder(
                requireActivity());
        alert.setTitle(R.string.delete_note);
        alert.setMessage(R.string.are_you_sure_delete_note);
        alert.setPositiveButton(R.string.confirm, (dialog, which) -> {
            DatabaseReference path = databaseReference.child(Constants.PETS).child(accountId).child(petId);
            path.removeValue();
            roomViewModel.deletePet(pet);
            mPetList.remove(pet);
            if (mBinding.recyclerMyPets.getAdapter() != null) {
                mBinding.recyclerMyPets.getAdapter().notifyDataSetChanged();
            }
            dialog.dismiss();
        });
        alert.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss());
        alert.show();
    }

    private void getPets() {
        mBinding.progressMyPets.setVisibility(View.VISIBLE);
        databaseReference.child(Constants.USERS).child(FirebaseHelper.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Owner ownerDb = snapshot.getValue(Owner.class);
                assert ownerDb != null;
                getPetsFromDb(ownerDb.getAccountId());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void getPetsFromDb(String accountId) {
        valueEventListener = databaseReference.child(Constants.PETS).child(accountId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mPetList.clear();

                for (DataSnapshot data : snapshot.getChildren()) {
                    Pet pet = data.getValue(Pet.class);
                    mPetList.add(pet);
                }
                setRecyclerView(mPetList);
                mBinding.progressMyPets.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void setRecyclerView(List<Pet> list) {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), calculateNoOfColumns(requireActivity()));
        mBinding.recyclerMyPets.setLayoutManager(gridLayoutManager);
        mBinding.recyclerMyPets.setHasFixedSize(true);
        RecyclerPetAdapter adapter = new RecyclerPetAdapter(getContext(), list);
        mBinding.recyclerMyPets.setAdapter(adapter);
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, v.getId(), 0, R.string.add_to_widget);
        menu.add(0, v.getId(), 0, getString(R.string.delete));
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if(item.getTitle().equals(getString(R.string.add_to_widget))) {

            SharedPreferences prefs = requireActivity().getApplicationContext().getSharedPreferences(requireActivity().getApplicationContext().getString(R.string.app_name), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(getString(R.string.widget_pet_name), pet.getName());
            editor.putString(getString(R.string.widget_pet_id), pet.getId());
            editor.apply();

            NoteService.startActionUpdateNotes(requireActivity());


        } else if(item.getTitle().equals(getString(R.string.delete))) {
            showDeleteDialog(pet);
        }

        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (NetworkUtils.checkConnectivity(requireActivity().getApplication()) && auth.getCurrentUser() != null) {
            getPets();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(valueEventListener != null) {
            databaseReference.removeEventListener(valueEventListener);
        }
    }

    public class MyPetFragmentClickHandler {

        final Context context;

        public MyPetFragmentClickHandler(Context context) {
            this.context = context;
        }

        public void addPet(View view) {
            Intent intent = new Intent(getContext(), AddPetActivity.class);
            startActivity(intent);
        }
    }
}