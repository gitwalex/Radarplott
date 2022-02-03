package com.gerwalex.radarplott.main;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.gerwalex.radarplott.R;
import com.gerwalex.radarplott.databinding.OpponentDataBinding;
import com.gerwalex.radarplott.math.OpponentVessel;

import java.util.Objects;

public class OpponentVesselData extends DialogFragment {
    private static final String POSITION = "POSITION";
    public final MutableLiveData<Float> dist1 = new MutableLiveData<>();
    public final MutableLiveData<Float> dist2 = new MutableLiveData<>();
    public final MutableLiveData<Float> minutes = new MutableLiveData<>();
    public final MutableLiveData<String> name = new MutableLiveData<>();
    public final MutableLiveData<Float> rwSp1 = new MutableLiveData<>();
    public final MutableLiveData<Float> rwSp2 = new MutableLiveData<>();
    private OpponentDataBinding binding;
    private MainModel mModel;
    private OpponentVessel opponent;
    private int position;

    public static OpponentVesselData newInstance(int position) {
        Bundle args = new Bundle();
        args.putInt(POSITION, position);
        OpponentVesselData fragment = new OpponentVesselData();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = new ViewModelProvider(requireActivity()).get(MainModel.class);
        Bundle args = getArguments();
        if (args != null) {
            position = args.getInt(POSITION);
            opponent = Objects.requireNonNull(mModel.opponentVesselList.getValue()).get(position);
            name.setValue(opponent.name);
            rwSp1.setValue(opponent.getRwP1());
            rwSp2.setValue(opponent.getRwP2());
            dist1.setValue(opponent.getDist1());
            dist2.setValue(opponent.getDist2());
            minutes.setValue((float) opponent.getTime());
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(onCreateView(getLayoutInflater(), null, savedInstanceState));
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (opponent == null) {
                    opponent = new OpponentVessel(mModel.ownVessel.getValue(), 0, name.getValue(), rwSp1.getValue(),
                            dist1.getValue(), minutes.getValue(), rwSp2.getValue(), dist2.getValue());
                    mModel.addOpponentVessel(opponent);
                }
            }
        });
        binding.setHandler(this);
        onViewCreated(binding.getRoot(), savedInstanceState);
        return builder.create();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = OpponentDataBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rwSp1.observe(this, new Observer<Float>() {
            @Override
            public void onChanged(Float value) {
                binding.rwsp1.setError("Fehler");
            }
        });
        name.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String value) {
                if (value != null && value.length() > 5) {
                    binding.name.setErrorEnabled(true);
                    binding.name.setError("Fehler");
                }
            }
        });
        minutes.observe(this, new Observer<Float>() {
            @Override
            public void onChanged(Float value) {
                if (value != null && value < 1) {
                    binding.minutes.setError(getString(R.string.minutesLt1));
                }
            }
        });
    }
}
