package com.gerwalex.radarplott.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.gerwalex.radarplott.databinding.RadarViewBinding;
import com.gerwalex.radarplott.radar.OpponentVessel;
import com.gerwalex.radarplott.radar.Vessel;
import com.gerwalex.radarplott.views.RadarBasisView;

public class RadarFragment extends Fragment {
    private RadarViewBinding binding;
    private MainModel mModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = new ViewModelProvider(requireActivity()).get(MainModel.class);
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
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        OpponentVessel otherVessel = new OpponentVessel('B', 10, 7);
        mModel.ownVessel.observe(getViewLifecycleOwner(), new Observer<Vessel>() {
            @Override
            public void onChanged(Vessel vessel) {
                binding.radar.setOwnVessel(vessel);
                otherVessel.setSecondSeitenpeilung(12, 20, 4.5, vessel);
                binding.radar.addVessel(otherVessel);
            }
        });
    }
}
