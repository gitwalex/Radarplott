package com.gerwalex.radarplott.main;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.gerwalex.lib.main.BasicFragment;
import com.gerwalex.radarplott.R;
import com.gerwalex.radarplott.databinding.OwnVesselBinding;
import com.gerwalex.radarplott.databinding.OwnVesselDataBinding;
import com.gerwalex.radarplott.math.Vessel;

public class OwnVessel extends BasicFragment {
    private OwnVesselBinding binding;
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
        binding = OwnVesselBinding.inflate(inflater, container, false);
        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                OwnVesselDataBinding dlg = OwnVesselDataBinding.inflate(LayoutInflater.from(requireContext()));
                Vessel me = binding.getMe();
                dlg.setVessel(me);
                builder.setView(dlg.getRoot());
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        me.setHeading(dlg.heading.getValue());
                        me.setSpeed(dlg.speed.getValue());
                    }
                });
                builder.show();
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mModel.ownVessel.observe(getViewLifecycleOwner(), new Observer<Vessel>() {
            @Override
            public void onChanged(Vessel vessel) {
                binding.setMe(vessel);
            }
        });
    }
}
