package com.gerwalex.radarplott.main;

import static com.gerwalex.radarplott.main.OpponentVesselData.POSITION;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.gerwalex.lib.main.BasicFragment;
import com.gerwalex.radarplott.databinding.OpponentNeuBinding;
import com.gerwalex.radarplott.math.Opponent;

import java.util.List;

public class OpponentVessel extends BasicFragment {

    private OpponentNeuBinding binding;
    private MainModel mModel;

    public static OpponentVessel newInstance(int position) {
        Bundle args = new Bundle();
        args.putInt(POSITION, position);
        OpponentVessel fragment = new OpponentVessel();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = new ViewModelProvider(requireActivity()).get(MainModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = OpponentNeuBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        int position = args.getInt(POSITION);
        mModel.opponentVesselList.observe(getViewLifecycleOwner(), new Observer<List<Opponent>>() {
            @Override
            public void onChanged(List<Opponent> opponents) {
                Opponent opponent = opponents.get(position);
                binding.setOpponent(opponent);
            }
        });
    }
}

