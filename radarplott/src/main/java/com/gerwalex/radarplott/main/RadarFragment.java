package com.gerwalex.radarplott.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.gerwalex.radarplott.R;
import com.gerwalex.radarplott.databinding.RadarViewBinding;
import com.gerwalex.radarplott.math.OpponentVessel;
import com.gerwalex.radarplott.math.Vessel;
import com.gerwalex.radarplott.views.RadarBasisView;

import java.util.List;

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
        binding.radar.setRadarObserver(new RadarBasisView.RadarObserver() {

            @Override
            public void onVesselClick(Vessel vessel) {
                mModel.clickedVessel.setValue(vessel);
            }
        });
        binding.radar.maxTime.observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                mModel.maxTime.setValue(integer);
            }
        });
        return binding.getRoot();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.zeigeKurse) {
            binding.radar.setDrawCourselineTexte(item.isChecked());
        } else if (id == R.id.zeigePositionen) {
            binding.radar.setDrawPositionTexte(item.isChecked());
        } else if (id == R.id.zeigeKurslinie) {
            binding.radar.setDrawCourseline(item.isChecked());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mModel.opponentVesselList.observe(getViewLifecycleOwner(), new Observer<List<OpponentVessel>>() {
            @Override
            public void onChanged(List<OpponentVessel> opponents) {
                binding.radar.setOpponents(opponents);
            }
        });
        mModel.ownVessel.observe(getViewLifecycleOwner(), new Observer<Vessel>() {
            @Override
            public void onChanged(Vessel me) {
                binding.radar.setOwnVessel(me);
                binding.setMe(me);
            }
        });
        mModel.currentTime.observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                binding.radar.setCurrentTime(integer);
            }
        });
        binding.radar.maxTime.observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                mModel.maxTime.setValue(integer);
            }
        });
    }
}
