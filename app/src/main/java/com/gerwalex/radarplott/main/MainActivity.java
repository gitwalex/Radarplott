package com.gerwalex.radarplott.main;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.gerwalex.radarplott.R;
import com.gerwalex.radarplott.databinding.ActivityMainBinding;
import com.gerwalex.radarplott.radar.Vessel;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        MainModel mModel = new ViewModelProvider(this).get(MainModel.class);
        Vessel me = new Vessel(80, 8);
        mModel.ownVessel.setValue(me);
        binding.radar.setModel(mModel);
        Vessel otherVessel = new Vessel('B', 10, 7);
        Vessel.Lage lage = otherVessel.setSecondSeitenpeilung(12, 20, 4.5, me);
        binding.radar.addVessel(otherVessel);
        mModel.clickedVessel.observe(this, new Observer<Vessel>() {
            @Override
            public void onChanged(Vessel vessel) {
                if (vessel.equals(me)) {
                    OwnVesselData input = new OwnVesselData();
                    input.show(getSupportFragmentManager(), null);
                }
            }
        });
    }
}