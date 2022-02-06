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
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.gerwalex.radarplott.R;
import com.gerwalex.radarplott.databinding.OpponentShortBinding;
import com.gerwalex.radarplott.databinding.RecyclerviewBinding;
import com.gerwalex.radarplott.math.OpponentVessel;

import java.util.List;

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
        mModel.opponentVesselList.observe(getViewLifecycleOwner(), new Observer<List<OpponentVessel>>() {
            @Override
            public void onChanged(List<OpponentVessel> opponentVessels) {
                adapter = new Adapter<>(getParentFragmentManager(), requireContext(), opponentVessels);
                binding.recyclerview.setAdapter(adapter);
            }
        });
    }

    public static class Adapter<V extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<ViewHolder> {
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
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            int pos = holder.getBindingAdapterPosition();
            holder.binding.lageCard.setStrokeColor(colors[pos]);
            OpponentVessel opponent = opponentList.get(position);
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
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(OpponentShortBinding.inflate(inflater));
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final OpponentShortBinding binding;

        public ViewHolder(@NonNull OpponentShortBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

