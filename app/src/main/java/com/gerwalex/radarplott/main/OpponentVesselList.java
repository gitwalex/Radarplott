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
import androidx.recyclerview.widget.RecyclerView;

import com.gerwalex.radarplott.R;
import com.gerwalex.radarplott.databinding.OpponentBinding;
import com.gerwalex.radarplott.databinding.RecyclerviewBinding;
import com.gerwalex.radarplott.math.Lage;
import com.gerwalex.radarplott.math.OpponentVessel;
import com.google.android.material.card.MaterialCardView;

import java.util.List;
import java.util.Objects;

public class OpponentVesselList extends Fragment {

    private Adapter<ViewHolder> adapter;
    private RecyclerviewBinding binding;
    private int[] colors;
    private MainModel mModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = new ViewModelProvider(requireActivity()).get(MainModel.class);
        colors = getResources().getIntArray(R.array.vesselcolors);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = RecyclerviewBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        adapter = new Adapter<>();
        binding.recyclerview.setAdapter(adapter);
        mModel.opponentVesselList.observe(getViewLifecycleOwner(), new Observer<List<OpponentVessel>>() {
            @Override
            public void onChanged(List<OpponentVessel> opponentVessels) {
                adapter.setOpponents(opponentVessels);
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final OpponentBinding binding;

        public ViewHolder(@NonNull OpponentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public class Adapter<V extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<ViewHolder> {
        private final LayoutInflater inflater;
        private List<OpponentVessel> opponentList;

        public Adapter() {
            inflater = LayoutInflater.from(requireContext());
        }

        @Override
        public int getItemCount() {
            return opponentList.size();
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            int pos = holder.getAdapterPosition();
            MaterialCardView b = holder.binding.lage;
            b.setStrokeColor(colors[pos]);
            b.setStrokeWidth((int) getResources().getDimension(R.dimen.cardviewborder));
            OpponentVessel opponent = opponentList.get(position);
            opponent.manoeverLage.observe(getViewLifecycleOwner(), new Observer<Lage>() {
                @Override
                public void onChanged(Lage lage) {
                    MaterialCardView b = holder.binding.manover;
                    b.setStrokeColor(colors[pos]);
                    b.setStrokeWidth((int) getResources().getDimension(R.dimen.cardviewborder));
                    holder.binding.manover.setVisibility(View.VISIBLE);
                    holder.binding.setManoever(lage);
                }
            });
            holder.binding.setLage(opponent.getLageAktuell());
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(OpponentBinding.inflate(inflater));
        }

        public void setOpponents(@NonNull List<OpponentVessel> opponents) {
            this.opponentList = Objects.requireNonNull(opponents);
            notifyDataSetChanged();
        }
    }
}

