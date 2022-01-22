package com.gerwalex.radarplott.main;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.Observable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.gerwalex.radarplott.R;
import com.gerwalex.radarplott.databinding.OpponentBinding;
import com.gerwalex.radarplott.databinding.RecyclerviewBinding;
import com.gerwalex.radarplott.math.OpponentVessel;

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
        adapter = new Adapter<>(requireContext());
        binding.recyclerview.setAdapter(adapter);
        mModel.opponentVesselList.observe(getViewLifecycleOwner(), new Observer<List<OpponentVessel>>() {
            @Override
            public void onChanged(List<OpponentVessel> opponentVessels) {
                adapter.setOpponents(opponentVessels);
            }
        });
    }

    public static class Adapter<V extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<ViewHolder> {
        private final int[] colors;
        private final LayoutInflater inflater;
        private List<OpponentVessel> opponentList;

        public Adapter(Context context) {
            inflater = LayoutInflater.from(context);
            colors = context.getResources().getIntArray(R.array.vesselcolors);
        }

        @Override
        public int getItemCount() {
            return opponentList.size();
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            int pos = holder.getAdapterPosition();
            holder.binding.lageCard.setStrokeColor(colors[pos]);
            OpponentVessel opponent = opponentList.get(position);
            holder.binding.setLage(opponent.getLage());
            opponent.manoever.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(Observable sender, int propertyId) {
                    holder.binding.manoverCard.setStrokeColor(colors[pos]);
                    holder.binding.manoverCard.setVisibility(View.VISIBLE);
                    holder.binding.setManoever(opponent.manoever.get());
                }
            });
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

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final OpponentBinding binding;

        public ViewHolder(@NonNull OpponentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

