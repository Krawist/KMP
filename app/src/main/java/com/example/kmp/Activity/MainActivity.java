package com.example.kmp.Activity;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.kmp.Fragment.AlbumFragment;
import com.example.kmp.Fragment.AllMusicFragment;
import com.example.kmp.Fragment.ArtistDetailFragment;
import com.example.kmp.Fragment.ArtistFragment;
import com.example.kmp.Fragment.DetailAlbumFragment;
import com.example.kmp.Fragment.FavoriFragment;
import com.example.kmp.Fragment.PlaylistDetailFragment;
import com.example.kmp.Helper.Helper;
import com.example.kmp.Modeles.Album;
import com.example.kmp.Modeles.Artiste;
import com.example.kmp.Modeles.Musique;
import com.example.kmp.Modeles.Playlist;
import com.example.kmp.Modeles.ThemeColor;
import com.example.kmp.R;
import com.example.kmp.Service.PlayerService;
import com.example.kmp.ViewModel.KmpViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.transition.ChangeBounds;
import androidx.transition.Transition;
import androidx.transition.TransitionInflater;
import androidx.transition.TransitionSet;
import androidx.viewpager.widget.ViewPager;

import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.List;
import java.util.Random;

import static com.example.kmp.Service.PlayerService.ACTION_PLAY_PLAYLIST;

public class MainActivity extends AppCompatActivity {

    public static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 10;
    private ViewPager viewPager;
    private KmpViewModel model;

    private PlayerService service;
    private boolean bound = false;
    //private ProgressBar progressBar;
    public boolean isFragmentUnder = false;
    private Fragment fragmentUnder = null;
    private BottomNavigationView bottomNavigationView;
    private ImageView playingMusicImage;
    private ImageView playingMusicButton;
    private View floatingView;
    int lastAction;
    int initialX;
    int initialY;
    float initialTouchX;
    float initialTouchY;
    private RelativeLayout rootLayout;
    private WindowManager.LayoutParams params;
    private long lastTouchMoment = System.currentTimeMillis();
    private boolean transitionFinished;
    private GestureDetectorCompat gestureDetectorCompat;
    private ImageView nextButton;
    private ImageView previousButton;
    private ImageView favoriteButton;
    private ImageView shuffleButton;
    private ImageView repeatButton;
    private boolean userHaveNavigate = true;
    private CoordinatorLayout controlLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
            setContentView(R.layout.activity_main);
            configureToolbar();
            initialiseApp();
        }else{
            startActivity(new Intent(this, PermissionActivity.class));
            finish();
        }

        //configureSwipeListening();

    }

    private void configureSwipeListening() {
        SwipeListener swipeListener = new SwipeListener();
        gestureDetectorCompat = new GestureDetectorCompat(this,swipeListener);
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
        if(model!=null) {
            boolean done = model.refreshData(this);
            if(!done){
                startActivity(new Intent(this, PermissionActivity.class));
            }
        }

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(connection);
        bound = false;
    }

    @Override
    public void onBackPressed() {
        if(isFragmentUnder) {
            userHaveNavigate = true;
            super.onBackPressed();
            isFragmentUnder = false;
            bottomNavigationView.setVisibility(View.VISIBLE);
        }else{
            if(userHaveNavigate){
                Toast.makeText(this, getString(R.string.appuyer_e_nouvea_pour_quitter),Toast.LENGTH_SHORT).show();
                userHaveNavigate = false;
            }else{
                super.onBackPressed();
            }
        }
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

        addActionsOnViews();

        iniatiseBottomSheetView();

/*        Intent intent = new Intent(this,PlayerService.class);
        intent.setAction(PlayerService.ACTION_LOAD);
        startService(intent);*/
    }

    private void configureViewModel() {
        model = KmpViewModel.getInstance(getApplication(), this);

        Intent intent = new Intent(this, PlayerService.class);
        intent.setAction(PlayerService.ACTION_LOAD);
        startService(intent);

        model.getCurrentPLayingMusic().observe(this, new Observer<Musique>() {
            @Override
            public void onChanged(Musique musique) {
                if(model.getListOfSongToPlay().getValue()!=null && !model.getListOfSongToPlay().getValue().isEmpty()){
                    configureBottomSheet();
                }

                if (musique==null){
                    controlLayout.setVisibility(View.GONE);
                }
            }
        });

        model.getLoopingMode().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
/*                Intent intent = new Intent(MainActivity.this,PlayerService.class);
                intent.setAction(PlayerService.ACTION_REPEAT);
                intent.putExtra(PlayerService.REPEAT_MODE,integer);
                startService(intent);*/
            }
        });

        model.getSongIsPlaying().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean)
                    playingMusicButton.setImageResource(R.drawable.ic_pause_black_24dp);
                else
                    playingMusicButton.setImageResource(R.drawable.ic_play_arrow_black_24dp);
            }
        });

        model.getPlayingSongPosition().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer progress) {
                if(model.getCurrentPLayingMusic().getValue()!=null){
                    Musique musique = model.getCurrentPLayingMusic().getValue();
                    if(musique!=null){
/*                        progressBar.setMax(musique.getDuration());
                        progressBar.setProgress(progress);*/
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

        model.getLoopingMode().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                switch (integer){

                    case PlaybackStateCompat.REPEAT_MODE_NONE:
                        repeatButton.setImageAlpha(100);
                        repeatButton.setImageResource(R.drawable.ic_repeat_black_24dp);
                        break;

                    case PlaybackStateCompat.REPEAT_MODE_ALL:
                        repeatButton.setImageAlpha(255);
                        repeatButton.setImageResource(R.drawable.ic_repeat_black_24dp);
                        break;

                    case PlaybackStateCompat.REPEAT_MODE_ONE:
                        repeatButton.setImageAlpha(255);
                        repeatButton.setImageResource(R.drawable.ic_repeat_one_black_24dp);
                        break;

                }
            }
        });

        model.getShuffleMode().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    shuffleButton.setImageAlpha(255);
                }else{
                    shuffleButton.setImageAlpha(100);
                }
            }
        });

        model.getThemeColor().observe(this, new Observer<ThemeColor>() {
            @Override
            public void onChanged(final ThemeColor themeColor) {
                final View vue = findViewById(R.id.layout_activity_main_control);
/*                ObjectAnimator animator = ObjectAnimator.ofInt(vue,"backgroundColor",themeColor.getBackgroundColor());
                animator.setDuration(1000);
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        vue.setBackgroundColor(themeColor.getBackgroundColor());
                    }
                });*/

                vue.getBackground().setColorFilter(themeColor.getBackgroundColor(), PorterDuff.Mode.SRC_ATOP);
            }
        });
    }

    private void configureBottomSheet() {
       Musique musique = model.getCurrentPLayingMusic().getValue();
       Helper.loadCircleImage(this,playingMusicImage,musique.getPochette(),56);

        if(model.getFavoriteSongs().getValue()!=null){
            if(model.getFavoriteSongsId().getValue().contains(musique.getIdMusique())){
                favoriteButton.setImageResource(R.drawable.ic_favorite_black_24dp);
            }else{
                favoriteButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);
            }
        }

/*        ImageView imageView = findViewById(R.id.imageview_home_background);
        Glide.with(this)
                .load(musique.getPochette())
                .error(R.drawable.headphones_high_quality)
                .centerCrop()
                .into(imageView);*/

    }

    private void configureView() {
        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
        int lastVisitedPage = PreferenceManager.getDefaultSharedPreferences(this).getInt(Helper.PREFERENCES_LAST_VISITED_PAGED_NUMBER,0);
        viewPager.setCurrentItem(lastVisitedPage,false);
        setSelectedFragment(lastVisitedPage);
    }

    private void initialiseViews() {
        //tabLayout = findViewById(R.id.tablayout_content_main);
        viewPager = findViewById(R.id.viewpager_content_main);
        bottomNavigationView = findViewById(R.id.nav_view);

        playingMusicImage = findViewById(R.id.imageview_playing_music_image);
        playingMusicButton = findViewById(R.id.imageview_playbutton);
        floatingView = findViewById(R.id.floatingview);
        rootLayout = findViewById(R.id.rootlayout);

        nextButton = findViewById(R.id.imageview_activity_main_forward_button);
        previousButton = findViewById(R.id.imageview_activity_main_rewind_button);
        favoriteButton = findViewById(R.id.imageview_activity_main_like_button);
        shuffleButton = findViewById(R.id.imageview_activity_main_shuffle_button);
        repeatButton = findViewById(R.id.imageview_activity_main_repeat_button);
        controlLayout = findViewById(R.id.layout_control);
    }

    private void addActionsOnViews() {

        floatingView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    Pair<View, String> p1 = Pair.create((View)previousButton,previousButton.getTransitionName());
                    Pair<View, String> p2 = Pair.create((View)shuffleButton,shuffleButton.getTransitionName());
                    Pair<View, String> p3 = Pair.create((View)favoriteButton,favoriteButton.getTransitionName());
                    Pair<View, String> p4 = Pair.create((View)repeatButton,repeatButton.getTransitionName());
                    Pair<View, String> p5 = Pair.create((View)nextButton,nextButton.getTransitionName());
                    Pair<View, String> p6 = Pair.create((View)playingMusicButton,playingMusicButton.getTransitionName());
                    ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this,p1,p2,p3,p4,p5,p6);
                    startActivity(new Intent(MainActivity.this,PlayingMusicActivity.class),optionsCompat.toBundle());
                }else{
                    startActivity(new Intent(MainActivity.this,PlayingMusicActivity.class));
                }
                return true;
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PlayerService.class);
                intent.setAction(PlayerService.ACTION_SKIP_TO_NEXT);
                startService(intent);
            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PlayerService.class);
                intent.setAction(PlayerService.ACTION_SKIP_TO_PREVIOUS);
                startService(intent);
            }
        });

        shuffleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bound){
                    service.changeShuffleMode();
                }
            }
        });

        repeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bound){
                    service.changeRepeatMode();
                }
            }
        });


        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Musique musique = model.getCurrentPLayingMusic().getValue();
                musique.setLiked(!musique.isLiked());
                if(model.getFavoriteSongsId().getValue()!=null){
                    if(model.getFavoriteSongsId().getValue().contains(musique.getIdMusique())){
                        favoriteButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                        model.removeFromFavorite(MainActivity.this,musique.getIdMusique());
                    }else{
                        favoriteButton.setImageResource(R.drawable.ic_favorite_black_24dp);
                        model.addToFavorite(MainActivity.this,musique.getIdMusique());
                    }
                }
            }
        });



        viewPager.setOffscreenPageLimit(2);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setSelectedFragment(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        floatingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PlayerService.class);
                intent.setAction(PlayerService.ACTION_TOGGLE_PAUSE);
                startService(intent);
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                userHaveNavigate = true;
                return openFragment(menuItem.getItemId());
            }
        });

        if(model.getCurrentPLayingMusic()==null || model.getCurrentPLayingMusic().getValue()==null){
            controlLayout.setVisibility(View.GONE);
        }else{
            controlLayout.setVisibility(View.VISIBLE);
        }

    }

    private void setSelectedFragment(int position) {
        int resId = 0;
        int pos = 0;
        String title = null;
        switch (position){
            case 0:
                resId = R.id.navigation_home;
                pos = 0;
                title = getString(R.string.home);
                break;

            case 1:
                resId = R.id.navigation_all_songs;
                pos = 1;
                title = getString(R.string.musiques);
                break;

            case 2:
                resId = R.id.navigation_albums;
                title = getString(R.string.albums);
                pos = 2;
                break;

            case 3:
                resId = R.id.navigation_artists;
                title = getString(R.string.artistes);
                pos = 3;
                break;
        }

        if(resId!=0){
            bottomNavigationView.setSelectedItemId(resId);
            PreferenceManager.getDefaultSharedPreferences(MainActivity.this)
                    .edit()
                    .putInt(Helper.PREFERENCES_LAST_VISITED_PAGED_NUMBER,pos)
                    .apply();
        }
    }

    private boolean openFragment(int itemId) {
        int selectedItem = 0;
        switch (itemId){

            case R.id.navigation_home:
                selectedItem = 0;
                break;

            case R.id.navigation_all_songs:
                selectedItem = 1;
                break;

            case R.id.navigation_albums:
                selectedItem = 2;
                break;

            case R.id.navigation_artists:
                selectedItem = 3;
                break;

                default:
                    selectedItem = -1;
                    break;

        }

        if(selectedItem!=-1){
            viewPager.setCurrentItem(selectedItem,true);
            return true;
        }

        return false;
    }

    private void iniatiseBottomSheetView(){

        //progressBar = findViewById(R.id.progressbar_bottom_sheet_progress);
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

    public void addListToPlaylist(List<Musique> musiques){
        if(bound){
            service.addSongs(musiques);
        }
    }

    private void openPlayingMusicActivity() {
        final int[] val = new int[2];
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        floatingView.getLocationOnScreen(val);
        final float actualPositionWidth = val[0];
        final float actualPositionHeight = val[1];

        final float screenMiddleHeight = displayMetrics.heightPixels/2;
        final float screenMiddleWidth = displayMetrics.widthPixels / 2;

        ObjectAnimator xanimator = ObjectAnimator.ofFloat(floatingView,"x", floatingView.getX(), screenMiddleWidth);
        ObjectAnimator yanimator = ObjectAnimator.ofFloat(floatingView,"y", floatingView.getY(),  screenMiddleHeight);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(xanimator, yanimator);
        animatorSet.setInterpolator(new LinearInterpolator());
        animatorSet.setDuration(500);
        animatorSet.start();

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

    public void openAlbumDetail(Album album, View view){
        DetailAlbumFragment fragment = DetailAlbumFragment.getInstance(album);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if(Build.VERSION.SDK_INT>=21){
            Transition transition = new ChangeBounds();
            fragment.setSharedElementEnterTransition(transition);
            fragment.setSharedElementReturnTransition(transition);
            fragmentTransaction.addSharedElement(view.findViewById(R.id.imageview_album_item_image),"album_art_transition");
        }

        fragmentTransaction
                .replace(R.id.container,fragment)
                .addToBackStack(null)
                .commit();

        isFragmentUnder = true;
        fragmentUnder = fragment;
        bottomNavigationView.setVisibility(View.GONE);
    }

    public void openArtistDetail(Artiste artiste){
        ArtistDetailFragment fragment = ArtistDetailFragment.getInstance(artiste);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container,fragment)
                .addToBackStack(null)
                .commit();

        isFragmentUnder = true;
        bottomNavigationView.setVisibility(View.GONE);

    }

    public void openPlaylistDetail(Playlist playlist){
        PlaylistDetailFragment fragment = PlaylistDetailFragment.getInstance(playlist);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container,fragment)
                .addToBackStack(null)
                .commit();
        isFragmentUnder = true;
        bottomNavigationView.setVisibility(View.GONE);
    }

    public class SwipeListener extends GestureDetector.SimpleOnGestureListener{

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            Log.e("TAG","on vient de swiper");

            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }
}
