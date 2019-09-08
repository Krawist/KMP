package com.example.kmp.Activity;


import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Observer;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.IBinder;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.kmp.Fragment.PlayingMusicPagerFragment;
import com.example.kmp.Helper.Helper;
import com.example.kmp.Modeles.Musique;
import com.example.kmp.Modeles.Favori;
import com.example.kmp.Modeles.ThemeColor;
import com.example.kmp.R;
import com.example.kmp.Service.PlayerService;
import com.example.kmp.ViewModel.KmpViewModel;

import java.util.Collections;
import java.util.List;

import static com.example.kmp.Helper.Helper.TRANSITION_TIME;
import static com.example.kmp.Service.PlayerService.ACTION_PLAY_PLAYLIST;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayingMusicActivity extends AppCompatActivity {

    KmpViewModel model;
    private ViewPager viewPager;
    private TextView titreMusique;
    private TextView artisteMusique;
    private TextView tempsEcoule;
    private TextView tempsTotal;
    private ImageButton favorite;
    private ImageButton shuffle;
    private ImageButton repeat;
    private SeekBar seekBar;
    private ImageView playButton;
    private ImageView nextButton;
    private ImageView previousButton;

    private static PlayingMusicActivity INSTANCE;

    PlayerService service;
    private boolean bound;
    private Palette palette;
    private LinearLayout rootLayout;
    private RelativeLayout layoutPlaylist;
    private RecyclerView recyclerviewPlaylist;
    private PlaylistAdapter adapter;
    private boolean playlistIsShow = false;

    public PlayingMusicActivity(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.playing_music_interface);

        model = KmpViewModel.getInstance(getApplication(),this);

        initialiseView();

        configureViewModel();

        configureToolbar();

        addDataToViews();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(!bound){
            Intent intent = new Intent(this, PlayerService.class);
            bindService(intent,connection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        unbindService(connection);
        bound = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.playing_music_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.action_open_playlist);
        menuItem.setVisible(!playlistIsShow);
        menuItem.setEnabled(!playlistIsShow);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.action_open_playlist:
                showPlaylist();
                return true;

            case R.id.action_add_to_playlist:
                addSongToPlayList();
                return true;

            case R.id.action_partager:
                Helper.shareMusics(this, model.getCurrentPLayingMusic().getValue());
                return true;

            case R.id.action_supprimer:
                deleteMusic();
                return true;

            case R.id.action_details:
                showDetails();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showDetails() {
        Musique musique = model.getCurrentPLayingMusic().getValue();
        Helper.showDetailsOf(this,musique);
    }

    private void showPlaylist() {
        int from = 0;
        int to = 0;
        if(playlistIsShow){
            from = 255;
            to = 0;
        }else{
            from = 0;
            to = 255;
        }

        ObjectAnimator animator = ObjectAnimator.ofFloat(layoutPlaylist,"alpha",from,to);
        animator.setDuration(500);
        animator.start();

        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                layoutPlaylist.setVisibility(playlistIsShow?View.GONE:View.VISIBLE);
                playlistIsShow = !playlistIsShow;
                invalidateOptionsMenu();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private void addSongToPlayList(){
        Musique musique = model.getCurrentPLayingMusic().getValue();
        Helper.addSongToPlaylist(this,model.getPlaylists().getValue(),musique);
    }

    private void deleteMusic() {

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_confirmation_layout);
        ((TextView)dialog.findViewById(R.id.textview_confirmation_text)).setText(getString(R.string.supprimer_un_song));
        Button cancelButton = dialog.findViewById(R.id.button_confirmation_cancel);
        cancelButton.setText(getString(R.string.annuler));
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Button validateButton = dialog.findViewById(R.id.button_confirmation_validate);
        validateButton.setText(getString(R.string.supprimer));
        final Musique musique = model.getCurrentPLayingMusic().getValue();
        validateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bound){
                    if(service.getPlayingQueueSize()==1){
                        service.stop();
                        Helper.deleteMusics(PlayingMusicActivity.this, musique);
                        finish();
                    }else{
                        service.getPlayingQueue().remove(service.getPosition());
                        service.getOriginalPlayList().remove(service.getPosition());
                        service.playNextSong();
                        Helper.deleteMusics(PlayingMusicActivity.this, musique);
                    }
                }
                dialog.dismiss();
            }
        });

        dialog.setCanceledOnTouchOutside(false);

        dialog.show();
    }

    private void configureToolbar() {
        findViewById(R.id.imageview_backbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
    }

    private void addDataToViews() {
        Musique musique = model.getCurrentPLayingMusic().getValue();
        artisteMusique.setText(musique.getNomArtiste());
        titreMusique.setText(musique.getTitreMusique());
        //favorite.setVisibility(View.GONE);

        if(model.getFavoriteSongs().getValue()!=null){
            if(model.getFavoriteSongsId().getValue().contains(musique.getIdMusique())){
                favorite.setImageResource(R.drawable.ic_favorite_black_24dp);
            }else{
                favorite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
            }
        }

        if(viewPager!=null){
            viewPager.setCurrentItem(model.getPositionOfSongToPLay().getValue(), false);
        }

        tempsTotal.setText(Helper.formatDurationToString(musique.getDuration()));

        if(bound)
            seekBar.setMax(service.getDuration());

    }

    private void configureViewModel() {
        model = KmpViewModel.getInstance(getApplication(), this);
        model.getCurrentPLayingMusic().observe(this, new Observer<Musique>() {
            @Override
            public void onChanged(Musique musique) {
                addDataToViews();
            }
        });

        model.getThemeColor().observe(this, new Observer<ThemeColor>() {
            @Override
            public void onChanged(ThemeColor themeColor) {

                updateUiColors(themeColor);

            }
        });

        model.getLoopingMode().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                switch (integer){

                    case PlaybackStateCompat.REPEAT_MODE_NONE:
                        repeat.setImageAlpha(100);
                        repeat.setImageResource(R.drawable.ic_repeat_black_24dp);
                        break;

                    case PlaybackStateCompat.REPEAT_MODE_ALL:
                        repeat.setImageAlpha(255);
                        repeat.setImageResource(R.drawable.ic_repeat_black_24dp);
                        break;

                    case PlaybackStateCompat.REPEAT_MODE_ONE:
                        repeat.setImageAlpha(255);
                        repeat.setImageResource(R.drawable.ic_repeat_one_black_24dp);
                        break;

                }
            }
        });

        model.getSongIsPlaying().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean)
                    playButton.setImageResource(R.drawable.ic_pause_circle_outline_black_24dp);
                else
                    playButton.setImageResource(R.drawable.ic_play_circle_outline_black_40dp);
            }
        });

        model.getShuffleMode().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    shuffle.setImageAlpha(255);
                }else{
                    shuffle.setImageAlpha(100);
                }
            }
        });

        model.getPlayingQueue().observe(this, new Observer<List<Musique>>() {
            @Override
            public void onChanged(List<Musique> list) {
                viewPager.getAdapter().notifyDataSetChanged();
                configurePlaylist(list);
            }
        });

        model.getPlayingSongPosition().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer progress) {
                if(bound)
                    seekBar.setMax(service.getDuration());
                seekBar.setProgress(progress);
                tempsEcoule.setText(Helper.formatDurationToString(progress));
            }
        });
    }

    private void updateUiColors(ThemeColor themeColor) {
        int previousColor = titreMusique.getHighlightColor();
        ObjectAnimator animation = ObjectAnimator.ofInt(titreMusique, "textColor",previousColor,  themeColor.getBackgroundColor());
        animation.setEvaluator(new ArgbEvaluator());
        animation.setDuration(TRANSITION_TIME);
        animation.start();

        animation = ObjectAnimator.ofInt(artisteMusique,"textColor", previousColor, themeColor.getBackgroundColor());
        animation.setEvaluator(new ArgbEvaluator());
        animation.setDuration(TRANSITION_TIME);
        animation.start();

        seekBar.getProgressDrawable().setColorFilter(themeColor.getBackgroundColor(), PorterDuff.Mode.SRC_ATOP);
    }

    private void initialiseView() {
        viewPager = findViewById(R.id.viewpager_playing_music_pochette);
        titreMusique = findViewById(R.id.textview_playing_music_song_title);
        artisteMusique = findViewById(R.id.textview_playing_music_song_artist);
        tempsEcoule = findViewById(R.id.textview_playing_music_song_current_position);
        tempsTotal = findViewById(R.id.textview_playing_music_song_duration);
        favorite = findViewById(R.id.imagebutton_playing_music_favorite);
        shuffle = findViewById(R.id.imagebutton_playing_music_shuffle);
        repeat = findViewById(R.id.imagebutton_playing_music_repeat);
        seekBar = findViewById(R.id.seekbar_playing_music_song_progress);
        playButton = findViewById(R.id.imageview_playing_music_play_button);
        nextButton = findViewById(R.id.imageview_playing_music_next_button);
        previousButton = findViewById(R.id.imageview_playing_music_previous_button);
        rootLayout = findViewById(R.id.layout_playingmusic_root_layout);
        layoutPlaylist = findViewById(R.id.layout_playing_music_playlist);
        recyclerviewPlaylist = findViewById(R.id.recyclerView_playing_music_playlist);

        configureViewPager();

        configurePlaylist(model.getPlayingQueue().getValue());

        addActionOnViews();
    }

    private void configurePlaylist(List<Musique> playlist) {
        recyclerviewPlaylist.setLayoutManager(new LinearLayoutManager(this));
        if(adapter!=null){
            adapter.setList(playlist);
        }else{
            adapter = new PlaylistAdapter();
        }
        recyclerviewPlaylist.setAdapter(adapter);
    }

    private void addActionOnViews() {
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(bound){
                   model.getSongIsPlaying().setValue(model.getSongIsPlaying().getValue());
                   service.playPauseMusic();
               }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bound){
                    service.playNextSong();
                }
            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bound){
                    service.playPreviousSong();
                }
            }
        });

        shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(bound){
                   service.changeShuffleMode();
               }
            }
        });

        repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bound){
                    service.changeRepeatMode();
                }
            }
        });


        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Musique musique = model.getCurrentPLayingMusic().getValue();
                musique.setLiked(!musique.isLiked());

                if(model.getFavoriteSongsId().getValue()!=null){
                    if(model.getFavoriteSongsId().getValue().contains(musique.getIdMusique())){
                        favorite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                        model.removeFromFavorite(PlayingMusicActivity.this,musique.getIdMusique());
                    }else{
                        favorite.setImageResource(R.drawable.ic_favorite_black_24dp);
                        model.addToFavorite(PlayingMusicActivity.this,musique.getIdMusique());
                    }
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                PlayingMusicActivity.this.seekBar.setProgress(seekBar.getProgress());
                if(bound){
                    service.seetTo(seekBar.getProgress());
                }
            }
        });
    }

    private void configureViewPager() {
        if(model.getPlayingQueue().getValue()!=null){
            viewPager.setAdapter(new SongPochetteAdapter(getSupportFragmentManager()));
            viewPager.setCurrentItem(model.getPositionOfSongToPLay().getValue(), false);

            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    model.getPositionOfSongToPLay().setValue(position);
                    model.getCurrentPLayingMusic().setValue(model.getPlayingQueue().getValue().get(position));
                    if(bound){
                        service.playSongAt(position);
                    }
/*                    Intent intent = new Intent(PlayingMusicActivity.this, PlayerService.class);
                    intent.setAction(ACTION_PLAY_PLAYLIST);
                    startService(intent);*/
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }
    }

    private class SongPochetteAdapter extends FragmentStatePagerAdapter{

        public SongPochetteAdapter(FragmentManager fragmentManager){
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            return PlayingMusicPagerFragment.getInstance(model.getPlayingQueue().getValue().get(position));
        }

        @Override
        public int getCount() {
            if(model.getPlayingQueue().getValue()!=null)
                return model.getPlayingQueue().getValue().size();
            else
                return 0;
        }
    }

    public static PlayingMusicActivity getInstance(){
        if(INSTANCE==null)
            INSTANCE = new PlayingMusicActivity();

        return INSTANCE;
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PlayerService.LocalBinder binder = (PlayerService.LocalBinder)service;
            PlayingMusicActivity.this.service = binder.getService();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };

    private class PlaylistAdapter extends RecyclerView.Adapter<PlaylistViewHolder> {

        List<Musique> playlist;

        LayoutInflater inflater;
        private final Context context;

        public PlaylistAdapter(){
            context = PlayingMusicActivity.this;
            inflater = LayoutInflater.from(context);
            playlist = model.getPlayingQueue().getValue();
        }

        @Override
        public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
            holder.bindData(playlist.get(position), position);
        }


        @NonNull
        @Override
        public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new PlaylistViewHolder(inflater.inflate(R.layout.simple_item_with_image_white,parent,false));
        }

        @Override
        public int getItemCount() {
            if(playlist !=null)
                return playlist.size();
            else
                return 0;
        }

        public void setList(List<Musique> playlist) {
            this.playlist = playlist;
            notifyDataSetChanged();
        }
    }

    public class PlaylistViewHolder extends RecyclerView.ViewHolder{

        private final TextView titreMusique;
        private final TextView artisteAlbum;
        private final TextView dureeMusique;
        private final ImageView image;

        public PlaylistViewHolder(View itemView){
            super(itemView);
            titreMusique = itemView.findViewById(R.id.textview_simple_item_title);
            artisteAlbum = itemView.findViewById(R.id.textview_simple_item_second_text);
            dureeMusique = itemView.findViewById(R.id.textview_simple_item_third_text);
            image = itemView.findViewById(R.id.imageview_simple_item_image);
        }

        public void bindData(Musique musique, final int position){
            titreMusique.setText(musique.getTitreMusique());
            artisteAlbum.setText(musique.getTitreAlbum());
            dureeMusique.setText(Helper.formatDurationToString(musique.getDuration()));

            Helper.loadCircleImage(PlayingMusicActivity.this,image,musique.getPochette(),40);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   if(bound){
                       service.playSongAt(position);
                   }
                }
            });
        }
    }
}
