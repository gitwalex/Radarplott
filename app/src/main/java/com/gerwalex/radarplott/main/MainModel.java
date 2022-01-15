package com.gerwalex.radarplott.main;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.gerwalex.radarplott.math.Lage;
import com.gerwalex.radarplott.math.OpponentVessel;
import com.gerwalex.radarplott.math.Vessel;

import java.util.HashMap;
import java.util.Map;

public class MainModel extends ViewModel {

    public final MutableLiveData<OpponentVessel> addOpponent = new MutableLiveData<>();
    public final MutableLiveData<Vessel> clickedVessel = new MutableLiveData<>();
    public final MutableLiveData<Lage> currentLage = new MutableLiveData<>();
    public final MutableLiveData<Lage> currentManoever = new MutableLiveData<>();

    public final MutableLiveData<Vessel> manoever = new MutableLiveData<>();
    public final MutableLiveData<Vessel> ownVessel = new MutableLiveData<>();
    private final Map<String, OpponentVessel> opponentVesselList = new HashMap<>();

    public MainModel() {
        Vessel me = new Vessel(80, 8);
        ownVessel.setValue(me);
    }

    public void addOpponentVessel(OpponentVessel opponent) {
        opponentVesselList.put(opponent.name, opponent);
    }

    public OpponentVessel getOpponent(String name) {
        return opponentVesselList.get(name);
    }
}
