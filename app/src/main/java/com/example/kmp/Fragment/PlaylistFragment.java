package com.example.kmp.Fragment;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kmp.Modeles.Playlist;
import com.example.kmp.R;
import com.example.kmp.ViewModel.KmpViewModel;

import java.util.List;

import static android.view.View.GONE;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlaylistFragment extends Fragment {


    private KmpViewModel model;
    private List<Playlist> favoriteSong;
    private PlaylistAdapter adapter;
    private RecyclerView recyclerView;
    private TextView holderTextView;


    public PlaylistFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.simple_list,container,false);

        recyclerView = view.findViewById(R.id.recyclerview_simple_list_items);
        holderTextView = view.findViewById(R.id.textview_simple_list_holder);
//        holderTextView.setText("Aucune musique ");

        configureRecyclerView();

        if(favoriteSong ==null)
            configureModel();

        configureAdapter();

        return view;
    }

    private void configureModel() {
        model = KmpViewModel.getInstance(getActivity().getApplication(), getContext());
        /*model.loadPlaylists().observe(this, new Observer<List<Playlist>>() {
            @Override
            public void onChanged(List<Playlist> list) {
                if(list!=null && !list.isEmpty()){
                    changeVisibility(recyclerView, VISIBLE, holderTextView, GONE);
                    configureAdapter();
                }else{
                    changeVisibility(recyclerView,GONE,holderTextView, VISIBLE);
                }
            }
        });*/

        //favoriteSong = model.loadPlaylists().getValue();
    }

    private void changeVisibility(View recyclerview, int visibilite1, View holderTextView, int visibilite2){
        recyclerview.setVisibility(visibilite1);
        holderTextView.setVisibility(visibilite2);
    }

    private void configureAdapter() {
        if(favoriteSong !=null){
            if(adapter!=null){
                adapter.setList(favoriteSong);
            }else{
                adapter = new PlaylistAdapter();
            }

            recyclerView.setAdapter(adapter);
        }else{

            //favoriteSong = model.loadPlaylists().getValue();
        }
    }

    private void configureRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
    }

    public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {

        LayoutInflater inflater;

        @Override
        public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
            holder.bindData(favoriteSong.get(position), position);
        }

        public PlaylistAdapter(){
            inflater = LayoutInflater.from(getContext());
        }

        @NonNull
        @Override
        public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new PlaylistViewHolder(inflater.inflate(R.layout.simple_item,parent,false));
        }

        @Override
        public int getItemCount() {
            if(favoriteSong !=null)
                return favoriteSong.size();
            else
                return 0;
        }

        public void setList(List<Playlist> playlists) {
            PlaylistFragment.this.favoriteSong = playlists;
            notifyDataSetChanged();
        }

        public class PlaylistViewHolder extends RecyclerView.ViewHolder{

            private final TextView titreMusique;
            private final TextView artisteAlbum;
            private final TextView dureeMusique;

            public PlaylistViewHolder(View itemView){
                super(itemView);
                titreMusique = itemView.findViewById(R.id.textview_simple_item_title);
                artisteAlbum = itemView.findViewById(R.id.textview_simple_item_second_text);
                dureeMusique = itemView.findViewById(R.id.textview_simple_item_third_text);
            }

            public void bindData(Playlist playlist, final int position){
                titreMusique.setText(playlist.getNomPlaylist());
                artisteAlbum.setVisibility(GONE);
                dureeMusique.setVisibility(GONE);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
/*                        model.getListOfSongToPlay().setValue(Helper.matchBasicCursorToMusics(favoriteSong));
                        model.getPositionOfSongToPLay().setValue(position);*/
                    }
                });
            }
        }
    }

}
