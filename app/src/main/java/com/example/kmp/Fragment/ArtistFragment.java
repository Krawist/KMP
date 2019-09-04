package com.example.kmp.Fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kmp.Helper.Helper;
import com.example.kmp.Activity.MainActivity;
import com.example.kmp.Modeles.Artiste;
import com.example.kmp.R;
import com.example.kmp.ViewModel.KmpViewModel;

import java.util.List;

public class ArtistFragment extends Fragment {
    private KmpViewModel model;
    private List<Artiste> artistes;
    private ArtistAdapter adapter;
    private RecyclerView recyclerView;

    public ArtistFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.simple_list,container,false);

        recyclerView = view.findViewById(R.id.recyclerview_simple_list_items);
        view.findViewById(R.id.floating_button_simple_list_shuffle_play).setVisibility(View.GONE);

        configureRecyclerView();

        if(artistes ==null)
            configureModel();

        configureAdapter();

        return view;
    }

    private void configureModel() {
        model = KmpViewModel.getInstance(getActivity().getApplication(), getContext());
        model.getAllArtistes().observe(this, new Observer<List<Artiste>>() {
            @Override
            public void onChanged(List<Artiste> cursor) {
                ArtistFragment.this.artistes = cursor;
                configureAdapter();
            }
        });
        artistes = model.getAllArtistes().getValue();
    }

    private void configureAdapter() {
        if(artistes !=null){
            if(adapter!=null){
                adapter.setList(artistes);
            }else{
                adapter = new ArtistAdapter();
            }

            recyclerView.setAdapter(adapter);
        }else{
            artistes = model.getAllArtistes().getValue();
        }
    }

    private void configureRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
    }

    public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder> {

        LayoutInflater inflater;

        @Override
        public void onBindViewHolder(@NonNull ArtistViewHolder holder, int position) {
            holder.bindData(artistes.get(position), position);
        }

        public ArtistAdapter(){
            inflater = LayoutInflater.from(getContext());
        }

        @NonNull
        @Override
        public ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ArtistViewHolder(inflater.inflate(R.layout.simple_item,parent,false));
        }

        @Override
        public int getItemCount() {
            if(artistes !=null)
                return artistes.size();
            else
                return 0;
        }

        public void setList(List<Artiste> artistes) {
            ArtistFragment.this.artistes = artistes;
            notifyDataSetChanged();
        }

        public class ArtistViewHolder extends RecyclerView.ViewHolder{

            private final TextView nomArtiste;
            private final TextView details;
            private final TextView dureeMusique;

            public ArtistViewHolder(View itemView){
                super(itemView);
                nomArtiste = itemView.findViewById(R.id.textview_simple_item_title);
                details = itemView.findViewById(R.id.textview_simple_item_second_text);
                dureeMusique = itemView.findViewById(R.id.textview_simple_item_third_text);
                dureeMusique.setVisibility(View.GONE);
            }

            public void bindData(final Artiste artiste, final int position){
                nomArtiste.setText(artiste.getNomArtiste().trim());

                String nombreSongString = "";
                String nombreAlbumString = "";

                if(artiste.getNombreMusique()>1){
                    nombreSongString = artiste.getNombreMusique() +" "+ getString(R.string.songs);
                }else if(artiste.getNombreMusique()>=0)
                    nombreSongString = artiste.getNombreMusique() +" "+ getString(R.string.song);

                if(artiste.getNombreAlbums()>1){
                    nombreAlbumString = artiste.getNombreAlbums() +" "+ getString(R.string.albums);
                }else if(artiste.getNombreAlbums()>=0)
                    nombreAlbumString = artiste.getNombreAlbums() +" "+ getString(R.string.album);

                details.setText(nombreAlbumString+","+nombreSongString);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((MainActivity)getContext()).openArtistDetail(artiste);
                    }
                });
            }
        }
    }
}
