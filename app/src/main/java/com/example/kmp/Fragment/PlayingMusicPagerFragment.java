package com.example.kmp.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.kmp.Modeles.Musique;
import com.example.kmp.R;

public class PlayingMusicPagerFragment extends Fragment {
    Musique musique;
    private static final String IMAGE_KEY = "cle_image";

    public PlayingMusicPagerFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        musique = (Musique)getArguments().getSerializable(IMAGE_KEY);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ImageView imageView = new ImageView(getContext());
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(getContext())
                .load(musique.getPochette())
                .centerCrop()
                .error(R.drawable.headphones_high_quality)
                .crossFade()
                .into(imageView);

        return imageView;
    }

    public static PlayingMusicPagerFragment getInstance(Musique musique){
        PlayingMusicPagerFragment fragment = new PlayingMusicPagerFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(IMAGE_KEY,musique);
        fragment.setArguments(bundle);

        return fragment;

    }
}


