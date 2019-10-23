package com.example.kmp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.kmp.Activity.Details;
import com.example.kmp.Activity.MainActivity;
import com.example.kmp.Helper.Helper;
import com.example.kmp.Modeles.Musique;
import com.example.kmp.Modeles.Playlist;
import com.example.kmp.R;
import com.example.kmp.ViewModel.KmpViewModel;

import java.util.ArrayList;
import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {

    List<Playlist> playlists;
    private Context context;
    KmpViewModel model;

    public PlaylistAdapter(List<Playlist> playlists, Context context, KmpViewModel model){
        this.playlists = playlists;
        this.context = context;
        this.model = model;
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PlaylistViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        holder.bindData(playlists.get(position), position);
    }

    public void setList(List<Playlist> playlists){
        this.playlists = playlists;
    }

    @Override
    public int getItemCount() {
        if(playlists!=null)
            return playlists.size();
        else
            return 0;
    }

    public class PlaylistViewHolder extends RecyclerView.ViewHolder{

        private final TextView playlistName;
        private final ImageButton playlistPlayAllButton;
        private final LinearLayout firstLayout;
        private final LinearLayout secondLayout;
        private final ImageView firstImage;
        private final ImageView secondImage;
        private final ImageView thirdImage;
        private final ImageView fourthImage;

        public PlaylistViewHolder(View view){
            super(view);
            playlistName = view.findViewById(R.id.textview_playlist_item_name);
            playlistPlayAllButton = view.findViewById(R.id.imagebutton_playlist_item_play_all);
            firstLayout = view.findViewById(R.id.layout_playlist_cover_first_block);
            secondLayout = view.findViewById(R.id.layout_playlist_cover_second_block);
            firstImage = view.findViewById(R.id.imageview_playlist_cover_first_image);
            secondImage = view.findViewById(R.id.imageview_playlist_cover_second_image);
            thirdImage = view.findViewById(R.id.imageview_playlist_cover_third_image);
            fourthImage = view.findViewById(R.id.imageview_playlist_cover_fourth_image);
        }

        public void bindData(final Playlist playlist, int position){
            playlistName.setText(playlist.getNomPlaylist());

            playlistPlayAllButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<Musique> list = model.getPlaylistSongs(context, playlist).getValue();
                    if(list==null || (list!=null && list.isEmpty())){
                        Toast.makeText(context,context.getString(R.string.playlist_vide),Toast.LENGTH_SHORT).show();
                    }else {
                        Helper.startPlaylist(list,null,0,false,model,context);
                    }
                }
            });

            buildPlaylistCover(playlist);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, Details.class);
                    intent.putExtra(Details.DETAIL_OF_WHAT_TO_SHOW,Details.PLAYLIST);
                    intent.putExtra(Details.PLAYLIST,playlist);
                    context.startActivity(intent);
                }
            });
        }

        private void buildPlaylistCover(Playlist playlist) {
            if(playlist.getSongsOfPlaylist()!=null && playlist.getSongsOfPlaylist().size()>0){
                List<String> imagePaths = new ArrayList<>();
                for(Musique musique: playlist.getSongsOfPlaylist()){
                    if(!imagePaths.contains(musique.getPochette())){
                        imagePaths.add(musique.getPochette());
                    }
                }
                if(imagePaths.size()>=4){
                    loadImageInView(firstImage,imagePaths.get(0));
                    loadImageInView(secondImage, imagePaths.get(1));
                    loadImageInView(thirdImage,imagePaths.get(2));
                    loadImageInView(fourthImage,imagePaths.get(3));
                }else if(imagePaths.size()==3){
                    loadImageInView(firstImage,imagePaths.get(0));
                    loadImageInView(secondImage, imagePaths.get(1));
                    loadImageInView(thirdImage,imagePaths.get(2));
                    fourthImage.setVisibility(View.GONE);
                }else if(imagePaths.size()==2){
                    loadImageInView(firstImage,imagePaths.get(0));
                    loadImageInView(secondImage, imagePaths.get(1));
                    secondLayout.setVisibility(View.GONE);
                }else if( imagePaths.size()==1){
                    loadImageInView(firstImage,imagePaths.get(0));
                    secondLayout.setVisibility(View.GONE);
                    secondImage.setVisibility(View.GONE);
                }else{
                    secondImage.setVisibility(View.GONE);
                    secondLayout.setVisibility(View.GONE);
                    Glide.with(context)
                            .load(R.color.colorSecondaryDark)
                            .into(firstImage);
                }
            }else{
                secondLayout.setVisibility(View.GONE);
                firstLayout.setVisibility(View.GONE);
            }
        }

        private void loadImageInView(ImageView imageView, String s) {
            Glide.with(context)
                    .load(s)
                    .centerCrop()
                    .error(R.drawable.headphones_black_and_white)
                    .into(imageView);
        }
    }
}
