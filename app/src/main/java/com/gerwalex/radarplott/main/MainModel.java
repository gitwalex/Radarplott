package com.gerwalex.radarplott.main;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.gerwalex.radarplott.math.Lage;
import com.gerwalex.radarplott.math.OpponentVessel;
import com.gerwalex.radarplott.math.Vessel;

public class MainModel extends ViewModel {

    public final MutableLiveData<OpponentVessel> addOpponent = new MutableLiveData<>();
    public final MutableLiveData<Vessel> clickedVessel = new MutableLiveData<>();
    public final MutableLiveData<Lage> currentLage = new MutableLiveData<>();
    public final MutableLiveData<Vessel> manoever = new MutableLiveData<>();
    public final MutableLiveData<Vessel> ownVessel = new MutableLiveData<>();

    public MainModel() {
        Vessel me = new Vessel(80, 8);
        ownVessel.setValue(me);
    }
}
