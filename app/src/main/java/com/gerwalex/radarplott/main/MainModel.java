package com.gerwalex.radarplott.main;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainModel extends ViewModel {

    public final MutableLiveData<Vessel> clickedVessel = new MutableLiveData<>();
    public final MutableLiveData<Vessel> ownVessel = new MutableLiveData<>();

    public MainModel() {
        Vessel me = new Vessel(80, 8);
        ownVessel.setValue(me);
    }
}
