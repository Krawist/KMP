package com.example.kmp.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.kmp.Adapter.MusicAdapterWithImage;
import com.example.kmp.Modeles.Album;
import com.example.kmp.Modeles.Artiste;
import com.example.kmp.Modeles.Musique;
import com.example.kmp.Modeles.Playlist;
import com.example.kmp.R;
import com.example.kmp.ViewModel.KmpViewModel;

import java.util.List;

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
    private List<Musique> musiqueList;
    private KmpViewModel model;
    private String toolbarTitle;

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
        String nbSon = "";
        if(album.getNombreMusique()>1){
            nbSon = album.getNombreMusique()+ " "+getString(R.string.songs);
        }else {
            nbSon = album.getNombreMusique() + " "+getString(R.string.song);
        }
        nombreSOng.setText(nbSon);

        Glide
                .with(this)
                .load(album.getPochette())
                .centerCrop()
                .placeholder(R.drawable.logo)
                .error(R.drawable.logo)
                .crossFade()
                .into(image);
    }

    private void configureAdapter() {
        if(musiqueList!=null){
            if(adapter!=null){
                adapter.setList(musiqueList);
            }else{
                adapter = new MusicAdapterWithImage(this,musiqueList, model);
            }

            recyclerView.setAdapter(adapter);
        }
    }
}
