package com.example.kmp.Fragment;

import android.content.Intent;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.kmp.Activity.MainActivity;
import com.example.kmp.Helper.Helper;
import com.example.kmp.Modeles.Album;
import com.example.kmp.Modeles.Musique;
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
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import static com.example.kmp.Service.PlayerService.ACTION_PLAY;
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

    private DetailAlbumFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        album = (Album)getArguments().getSerializable(ALBUM_TO_DISPLAY_KEY);
        configureViewModel();

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position = item.getGroupId();
        switch (item.getItemId()){

            case R.id.action_add_to_playlist:
                ((MainActivity)getContext()).addToPlaylist(musiqueList.get(position));
                break;

            case R.id.action_play_after_current:
                ((MainActivity)getContext()).playAfterCurrent(musiqueList.get(position));
                break;

            case R.id.action_partager:
                Helper.shareMusics(getContext(),musiqueList.get(position));
                return true;

            case R.id.action_supprimer:
                Helper.deleteMusics(getContext(),musiqueList.get(position));
                return true;

            case R.id.action_details:
                Helper.showDetailsOf(getContext(),musiqueList.get(position));
                return true;
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
        model.getAllAlbumMusics(getContext(), album);
        model.getAllAlbumMusics().observe(this, new Observer<List<Musique>>() {
            @Override
            public void onChanged(List<Musique> musiques) {
                musiqueList = musiques;
                configureAdapter();
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

                itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                    @Override
                    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                        menu.setHeaderTitle(musique.getTitreMusique());
                        menu.add(position,R.id.action_play_after_current,0,getString(R.string.jouer_apres));
                        menu.add(position,R.id.action_add_to_playlist,1,getString(R.string.ajouter_a_la_playlist));
                        menu.add(position,R.id.action_partager,2,getString(R.string.partager));
                        menu.add(position,R.id.action_supprimer,3,getString(R.string.supprimer));
                        menu.add(position,R.id.action_details,4,getString(R.string.details));
                    }
                });
            }
        }
    }

}
