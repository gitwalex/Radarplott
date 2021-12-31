package com.gerwalex.radarplott.main;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.gerwalex.radarplott.radar.Vessel;

public class MainModel extends ViewModel {

    public final MutableLiveData<Vessel> clickedVessel = new MutableLiveData<>();
    public final MutableLiveData<Vessel> ownVessel = new MutableLiveData<>();
}
