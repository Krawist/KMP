package com.example.kmp.Fragment;


import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Dialog;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kmp.Activity.MainActivity;
import com.example.kmp.Helper.Helper;
import com.example.kmp.Modeles.Favori;
import com.example.kmp.Modeles.Musique;
import com.example.kmp.Modeles.ThemeColor;
import com.example.kmp.R;
import com.example.kmp.ViewModel.KmpViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import static com.example.kmp.Helper.Helper.TRANSITION_TIME;

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

    private void configureModel() {
        model = KmpViewModel.getInstance(getActivity().getApplication(), getContext());
        model.getAllSongs().observe(this, new Observer<List<Musique>>() {
            @Override
            public void onChanged(List<Musique> list) {
                musiqueList = list;
                configureAdapter();
            }
        });
        musiqueList = model.getAllSongs().getValue();

        model.getThemeColor().observe(this, new Observer<ThemeColor>() {
            @Override
            public void onChanged(ThemeColor themeColor) {
                if(adapter!=null)
                    adapter.notifyDataSetChanged();
            }
        });
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

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(getUserVisibleHint()){
            Helper.handleMusicContextItemSelected(getContext(),item,musiqueList);
        }
        return super.onContextItemSelected(item);
    }

    public class AllMusicAdapter extends RecyclerView.Adapter<AllMusicAdapter.MusiqueViewHolder> {

        LayoutInflater inflater;

        public AllMusicAdapter(){
            inflater = LayoutInflater.from(AllMusicFragment.this.getContext());
        }

        @NonNull
        @Override
        public MusiqueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MusiqueViewHolder(inflater.inflate(R.layout.simple_item_with_image_white,parent,false));
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

            private void restoreDefaultColor(){
                titreMusique.setTextColor(getResources().getColor(android.R.color.black));
                artisteMusique.setTextColor(getResources().getColor(android.R.color.black));
                itemView.setBackgroundColor(getResources().getColor(android.R.color.white));
            }

            public void bindData(final Musique musique, final int position){
                titreMusique.setText(musique.getTitreMusique().trim());
                artisteMusique.setText(musique.getNomArtiste() + " * " + musique.getTitreAlbum());
                dureeMusique.setText(Helper.formatDurationToString(musique.getDuration()));
                Helper.loadCircleImage(getContext(),image,musique.getPochette(),40);

                if(model.getCurrentPLayingMusic().getValue()!=null){
                    if(musique.getIdMusique()==model.getCurrentPLayingMusic().getValue().getIdMusique()){
                        ThemeColor themeColor = model.getThemeColor().getValue();
                        if(themeColor!=null){
                            //itemView.setBackgroundColor(themeColor.getBackgroundColor());
                            int previousColor = titreMusique.getHighlightColor();
                            ObjectAnimator animation = ObjectAnimator.ofInt(titreMusique, "textColor",previousColor,  themeColor.getBackgroundColor());
                            animation.setEvaluator(new ArgbEvaluator());
                            animation.setDuration(TRANSITION_TIME);
                            animation.start();

                            animation = ObjectAnimator.ofInt(artisteMusique,"textColor", previousColor, themeColor.getBackgroundColor());
                            animation.setEvaluator(new ArgbEvaluator());
                            animation.setDuration(TRANSITION_TIME);
                            animation.start();
                        }else
                            restoreDefaultColor();
                    }else
                        restoreDefaultColor();

                }else{
                    restoreDefaultColor();
                }


                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((MainActivity)getContext()).startPlaylist(musiqueList,musique,position,false);
                    }
                });

                Helper.builMusicItemContextMenu(getContext(),itemView,musique,position);
            }
        }
    }

}
