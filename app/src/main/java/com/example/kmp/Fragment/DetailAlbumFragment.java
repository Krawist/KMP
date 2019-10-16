package com.example.kmp.Fragment;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.kmp.Activity.MainActivity;
import com.example.kmp.Helper.Helper;
import com.example.kmp.Modeles.Album;
import com.example.kmp.Modeles.Favori;
import com.example.kmp.Modeles.Musique;
import com.example.kmp.Modeles.ThemeColor;
import com.example.kmp.R;
import com.example.kmp.Service.PlayerService;
import com.example.kmp.ViewModel.KmpViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import static com.example.kmp.Helper.Helper.TRANSITION_TIME;
import static com.example.kmp.Service.PlayerService.ACTION_PLAY_PLAYLIST;

public class DetailAlbumFragment extends Fragment {

    private Album album;
    public static final String ALBUM_TO_DISPLAY_KEY = "album_a_afficher_key";
    private List<Musique> musiqueList;
    private MusicAdapter adapter;
    KmpViewModel model;
    RecyclerView recyclerView;
    private TextView nombreSOng;
    private TextView nomArtiste;
    private ImageView image;
    private LinearLayout playAllButton;

/*    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_album);

        album = (Album)getIntent().getSerializableExtra(ALBUM_TO_DISPLAY_KEY);
        FloatingActionButton fab = findViewById(R.id.floating_button_content_detail_play_all);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                model.getListOfSongToPlay().setValue(musiqueList);
                model.getCurrentPLayingMusic().setValue(musiqueList.get(0));
                model.getPositionOfSongToPLay().setValue(0);
                startService(new Intent(DetailAlbumFragment.this,PlayerServiceOld.class));
            }
        });

        configureRecyclerView();

        iniatiseBottomSheetView();

        configureToolbar();

        configureViewModel();

        if(musiqueList!=null){
            adapter = new MusicAdapter();
            recyclerView.setAdapter(adapter);
        }
    }*/

    public DetailAlbumFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        album = (Album)getArguments().getSerializable(ALBUM_TO_DISPLAY_KEY);
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
        View view = inflater.inflate(R.layout.activity_detail_album, container, false);

        FloatingActionButton fab = view.findViewById(R.id.floating_button_content_detail_play_all);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getContext()).startPlaylist(musiqueList,null,0,true);
            }
        });

        initialiseViews(view);

        configureRecyclerView();

        configureToolbar(view);

        addDataToViews();

        configureAdapter();

        return view;

    }

    private void configureAdapter() {
        if(musiqueList!=null){
            if(adapter!=null){
                adapter.setList(musiqueList);
            }else{
                adapter = new MusicAdapter();
            }

            recyclerView.setAdapter(adapter);
        }
    }

    private void addDataToViews() {
        nomArtiste.setText(album.getNomArtiste());
        String nbSon = "";
        if(album.getNombreMusique()>1){
            nbSon = album.getNombreMusique()+ " "+getString(R.string.songs);
        }else {
            nbSon = album.getNombreMusique() + " "+getString(R.string.song);
        }
        nombreSOng.setText(nbSon);

        Glide
                .with(getContext())
                .load(album.getPochette())
                .centerCrop()
                .placeholder(R.drawable.logo)
                .error(R.drawable.logo)
                .crossFade()
                .into(image);
    }

    private void configureToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ImageView backbutton = view.findViewById(R.id.imageview_backbutton);
        TextView title = view.findViewById(R.id.toolbar_title);

        title.setText(album.getTitreAlbum());
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

    private void initialiseViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerview_simple_list_items);
        nombreSOng = view.findViewById(R.id.textview_activity_detail_nb_song_album);
        nomArtiste = view.findViewById(R.id.textview_activity_detail_artiste_album);
        image = view.findViewById(R.id.imageview_detail_album_image);
    }

    private void configureRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
    }

    private void configureViewModel(){
        model = KmpViewModel.getInstance(getActivity().getApplication(), getContext());
        musiqueList = model.getAllAlbumMusics(getContext(),album).getValue();
        model.getAllAlbumMusics().observe(this, new Observer<List<Musique>>() {
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

    public static DetailAlbumFragment getInstance(Album album){
        DetailAlbumFragment fragment = new DetailAlbumFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ALBUM_TO_DISPLAY_KEY,album);
        fragment.setArguments(bundle);
        return fragment;
    }

    public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusiqueViewHolder> {

        LayoutInflater inflater;

        public MusicAdapter(){
            inflater = LayoutInflater.from(getContext());
        }

        @NonNull
        @Override
        public MusiqueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MusiqueViewHolder(inflater.inflate(R.layout.simple_item,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull MusiqueViewHolder holder, int position) {
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
            DetailAlbumFragment.this.musiqueList = list;
            notifyDataSetChanged();
        }

        public class MusiqueViewHolder extends RecyclerView.ViewHolder{

            private final TextView titreMusique;
            private final TextView artisteMusique;
            private final TextView dureeMusique;

            public MusiqueViewHolder(View itemView){
                super(itemView);
                titreMusique = itemView.findViewById(R.id.textview_simple_item_title);
                artisteMusique = itemView.findViewById(R.id.textview_simple_item_second_text);
                dureeMusique = itemView.findViewById(R.id.textview_simple_item_third_text);
            }


            private void restoreDefaultColor(){
                titreMusique.setTextColor(getResources().getColor(android.R.color.black));
                artisteMusique.setTextColor(getResources().getColor(android.R.color.black));
                itemView.setBackgroundColor(getResources().getColor(android.R.color.white));
            }

            public void bindData(final Musique musique, final int position){
                titreMusique.setText(musique.getTitreMusique().trim());
                artisteMusique.setText(musique.getNomArtiste());
                dureeMusique.setText(Helper.formatDurationToString(musique.getDuration()));

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

}
