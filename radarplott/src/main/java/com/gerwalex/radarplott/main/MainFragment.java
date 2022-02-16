package com.gerwalex.radarplott.main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.transition.TransitionManager;

import com.gerwalex.lib.main.BasicFragment;
import com.gerwalex.radarplott.R;
import com.gerwalex.radarplott.databinding.MainFragmentBinding;
import com.google.android.material.slider.LabelFormatter;

public class MainFragment extends BasicFragment {
    boolean smallRadar = true;
    private MainFragmentBinding binding;
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
        binding = MainFragmentBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ConstraintSet constraint1 = new ConstraintSet();
        constraint1.clone(binding.mainFragment);
        ConstraintSet constraint2 = new ConstraintSet();
        mModel.radarClicked.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean clicked) {
                if (clicked) {
                    constraint2.clone(view.getContext(), R.layout.main_fragment_large_radar);
                    mModel.radarClicked.setValue(false);
                    TransitionManager.beginDelayedTransition((ViewGroup) view, null);
                    ConstraintSet currentConstraint = smallRadar ? constraint2 : constraint1;
                    smallRadar = !smallRadar;
                    currentConstraint.applyTo((ConstraintLayout) view);
                }
            }
        });
        mModel.maxTime.observe(getViewLifecycleOwner(), new Observer<Integer>() {
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
}