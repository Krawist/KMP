package com.example.kmp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.kmp.Activity.Details;
import com.example.kmp.Activity.MainActivity;
import com.example.kmp.Modeles.Musique;
import com.example.kmp.Modeles.Playlist;
import com.example.kmp.R;
import com.example.kmp.ViewModel.KmpViewModel;

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
        private final ImageView playlistIcon;
        private final ImageButton playlistPlayAllButton;

        public PlaylistViewHolder(View view){
            super(view);
            playlistName = view.findViewById(R.id.textview_playlist_item_name);
            playlistIcon = view.findViewById(R.id.imageview_playlist_item_image);
            playlistPlayAllButton = view.findViewById(R.id.imagebutton_playlist_item_play_all);
        }

        public void bindData(final Playlist playlist, int position){
            playlistName.setText(playlist.getNomPlaylist());
            Glide.with(context)
                    .load(playlist.getPochette())
                    .error(R.color.colorPrimaryDark)
                    .centerCrop()
                    .into(playlistIcon);

            playlistPlayAllButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<Musique> list = model.getPlaylistSongs(context, playlist).getValue();
                    if(list==null || (list!=null && list.isEmpty())){
                        Toast.makeText(context,context.getString(R.string.playlist_vide),Toast.LENGTH_SHORT).show();
                    }else {
                        //((MainActivity)).startPlaylist(list,list.get(0),0,false);
                    }
                }
            });

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
    }
}
