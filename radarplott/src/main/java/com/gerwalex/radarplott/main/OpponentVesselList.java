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

import com.gerwalex.lib.databinding.LinearRecyclerviewBinding;
import com.gerwalex.radarplott.R;
import com.gerwalex.radarplott.databinding.OpponentLageBinding;
import com.gerwalex.radarplott.math.Opponent;

import java.util.List;

public class OpponentVesselList extends Fragment {

    private LinearRecyclerviewBinding binding;
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
        binding = LinearRecyclerviewBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mModel.opponentVesselList.observe(getViewLifecycleOwner(), new Observer<List<Opponent>>() {
            @Override
            public void onChanged(List<Opponent> opponents) {
                Adapter<RecyclerView.ViewHolder> adapter =
                        new Adapter<>(getParentFragmentManager(), requireContext(), opponents);
                binding.defaultRecyclerView.setAdapter(adapter);
            }
        });
    }

    public static class Adapter<V extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<ViewHolder> {
        private final int[] colors;
        private final FragmentManager fm;
        private final LayoutInflater inflater;
        private final List<Opponent> opponentList;

        public Adapter(@NonNull FragmentManager fm, @NonNull Context context, @NonNull List<Opponent> opponents) {
            this.fm = fm;
            inflater = LayoutInflater.from(context);
            colors = context.getResources().getIntArray(R.array.vesselcolors);
            this.opponentList = opponents;
        }

        @Override
        public int getItemCount() {
            return opponentList.size();
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            int pos = holder.getBindingAdapterPosition();
            holder.binding.lageCard.setStrokeColor(colors[pos]);
            Opponent opponent = opponentList.get(position);
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
                    OpponentDataDialog dlg = OpponentDataDialog.newInstance(holder.getAbsoluteAdapterPosition());
                    dlg.show(fm, null);
                }
            });
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(OpponentLageBinding.inflate(inflater));
        }
    }

    public static class ViewHolder extends com.gerwalex.lib.adapters.ViewHolder {

        public final OpponentLageBinding binding;

        public ViewHolder(@NonNull OpponentLageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

