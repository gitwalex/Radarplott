package com.gerwalex.radarplott.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.gerwalex.lib.main.BasicFragment;
import com.gerwalex.radarplott.databinding.OpponentLageBinding;
import com.gerwalex.radarplott.math.Opponent;

public class OpponentLage extends BasicFragment {

    private OpponentLageBinding binding;
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
        binding = OpponentLageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mModel.currentOpponent.observe(getViewLifecycleOwner(), new Observer<Opponent>() {
            @Override
            public void onChanged(Opponent opponent) {
                binding.setOpponent(opponent);
            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpponentDataDialog dlg = new OpponentDataDialog();
                dlg.show(getChildFragmentManager(), null);
            }
        });
    }
}

