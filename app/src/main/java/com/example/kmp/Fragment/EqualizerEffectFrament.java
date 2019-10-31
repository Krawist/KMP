package com.example.kmp.Fragment;

import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.kmp.Activity.EffectActivity;
import com.example.kmp.Activity.PlayingMusicActivity;
import com.example.kmp.Helper.Helper;
import com.example.kmp.Modeles.MusicEffect;
import com.example.kmp.R;
import com.example.kmp.ViewModel.KmpViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class EqualizerEffectFrament extends Fragment {

    private short currentEffectIndex = 0;
    private Equalizer equalizer;
    private CheckBox lastSelectCheckbox;
    private MusicEffect musicEffect;
    private KmpViewModel model;

    public EqualizerEffectFrament() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        RecyclerView recyclerView = new RecyclerView(getContext());
        equalizer = new Equalizer(0,new MediaPlayer().getAudioSessionId());
        currentEffectIndex = Helper.getMusicEffect(getContext()).getEqualizerEffectIndex();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new Adapter());
        configureViewModel();
        return recyclerView;
    }

    private void configureViewModel(){
        model = KmpViewModel.getInstance(getActivity().getApplication(),getContext());
        model.getMusicEffect().observe(this, new Observer<MusicEffect>() {
            @Override
            public void onChanged(MusicEffect musicEffect) {
                EqualizerEffectFrament.this.musicEffect = musicEffect;
            }
        });
    }

    private class Adapter extends RecyclerView.Adapter<EffectViewHolder>{

        @NonNull
        @Override
        public EffectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new EffectViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.effect_item,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull EffectViewHolder holder, int position) {
                holder.bindData(equalizer.getPresetName((short)position),(short)position);
        }

        @Override
        public int getItemCount() {
            if(equalizer!=null){
                return equalizer.getNumberOfPresets();
            }

            return 0;
        }
    }

    private class EffectViewHolder extends RecyclerView.ViewHolder{

        private final TextView effectName;
        private final CheckBox checkBox;

        public EffectViewHolder(View view){
            super(view);
            effectName = view.findViewById(R.id.textview_effect_item_name);
            checkBox = view.findViewById(R.id.checkbox);
        }

        public void bindData(String effectName, final short effectIndex){
            this.effectName.setText(effectName);
            checkBox.setChecked(effectIndex==currentEffectIndex);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        currentEffectIndex = effectIndex;
                        musicEffect.setEqualizerEffectIndex(effectIndex);
                        if(effectIndex==0){
                            musicEffect.setActif(false);
                        }else{
                            musicEffect.setActif(true);
                        }
                        presetEffect();

                        if(lastSelectCheckbox!=null && lastSelectCheckbox!=checkBox){
                            lastSelectCheckbox.setChecked(false);
                        }
                        lastSelectCheckbox = checkBox;
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkBox.setChecked(true);
                }
            });

        }

        private void presetEffect() {
            ((EffectActivity)getContext()).setEffectPreset(musicEffect);
        }
    }
}
