package com.gerwalex.radarplott.main;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.gerwalex.radarplott.math.Opponent;
import com.gerwalex.radarplott.math.Vessel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainModel extends ViewModel {

    public final MutableLiveData<Vessel> clickedVessel = new MutableLiveData<>();
    public final MutableLiveData<Opponent> currentOpponent = new MutableLiveData<>();
    public final MutableLiveData<Integer> currentTime = new MutableLiveData<>();
    public final MutableLiveData<Vessel> manoever = new MutableLiveData<>();
    public final MutableLiveData<Integer> maxTime = new MutableLiveData<>();
    public final MutableLiveData<List<Opponent>> opponentVesselList = new MutableLiveData<>(new ArrayList<>());
    public final MutableLiveData<Vessel> ownVessel = new MutableLiveData<>();
    public final MutableLiveData<Boolean> radarClicked = new MutableLiveData<>();

    public MainModel() {
        Vessel me = new Vessel(80, 8);
        ownVessel.setValue(me);
        Opponent otherVessel = new Opponent(me, 600, "B", 10, 7);
        otherVessel.setSecondSeitenpeilung(612, 20, 4.5);
        addOpponentVessel(otherVessel);
    }

    public void addOpponentVessel(Opponent opponent) {
        List<Opponent> opponents = Objects.requireNonNull(opponentVesselList.getValue());
        opponents.add(opponent);
        opponentVesselList.setValue(opponents);
        currentOpponent.setValue(opponent);
    }
}
