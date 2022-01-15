package com.gerwalex.radarplott.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.gerwalex.radarplott.databinding.ManoeverBinding;
import com.gerwalex.radarplott.math.Lage;

public class OpponentManoeverData extends OpponentVesselData {
    private ManoeverBinding binding;

    public static OpponentManoeverData newInstance(@NonNull String name) {
        Bundle args = new Bundle();
        args.putString(OpponentName, name);
        OpponentManoeverData fragment = new OpponentManoeverData();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = ManoeverBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mOpponent.manoeverLage.observe(getViewLifecycleOwner(), new Observer<Lage>() {
            @Override
            public void onChanged(Lage lage) {
                binding.setLage(lage);
            }
        });
    }
}
