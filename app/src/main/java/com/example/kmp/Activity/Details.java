package com.example.kmp.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Fade;
import androidx.transition.TransitionManager;
import androidx.transition.TransitionSet;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.kmp.Adapter.DetailsAlbumMusicAdapter;
import com.example.kmp.Adapter.MusicAdapterWithImage;
import com.example.kmp.Helper.Helper;
import com.example.kmp.Modeles.Album;
import com.example.kmp.Modeles.Artiste;
import com.example.kmp.Modeles.Musique;
import com.example.kmp.Modeles.Playlist;
import com.example.kmp.R;
import com.example.kmp.Service.PlayerService;
import com.example.kmp.ViewModel.KmpViewModel;

import java.util.ArrayList;
import java.util.List;

import static com.example.kmp.Activity.MainActivity.ACTION_PARTAGER;
import static com.example.kmp.Activity.MainActivity.ACTION_SUPPRIMER;

public class Details extends AppCompatActivity {

    public static final String DETAIL_OF_WHAT_TO_SHOW = "quoi_detailler";
    public static final String PLAYLIST = "playlist";
    public static final String ALBUM = "album";
    public static final String ARTISTE = "artiste";
    public static final String KEYWORD = "mot_cle";

    private Album album;
    private Artiste artiste;
    private Playlist playlist;
    private String keyWord;
    private String whatToDetail;
    private TextView nombreSOng;
    private RecyclerView recyclerView;
    private TextView nomArtiste;
    private ImageView image;
    private MusicAdapterWithImage adapter;
    private DetailsAlbumMusicAdapter adapterAlbumMusic;
    private List<Musique> musiqueList;
    private KmpViewModel model;
    private String toolbarTitle;
    private PlayerService service;
    private boolean bound = false;
    private boolean isActionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getIntent()!=null){
            whatToDetail = getIntent().getStringExtra(DETAIL_OF_WHAT_TO_SHOW);
            switch (whatToDetail){

                case ALBUM:
                    album = (Album)getIntent().getSerializableExtra(ALBUM);
                    toolbarTitle = album.getTitreAlbum();
                    setContentView(R.layout.activity_detail_album);
                    initialiseViewsForAlbumInterface();
                    addDataToAlbumViews();
                    break;

                case ARTISTE:
                    artiste = (Artiste) getIntent().getSerializableExtra(ARTISTE);
                    toolbarTitle = artiste.getNomArtiste();
                    setContentView(R.layout.detail_artiste_layout);
                    defaultViewInitialisation();
                    break;

                case KEYWORD:
                    finish();
                    break;

                case PLAYLIST:
                    playlist = (Playlist) getIntent().getSerializableExtra(PLAYLIST);
                    toolbarTitle = playlist.getNomPlaylist();
                    setContentView(R.layout.detail_artiste_layout);
                    defaultViewInitialisation();
                    break;

                default:
                    finish();
            }
            configureViewModel();
        }else{
            finish();
        }
    }

    private void configureViewModel() {
        model = KmpViewModel.getInstance(getApplication(),this);
        switch (whatToDetail){
            case ALBUM:
                model.getAllAlbumMusics(this,album);
                model.getAllAlbumMusics().observe(this, new Observer<List<Musique>>() {
                    @Override
                    public void onChanged(List<Musique> musiques) {
                        musiqueList = musiques;
                        configureAdapter();
                    }
                });
                recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
                break;

            case ARTISTE:
                model.getAllArtistMusics(this,artiste);
                model.getAllArtistMusics().observe(this, new Observer<List<Musique>>() {
                    @Override
                    public void onChanged(List<Musique> musiques) {
                        musiqueList = musiques;
                        configureAdapter();
                    }
                });
                break;

                case PLAYLIST:
                model.getPlaylistSongs(this,playlist);
                model.getPlaylistSongs().observe(this, new Observer<List<Musique>>() {
                    @Override
                    public void onChanged(List<Musique> musiques) {
                        musiqueList = musiques;
                        configureAdapter();
                    }
                });
                break;

        }

        model.getCurrentPLayingMusic().observe(this, new Observer<Musique>() {
            @Override
            public void onChanged(Musique musique) {
                switch (whatToDetail){
                    case ALBUM:
                        if(musique!=null && adapterAlbumMusic!=null){
                            adapterAlbumMusic.setPlayingSong(musique);
                        }
                        break;

                    case PLAYLIST:
                        if(musique!=null && adapter!=null){
                            adapter.setPlayingSong(musique);
                        }
                        break;

                    case ARTISTE:
                    if(musique!=null && adapter!=null){
                        adapter.setPlayingSong(musique);
                    }
                    break;

                    case KEYWORD:
                        if(musique!=null && adapter!=null){
                            adapter.setPlayingSong(musique);
                        }
                        break;
                }
            }
        });
    }

    public void playAfterCurrent(Musique musique) {
        // ajoute le song juste apres celui en cours
        if(bound){
            service.addSong(service.getNextPosition(),musique);
        }
    }

    public void addToPlaylist(Musique musique){
        if(bound){
            service.addSong(musique);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final int position = item.getGroupId();
        final Musique musique = musiqueList.get(position);
        switch (item.getItemId()) {
            case R.id.action_music_play_after:
                playAfterCurrent(musique);
                return true;

            case R.id.action_music_add_to_playslist:
                addToPlaylist(musiqueList.get(position));
                return true;

            case R.id.action_music_partager:
                Helper.shareMusics(this, musiqueList.get(position));
                return true;

            case R.id.action_music_supprimer:
                final Dialog dialog = new Dialog(this);
                dialog.setContentView(R.layout.dialog_confirmation_layout);
                ((TextView)dialog.findViewById(R.id.textview_confirmation_text)).setText(R.string.supprimer_un_song);
                Button cancelButton = dialog.findViewById(R.id.button_confirmation_cancel);
                cancelButton.setText(R.string.annuler);
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                Button validateButton = dialog.findViewById(R.id.button_confirmation_validate);
                validateButton.setText(R.string.supprimer);
                validateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Helper.deleteMusics(Details.this, musique);
                        if(musiqueList.size()<=1){
                            finish();
                        }else{
                            if(model.getCurrentPLayingMusic()!=null && model.getCurrentPLayingMusic().getValue()!=null && model.getCurrentPLayingMusic().getValue().getIdMusique()==musique.getIdMusique()){
                                Intent intent = new Intent(Details.this,PlayerService.class);
                                intent.setAction(PlayerService.ACTION_SKIP_TO_NEXT);
                                startService(intent);
                            }
                            musiqueList.remove(position);
                            adapter.notifyItemRemoved(position);
                            setAlbumNumberOfSong(musiqueList.size());
                        }
                    }
                });

                dialog.setCanceledOnTouchOutside(false);

                dialog.show();

                return true;

            case R.id.action_music_details:
                Helper.showDetailsOf(this, musiqueList.get(position));
                return true;

            case R.id.action_music_retirer_des_favoris:
                model.removeFromFavorite(this, musiqueList.get(position).getIdMusique());
                return true;

            case R.id.action_music_ajouter_aux_favoris:
                model.addToFavorite(this,musiqueList.get(position).getIdMusique());
                return true;
        }

        return false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(connection);
        bound = false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, PlayerService.class);
        bindService(intent,connection, Context.BIND_AUTO_CREATE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.action_supprimer:
                launchActionMode(ACTION_SUPPRIMER);
                break;

            case R.id.action_rechercher:
                launchSearch();
                break;

            case R.id.action_partager:
                launchActionMode(ACTION_PARTAGER);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void launchSearch() {
    }

    private void launchActionMode(String action) {
        isActionMode = true;
        adapter.setActionMode(isActionMode);
        startTransitionForActionMode(action);
    }

    private void startTransitionForActionMode(final String action) {

        if(isActionMode){
            findViewById(R.id.appbarlayout).setVisibility(View.GONE);

            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setGravity(GravityCompat.END);

            Button cancel = new Button(this);
            cancel.setText(R.string.annuler);
            cancel.setBackgroundResource(android.R.color.transparent);
            cancel.setTypeface(Typeface.DEFAULT_BOLD);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stopActionMode(action);
                }
            });

            linearLayout.addView(cancel);

            Button validate = new Button(this);
            validate.setText(R.string.supprimer);
            validate.setBackgroundResource(android.R.color.transparent);
            validate.setTypeface(Typeface.DEFAULT_BOLD);
            validate.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    List<Musique> musiqueList = new ArrayList<>();
                    if(musiqueList!=null && !musiqueList.isEmpty()){
                        if(ACTION_SUPPRIMER.equalsIgnoreCase(action)){
                            deleteSong(musiqueList);
                        }else if(ACTION_PARTAGER.equalsIgnoreCase(action)){
                            Musique[] m = new Musique[musiqueList.size()];
                            Helper.shareMusics(Details.this,musiqueList.toArray(m));
                            stopActionMode(null);
                        }
                    }else{
                        Toast.makeText(Details.this,getString(R.string.veuillez_choisir_au_moins_un_element), Toast.LENGTH_SHORT).show();
                    }

                }
            });

            linearLayout.addView(validate);


        }else{

            findViewById(R.id.appbarlayout).setVisibility(View.VISIBLE);
        }
    }

    private void deleteSong(final List<Musique> musiques) {

        final Dialog dialog = Helper.confirmSongsSuppresion(this, (musiques!=null && musiques.size()>1));
        dialog.findViewById(R.id.button_confirmation_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                stopActionMode(null);
            }
        });

        dialog.findViewById(R.id.button_confirmation_validate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Musique[] m = new Musique[musiques.size()];
                Helper.deleteMusics(Details.this,musiques.toArray(m));
                dialog.dismiss();
                stopActionMode(null);
                model.refreshData(Details.this);
            }
        });

        dialog.show();
    }

    private void stopActionMode(String action) {
        isActionMode = false;
        adapter.setActionMode(isActionMode);
        startTransitionForActionMode(action);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    private void initialiseViewsForAlbumInterface() {
        nombreSOng = findViewById(R.id.textview_activity_detail_nb_song_album);
        nomArtiste = findViewById(R.id.textview_activity_detail_artiste_album);
        image = findViewById(R.id.imageview_detail_album_image);
        defaultViewInitialisation();
    }

    private void defaultViewInitialisation() {
        recyclerView = findViewById(R.id.recyclerview_simple_list_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        findViewById(R.id.floating_button_content_detail_shuffle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.startPlaylist(musiqueList,null,0,true,model,Details.this);
            }
        });
        configureToolbar();
    }

    private void configureToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        ImageView backbutton = findViewById(R.id.imageview_backbutton);
        TextView title = findViewById(R.id.toolbar_title);

        title.setText(toolbarTitle);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        setSupportActionBar(toolbar);
    }

    private void addDataToAlbumViews() {
        nomArtiste.setText(album.getNomArtiste());
        setAlbumNumberOfSong(album.getNombreMusique());
        Glide
                .with(this)
                .load(album.getPochette())
                .centerCrop()
                .placeholder(R.drawable.headphones_black_and_white)
                .error(R.drawable.headphones_black_and_white)
                .crossFade()
                .into(image);
    }

    private void setAlbumNumberOfSong(int nbSong) {
        String nbSon = "";
        if(nbSong>1){
            nbSon = nbSong+ " "+getString(R.string.songs);
        }else {
            nbSon = nbSong + " "+getString(R.string.song);
        }
        nombreSOng.setText(nbSon);
    }

    private void configureAdapter() {
        switch (whatToDetail){

            case ALBUM:
                configureAlbumMusicAdapter();
                break;

            case ARTISTE:
                defaultAdapterConfiguration();
                break;

            case PLAYLIST:
                defaultAdapterConfiguration();
                break;

            case KEYWORD:
                defaultAdapterConfiguration();
                break;
        }
    }

    private void defaultAdapterConfiguration() {
        if(musiqueList!=null){
            if(adapter!=null){
                adapter.setList(musiqueList);
            }else {
                adapter = new MusicAdapterWithImage(this, musiqueList, model, model.getCurrentPLayingMusic().getValue());
            }

            recyclerView.setAdapter(adapter);
        }
    }

    private void configureAlbumMusicAdapter() {
        if(musiqueList!=null){
            if(adapter!=null){
                adapter.setList(musiqueList);
            }else{
                adapterAlbumMusic = new DetailsAlbumMusicAdapter(this,musiqueList, model,model.getCurrentPLayingMusic().getValue());
            }

            recyclerView.setAdapter(adapterAlbumMusic);
        }
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PlayerService.LocalBinder binder = (PlayerService.LocalBinder)service;
            Details.this.service = binder.getService();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };
}
