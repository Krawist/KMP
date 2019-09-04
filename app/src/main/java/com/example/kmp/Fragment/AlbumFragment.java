package com.example.kmp.Fragment;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.kmp.Helper.Helper;
import com.example.kmp.Activity.MainActivity;
import com.example.kmp.Modeles.Album;
import com.example.kmp.R;
import com.example.kmp.ViewModel.KmpViewModel;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class AlbumFragment extends Fragment {

    private KmpViewModel model;
    private List<Album> albums;
    private AlbumAdapter adapter;
    private RecyclerView recyclerView;
    public static final String ARTIST_ID = "artiste_a_afficher";
    private int idArtist = 0;


    public AlbumFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.simple_list,container,false);

        recyclerView = view.findViewById(R.id.recyclerview_simple_list_items);
        view.findViewById(R.id.floating_button_simple_list_shuffle_play).setVisibility(View.GONE);

        configureRecyclerView();

        if(albums==null)
            configureModel();


        configureAdapter();

        return view;
    }

    private void configureModel() {
        model = KmpViewModel.getInstance(getActivity().getApplication(), getContext());
        model.getAllAlbums().observe(this, new Observer<List<Album>>() {
            @Override
            public void onChanged(List<Album> albums) {
                AlbumFragment.this.albums = albums;
                configureAdapter();
            }
        });
        albums = model.getAllAlbums().getValue();
    }

    private void configureAdapter() {
        if(albums!=null){
            if(adapter!=null){
                adapter.setalbums(albums);
            }else{
                adapter = new AlbumAdapter();
            }

            recyclerView.setAdapter(adapter);
        }else{
            albums = model.getAllAlbums().getValue();
        }
    }

    private void configureRecyclerView() {
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
    }

    public class AlbumAdapter extends RecyclerView.Adapter<AlbumViewHolder>{

        @NonNull
        @Override
        public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new AlbumViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.album_item,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {
            holder.bindData(albums.get(position), position);
        }

        @Override
        public int getItemCount() {
            if(albums!=null)
                return albums.size();
            else
                return 0;
        }

        public void setalbums(List<Album> albums) {
            AlbumFragment.this.albums = albums;
            notifyDataSetChanged();
        }
    }

    public class AlbumViewHolder extends RecyclerView.ViewHolder{

        private final ImageView image;
        private final TextView nomArtiste;
        private final TextView nomAlbum;

        public AlbumViewHolder(View itemView){
            super(itemView);
            image = itemView.findViewById(R.id.imageview_album_item_image);
            nomArtiste = itemView.findViewById(R.id.textview_album_item_artiste_album);
            nomAlbum = itemView.findViewById(R.id.textview_album_item_titre_album);
        }

        private void bindData(final Album album, final int position){
            Glide.with(getContext())
                    .load(album.getPochette())
                    .error(R.drawable.logo)
                    .error(R.drawable.logo)
                    .crossFade()
                    .centerCrop()
                    .into(image);

            nomAlbum.setText(album.getTitreAlbum());
            nomArtiste.setText(album.getNomArtiste());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity)getContext()).openAlbumDetail(album);
                }
            });

        }
    }

}
