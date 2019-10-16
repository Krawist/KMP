package com.example.kmp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kmp.Activity.Details;
import com.example.kmp.Activity.MainActivity;
import com.example.kmp.Fragment.ArtistFragment;
import com.example.kmp.Helper.Helper;
import com.example.kmp.Modeles.Artiste;
import com.example.kmp.R;

import java.util.List;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder> {

    private Context context;
    private List<Artiste> artistes;

    public ArtistAdapter(Context context, List<Artiste> artistes) {
        this.context = context;
        this.artistes = artistes;
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistViewHolder holder, int position) {
        holder.bindData(artistes.get(position), position);
    }

    @NonNull
    @Override
    public ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ArtistViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_item,parent,false));
    }

    @Override
    public int getItemCount() {
        if(artistes !=null)
            return artistes.size();
        else
            return 0;
    }

    public void setList(List<Artiste> artistes) {
        this.artistes = artistes;
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
                nombreSongString = artiste.getNombreMusique() +" "+ context.getString(R.string.songs);
            }else if(artiste.getNombreMusique()>=0)
                nombreSongString = artiste.getNombreMusique() +" "+ context.getString(R.string.song);

            if(artiste.getNombreAlbums()>1){
                nombreAlbumString = artiste.getNombreAlbums() +" "+ context.getString(R.string.albums);
            }else if(artiste.getNombreAlbums()>=0)
                nombreAlbumString = artiste.getNombreAlbums() +" "+ context.getString(R.string.album);

            details.setText(nombreAlbumString+","+nombreSongString);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, Details.class);
                    intent.putExtra(Details.DETAIL_OF_WHAT_TO_SHOW,Details.ARTISTE);
                    intent.putExtra(Details.ARTISTE,artiste);
                    context.startActivity(intent);
                }
            });

            Helper.buildListMusicContextMenu(context,itemView,position);
        }
    }
}
