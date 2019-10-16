package com.example.kmp.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kmp.Activity.MainActivity;
import com.example.kmp.Adapter.MusicAdapterWithImage;
import com.example.kmp.Adapter.PlaylistAdapter;
import com.example.kmp.Helper.Helper;
import com.example.kmp.Modeles.Musique;
import com.example.kmp.Modeles.Playlist;
import com.example.kmp.Modeles.ThemeColor;
import com.example.kmp.R;
import com.example.kmp.Service.PlayerService;
import com.example.kmp.ViewModel.KmpViewModel;

import java.util.List;

public class FavoriFragment  extends Fragment {

    private KmpViewModel model;
    private List<Musique> favoriteSong;
    private List<Playlist> playlists;
    private MusicAdapterWithImage favorisAdapter;
    private PlaylistAdapter playlistAdapter;
    private RecyclerView recyclerViewPlaylist;
    private TextView holderTextView;
    private RecyclerView recyclerViewFavoris;
    private LinearLayout layoutPlaylistPresentation;
    private LinearLayout layoutFavorisPresentation;
    private ImageButton shuffleFavorisButton;
    private Button playAllFavorisButton;

    public FavoriFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_accueil_layout,container,false);

        initialiseViews(view);

        configureRecyclerView();

        configureModel();

        configureFavorisAdapter();

        configurePlaylistAdapter();

        return view;
    }

    private void initialiseViews(View view) {
        recyclerViewPlaylist = view.findViewById(R.id.recyclerview_fragment_acceuil_list_playlist);
        recyclerViewFavoris = view.findViewById(R.id.recyclerview_fragment_acceuil_list_favoris);
        layoutPlaylistPresentation = view.findViewById(R.id.layout_fragment_acceuil_playlist_presentation);
        layoutFavorisPresentation = view.findViewById(R.id.layout_fragment_acceuil_favoris_presentation);
        shuffleFavorisButton = view.findViewById(R.id.imagebutton_fragment_accueil_shuffle_favoris_button);
        playAllFavorisButton = view.findViewById(R.id.button_fragment_acceuil_play_all_button);

        addActionToViews();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(getUserVisibleHint() && isVisible() && isResumed() && !((MainActivity)getContext()).isFragmentUnder){
            Helper.handleMusicContextItemSelected(getContext(),item,favoriteSong);
        }
        return super.onContextItemSelected(item);
    }

    private void addActionToViews() {
        shuffleFavorisButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getContext()).startPlaylist(favoriteSong,null,0,true);
            }
        });

        playAllFavorisButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PlayerService.class);
                model.getCurrentPLayingMusic().setValue(favoriteSong.get(0));
                model.getListOfSongToPlay().setValue(favoriteSong);
                intent.setAction(PlayerService.ACTION_PLAY_PLAYLIST);
                getActivity().startService(intent);
            }
        });
    }

    private void configureModel() {
        model = KmpViewModel.getInstance(getActivity().getApplication(), getContext());

        model.getFavoriteSongs().observe(this, new Observer<List<Musique>>() {
            @Override
            public void onChanged(List<Musique> musiques) {
                favoriteSong = musiques;
                configureFavorisAdapter();
            }
        });

        model.getPlaylists().observe(this, new Observer<List<Playlist>>() {
            @Override
            public void onChanged(List<Playlist> playlistList) {
                playlists = playlistList;
                configurePlaylistAdapter();
            }
        });

        model.getThemeColor().observe(this, new Observer<ThemeColor>() {
            @Override
            public void onChanged(ThemeColor themeColor) {
                if (favorisAdapter != null){
                    favorisAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void changeVisibility(View recyclerview, int visibilite1, View holderTextView, int visibilite2){
        recyclerview.setVisibility(visibilite1);
        holderTextView.setVisibility(visibilite2);
    }

    private void configureFavorisAdapter() {
        if(favoriteSong!=null && !favoriteSong.isEmpty()){
            if(favorisAdapter !=null){
                favorisAdapter.setList(favoriteSong);
            }else{
                favorisAdapter = new MusicAdapterWithImage(getContext(),favoriteSong, model);
            }

            recyclerViewFavoris.setAdapter(favorisAdapter);

            layoutFavorisPresentation.setVisibility(View.VISIBLE);

        }else{
           layoutFavorisPresentation.setVisibility(View.GONE);
        }
    }

    private void configurePlaylistAdapter() {
        if(playlists !=null && !playlists.isEmpty()){
            if(playlistAdapter !=null){
                playlistAdapter.setList(playlists);
            }else{
                playlistAdapter = new PlaylistAdapter(playlists,getContext(),model);
            }
            recyclerViewPlaylist.setAdapter(playlistAdapter);

            layoutPlaylistPresentation.setVisibility(View.VISIBLE);
        }else{

            layoutPlaylistPresentation.setVisibility(View.GONE);
        }
    }

    private void configureRecyclerView() {
        recyclerViewFavoris.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewPlaylist.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false));
    }

}
