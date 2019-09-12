package com.example.kmp.Fragment;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.kmp.Activity.MainActivity;
import com.example.kmp.Helper.Helper;
import com.example.kmp.Modeles.Musique;
import com.example.kmp.Modeles.Playlist;
import com.example.kmp.Modeles.ThemeColor;
import com.example.kmp.R;
import com.example.kmp.Service.PlayerService;
import com.example.kmp.ViewModel.KmpViewModel;

import java.util.List;

import static com.example.kmp.Helper.Helper.TRANSITION_TIME;

public class FavoriFragment  extends Fragment {

    private KmpViewModel model;
    private List<Musique> favoriteSong;
    private List<Playlist> playlists;
    private FavoriAdapter favorisAdapter;
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
                if(favorisAdapter!=null)
                    favorisAdapter.notifyDataSetChanged();
                    playlistAdapter.notifyDataSetChanged();
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
                favorisAdapter = new FavoriAdapter();
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
                playlistAdapter = new PlaylistAdapter(this.playlists);
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

    private class FavoriAdapter extends RecyclerView.Adapter<FavoriAdapter.FavorisViewHolder> {

        LayoutInflater inflater;

        @Override
        public void onBindViewHolder(@NonNull FavoriAdapter.FavorisViewHolder holder, int position) {
            holder.bindData(favoriteSong.get(position), position);
        }

        public FavoriAdapter(){
            inflater = LayoutInflater.from(getContext());
        }

        @NonNull
        @Override
        public FavoriAdapter.FavorisViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new FavoriAdapter.FavorisViewHolder(inflater.inflate(R.layout.simple_item_with_image_black,parent,false));
        }

        @Override
        public int getItemCount() {
            if(favoriteSong !=null)
                return favoriteSong.size();
            else
                return 0;
        }

        public void setList(List<Musique> cursor) {
            FavoriFragment.this.favoriteSong = cursor;
            notifyDataSetChanged();
        }

        public class FavorisViewHolder extends RecyclerView.ViewHolder{

            private final TextView titreMusique;
            private final TextView artisteMusique;
            private final TextView dureeMusique;
            private final ImageView image;


            public FavorisViewHolder(View itemView){
                super(itemView);
                titreMusique = itemView.findViewById(R.id.textview_simple_item_title);
                artisteMusique = itemView.findViewById(R.id.textview_simple_item_second_text);
                dureeMusique = itemView.findViewById(R.id.textview_simple_item_third_text);
                image = itemView.findViewById(R.id.imageview_simple_item_image);
            }


            private void restoreDefaultColor(){
                titreMusique.setTextColor(getResources().getColor(android.R.color.black));
                artisteMusique.setTextColor(getResources().getColor(android.R.color.black));
                itemView.setBackgroundColor(getResources().getColor(android.R.color.white));
            }

            public void bindData(final Musique musique, final int position){
                titreMusique.setText(musique.getTitreMusique().trim());
                artisteMusique.setText(musique.getNomArtiste().trim());
                dureeMusique.setText(Helper.formatDurationToString(musique.getDuration()));

                Helper.loadCircleImage(getContext(),image, musique.getPochette(),35);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((MainActivity)getContext()).startPlaylist(favoriteSong,musique, position, false);
                    }
                });

                if(model.getCurrentPLayingMusic().getValue()!=null){
                    if(musique.getIdMusique()==model.getCurrentPLayingMusic().getValue().getIdMusique()){
                        ThemeColor themeColor = model.getThemeColor().getValue();
                        if(themeColor!=null){
                            //itemView.setBackgroundColor(themeColor.getBackgroundColor());
                            int previousColor = titreMusique.getHighlightColor();
                            ObjectAnimator animation = ObjectAnimator.ofInt(titreMusique, "textColor",previousColor,  themeColor.getBackgroundColor());
                            animation.setEvaluator(new ArgbEvaluator());
                            animation.setDuration(TRANSITION_TIME);
                            animation.start();

                            animation = ObjectAnimator.ofInt(artisteMusique,"textColor", previousColor, themeColor.getBackgroundColor());
                            animation.setEvaluator(new ArgbEvaluator());
                            animation.setDuration(TRANSITION_TIME);
                            animation.start();
                        }else
                            restoreDefaultColor();
                    }else
                        restoreDefaultColor();
                }else{
                    restoreDefaultColor();
                }

                Helper.builMusicItemContextMenu(getContext(),itemView,musique,position);
            }
        }
    }

    private class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {

        List<Playlist> playlists;
        private final LayoutInflater inflater;

        public PlaylistAdapter(List<Playlist> playlists){
            this.playlists = playlists;
            inflater = LayoutInflater.from(getContext());
        }

        @NonNull
        @Override
        public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new PlaylistViewHolder(inflater.inflate(R.layout.playlist_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
                holder.bindData(playlists.get(position), position);
        }

        public void setList(List<Playlist> playlists){
            this.playlists = playlists;
        }

        @Override
        public int getItemCount() {
            if(playlists!=null)
                return playlists.size();
            else
                return 0;
        }

        private class PlaylistViewHolder extends RecyclerView.ViewHolder{

            private final TextView playlistName;
            private final ImageView playlistIcon;
            private final ImageButton playlistPlayAllButton;

            public PlaylistViewHolder(View view){
                super(view);
                playlistName = view.findViewById(R.id.textview_playlist_item_name);
                playlistIcon = view.findViewById(R.id.imageview_playlist_item_image);
                playlistPlayAllButton = view.findViewById(R.id.imagebutton_playlist_item_play_all);
            }

            public void bindData(final Playlist playlist, int position){
                playlistName.setText(playlist.getNomPlaylist());
                Glide.with(getContext())
                        .load(playlist.getPochette())
                        .error(R.color.colorPrimaryDark)
                        .centerCrop()
                        .into(playlistIcon);

                playlistPlayAllButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        List<Musique> list = model.getPlaylistSongs(getContext(), playlist).getValue();
                        ((MainActivity)getContext()).startPlaylist(list,list.get(0),0,false);
                    }
                });

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((MainActivity)getContext()).openPlaylistDetail(playlist);
                    }
                });
            }
        }
    }
}
