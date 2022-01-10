package com.gerwalex.radarplott.main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.Observable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.gerwalex.radarplott.R;
import com.gerwalex.radarplott.databinding.RadarViewBinding;
import com.gerwalex.radarplott.views.RadarBasisView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.slider.LabelFormatter;

public class RadarFragment extends Fragment {
    private RadarViewBinding binding;
    private MainModel mModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = new ViewModelProvider(requireActivity()).get(MainModel.class);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = RadarViewBinding.inflate(inflater, container, false);
        binding.radar.setOnVessselClickListener(new RadarBasisView.OnVesselClickListener() {
            @Override
            public void onVesselClick(Vessel vessel) {
                mModel.clickedVessel.setValue(vessel);
            }
        });
        binding.radar.maxTime.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                binding.time.setValueTo(binding.radar.maxTime.get());
            }
        });
        binding.time.addOnChangeListener((slider, value, fromUser) -> {
            if (fromUser) {
                binding.radar.setCurrentTime((int) value);
            }
        });
        binding.time.setLabelFormatter(new LabelFormatter() {
            @SuppressLint("DefaultLocale")
            @NonNull
            @Override
            public String getFormattedValue(float value) {
                int time = (int) (value + binding.radar.getStarttimeInMinutes());
                return String.format("%02d:%02d", time / 60, time % 60);
            }
        });
        binding.kurs.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                binding.radar.setDrawCourselineTexte(isChecked);
            }
        });
        binding.position.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                binding.radar.setDrawPositionTexte(isChecked);
            }
        });
        return binding.getRoot();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.kurse) {
            binding.radar.setDrawCourselineTexte(item.isChecked());
        } else if (id == R.id.position) {
            binding.radar.setDrawPositionTexte(item.isChecked());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return onOptionsItemSelected(item);
            }
        });
        OpponentVessel otherVessel = new OpponentVessel(600, 'B', 10, 7);
        mModel.ownVessel.observe(getViewLifecycleOwner(), new Observer<Vessel>() {
            @Override
            public void onChanged(Vessel vessel) {
                binding.radar.setOwnVessel(vessel);
                otherVessel.setSecondSeitenpeilung(612, 20, 4.5);
                binding.radar.addVessel(otherVessel);
            }
        });
    }
}
