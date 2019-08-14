package com.example.kmp.Fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kmp.Activity.MainActivity;
import com.example.kmp.Helper.Helper;
import com.example.kmp.Modeles.Musique;
import com.example.kmp.R;
import com.example.kmp.Service.PlayerService;
import com.example.kmp.ViewModel.KmpViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import static com.example.kmp.Service.PlayerService.ACTION_PLAY_PLAYLIST;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllMusicFragment extends Fragment {

    private KmpViewModel model;
    private List<Musique> musiqueList;
    private AllMusicAdapter adapter;
    private RecyclerView recyclerView;
    private FloatingActionButton playShuffle;

    public AllMusicFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.simple_list,container,false);

        recyclerView = view.findViewById(R.id.recyclerview_simple_list_items);
        playShuffle = view.findViewById(R.id.floating_button_simple_list_shuffle_play);

        playShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getContext()).startPlaylist(musiqueList,null,0,true);
            }
        });

        configureRecyclerView();

        if(musiqueList ==null)
            configureModel();

        configureAdapter();

        return view;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position = item.getGroupId();
        switch (item.getItemId()){

            case R.id.action_add_to_playlist:
                ((MainActivity)getContext()).addToPlaylist(musiqueList.get(position));
                break;

            case R.id.action_play_after_current:
                ((MainActivity)getContext()).playAfterCurrent(musiqueList.get(position));
                break;

            case R.id.action_partager:
                Helper.shareMusics(getContext(),musiqueList.get(position));
                return true;

            case R.id.action_supprimer:
                Helper.deleteMusics(getContext(),musiqueList.get(position));
                return true;

            case R.id.action_details:
                Helper.showDetailsOf(getContext(),musiqueList.get(position));
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private void addToNext(Musique musique) {
        ((MainActivity)getContext()).playAfterCurrent(musique);
    }

    private void configureModel() {
        model = KmpViewModel.getInstance(getActivity().getApplication(), getContext());
        model.getAllSongs().observe(this, new Observer<List<Musique>>() {
            @Override
            public void onChanged(List<Musique> cursor) {
                configureAdapter();
            }
        });
        musiqueList = model.getAllSongs().getValue();
    }

    private void configureAdapter() {
        if(musiqueList !=null){
            if(adapter!=null){
                adapter.setList(musiqueList);
            }else{
                adapter = new AllMusicAdapter();
            }

            recyclerView.setAdapter(adapter);
        }else{
            musiqueList = model.getAllSongs().getValue();
        }
    }

    private void configureRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    public class AllMusicAdapter extends RecyclerView.Adapter<AllMusicAdapter.MusiqueViewHolder> {

        LayoutInflater inflater;

        public AllMusicAdapter(){
            inflater = LayoutInflater.from(AllMusicFragment.this.getContext());
        }

        @NonNull
        @Override
        public MusiqueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MusiqueViewHolder(inflater.inflate(R.layout.simple_item_with_image_black,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull MusiqueViewHolder holder, int position) {
            holder.bindData(musiqueList.get(position), position);
        }

        @Override
        public int getItemCount() {
            if(musiqueList !=null)
                return musiqueList.size();
            else
                return 0;
        }

        public void setList(List<Musique> musiqueList) {
            AllMusicFragment.this.musiqueList = musiqueList;
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
                artisteMusique.setText(musique.getNomArtiste());
                dureeMusique.setText(Helper.formatDurationToString(musique.getDuration()));
                Helper.loadCircleImage(getContext(),image,musique.getPochette(),40);


                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((MainActivity)getContext()).startPlaylist(musiqueList,musique,position,false);
                    }
                });

                itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                    @Override
                    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                        menu.setHeaderTitle(musique.getTitreMusique());
                        menu.add(position,R.id.action_play_after_current,0,getString(R.string.jouer_apres));
                        menu.add(position,R.id.action_add_to_playlist,1,getString(R.string.ajouter_a_la_playlist));
                        menu.add(position,R.id.action_partager,2,getString(R.string.partager));
                        menu.add(position,R.id.action_supprimer,3,getString(R.string.supprimer));
                        menu.add(position,R.id.action_details,4,getString(R.string.details));
                    }
                });
            }
        }
    }

}
