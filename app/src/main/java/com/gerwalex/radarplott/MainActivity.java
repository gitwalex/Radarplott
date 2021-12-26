package com.gerwalex.radarplott;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.gerwalex.radarplott.databinding.ActivityMainBinding;
import com.gerwalex.radarplott.radar.Vessel;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Vessel me = new Vessel(100.0, 6);
        binding.radar.addVessel(me);
    }
}