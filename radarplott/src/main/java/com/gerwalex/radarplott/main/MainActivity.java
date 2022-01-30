package com.gerwalex.radarplott.main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.gerwalex.radarplott.R;
import com.gerwalex.radarplott.databinding.ActivityMainBinding;
import com.gerwalex.radarplott.math.Vessel;
import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewPager2 pager = binding.viewPager;
        pager.setUserInputEnabled(false);
        pager.setAdapter(new ViewpagerAdapter(this));
        new TabLayoutMediator(binding.tabLayout, pager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(position == 0 ? R.string.data : R.string.radarbild);
            }
        }).attach();
        MainModel mModel = new ViewModelProvider(this).get(MainModel.class);
        mModel.clickedVessel.observe(this, new Observer<Vessel>() {
            @Override
            public void onChanged(Vessel vessel) {
                if (!Objects.equals(mModel.ownVessel.getValue(), vessel)) {
                }
            }
        });
        mModel.maxTime.observe(this, new Observer<Integer>() {
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private static class ViewpagerAdapter extends FragmentStateAdapter {

        public ViewpagerAdapter(@NonNull AppCompatActivity activity) {
            super(activity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            Fragment f;
            switch (position) {
                case 0:
                    f = new MainFragment();
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