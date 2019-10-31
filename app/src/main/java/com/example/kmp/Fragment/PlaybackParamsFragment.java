package com.example.kmp.Fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.kmp.Activity.EffectActivity;
import com.example.kmp.Modeles.MusicEffect;
import com.example.kmp.R;
import com.example.kmp.ViewModel.KmpViewModel;

import java.text.DecimalFormat;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlaybackParamsFragment extends Fragment {

    private TextView pitchValue;
    private TextView speedValue;
    private Button increasePitch;
    private Button decreasePitch;
    private Button increaseSpeed;
    private Button decreaseSpeed;
    private ImageButton resetPitchAndSpeed;
    private MusicEffect musicEffect;

    private static final int DEFAULT_DIVIDER = 10;

    public PlaybackParamsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureViewmodel();
    }

    private void configureViewmodel() {
        KmpViewModel viewModel = KmpViewModel.getInstance(getActivity().getApplication(),getContext());
        musicEffect = viewModel.getMusicEffect().getValue();
        viewModel.getMusicEffect().observe(this, new Observer<MusicEffect>() {
            @Override
            public void onChanged(MusicEffect musicEffec) {
                musicEffect = musicEffec;
                setPicthAndSpeed();
            }
        });
    }

    private void setPicthAndSpeed() {
        if(musicEffect.getSoundSpeed()!=1.0f){
            speedValue.setTextColor(getResources().getColor(R.color.colorPrimary));
        }else{
            speedValue.setTextColor(getResources().getColor(android.R.color.black));
        }

        if(musicEffect.getSoundPitch()!=1.0f){
            pitchValue.setTextColor(getResources().getColor(R.color.colorPrimary));
        }else{
            pitchValue.setTextColor(getResources().getColor(android.R.color.black));
        }
        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        pitchValue.setText(decimalFormat.format(musicEffect.getSoundPitch())+"");
        speedValue.setText(decimalFormat.format(musicEffect.getSoundSpeed())+"");
    }

    private void updateValues() {
        ((EffectActivity)getContext()).setEffectPreset(musicEffect);
        setPicthAndSpeed();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_playback_params, container, false);

        initialiseViews(view);

        addActionsOnViews();

        return view;

    }

    private void addActionsOnViews() {
        increasePitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increasePitch();
            }
        });

        decreasePitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decreasePitch();
            }
        });

        increaseSpeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increaseSpeed();
            }
        });

        decreaseSpeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decreaseSpeed();
            }
        });

        resetPitchAndSpeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPitchAndSpeed();
            }
        });
    }

    private void resetPitchAndSpeed() {
        musicEffect.setSoundSpeed(1.0f);
        musicEffect.setSoundPitch(1.0f);
        updateValues();
    }

    private void decreaseSpeed() {
        float speed = musicEffect.getSoundSpeed();
        speed = (float)(speed - 1.0f/(DEFAULT_DIVIDER));
        if(speed>0.0f && speed<2.0){
            musicEffect.setSoundSpeed(speed);
        }else{
            //musicEffect.setSoundSpeed(1.0f);
        }
        updateValues();
    }

    private void increaseSpeed() {
        float speed = musicEffect.getSoundSpeed();
        speed = (float)(speed + 1.0f/(DEFAULT_DIVIDER));
        if(speed>0.0f && speed<2.0){
            musicEffect.setSoundSpeed(speed);
        }else{
            //musicEffect.setSoundSpeed(1.0f);
        }
        updateValues();
    }

    private void decreasePitch() {
        float pitch = musicEffect.getSoundPitch();
        pitch = (float)(pitch - 1.0f/(DEFAULT_DIVIDER));
        if(pitch>0.0f && pitch<2.0){
            musicEffect.setSoundPitch(pitch);
        }else{
           // musicEffect.setSoundPitch(1.0f);
        }

        updateValues();
    }

    private void increasePitch() {
        float pitch = musicEffect.getSoundPitch();
        pitch = (float)(pitch + 1.0f/(DEFAULT_DIVIDER));
        if(pitch>0.0f && pitch<2.0){
            musicEffect.setSoundPitch(pitch);
        }else{
            musicEffect.setSoundPitch(1.0f);
        }

        updateValues();
    }

    private void initialiseViews(View view) {
        pitchValue = view.findViewById(R.id.textview_fragment_playback_picth_value);
        speedValue = view.findViewById(R.id.textview_frgament_playback_speed_value);
        increasePitch = view.findViewById(R.id.button_fragment_plaback_increase_pitch);
        decreasePitch = view.findViewById(R.id.button_fragment_plaback_decrease_pitch);
        increaseSpeed = view.findViewById(R.id.button_fragment_plaback_increase_speed);
        decreaseSpeed = view.findViewById(R.id.button_fragment_plaback_decrease_speed);
        resetPitchAndSpeed = view.findViewById(R.id.imageButton_fragment_playback_reset_pitch_and_speed);
    }
}
