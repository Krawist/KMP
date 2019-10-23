package com.example.kmp.Fragment;


import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kmp.Activity.MainActivity;
import com.example.kmp.Helper.Helper;
import com.example.kmp.Modeles.Musique;
import com.example.kmp.Modeles.Playlist;
import com.example.kmp.Modeles.ThemeColor;
import com.example.kmp.R;
import com.example.kmp.Service.PlayerService;
import com.example.kmp.ViewModel.KmpViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import static com.example.kmp.Helper.Helper.TRANSITION_TIME;
import static com.example.kmp.Service.PlayerService.ACTION_PLAY_PLAYLIST;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlaylistDetailFragment extends Fragment {


    private Playlist playlist = null;
    KmpViewModel model;
    private List<Musique> musiqueList;
    private RecyclerView recyclerView;
    private MusicAdapter adapter;
    private static final String PLAYLIST_TO_DISPLAY = "playlist_a_afficger";

    public PlaylistDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle!=null){
            if(bundle.getSerializable(PLAYLIST_TO_DISPLAY)!=null){
                playlist = (Playlist) bundle.getSerializable(PLAYLIST_TO_DISPLAY);
            }
        }
        configureViewModel();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(getUserVisibleHint()){
            Helper.handleMusicContextItemSelected(getContext(),item,musiqueList);
        }
        return super.onContextItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_artiste_layout, container, false);

        FloatingActionButton fab = view.findViewById(R.id.floating_button_content_detail_shuffle);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getContext()).startPlaylist(musiqueList,null,0,true);
            }
        });

        configureToolbar(view);

        initialiseViews(view);

        configureRecyclerView();

        configureAdapter();

        return view;

    }

    private void configureAdapter() {
        if(musiqueList!=null){
            if(adapter!=null)
                adapter.setList(musiqueList);
            else
                adapter = new MusicAdapter();

            recyclerView.setAdapter(adapter);
        }
    }

    private void configureToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);

        ImageView backbutton = view.findViewById(R.id.imageview_backbutton);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        ((TextView)view.findViewById(R.id.toolbar_title)).setText(playlist.getNomPlaylist());
    }

    private void initialiseViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerview_simple_list_items);
    }

    private void configureRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void configureViewModel(){
        model = KmpViewModel.getInstance(getActivity().getApplication(), getContext());
        model.getPlaylistSongs(getContext(), playlist);
        model.getPlaylistSongs().observe(this, new Observer<List<Musique>>() {
            @Override
            public void onChanged(List<Musique> musiques) {
                musiqueList = musiques;
                configureAdapter();
            }
        });

        model.getThemeColor().observe(this, new Observer<ThemeColor>() {
            @Override
            public void onChanged(ThemeColor themeColor) {
                if(adapter!=null)
                    adapter.notifyDataSetChanged();
            }
        });
    }

    public static PlaylistDetailFragment getInstance(Playlist playlist){
        PlaylistDetailFragment artistDetailFragment = new PlaylistDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(PLAYLIST_TO_DISPLAY, playlist);
        artistDetailFragment.setArguments(bundle);
        return artistDetailFragment;
    }

    public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusiqueViewHolder> {

        LayoutInflater inflater;

        public MusicAdapter(){
            inflater = LayoutInflater.from(getContext());
        }

        @NonNull
        @Override
        public MusicAdapter.MusiqueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MusicAdapter.MusiqueViewHolder(inflater.inflate(R.layout.simple_item_with_image_black,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull MusicAdapter.MusiqueViewHolder holder, int position) {
            holder.bindData(musiqueList.get(position), position);
        }

        @Override
        public int getItemCount() {
            if(musiqueList!=null)
                return musiqueList.size();
            else
                return 0;
        }

        public void setList(List<Musique> list) {
            PlaylistDetailFragment.this.musiqueList = list;
            notifyDataSetChanged();
        }

        public class MusiqueViewHolder extends RecyclerView.ViewHolder{

            private final TextView titreMusique;
            private final TextView artisteMusique;
            private final TextView dureeMusique;
            private final ImageView image;

            public MusiqueViewHolder(View itemView){
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
                artisteMusique.setText(musique.getTitreAlbum());
                dureeMusique.setText(Helper.formatDurationToString(musique.getDuration()));

                Helper.loadCircleImage(getContext(),image, musique.getPochette(), 45);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        model.setPlayingList(musiqueList,getContext());
                        model.getPositionOfSongToPLay().setValue(position);
                        model.getCurrentPLayingMusic().setValue(musique);
                        Intent intent = new Intent(getContext(), PlayerService.class);
                        intent.setAction(ACTION_PLAY_PLAYLIST);
                        getActivity().startService(intent);
                    }
                });

                Helper.builMusicItemContextMenu(getContext(), itemView, musique, position);
            }
        }
    }
}
