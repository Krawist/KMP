package com.example.kmp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kmp.Activity.MainActivity;
import com.example.kmp.Helper.Helper;
import com.example.kmp.Modeles.Musique;
import com.example.kmp.R;

import java.util.List;

public class AllMusicAdapterWithImage extends RecyclerView.Adapter<AllMusicAdapterWithImage.MusiqueViewHolder> {
    private List<Musique> list;
    private Context context;
    private LayoutInflater inflater;

    public AllMusicAdapterWithImage(Context context, List<Musique> list) {
        inflater = LayoutInflater.from(context);
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public MusiqueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MusiqueViewHolder(inflater.inflate(R.layout.simple_item_with_image_black,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MusiqueViewHolder holder, int position) {
        holder.bindData(list.get(position), position);
    }

    @Override
    public int getItemCount() {
        if(list !=null)
            return list.size();
        else
            return 0;
    }

    public void setList(List<Musique> musiqueList) {
        list = musiqueList;
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

        public void bindData(final Musique musique, final int position){
            titreMusique.setText(musique.getTitreMusique().trim());
            artisteMusique.setText(musique.getNomArtiste() + " * " + musique.getTitreAlbum());
            dureeMusique.setText(Helper.formatDurationToString(musique.getDuration()));
            Helper.loadCircleImage(context,image,musique.getPochette(),40);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity)context).startPlaylist(list,musique,position,false);
                }
            });

            Helper.builMusicItemContextMenu(context,itemView,musique,position);
        }
    }
}
