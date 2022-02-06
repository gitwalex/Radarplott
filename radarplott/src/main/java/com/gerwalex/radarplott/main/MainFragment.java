package com.gerwalex.radarplott.main;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.Observable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.gerwalex.radarplott.R;
import com.gerwalex.radarplott.databinding.MainFragmentBinding;
import com.gerwalex.radarplott.databinding.OpponentBinding;
import com.gerwalex.radarplott.math.OpponentVessel;
import com.gerwalex.radarplott.math.Vessel;
import com.gerwalex.radarplott.views.RadarBasisView;
import com.google.android.material.slider.LabelFormatter;

import java.util.List;

public class MainFragment extends Fragment {
    private MainFragmentBinding binding;
    private MainModel mModel;

    private void flipCard(View visibleView, View invisibleView) {
        Context context = visibleView.getContext();
        visibleView.setVisibility(View.VISIBLE);
        Animator flipOutAnimatorSet = AnimatorInflater.loadAnimator(context, R.animator.flip_out);
        flipOutAnimatorSet.setTarget(invisibleView);
        Animator flipInAnimatorset = AnimatorInflater.loadAnimator(context, R.animator.flip_in);
        flipInAnimatorset.setTarget(visibleView);
        flipInAnimatorset.start();
        flipOutAnimatorSet.start();
        flipInAnimatorset.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                invisibleView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationStart(Animator animation) {
            }
        });
    }

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
        binding = MainFragmentBinding.inflate(inflater);
        binding.radar.setRadarObserver(new RadarBasisView.RadarObserver() {

            @Override
            public void onCreateManoever(Vessel manoverVessel) {
                mModel.manoever.setValue(manoverVessel);
            }

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
        mModel.manoever.observe(getViewLifecycleOwner(), new Observer<Vessel>() {
            @Override
            public void onChanged(Vessel vessel) {
                binding.radar.setManoeverVessel(vessel);
            }
        });
        mModel.opponentVesselList.observe(getViewLifecycleOwner(), new Observer<List<OpponentVessel>>() {
            @Override
            public void onChanged(List<OpponentVessel> opponents) {
                binding.radar.setOpponents(opponents);
                OpponentVesselList.Adapter<RecyclerView.ViewHolder> adapter =
                        new OpponentVesselList.Adapter<>(getParentFragmentManager(), requireContext(), opponents);
                binding.recyclerview.setAdapter(adapter);
            }
        });
        mModel.ownVessel.observe(getViewLifecycleOwner(), new Observer<Vessel>() {
            @Override
            public void onChanged(Vessel me) {
                binding.radar.setOwnVessel(me);
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
        binding.flipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.radar.getVisibility() == View.GONE) {
                    flipCard(binding.radar, binding.recyclerview);
                }
                if (binding.recyclerview.getVisibility() == View.GONE) {
                    flipCard(binding.recyclerview, binding.radar);
                }
            }
        });
    }

    public static class Adapter<V extends RecyclerView.ViewHolder>
            extends RecyclerView.Adapter<OpponentVesselList.ViewHolder> {
        private final int[] colors;
        private final FragmentManager fm;
        private final LayoutInflater inflater;
        private final List<OpponentVessel> opponentList;

        public Adapter(@NonNull FragmentManager fm, @NonNull Context context,
                       @NonNull List<OpponentVessel> opponentVessels) {
            this.fm = fm;
            inflater = LayoutInflater.from(context);
            colors = context.getResources().getIntArray(R.array.vesselcolors);
            this.opponentList = opponentVessels;
        }

        @Override
        public int getItemCount() {
            return opponentList.size();
        }

        @Override
        public void onBindViewHolder(@NonNull OpponentVesselList.ViewHolder holder, int position) {
            int pos = holder.getBindingAdapterPosition();
            holder.binding.lageCard.setStrokeColor(colors[pos]);
            OpponentVessel opponent = opponentList.get(position);
            holder.binding.oppenentManoever.TVTitle.setText(R.string.nachManoever);
            holder.binding.setOpponent(opponent);
            opponent.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(Observable sender, int propertyId) {
                    notifyItemChanged(holder.getAbsoluteAdapterPosition());
                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OpponentVesselData dlg = OpponentVesselData.newInstance(holder.getAbsoluteAdapterPosition());
                    dlg.show(fm, null);
                }
            });
        }

        @NonNull
        @Override
        public OpponentVesselList.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new OpponentVesselList.ViewHolder(OpponentBinding.inflate(inflater));
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final OpponentBinding binding;

        public ViewHolder(@NonNull OpponentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}