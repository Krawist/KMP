package com.example.kmp.Adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.kmp.Activity.Details;
import com.example.kmp.Helper.Helper;
import com.example.kmp.Modeles.Album;
import com.example.kmp.R;

import java.util.ArrayList;
import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>{

    private Context context;
    private List<Album> albums;
    private boolean isActionMode;
    private List<Album> checkedAlbums = new ArrayList<>();
    public AlbumAdapter(Context context, List<Album> albums){
        this.context = context;
        this.albums = albums;
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AlbumViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.album_item,parent,false));
    }

    public boolean isActionMode() {
        return isActionMode;
    }

    public void setActionMode(boolean actionMode) {
        isActionMode = actionMode;
        for (int i=0; i<albums.size(); i++){
            notifyItemChanged(i);
        }

        if(!isActionMode){
            checkedAlbums.clear();
        }
    }

    public List<Album> getCheckedAlbums() {
        return checkedAlbums;
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
        this.albums = albums;
        notifyDataSetChanged();
    }

    public class AlbumViewHolder extends RecyclerView.ViewHolder{

        private final ImageView image;
        private final TextView nomArtiste;
        private final TextView nomAlbum;
        private final CheckBox checkBox;

        public AlbumViewHolder(View itemView){
            super(itemView);
            image = itemView.findViewById(R.id.imageview_album_item_image);
            nomArtiste = itemView.findViewById(R.id.textview_album_item_artiste_album);
            nomAlbum = itemView.findViewById(R.id.textview_album_item_titre_album);
            checkBox = itemView.findViewById(R.id.checkbox);
        }

        private void bindData(final Album album, final int position){
            Glide.with(context)
                    .load(album.getPochette())
                    .error(R.drawable.headphones_black_and_white)
                    .crossFade()
                    .centerCrop()
                    .into(image);

            nomAlbum.setText(album.getTitreAlbum());
            nomArtiste.setText(album.getNomArtiste());

            if(isActionMode()){

                checkBox.setVisibility(View.VISIBLE);

                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked){
                            if(!checkedAlbums.contains(album)){
                                checkedAlbums.add(album);
                            }
                        }else{
                            if(checkedAlbums.contains(album)){
                                checkedAlbums.remove(album);
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

                if(checkedAlbums.contains(album)){
                    checkBox.setChecked(true);
                }else{
                    checkBox.setChecked(false);
                }

            }else{
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, Details.class);
                        intent.putExtra(Details.DETAIL_OF_WHAT_TO_SHOW,Details.ALBUM);
                        intent.putExtra(Details.ALBUM,album);
                        if(Build.VERSION.SDK_INT>=21){
                            Pair<View, String> p1 = Pair.create((View)image,image.getTransitionName());
                            Pair<View, String> p2 = Pair.create((View)nomAlbum,nomAlbum.getTransitionName());
                            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity)context,image,image.getTransitionName());
                            context.startActivity(intent,options.toBundle());
                        }else{
                            context.startActivity(intent);
                        }

                    }
                });

                Helper.buildListMusicContextMenu(context,itemView,position);

                checkBox.setVisibility(View.GONE);
            }
        }
    }
}
