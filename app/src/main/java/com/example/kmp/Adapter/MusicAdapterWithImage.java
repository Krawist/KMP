package com.example.kmp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.example.kmp.Service.PlayerService.ACTION_PLAY_PLAYLIST;

public class MusicAdapterWithImage extends RecyclerView.Adapter<MusicAdapterWithImage.MusiqueViewHolder> {
    private final KmpViewModel model;
    private List<Musique> list;
    private Context context;
    private LayoutInflater inflater;
    private Musique playingSong;
    private  int playingSongPosition;
    private int layoutRes;
    private boolean isActionMode = false;
    private List<Musique> checkedMusics = new ArrayList<>();

    public MusicAdapterWithImage(Context context, List<Musique> list, KmpViewModel model) {
        inflater = LayoutInflater.from(context);
        this.list = list;
        this.context = context;
        this.model = model;
    }

    public List<Musique> getCheckedMusics() {
        return checkedMusics;
    }

    public void setActionMode(boolean actionMode) {
        isActionMode = actionMode;
        for(int i=0; i<getItemCount();i++){
            notifyItemChanged(i);
        }

        if(!isActionMode){
            checkedMusics.clear();
        }
    }

    public boolean isActionMode() {
        return isActionMode;
    }

    public MusicAdapterWithImage(Context context, List<Musique> list, KmpViewModel model, Musique playingSong) {
        inflater = LayoutInflater.from(context);
        this.list = list;
        this.context = context;
        this.model = model;
        this.playingSong = playingSong;
    }

    public void setPlayingSong(Musique playingSong){
        this.playingSong = playingSong;
        Musique musique = null;
        for(int i=0; i<list.size(); i++){
             musique= list.get(i);
            if(musique.getIdMusique()==playingSong.getIdMusique()){
                notifyItemChanged(playingSongPosition);
                playingSongPosition = i;
                notifyItemChanged(playingSongPosition);
                break;
            }
        }
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
        private final ImageView icon;
        private final CheckBox checkBox;


        public MusiqueViewHolder(View itemView){
            super(itemView);
            titreMusique = itemView.findViewById(R.id.textview_simple_item_title);
            artisteMusique = itemView.findViewById(R.id.textview_simple_item_second_text);
            dureeMusique = itemView.findViewById(R.id.textview_simple_item_third_text);
            image = itemView.findViewById(R.id.imageview_simple_item_image);
            icon = itemView.findViewById(R.id.imageview_simple_item_is_playing_icon);
            checkBox = itemView.findViewById(R.id.checkbox);
        }

        public void bindData(final Musique musique, final int position){
            titreMusique.setText(musique.getTitreMusique().trim());
            artisteMusique.setText(musique.getNomArtiste() + " * " + musique.getTitreAlbum());
            dureeMusique.setText(Helper.formatDurationToString(musique.getDuration()));
            Helper.loadCircleImage(context,image,musique.getPochette(),50);

            if(isActionMode){
                checkBox.setVisibility(View.VISIBLE);
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked){
                            if(!checkedMusics.contains(musique)){
                                checkedMusics.add(musique);
                            }
                        }else{
                            if(checkedMusics.contains(musique)){
                                checkedMusics.remove(musique);
                            }
                        }
                    }
                });

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkBox.setChecked(!checkBox.isChecked());
                    }
                });

                if(checkedMusics.contains(musique)){
                    checkBox.setChecked(true);
                }else{
                    checkBox.setChecked(false);
                }

            }else {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Helper.startPlaylist(list,musique,position,false, model,context);
                    }
                });

                Musique currentMusic = model.getCurrentPLayingMusic().getValue();
                if(currentMusic!=null){
                    if(currentMusic.getIdMusique() == musique.getIdMusique() ){
                        icon.setVisibility(View.VISIBLE);
                        playingSongPosition = position;
                    }else{
                        icon.setVisibility(View.INVISIBLE);
                    }
                }

                Helper.builMusicItemContextMenu(context,itemView,musique,position);

                checkBox.setVisibility(View.GONE);
            }
        }
    }
}
