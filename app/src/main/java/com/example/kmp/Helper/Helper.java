package com.example.kmp.Helper;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.kmp.Activity.MainActivity;
import com.example.kmp.Modeles.Album;
import com.example.kmp.Modeles.Artiste;
import com.example.kmp.Modeles.Musique;
import com.example.kmp.Modeles.Playlist;
import com.example.kmp.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Helper {

    public static final String[] PERMISSION_ARRAY = {Manifest.permission.READ_EXTERNAL_STORAGE};

    public static final String PREFERENCES_LOOPING_MODE_KEY = "looping_mode";
    public static final String PREFERENCES_SHUFFLE_MODE_KEY = "shuffle_mode";

    public static Cursor getAllMusic(Context context){

        String[] projection = {MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ARTIST_ID,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.TRACK,
                MediaStore.Audio.Media.IS_MUSIC,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.BOOKMARK};

        String selection = MediaStore.Audio.Media.IS_MUSIC + " = ?";

        String[] selectionArgs = {String.valueOf(1)};

        return context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projection,selection,selectionArgs,MediaStore.Audio.Media.TITLE);
    }

    public static Cursor getAllAlbum(Context context){

        String[] projection = {MediaStore.Audio.Albums.ALBUM,
                MediaStore.Audio.Albums._ID,
                MediaStore.Audio.Albums.ARTIST,
                MediaStore.Audio.Albums.ALBUM_ART,
                MediaStore.Audio.Albums.NUMBER_OF_SONGS};


        return context.getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,projection,null,null,MediaStore.Audio.Albums.DEFAULT_SORT_ORDER);
    }

    public static Cursor getArtistSongs(Context context, int artistId){

        String[] projection = {MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ARTIST_ID,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.TRACK,
                MediaStore.Audio.Media.IS_MUSIC,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Playlists.Members.BOOKMARK};

        String selection = MediaStore.Audio.Media.IS_MUSIC + " = ? AND "+ MediaStore.Audio.Media.ARTIST_ID + " = ?";

        String[] selectionArgs = {String.valueOf(1), String.valueOf(artistId)};

        return context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projection,selection,selectionArgs,MediaStore.Audio.Media.TITLE);
    }

    public static Cursor getAllArtist(Context context){

        String[] projection = {MediaStore.Audio.Artists._ID,
                MediaStore.Audio.Artists.ARTIST,
                MediaStore.Audio.Artists.NUMBER_OF_ALBUMS,
                MediaStore.Audio.Artists.NUMBER_OF_TRACKS};

        return context.getContentResolver().query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, projection, null, null, MediaStore.Audio.Artists.ARTIST);

    }

    public static List<Musique> matchCursorToMusics(Cursor cursor){

        ArrayList<Musique> list = new ArrayList<>();
        cursor.moveToPosition(-1);
        while (cursor.moveToNext()){
            Musique musique= new Musique();
            musique.setIdMusique(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
            musique.setIdAlbum(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
            musique.setIdArtiste(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID)));
            musique.setNomArtiste(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
            musique.setTitreAlbum(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
            musique.setDuration(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
            musique.setLiked(false);
            musique.setParoleMusique("");
            musique.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
            musique.setPochette("");
            musique.setSize(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE)));
            musique.setTrack(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.TRACK)));
            musique.setTitreMusique(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
            musique.setBookmark(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.BOOKMARK)));

            list.add(musique);
        }

        return list;
    }

    public static List<Album> matchCursorToAlbums(Cursor cursor){

        ArrayList<Album> list = new ArrayList<>();

        while (cursor.moveToNext()){
            Album album = new Album();

            album.setIdAlbum(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Albums._ID)));
            album.setNomArtiste(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST)));
            album.setNombreMusique(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS)));
            album.setTitreAlbum(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM)));
            album.setPochette(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART)));

            list.add(album);
        }

        return list;
    }

    public static List<Artiste> matchCursorToArtists(Cursor cursor){

        List<Artiste> list = new ArrayList<>();

        while (cursor.moveToNext()){
            Artiste artiste = new Artiste();

            artiste.setIdArtiste(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Artists._ID)));
            artiste.setNomArtiste(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST)));
            artiste.setNombreAlbums(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS)));
            artiste.setNombreMusique(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_TRACKS)));

            list.add(artiste);
        }

        return list;
    }

    public static Musique matchCursorToSpecificMusic(Cursor cursor, int position){

        if(cursor!=null){
            Musique musique= new Musique();
            if(cursor.moveToPosition(position)){
                musique.setIdMusique(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
                musique.setIdAlbum(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
                musique.setIdArtiste(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID)));
                musique.setNomArtiste(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
                musique.setTitreAlbum(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
                musique.setDuration(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
                musique.setLiked(false);
                musique.setParoleMusique("");
                musique.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
                musique.setPochette("");
                musique.setSize(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE)));
                musique.setTrack(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.TRACK)));
                musique.setTitreMusique(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
            }

            return musique;
        }

        return null;
    }

    public static Album matchCursorToSpecificAlbum(Cursor cursor, int position){

        if (cursor!=null){
            Album album = new Album();
            if(cursor.moveToPosition(position)){
                album.setIdAlbum(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Albums._ID)));
                album.setNomArtiste(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST)));
                album.setNombreMusique(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS)));
                album.setTitreAlbum(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM)));
                album.setPochette(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART)));
            }
           return album;
        }

        return null;
    }

    public static Artiste matchCursorToSpecificArtist(Cursor cursor, int positon){

        if(cursor!=null) {
            Artiste artiste = new Artiste();
            if (cursor.moveToPosition(positon)) {
                artiste.setIdArtiste(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Artists._ID)));
                artiste.setNomArtiste(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST)));
                artiste.setNombreAlbums(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS)));
                artiste.setNombreMusique(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_TRACKS)));
            }
            return artiste;
        }
        return null;
    }

    public static List<Playlist> matchCursorToPlaylist(Cursor cursor){
        List<Playlist> playlists = new ArrayList<>();
        if(cursor.moveToPosition(-1)){
            while (cursor.moveToNext()){
                Playlist playlist = new Playlist();
                playlist.setIdPlaylist(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Playlists._ID)));
                playlist.setNomPlaylist(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.NAME)));

                playlists.add(playlist);
            }
        }

        return playlists;
    }

    public static String formatDurationToString(int duration) {

        long hour = TimeUnit.MILLISECONDS.toHours(duration);
        long minute = TimeUnit.MILLISECONDS.toMinutes(duration)-hour*60;
        long second = TimeUnit.MILLISECONDS.toSeconds(duration)-minute*60;

        String time = "";

        if(second<10)
            time = "0"+second;
        else
            time = String.valueOf(second);

        if(minute<10)
            time =  "0"+minute+":"+time;
        else
            time =  minute+":" + time;

        if(hour<10 && hour>0){
            time = "0"+hour+":"+time;
        }else if(hour>=10){
            time = hour+":"+time;
        }

        return time;
    }

    public static Cursor getAlbumMusic(Context context, int albumId){

        String[] projection = {MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ARTIST_ID,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.TRACK,
                MediaStore.Audio.Media.IS_MUSIC,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.BOOKMARK};

        String selection = MediaStore.Audio.Media.IS_MUSIC + " = ? AND " + MediaStore.Audio.Media.ALBUM_ID + " = ?";

        String[] selectionArgs = {String.valueOf(1), String.valueOf(albumId)};

        return context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projection,selection,selectionArgs,MediaStore.Audio.Media.TRACK);
    }

    public static List<Musique> updateMusic(List<Musique> musiques, Context context){
        for(Musique musique: musiques){
            String[] projection = {MediaStore.Audio.Albums.ALBUM_ART, MediaStore.Audio.Albums._ID};
            String[] selectionArgs = {musique.getIdAlbum()+""};
            //Log.e("Krawist"," Album Id "+musique.getAlbumId());
            String selection = MediaStore.Audio.Albums._ID + " = ?";
            Cursor anotherCursor = context.getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,projection,selection,selectionArgs,null);
            if(anotherCursor!=null && anotherCursor.moveToFirst()){
                musique.setPochette(anotherCursor.getString(anotherCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART)));
                musique.setLiked(true);
            }
        }

        return musiques;
    }

    public static void showDetailsOf(Context context, Musique musique) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_musique_details_layout);
        String espace = ": ";
        TextView titreAlbum = dialog.findViewById(R.id.textview_dialog_music_details_titre_musique);
        TextView nomAlbum = dialog.findViewById(R.id.textview_dialog_music_details_nom_album);
        TextView nomArtiste = dialog.findViewById(R.id.textview_dialog_music_details_nom_artiste);
        //TextView genre = dialog.findViewById(R.id.textview_dialog_music_details_genre_musique);
        TextView track = dialog.findViewById(R.id.textview_dialog_music_details_track_musique);
        TextView taille = dialog.findViewById(R.id.textview_dialog_music_details_taille_musique);
        TextView duree = dialog.findViewById(R.id.textview_dialog_music_details_duree_musique);
        TextView localisation = dialog.findViewById(R.id.textview_dialog_music_details_localisation_musique);
        Button buttonFermer = dialog.findViewById(R.id.button_dialog_detail_musique_button_fermer);
        
        buttonFermer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        
        titreAlbum.setText(espace + musique.getTitreMusique().trim());
        nomAlbum.setText(espace + musique.getTitreAlbum());
        nomArtiste.setText(espace + musique.getNomArtiste());
        track.setText(espace + getMusicTrack(musique, context));
        taille.setText(espace + getMusicSize(musique));
        duree.setText(espace + formatDurationToString(musique.getDuration()));
        localisation.setText(espace + musique.getPath());

        dialog.show();
        
    }

    private static String getMusicSize(Musique musique) {
        int sizeInOctet = musique.getSize();
        int BASE = 1024;
        if(sizeInOctet>BASE){
            if(sizeInOctet>Math.pow(BASE, 2)){
                if(sizeInOctet>Math.pow(BASE,3)){
                    if(sizeInOctet>Math.pow(BASE,4)){
                        return sizeInOctet/Math.pow(BASE,4)+"TB";
                    }else
                        return sizeInOctet/Math.pow(BASE,3)+"GB";
                }else
                    return sizeInOctet/(Math.pow(1024,2))+"MB";
            }else{
                return sizeInOctet/BASE+"KB";
            }
        }else
            return sizeInOctet+"O";


    }

    private static String getMusicTrack(Musique musique, Context context) {
        int track = musique.getTrack();
        int MILLE = 1000;
        int DEUX_MILLE = 2000;
        int TROIS_MILLE = 3000;
        int QUATRE_MILLE = 4000;
        int CINQ_MILLE = 5000;
        
        if(track>=MILLE && track<DEUX_MILLE){
            return track%1000+"";
        }else if(track>=DEUX_MILLE && track<TROIS_MILLE)
            return track%DEUX_MILLE + context.getString(R.string.disque) + " 2";
        else if(track>=TROIS_MILLE && track<QUATRE_MILLE)
            return track%DEUX_MILLE + context.getString(R.string.disque) + " 3";
        else if(track>=QUATRE_MILLE && track<CINQ_MILLE)
            return track%DEUX_MILLE + context.getString(R.string.disque) + " 4";
        else if(track>=CINQ_MILLE && track<6000)
            return track%DEUX_MILLE + context.getString(R.string.disque) + " 5";
        else
            return track+"";
    }

    public static Cursor getPlaylist(Context context){

        String[] projection = {
                MediaStore.Audio.Playlists._ID,
                MediaStore.Audio.Playlists.NAME
            };

        return context.getContentResolver().query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,projection, null, null, MediaStore.Audio.Playlists.DEFAULT_SORT_ORDER);
    }

    public static Cursor getPLaysListSongs(Context context, int plalistId){
        String[] projection = {
                MediaStore.Audio.Playlists.Members.AUDIO_ID,
                MediaStore.Audio.Playlists.Members.ARTIST_ID,
                MediaStore.Audio.Playlists.Members.ALBUM_ID,
                MediaStore.Audio.Playlists.Members.ARTIST,
                MediaStore.Audio.Playlists.Members.ALBUM,
                MediaStore.Audio.Playlists.Members.DATA,
                MediaStore.Audio.Playlists.Members.SIZE,
                MediaStore.Audio.Playlists.Members.IS_MUSIC,
                MediaStore.Audio.Playlists.Members.TRACK,
                MediaStore.Audio.Playlists.Members.DURATION,
                MediaStore.Audio.Playlists.Members.TITLE,
                MediaStore.Audio.Playlists.Members.PLAYLIST_ID,
                MediaStore.Audio.Playlists.Members.BOOKMARK

        };

        String selection = MediaStore.Audio.Playlists.Members.PLAYLIST_ID + " = ? AND "+ MediaStore.Audio.Playlists.Members.IS_MUSIC + " = ?";
        String[] selectionARgs = {String.valueOf(plalistId), "1"};

        return context.getContentResolver().query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, projection,selection,selectionARgs, MediaStore.Audio.Playlists.Members.DEFAULT_SORT_ORDER);
    }

    public static void shareMusics(Context context, Musique... musiques){
        Intent shareIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        ArrayList<Uri> listUris = new ArrayList<>();
        for (int i=0; i<musiques.length; i++) {
            listUris.add(Uri.parse(musiques[i].getPath()));
        }
        shareIntent.setType("music/*");
        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, listUris);
        context.startActivity(shareIntent);
    }

    public static void deleteMusics(Context context, Musique... musiques){

        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_waiting_process_with_progress_layout);
        ProgressBar progressBar = dialog.findViewById(R.id.progress_horizontal);
        TextView progress = dialog.findViewById(R.id.textview_progress);
        TextView progressLimit = dialog.findViewById(R.id.textview_progress_limit);
        progressLimit.setText(musiques.length+"");
        progressBar.setMax(musiques.length);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        for(int i=0; i<musiques.length; i++){
            File file = new File(musiques[i].getPath());
            file.delete();
            progress.setText(i+"");
            ContentValues contentValues  = new ContentValues();
            String where = MediaStore.Audio.Media._ID +" = ?";
            String[] args = {musiques[i].getIdMusique()+""};
            context.getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,where,args);
        }

        dialog.dismiss();
    }

    public static Dialog buildCOnfirmationDialog(Context context, String text,
                                                 String cancelMessage,
                                                 String validateMessage,
                                                 View.OnClickListener cancelButtonEvent,
                                                 View.OnClickListener validateClicEvent,
                                                 boolean cancelOnTouchOutside){
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_confirmation_layout);
        ((TextView)dialog.findViewById(R.id.textview_confirmation_text)).setText(text);
        Button cancelButton = dialog.findViewById(R.id.button_confirmation_cancel);
        cancelButton.setText(cancelMessage);
        cancelButton.setOnClickListener(cancelButtonEvent);
        Button validateButton = dialog.findViewById(R.id.button_confirmation_validate);
        validateButton.setText(validateMessage);
        validateButton.setOnClickListener(validateClicEvent);

        dialog.setCanceledOnTouchOutside(cancelOnTouchOutside);

        return dialog;
    }

    public static void addSongToPlaylist(final Musique musique, final Context context, final List<Playlist> playlistList) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_list_layout);
        RecyclerView list = dialog.findViewById(R.id.recyclerview_dialog_list_);
        dialog.setCanceledOnTouchOutside(false);
        RecyclerView.Adapter<BasicViewHolder> adapter = new RecyclerView.Adapter<BasicViewHolder>() {
            @NonNull
            @Override
            public BasicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
                return new BasicViewHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull BasicViewHolder holder, final int position) {

                TextView textView = holder.getTextView();
                if(position==playlistList.size()){
                    textView.setText(context.getString(R.string.nouvelle_playlist));

                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            final Dialog dialog1 = new Dialog(context);
                            dialog1.setContentView(R.layout.dialog_with_one_textfied_layout);
                            final EditText editText = dialog1.findViewById(R.id.editext_dialog_with_one_field_edittext);
                            Button positiveButton = dialog1.findViewById(R.id.button_dialog_positive_action);
                            Button negativeButton = dialog1.findViewById(R.id.button_dialog_negative_action);
                            positiveButton.setText(context.getString(R.string.valider));
                            negativeButton.setText(context.getString(R.string.annuler));
                            dialog1.setCanceledOnTouchOutside(false);
                            positiveButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String plalistName = editText.getText().toString();
                                    if(TextUtils.isEmpty(plalistName)){
                                        Toast.makeText(context,context.getString(R.string.donner_un_nom_a_la_playlist),Toast.LENGTH_LONG).show();
                                    }else{
                                        createAndAddSongToPlaylist(context, musique, plalistName);
                                        dialog1.dismiss();
                                        dialog.dismiss();
                                    }
                                }
                            });

                            negativeButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog1.dismiss();
                                    dialog.dismiss();
                                }
                            });

                            dialog1.show();


                        }
                    });
                }else{
                    textView.setText(playlistList.get(position).getNomPlaylist());
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            addSongToPlaylist(context, musique, playlistList.get(position));
                        }
                    });
                }
            }

            @Override
            public int getItemCount() {
                if(playlistList!=null)
                    return playlistList.size() + 1;
                else 
                    return 1;
            }
        };

        Button positiveAction = dialog.findViewById(R.id.button_dialog_positive_action);
        Button negativeAction = dialog.findViewById(R.id.button_dialog_negative_action);

        positiveAction.setVisibility(View.GONE);
        negativeAction.setText(context.getString(R.string.annuler));

        negativeAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        list.setLayoutManager(new LinearLayoutManager(context));
        list.addItemDecoration(new DividerItemDecoration(context,DividerItemDecoration.VERTICAL));
        list.setAdapter(adapter);

        dialog.show();
    }

    public static void loadCircleImage(final Context context, final ImageView image, String imagePath, final int size){
        Glide.with(context)
                .load(imagePath)
                .asBitmap()
                .error(R.drawable.logo)
                .placeholder(R.drawable.logo)
                .centerCrop()
                .into(new BitmapImageViewTarget(image){
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(context.getResources(),
                                Bitmap.createScaledBitmap(resource,size,size,false));
                        drawable.setCircular(true);
                        image.setImageDrawable(drawable);
                    }
                });
    }

    public static void createAndAddSongToPlaylist(Context context, Musique musique, String plalistName) {
        
    }

    public static void addSongToPlaylist(Context context, Musique musique, Playlist playlist){
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Audio.Playlists.Members.PLAYLIST_ID, playlist.getIdPlaylist());
        contentValues.put(MediaStore.Audio.Playlists.Members.ALBUM_ID, musique.getIdAlbum());
        contentValues.put(MediaStore.Audio.Playlists.Members.ALBUM, musique.getTitreAlbum());
        contentValues.put(MediaStore.Audio.Playlists.Members.ARTIST, musique.getNomArtiste());
        contentValues.put(MediaStore.Audio.Playlists.Members.ARTIST_ID, musique.getIdArtiste());
        contentValues.put(MediaStore.Audio.Playlists.Members.TITLE, musique.getTitreMusique());
        contentValues.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, musique.getIdMusique());

        //context.getContentResolver().insert(MediaStore.Audio.Playlists.Members.)
    }
    
}
