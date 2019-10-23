package com.example.kmp.Fragment;


import android.app.Dialog;
import android.content.Context;
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
import com.example.kmp.Adapter.AlbumAdapter;
import com.example.kmp.Helper.Helper;
import com.example.kmp.Activity.MainActivity;
import com.example.kmp.Modeles.Album;
import com.example.kmp.Modeles.Musique;
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
                if(albums!=null){
                    if(AlbumFragment.this.albums.size() != albums.size()){
                        AlbumFragment.this.albums = albums;
                        configureAdapter();
                    }
                }
            }
        });
        albums = model.getAllAlbums().getValue();
    }

    private void configureAdapter() {
        if(albums!=null){
            if(adapter!=null){
                adapter.setalbums(albums);
            }else{
                adapter = new AlbumAdapter(getContext(),albums);
            }

            recyclerView.setAdapter(adapter);
        }else{
            albums = model.getAllAlbums().getValue();
        }
    }

    @Override
    public boolean onContextItemSelected(final MenuItem item) {
        if(getUserVisibleHint() && isVisible() && isResumed() && !((MainActivity)getContext()).isFragmentUnder){
            final Album album = albums.get(item.getGroupId());
            if(item.getItemId()==R.id.action_supprimer){
                final Dialog dialog = Helper.confirmSongsSuppresion(getContext());
                dialog.findViewById(R.id.button_confirmation_validate).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        List<Musique> musiqueList = Helper.matchBasicCursorToMusics(Helper.getAlbumMusic(getContext(),album.getIdAlbum()));
                        if(musiqueList!=null && !musiqueList.isEmpty()){
                            Musique[] musiques = new Musique[musiqueList.size()];
                            for(int i=0; i<musiqueList.size();i++)
                                musiques[i] = musiqueList.get(i);
                            Helper.deleteMusics(getContext(),musiques);
                            albums.remove(item.getGroupId());
                            adapter.notifyItemRemoved(item.getGroupId());
                        }
                    }
                });

                dialog.findViewById(R.id.button_confirmation_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }else
                Helper.handleMusicListContextItemSelected(getContext(),item,model.getAllAlbumMusics(getContext(), album).getValue());
        }
        return super.onContextItemSelected(item);
    }

    private void configureRecyclerView() {
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),Helper.getNumberItmePlatInLine(getActivity())));
    }
}
