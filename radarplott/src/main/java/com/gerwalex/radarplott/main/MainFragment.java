package com.gerwalex.radarplott.main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.gerwalex.radarplott.R;
import com.gerwalex.radarplott.databinding.MainFragmentBinding;
import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainFragment extends Fragment {
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
        ViewPager2 pager = binding.viewPager;
        pager.setUserInputEnabled(false);
        pager.setAdapter(new ViewpagerAdapter(this));
        new TabLayoutMediator(binding.tabLayout, pager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(position == 0 ? R.string.data : R.string.radarbild);
            }
        }).attach();
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

    private static class ViewpagerAdapter extends FragmentStateAdapter {

        public ViewpagerAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            Fragment f;
            switch (position) {
                case 0:
                    f = new DataFragment();
                    break;
                case 1:
                    f = new RadarFragment();
                    break;
                default:
                    throw new IllegalArgumentException("Kein Fragment für Position " + position);
            }
            return f;
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
}