package com.example.kmp.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;

import com.example.kmp.Fragment.EqualizerEffectFrament;
import com.example.kmp.Fragment.PlaybackParamsFragment;
import com.example.kmp.Modeles.MusicEffect;
import com.example.kmp.R;
import com.example.kmp.Service.PlayerService;
import com.example.kmp.ViewModel.KmpViewModel;
import com.google.android.material.tabs.TabLayout;

public class EffectActivity extends AppCompatActivity {

    private KmpViewModel model;
    private boolean bound = false;
    private PlayerService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_effect_presentation);
        model = KmpViewModel.getInstance(getApplication(),this);
        TabLayout tabLayout = findViewById(R.id.tablayout_dialog_effect_tablayout);
        ViewPager viewPager = findViewById(R.id.viewpager_dialog_effect_viewpager);
        Button positiveAction = findViewById(R.id.button_dialog_positive_action);
        Button negativeAction = findViewById(R.id.button_dialog_negative_action);
        tabLayout.setupWithViewPager(viewPager);

        final MusicEffect currentMusicEffect = new MusicEffect();

        currentMusicEffect.setEqualizerEffectIndex(model.getMusicEffect().getValue().getEqualizerEffectIndex());
        currentMusicEffect.setActif(model.getMusicEffect().getValue().isActif());
        currentMusicEffect.setSoundPitch(model.getMusicEffect().getValue().getSoundPitch());
        currentMusicEffect.setSoundSpeed(model.getMusicEffect().getValue().getSoundSpeed());
        positiveAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        negativeAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                model.saveMusicEffect(EffectActivity.this,currentMusicEffect);
                setEffectPreset(currentMusicEffect);
                finish();
            }
        });

        viewPager.setAdapter(new EffectViewPagerAdapter(getSupportFragmentManager()));
    }

    @Override
    public void onStart() {
        super.onStart();
        if(!bound){
            Intent intent = new Intent(this, PlayerService.class);
            bindService(intent,connection, Context.BIND_AUTO_CREATE);
        }
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PlayerService.LocalBinder binder = (PlayerService.LocalBinder)service;
            EffectActivity.this.service = binder.getService();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };


    @Override
    public void onStop() {
        super.onStop();
        unbindService(connection);
        bound = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    public void setEffectPreset(MusicEffect musicEffect){
        model.saveMusicEffect(this,musicEffect);

        if(bound){
            service.userPreset(musicEffect.getEqualizerEffectIndex());
        }
    }

    public class EffectViewPagerAdapter extends FragmentPagerAdapter {
        public EffectViewPagerAdapter(FragmentManager fragmentManager){
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return new EqualizerEffectFrament();

                case 1:
                    return new PlaybackParamsFragment();
            }

            return null;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0:
                    return "Equalizer";

                case 1:
                    return "Pitch & Speed";
            }

            return null;
        }

        @Override
        public int getCount() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return 2;
            }
            return 1;
        }
    }
}
