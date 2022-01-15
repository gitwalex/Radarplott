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

import com.gerwalex.radarplott.databinding.OpponentBinding;
import com.gerwalex.radarplott.math.Lage;
import com.gerwalex.radarplott.math.OpponentVessel;

public class OpponentVesselData extends Fragment {

    public static final String OpponentName = "OPPONENTNAME";
    protected OpponentVessel mOpponent;
    private OpponentBinding binding;
    private MainModel mModel;
    private Bundle args;

    public static OpponentVesselData newInstance(@NonNull String name) {
        Bundle args = new Bundle();
        args.putString(OpponentName, name);
        OpponentVesselData fragment = new OpponentVesselData();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = new ViewModelProvider(requireActivity()).get(MainModel.class);
        if (savedInstanceState != null) {
            args = savedInstanceState;
        }
        args.putAll(getArguments());
        mOpponent = mModel.getOpponent(args.getString(OpponentName));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = OpponentBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mOpponent.lage.observe(getViewLifecycleOwner(), new Observer<Lage>() {
            @Override
            public void onChanged(Lage lage) {
                binding.setLage(lage);
            }
        });
    }
}
