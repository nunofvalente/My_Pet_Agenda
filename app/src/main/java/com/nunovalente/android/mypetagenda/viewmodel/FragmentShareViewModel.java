package com.nunovalente.android.mypetagenda.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nunovalente.android.mypetagenda.model.Pet;

public class FragmentShareViewModel extends ViewModel {
    private final MutableLiveData<Pet> selectedPet = new MutableLiveData<>();

    public void selectPet(Pet pet) {
        selectedPet.setValue(pet);
    }

    public LiveData<Pet> getSelectedPet() {
        return selectedPet;
    }
}
