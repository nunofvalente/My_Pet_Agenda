package com.nunovalente.android.mypetagenda.fragments.mypets;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MyPetsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public MyPetsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is My Pets fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}