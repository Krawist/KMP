package com.example.kmp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kmp.Helper.Helper;
import com.example.kmp.Modeles.Musique;
import com.example.kmp.R;
import com.example.kmp.Service.PlayerService;
import com.example.kmp.ViewModel.KmpViewModel;

import java.util.List;

import static com.example.kmp.Service.PlayerService.ACTION_PLAY_PLAYLIST;

public class MusicAdapterWithImage extends RecyclerView.Adapter<MusicAdapterWithImage.MusiqueViewHolder> {
    private final KmpViewModel model;
    private List<Musique> list;
    private Context context;
    private LayoutInflater inflater;

    public MusicAdapterWithImage(Context context, List<Musique> list, KmpViewModel model) {
        inflater = LayoutInflater.from(context);
        this.list = list;
        this.context = context;
        this.model = model;
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
                    startPlaylist(list,musique,position,false);
                }
            });

            Helper.builMusicItemContextMenu(context,itemView,musique,position);
        }
    }

    public void startPlaylist(List<Musique> list, Musique musique, int position, boolean isShuffle){
        int positionOfStart = position;
/*        if(isShuffle){
            if(bound)
                service.setShuffle();
            positionOfStart = new Random().nextInt(list.size());
        }*/

        model.setPlayingList(list,context);
        model.getPositionOfSongToPLay().setValue(positionOfStart);

        if(list!=null){
            model.getCurrentPLayingMusic().setValue(list.get(positionOfStart));
        }else{
            model.getCurrentPLayingMusic().setValue(null);
        }

        if(model.getCurrentPLayingMusic().getValue()==null ||
                model.getPositionOfSongToPLay().getValue()==null ||
                model.getListOfSongToPlay().getValue() == null){

            Toast.makeText(context, R.string.une_erreur_est_survenue,Toast.LENGTH_SHORT).show();

        }else {
            Intent intent = new Intent(context, PlayerService.class);
            intent.setAction(ACTION_PLAY_PLAYLIST);
            context.startService(intent);
        }
    }
}
