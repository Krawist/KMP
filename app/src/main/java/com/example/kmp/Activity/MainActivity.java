package com.example.kmp.Activity;

import android.Manifest;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.example.kmp.Fragment.AlbumFragment;
import com.example.kmp.Fragment.AllMusicFragment;
import com.example.kmp.Fragment.ArtistDetailFragment;
import com.example.kmp.Fragment.ArtistFragment;
import com.example.kmp.Fragment.DetailAlbumFragment;
import com.example.kmp.Fragment.FavoriFragment;
import com.example.kmp.Fragment.PlaylistFragment;
import com.example.kmp.Helper.Helper;
import com.example.kmp.Modeles.Album;
import com.example.kmp.Modeles.Artiste;
import com.example.kmp.Modeles.Favori;
import com.example.kmp.Modeles.Musique;
import com.example.kmp.Modeles.Playlist;
import com.example.kmp.Playback;
import com.example.kmp.R;
import com.example.kmp.Service.PlayerService;
import com.example.kmp.ViewModel.KmpViewModel;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Observer;
import androidx.media.session.MediaButtonReceiver;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.os.IBinder;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Random;

import static com.example.kmp.Service.PlayerService.ACTION_PLAY_PLAYLIST;

public class MainActivity extends AppCompatActivity {

    public static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 10;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private KmpViewModel model;
    private ImageView image;
    private TextView titre;
    private TextView artiste;
    private ImageView previous;
    private ImageView play;
    private ImageView next;

    private PlayerService service;
    private boolean bound = false;
    private ProgressBar progressBar;
    public boolean isFragmentUnder = false;
    private Fragment fragmentUnder = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        configureToolbar();

        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
            initialiseApp();
        }else{
            startActivity(new Intent(this, PermissionActivity.class));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, PlayerService.class);
        bindService(intent,connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(model!=null)
            model.refreshData(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(connection);
        bound = false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(isFragmentUnder)
            isFragmentUnder = false;
    }

    private void configureToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
    }

    private void initialiseApp() {
        initialiseViews();

        configureView();

        configureViewModel();

/*        Intent intent = new Intent(this,PlayerService.class);
        intent.setAction(PlayerService.ACTION_LOAD);
        startService(intent);*/
    }

    private void configureViewModel() {
        model = KmpViewModel.getInstance(getApplication(), this);

        model.getCurrentPLayingMusic().observe(this, new Observer<Musique>() {
            @Override
            public void onChanged(Musique musique) {
                if(model.getListOfSongToPlay().getValue()!=null && !model.getListOfSongToPlay().getValue().isEmpty()){
                    configureBottomSheet();
                }
            }
        });

        model.getLoopingMode().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                Intent intent = new Intent(MainActivity.this,PlayerService.class);
                intent.setAction(PlayerService.ACTION_REPEAT);
                intent.putExtra(PlayerService.REPEAT_MODE,integer);
                startService(intent);
            }
        });

        model.getSongIsPlaying().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean)
                    play.setImageResource(R.drawable.ic_pause_circle_outline_black_24dp);
                else
                    play.setImageResource(R.drawable.ic_play_circle_outline_black_40dp);
            }
        });

        model.getPlayingSongPosition().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer progress) {
                if(model.getCurrentPLayingMusic().getValue()!=null){
                    Musique musique = model.getCurrentPLayingMusic().getValue();
                    if(musique!=null){
                        progressBar.setMax(musique.getDuration());
                        progressBar.setProgress(progress);
                    }
                }
            }
        });

        model.getFavoriteSongsId().observe(this, new Observer<List<Integer>>() {
            @Override
            public void onChanged(List<Integer> favoris) {
                model.refreshFavoriteSong(MainActivity.this);
            }
        });
    }

    private void configureBottomSheet() {
        Musique musique = model.getCurrentPLayingMusic().getValue();
        titre.setText(musique.getTitreMusique());
        artiste.setText(musique.getNomArtiste());

        Helper.loadCircleImage(this,image, musique.getPochette(),50);

        progressBar.setMax(musique.getDuration());

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,PlayerService.class);
                intent.setAction(PlayerService.ACTION_SKIP_TO_NEXT);
                startService(intent);
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,PlayerService.class);
                intent.setAction(PlayerService.ACTION_SKIP_TO_PREVIOUS);
                startService(intent);
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,PlayerService.class);
                intent.setAction(PlayerService.ACTION_TOGGLE_PAUSE);
                startService(intent);
            }
        });
        ((RelativeLayout)findViewById(R.id.bottomsheet)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            startActivity(new Intent(MainActivity.this, PlayingMusicActivity.class));
            }
        });
    }

    private void configureView() {
        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
    }

    private void initialiseViews() {
        tabLayout = findViewById(R.id.tablayout_content_main);
        viewPager = findViewById(R.id.viewpager_content_main);
        iniatiseBottomSheetView();
    }

    private void iniatiseBottomSheetView(){
        image = findViewById(R.id.imageview_bottomsheet_image);
        titre = findViewById(R.id.textview_bottomsheet_music_title);
        artiste = findViewById(R.id.textview_bottomsheet_music_artist);
        previous = findViewById(R.id.imageview_bottomsheet_previous_button);
        play = findViewById(R.id.imageview_bottomsheet_play_button);
        next = findViewById(R.id.imageview_bottomsheet_next_button);
        progressBar = findViewById(R.id.progressbar_bottom_sheet_progress);
    }

    public void playAfterCurrent(Musique musique) {
        // ajoute le song juste apres celui en cours
        if(bound){
            service.addSong(service.getNextPosition(),musique);
        }
    }

    public void playListAfterCurrent(List<Musique> musiques) {
        // ajoute la liste juste apres le song en cours en cours
        if(bound){
            service.addSongs(service.getNextPosition(),musiques);
        }
    }

    public void addToPlaylist(Musique musique){
        if(bound){
            service.addSong(musique);
        }
    }

    public void startPlaylist(List<Musique> list, Musique musique, int position, boolean isShuffle){
        int positionOfStart = position;
        if(isShuffle){
            if(bound)
                service.setShuffle();
            positionOfStart = new Random().nextInt(list.size());
        }

        model.setPlayingList(list,this);
        model.getPositionOfSongToPLay().setValue(positionOfStart);

        if(list!=null){
            model.getCurrentPLayingMusic().setValue(list.get(positionOfStart));
        }else{
            model.getCurrentPLayingMusic().setValue(null);
        }

        if(model.getCurrentPLayingMusic().getValue()==null ||
            model.getPositionOfSongToPLay().getValue()==null ||
            model.getListOfSongToPlay().getValue() == null){

            Toast.makeText(this, R.string.une_erreur_est_survenue,Toast.LENGTH_SHORT).show();

        }else {
            Intent intent = new Intent(this, PlayerService.class);
            intent.setAction(ACTION_PLAY_PLAYLIST);
            startService(intent);
        }
    }

    public void addListToPlaylist(List<Musique> musiques){
        if(bound){
           service.addSongs(musiques);
        }
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0: return getString(R.string.favoris);
                case 1: return getString(R.string.tout);
                case 2: return getString(R.string.albums);
                case 3: return getString(R.string.artistes);
                case 4: return getString(R.string.playlist);
            }

            return "";
        }

        public ViewPagerAdapter(FragmentManager fragmentManager){
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;

            switch (position){

                case 0:
                    return new FavoriFragment();

                case 1:
                    return new AllMusicFragment();

                case 2:
                    return new AlbumFragment();

                case 3:
                    return new ArtistFragment();
            }

            return fragment;
        }

        @Override
        public int getCount() {
            return 4;
        }
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PlayerService.LocalBinder binder = (PlayerService.LocalBinder)service;
            MainActivity.this.service = binder.getService();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };

    public void openAlbumDetail(Album album){
        DetailAlbumFragment fragment = DetailAlbumFragment.getInstance(album);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container_fragment,fragment)
                .addToBackStack(null)
                .commit();

        isFragmentUnder = true;
        fragmentUnder = fragment;
    }

    public void openArtistDetail(Artiste artiste){
        ArtistDetailFragment fragment = ArtistDetailFragment.getInstance(artiste);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container_fragment,fragment)
                .addToBackStack(null)
                .commit();

        isFragmentUnder = true;
        fragmentUnder = fragmentUnder;
    }

    public void openPlaylistDetail(Playlist playlist){
        PlaylistFragment fragment = PlaylistFragment.getInstance(playlist);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container_fragment,fragment)
                .addToBackStack(null)
                .commit();
        isFragmentUnder = false;
    }

}
