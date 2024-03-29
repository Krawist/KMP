package com.example.kmp.Helper;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.kmp.Activity.MainActivity;
import com.example.kmp.Adapter.MusicAdapterWithImage;
import com.example.kmp.Modeles.Album;
import com.example.kmp.Modeles.Artiste;
import com.example.kmp.Modeles.MusicEffect;
import com.example.kmp.Modeles.Musique;
import com.example.kmp.Modeles.Playlist;
import com.example.kmp.R;
import com.example.kmp.Service.PlayerService;
import com.example.kmp.ViewModel.KmpViewModel;
import com.google.gson.Gson;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.example.kmp.Service.PlayerService.ACTION_PLAY_PLAYLIST;

public class Helper {

    public static final String[] PERMISSION_ARRAY = {Manifest.permission.READ_EXTERNAL_STORAGE};

    public static final String PREFERENCES_LOOPING_MODE_KEY = "looping_mode";
    public static final String PREFERENCES_SHUFFLE_MODE_KEY = "shuffle_mode";
    public static final String PREFERENCES_LAST_VISITED_PAGED_NUMBER = "derniere_page_ouverte";
    public static final String PREFERENCES_MUSIC_EFFECT_KEY = "music_effect_key";

    public static final String EXTERNAL_CONTENT_URI_VOLUME_NAME = "external";

    public static final int TRANSITION_TIME = 2000;

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

    public static Cursor getAllArtist(Context context){

        String[] projection = {MediaStore.Audio.Artists._ID,
                MediaStore.Audio.Artists.ARTIST,
                MediaStore.Audio.Artists.NUMBER_OF_ALBUMS,
                MediaStore.Audio.Artists.NUMBER_OF_TRACKS};

        return context.getContentResolver().query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, projection, null, null, MediaStore.Audio.Artists.ARTIST);
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

    public static Cursor getAllArtistSongs(Context context, int artisId) {
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

        String selection = MediaStore.Audio.Media.IS_MUSIC + " = ? AND " + MediaStore.Audio.Media.ARTIST_ID + " = ?";

        String[] selectionArgs = {String.valueOf(1), String.valueOf(artisId)};

        return context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projection,selection,selectionArgs,MediaStore.Audio.Media.TRACK);
    }

    public static Cursor getPlaylist(Context context){

        String[] projection = {
                MediaStore.Audio.Playlists._ID,
                MediaStore.Audio.Playlists.NAME
        };

        return context.getContentResolver().query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,projection, null, null, MediaStore.Audio.Playlists.DATE_MODIFIED);
    }

    public static Cursor getPLaysListSongs(Context context, int idPlaylist){
        String[] projection = {MediaStore.Audio.Playlists.Members.AUDIO_ID,
                MediaStore.Audio.Playlists.Members.ALBUM,
                MediaStore.Audio.Playlists.Members.TITLE,
                MediaStore.Audio.Playlists.Members.ALBUM_ID,
                MediaStore.Audio.Playlists.Members.ARTIST,
                MediaStore.Audio.Playlists.Members.ARTIST_ID,
                MediaStore.Audio.Playlists.Members.DURATION,
                MediaStore.Audio.Playlists.Members.DATA,
                MediaStore.Audio.Playlists.Members.TRACK,
                MediaStore.Audio.Playlists.Members.IS_MUSIC,
                MediaStore.Audio.Playlists.Members.SIZE,
                MediaStore.Audio.Playlists.Members.BOOKMARK};

        String selection = MediaStore.Audio.Playlists.Members.IS_MUSIC + " = ? AND "+ MediaStore.Audio.Playlists.Members.PLAYLIST_ID + " = ?";
        String[] selectionARgs = {"1", String.valueOf(idPlaylist)};

        return context.getContentResolver().query(MediaStore.Audio.Playlists.Members.getContentUri(EXTERNAL_CONTENT_URI_VOLUME_NAME, idPlaylist), projection,selection,selectionARgs, MediaStore.Audio.Playlists.Members.DEFAULT_SORT_ORDER);
    }

    public static List<Musique> matchBasicCursorToMusics(Cursor cursor){

        ArrayList<Musique> list = new ArrayList<>();
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        if(cursor!=null){
            cursor.moveToPosition(-1);
            while (cursor.moveToNext()){
                Musique musique= new Musique();
                musique.setIdMusique(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
                musique.setIdAlbum(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
                musique.setIdArtiste(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID)));
                musique.setNomArtiste(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
                musique.setTitreAlbum(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
                musique.setDuration(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
                musique.setParoleMusique("");
                musique.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
                musique.setSize(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE)));
                musique.setTrack(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.TRACK)));
                musique.setTitreMusique(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
                musique.setBookmark(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.BOOKMARK)));

                list.add(musique);
            }
            cursor.close();
        }

        return list;
    }

    public static List<Musique> matchPlaylistCursorToMusics(Cursor cursor){

        ArrayList<Musique> list = new ArrayList<>();
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        if(cursor!=null){
            cursor.moveToPosition(-1);
            while (cursor.moveToNext()){
                Musique musique= new Musique();
                musique.setIdMusique(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.AUDIO_ID)));
                musique.setIdAlbum(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.ALBUM_ID)));
                musique.setIdArtiste(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.ARTIST_ID)));
                musique.setNomArtiste(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.ARTIST)));
                musique.setTitreAlbum(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.ALBUM)));
                musique.setDuration(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.DURATION)));
                musique.setParoleMusique("");
                musique.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.DATA)));
                musique.setSize(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.SIZE)));
                musique.setTrack(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.TRACK)));
                musique.setTitreMusique(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.TITLE)));
                musique.setBookmark(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.BOOKMARK)));
                list.add(musique);
            }
            cursor.close();
        }

        return list;
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
            cursor.close();
            return artiste;

        }
        return null;
    }

    public static List<Playlist> matchCursorToPlaylist(Cursor cursor){
        List<Playlist> playlists = new ArrayList<>();
        if(cursor!=null){
            while (cursor.moveToNext()){
                Playlist playlist = new Playlist();
                playlist.setIdPlaylist(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Playlists._ID)));
                playlist.setNomPlaylist(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.NAME)));
                playlists.add(playlist);
            }
            cursor.close();
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

    public static List<Musique> updateMusic(List<Musique> musiques, Context context){
        if(musiques!=null){
            for(Musique musique: musiques){
                String[] projection = {MediaStore.Audio.Albums.ALBUM_ART, MediaStore.Audio.Albums._ID};
                String[] selectionArgs = {musique.getIdAlbum()+""};
                String selection = MediaStore.Audio.Albums._ID + " = ?";
                Cursor anotherCursor = context.getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,projection,selection,selectionArgs,null);
                if(anotherCursor!=null && anotherCursor.moveToFirst()){
                    musique.setPochette(anotherCursor.getString(anotherCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART)));
                    anotherCursor.close();
                }
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

    public static String getMusicSize(Musique musique) {
        DecimalFormat format = new DecimalFormat("#,##");
        int sizeInOctet = musique.getSize();
        int BASE = 1024;
        if(sizeInOctet>BASE){
            if(sizeInOctet>Math.pow(BASE, 2)){
                if(sizeInOctet>Math.pow(BASE,3)){
                    if(sizeInOctet>Math.pow(BASE,4)){
                        return format.format(sizeInOctet/Math.pow(BASE,4))+" TB";
                    }else
                        return format.format(sizeInOctet/Math.pow(BASE,3))+" GB";
                }else
                    return format.format(sizeInOctet/(Math.pow(BASE,2)))+" MB";
            }else{
                return format.format(sizeInOctet/BASE)+" KB";
            }
        }else
            return sizeInOctet+" O";
    }

    public static String getMusicTrack(Musique musique, Context context) {
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

    public static String getMusicTrackForMusicAlbumDetails(Musique musique) {
        int track = musique.getTrack();
        int MILLE = 1000;
        int DEUX_MILLE = 2000;
        int TROIS_MILLE = 3000;
        int QUATRE_MILLE = 4000;
        int CINQ_MILLE = 5000;

        if(track>=MILLE && track<DEUX_MILLE){
            return track%1000+"";
        }else if(track>=DEUX_MILLE && track<TROIS_MILLE)
            return track%DEUX_MILLE +"";
        else if(track>=TROIS_MILLE && track<QUATRE_MILLE)
            return track%DEUX_MILLE + "";
        else if(track>=QUATRE_MILLE && track<CINQ_MILLE)
            return track%DEUX_MILLE + "";
        else if(track>=CINQ_MILLE && track<6000)
            return track%DEUX_MILLE + "";
        else if(track==0)
            return "-";
        else
            return track+"";
    }

    public static void shareMusics(Context context, Musique... musiques){
        Intent shareIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        ArrayList<Uri> listUris = new ArrayList<>();
        for (int i=0; i<musiques.length; i++) {
            listUris.add(Uri.parse(musiques[i].getPath()));
        }
        shareIntent.setType("file/*");
        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, listUris);
        context.startActivity(Intent.createChooser(shareIntent,context.getString(R.string.partager_via)));
    }

    public static Dialog confirmSongsSuppresion(final Context context, boolean isManySong){

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_confirmation_layout);
        ((TextView)dialog.findViewById(R.id.textview_confirmation_text)).setText(isManySong?context.getString(R.string.supprimer_plusieurs_songs):context.getString(R.string.supprimer_un_song));
        Button cancelButton = dialog.findViewById(R.id.button_confirmation_cancel);
        cancelButton.setText(R.string.annuler);
        dialog.setCanceledOnTouchOutside(false);
        Button validateButton = dialog.findViewById(R.id.button_confirmation_validate);
        validateButton.setText(R.string.supprimer);

        return dialog;
    }

    public static void deleteMusics(Context context, Musique... musiques) {
        KmpViewModel model = KmpViewModel.getInstance(((Activity)context).getApplication(), context);
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
            progress.setText(i+"");
            /* on efface le fichier de la machine */
            File file = new File(musiques[i].getPath());
            file.delete();


             /*on efface le fichier du provider*/
            String where = MediaStore.Audio.Media._ID +" = ?";
            String[] args = {musiques[i].getIdMusique()+""};
            context.getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,where,args);
        }
   /*     model.refreshData(context);*/
        dialog.dismiss();
    }

    public static void addSongToPlaylist(final Context context, final List<Playlist> playlistList, final Musique... musique) {
        if(musique!=null && musique.length>0){
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
                                dialog.dismiss();
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
                                            dialog1.dismiss();
                                            createAndAddSongToPlaylist(context, plalistName, musique);
                                            Toast.makeText(context, context.getString(R.string.ajoute_a)+" "+plalistName, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                                negativeButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog1.dismiss();
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
                                for(int i=0; i<musique.length;i++){
                                    addSongToPlaylist(context, playlistList.get(position), musique[i]);
                                }
                                dialog.dismiss();
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
    }

    public static void loadCircleImage(final Context context, final ImageView image, String imagePath, final int size){
        if(imagePath==null || TextUtils.isEmpty(imagePath)){
            Glide.with(context)
                    .load(R.drawable.headphones_black_and_white)
                    .asBitmap()
                    .error(R.drawable.headphones_black_and_white)
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
        }else{
            Glide.with(context)
                    .load(imagePath)
                    .asBitmap()
                    .error(R.drawable.headphones_black_and_white)
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
    }

    public static void createAndAddSongToPlaylist(Context context, String plalistName, Musique... musiques) {
        Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Audio.Playlists.NAME,plalistName);

        uri = context.getContentResolver().insert(uri,contentValues);
        if(uri!=null){
            Log.e("TAG",uri.toString());
            int playlistId = (int)ContentUris.parseId(uri);
            Playlist newPlaylist = new Playlist();
            newPlaylist.setIdPlaylist(playlistId);
            newPlaylist.setNomPlaylist(plalistName);

            addSongToPlaylist(context,newPlaylist,musiques);
        }
    }

    public static void addSongToPlaylist(Context context, Playlist playlist, Musique... musiques){
        if(musiques!=null && musiques.length>0){
            String[] cols = new String[] {
                    "count ( * )"
            };
            Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external",playlist.getIdPlaylist());
            Cursor cur = context.getContentResolver().query(uri,cols,null,null,null);
            if(cur!=null){
                cur.moveToFirst();
                int base = cur.getInt(0);
                cur.close();

                ContentValues[] contentValues = new ContentValues[musiques.length];

                for(int i=0; i<musiques.length; i++){
                    contentValues[i] = new ContentValues();
                    contentValues[i].put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, Integer.valueOf(base + musiques[i].getIdMusique()));
                    contentValues[i].put(MediaStore.Audio.Playlists.Members.AUDIO_ID, musiques[i].getIdMusique());
                }

                int size = context.getContentResolver().bulkInsert(uri,contentValues);

                if(size>0){
                    Toast.makeText(context,context.getString(R.string.ajoute_a) + " "+playlist.getNomPlaylist(),Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context,context.getString(R.string.erreur_survenue_veuillez_reesayez),Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public static void builMusicItemContextMenu(final Context context, View itemView, final Musique musique, final int position){
        KmpViewModel model = KmpViewModel.getInstance(((Activity)context).getApplication(), context);
        final List<Integer> favorite = model.getFavoriteSongsId().getValue();
        itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.setHeaderTitle(musique.getTitreMusique());
                int i=0;
                menu.add(position,R.id.action_music_play_after,i,context.getString(R.string.jouer_apres));
                i++;
                menu.add(position,R.id.action_music_add_to_playslist,i,context.getString(R.string.ajouter_a_la_playlist));
                i++;
                if(favorite!=null && favorite.contains(musique.getIdMusique())){
                    menu.add(position,R.id.action_music_retirer_des_favoris,i,context.getString(R.string.retirer_des_favoris));
                }else{
                    menu.add(position,R.id.action_music_ajouter_aux_favoris,i,context.getString(R.string.ajouter_aux_favori));
                }
                i++;
                menu.add(position,R.id.action_music_partager,i,context.getString(R.string.partager));
                i++;
                menu.add(position,R.id.action_music_supprimer,i,context.getString(R.string.supprimer));
                i++;
                menu.add(position,R.id.action_music_details,i,context.getString(R.string.details));
            }
        });
    }

    public static void buildListMusicContextMenu(final Context context, View itemView, final int position){
        itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                int i=0;
                menu.add(position,R.id.action_music_list_play_after,i,R.string.lire_juste_apres);
                i++;
                menu.add(position,R.id.action_music_list_add_to_playslist, i, R.string.ajouter_a_la_playlist);
                i++;
                menu.add(position, R.id.action_partager, i, R.string.partager);
                i++;
                menu.add(position,R.id.action_supprimer,i,R.string.supprimer);
            }
        });
    }

    public static boolean handleMusicContextItemSelected(Context context, MenuItem item, List<Musique> musiqueList) {

        int position = item.getGroupId();
        switch (item.getItemId()) {
            case R.id.action_music_play_after:
                ((MainActivity) context).playAfterCurrent(musiqueList.get(position));
                return true;

            case R.id.action_music_add_to_playslist:
                ((MainActivity) context).addToPlaylist(musiqueList.get(position));
                return true;

            case R.id.action_music_partager:
                Helper.shareMusics(context, musiqueList.get(position));
                return true;

            case R.id.action_music_supprimer:
                Helper.confirmSongsSuppresion(context, false);
                return true;

            case R.id.action_music_details:
                Helper.showDetailsOf(context, musiqueList.get(position));
                return true;

            case R.id.action_music_retirer_des_favoris:
                KmpViewModel.getInstance(((Activity) context).getApplication(), context).removeFromFavorite(context, musiqueList.get(position).getIdMusique());
                return true;

            case R.id.action_music_ajouter_aux_favoris:
                KmpViewModel.getInstance(((Activity) context).getApplication(), context).addToFavorite(context,musiqueList.get(position).getIdMusique());
                return true;
        }

        return false;
    }


    public static boolean handleMusicContextItemSelected(final Context context, MenuItem item, final List<Musique> musiqueList, MusicAdapterWithImage adapter) {

        final int position = item.getGroupId();
        switch (item.getItemId()) {
            case R.id.action_music_play_after:
                ((MainActivity) context).playAfterCurrent(musiqueList.get(position));
                return true;

            case R.id.action_music_add_to_playslist:
                ((MainActivity) context).addToPlaylist(musiqueList.get(position));
                return true;

            case R.id.action_music_partager:
                Helper.shareMusics(context, musiqueList.get(position));
                return true;

            case R.id.action_music_supprimer:

                return true;

            case R.id.action_music_details:
                Helper.showDetailsOf(context, musiqueList.get(position));
                return true;

            case R.id.action_music_retirer_des_favoris:
                KmpViewModel.getInstance(((Activity) context).getApplication(), context).removeFromFavorite(context, musiqueList.get(position).getIdMusique());
                return true;

            case R.id.action_music_ajouter_aux_favoris:
                KmpViewModel.getInstance(((Activity) context).getApplication(), context).addToFavorite(context,musiqueList.get(position).getIdMusique());
                return true;
        }

        return false;
    }

    public static boolean handleMusicListContextItemSelected(Context context, MenuItem item, List<Musique> musiqueList) {
        Musique[] musiques = new Musique[musiqueList.size()];
        switch (item.getItemId()) {
            case R.id.action_music_list_play_after:
                ((MainActivity) context).playListAfterCurrent(musiqueList);
                return true;

            case R.id.action_music_list_add_to_playslist:
                ((MainActivity) context).addListToPlaylist(musiqueList);
                return true;

            case R.id.action_partager:
                for(int i=0; i<musiqueList.size();i++)
                    musiques[i] = musiqueList.get(i);
                Helper.shareMusics(context,musiques);
                return true;

            case R.id.action_supprimer:
                for(int i=0; i<musiqueList.size();i++)
                    musiques[i] = musiqueList.get(i);
                Helper.confirmSongsSuppresion(context, true);
                return true;
        }

        return false;
    }

    public static List<Album> matchCursorToAlbums(Cursor cursor){

        ArrayList<Album> list = new ArrayList<>();

        if(cursor!=null){
            while (cursor.moveToNext()){
                Album album = new Album();

                album.setIdAlbum(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Albums._ID)));
                album.setNomArtiste(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST)));
                album.setNombreMusique(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS)));
                album.setTitreAlbum(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM)));
                album.setPochette(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART)));

                list.add(album);
            }
        }

        return list;
    }

    public static List<Artiste> matchCursorToArtists(Cursor cursor){

        List<Artiste> list = new ArrayList<>();

        if(cursor!=null){
            while (cursor.moveToNext()){
                Artiste artiste = new Artiste();

                artiste.setIdArtiste(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Artists._ID)));
                artiste.setNomArtiste(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST)));
                artiste.setNombreAlbums(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS)));
                artiste.setNombreMusique(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_TRACKS)));

                list.add(artiste);
            }
        }

        return list;
    }

    public static Cursor getFavoriteSongs(Context context, List<Integer> favoriList) {
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

        String selection = MediaStore.Audio.Media._ID + " = -1";
        String[] selectionArgs = null;

        if(favoriList!=null && !favoriList.isEmpty()){
            selectionArgs = new String[favoriList.size()];
            for(int i=0;i<favoriList.size(); i++){
                selection = selection + " OR " + MediaStore.Audio.Media._ID + " = ? ";
                selectionArgs[i] = String.valueOf(favoriList.get(i));
            }
        }

        return context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projection,selection,selectionArgs,MediaStore.Audio.Media.TITLE);
    }

    public static List<Album> matchCursorToAlbums(Cursor cursor, HashMap<Integer, String> pathOfSongAlbumArt) {

        ArrayList<Album> list = new ArrayList<>();
        if(cursor!=null){
            if (pathOfSongAlbumArt==null)
                pathOfSongAlbumArt = new HashMap<>();

            while (cursor.moveToNext()){
                Album album = new Album();
                album.setIdAlbum(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Albums._ID)));
                album.setNomArtiste(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST)));
                album.setNombreMusique(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS)));
                album.setTitreAlbum(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM)));
                album.setPochette(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART)));

                pathOfSongAlbumArt.put(album.getIdAlbum(), album.getPochette());

                list.add(album);
            }
        }

        return list;
    }

    public static List<Musique> matchBasicCursorToMusics(Cursor cursor, HashMap<Integer, String> pathOfSongAlbumArt) {

        ArrayList<Musique> list = new ArrayList<>();
        if(cursor!=null){
            cursor.moveToPosition(-1);
            while (cursor.moveToNext()){
                Musique musique= new Musique();
                musique.setIdMusique(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
                musique.setIdAlbum(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
                musique.setIdArtiste(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID)));
                musique.setNomArtiste(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
                musique.setTitreAlbum(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
                musique.setDuration(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
                musique.setParoleMusique("");
                musique.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
                musique.setSize(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE)));
                musique.setTrack(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.TRACK)));
                musique.setTitreMusique(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
                musique.setBookmark(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.BOOKMARK)));

                if(pathOfSongAlbumArt!=null)
                    musique.setPochette(pathOfSongAlbumArt.get(musique.getIdAlbum()));

                list.add(musique);
            }
            cursor.close();
        }

        return list;
    }

    public static List<Musique> matchPlaylistCursorToMusics(Cursor cursor, HashMap<Integer, String> pathOfSongAlbumArt) {

        ArrayList<Musique> list = new ArrayList<>();
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        if(cursor!=null){
            cursor.moveToPosition(-1);
            while (cursor.moveToNext()){
                Musique musique= new Musique();
                musique.setIdMusique(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.AUDIO_ID)));
                musique.setIdAlbum(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.ALBUM_ID)));
                musique.setIdArtiste(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.ARTIST_ID)));
                musique.setNomArtiste(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.ARTIST)));
                musique.setTitreAlbum(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.ALBUM)));
                musique.setDuration(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.DURATION)));
                musique.setParoleMusique("");
                musique.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.DATA)));
                musique.setSize(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.SIZE)));
                musique.setTrack(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.TRACK)));
                musique.setTitreMusique(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.TITLE)));
                musique.setBookmark(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.BOOKMARK)));

                if(pathOfSongAlbumArt!=null)
                    musique.setPochette(pathOfSongAlbumArt.get(musique.getIdAlbum()));

                list.add(musique);
            }
            cursor.close();
        }

        return list;
    }


    public static int getNumberItmePlatInLine(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int with = size.x;

        int oneItemWidth = (int)activity.getResources().getDimension(R.dimen.album_item_size);

        int numberOfItemInLine = with / oneItemWidth;

        return numberOfItemInLine;

    }

    public static MusicEffect getMusicEffect(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String musicEffectInString = sharedPreferences.getString(PREFERENCES_MUSIC_EFFECT_KEY,null);
        if(musicEffectInString!=null){
            MusicEffect musicEffect = new Gson().fromJson(musicEffectInString,MusicEffect.class);
            return musicEffect;
        }else{
            MusicEffect musicEffect = new MusicEffect();
            saveMusicEffect(context, musicEffect);
            return musicEffect;
        }
    }

    public static void saveMusicEffect(Context context, MusicEffect musicEffect) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREFERENCES_MUSIC_EFFECT_KEY,new Gson().toJson(musicEffect))
                .apply();
    }

    public static void startPlaylist(List<Musique> list, Musique musique, int position, boolean isShuffle, KmpViewModel model, Context context){
        int positionOfStart = position;
        if(isShuffle){
            model.setShuffleMode(context,isShuffle);
            positionOfStart = new Random().nextInt(list.size());
        }

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
