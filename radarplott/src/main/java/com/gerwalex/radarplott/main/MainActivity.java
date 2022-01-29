package com.gerwalex.radarplott.main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.gerwalex.radarplott.databinding.ActivityMainBinding;
import com.gerwalex.radarplott.math.Vessel;
import com.google.android.material.slider.LabelFormatter;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        MainModel mModel = new ViewModelProvider(this).get(MainModel.class);
        mModel.clickedVessel.observe(this, new Observer<Vessel>() {
            @Override
            public void onChanged(Vessel vessel) {
                if (!Objects.equals(mModel.ownVessel.getValue(), vessel)) {
                }
            }
        });
        mModel.maxTime.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer != 0) {
                    binding.time.setValueTo(integer);
                }
            }
        });
        binding.time.addOnChangeListener((slider, value, fromUser) -> {
            if (fromUser) {
                mModel.currentTime.setValue((int) value);
            }
        });
        binding.time.setLabelFormatter(new LabelFormatter() {
            @SuppressLint("DefaultLocale")
            @NonNull
            @Override
            public String getFormattedValue(float value) {
                int time = (int) value;
                return String.format("%02d:%02d", time / 60, time % 60);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}