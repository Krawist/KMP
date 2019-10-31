package com.example.kmp.Fragment;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.kmp.Activity.MainActivity;
import com.example.kmp.Adapter.MusicAdapterWithImage;
import com.example.kmp.Helper.Helper;
import com.example.kmp.Modeles.Musique;
import com.example.kmp.Modeles.ThemeColor;
import com.example.kmp.R;
import com.example.kmp.Service.PlayerService;
import com.example.kmp.ViewModel.KmpViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllMusicFragment extends Fragment {

    private KmpViewModel model;
    private List<Musique> musiqueList;
    private MusicAdapterWithImage adapter;
    private RecyclerView recyclerView;
    private FloatingActionButton playShuffle;

    public AllMusicFragment() {
        // Required empty public constructor
    }

    public MusicAdapterWithImage getAdapter() {
        return adapter;
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
                if(list!=null){
                    if(musiqueList.size() != list.size()){
                        musiqueList = list;
                        configureAdapter();
                    }
                }
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

        model.getCurrentPLayingMusic().observe(this, new Observer<Musique>() {
            @Override
            public void onChanged(Musique musique) {
                if(musique!=null && adapter!=null){
                    adapter.setPlayingSong(musique);
                }
            }
        });
    }

    private void configureAdapter() {
        if(musiqueList !=null){
            if(adapter!=null){
                adapter.setList(musiqueList);
            }else{
                adapter = new MusicAdapterWithImage(getContext(), musiqueList, model);
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
    public boolean onContextItemSelected(final MenuItem item) {
        if(getUserVisibleHint()){
            if(item.getItemId()==R.id.action_music_supprimer){
                final int position = item.getGroupId();
                final Dialog dialog = Helper.confirmSongsSuppresion(getContext(), false);
                dialog.findViewById(R.id.button_confirmation_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.findViewById(R.id.button_confirmation_validate).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Helper.deleteMusics(getContext(), musiqueList.get(position));
                        if(model.getCurrentPLayingMusic()!=null && model.getCurrentPLayingMusic().getValue()!=null && model.getCurrentPLayingMusic().getValue().getIdMusique()==musiqueList.get(position).getIdMusique()){
                            Intent intent = new Intent(getContext(), PlayerService.class);
                            intent.setAction(PlayerService.ACTION_SKIP_TO_NEXT);
                            getContext().startService(intent);
                        }
                        musiqueList.remove(position);
                        adapter.notifyItemRemoved(position);
                    }
                });

                dialog.show();
                return true;
            }else{
                return Helper.handleMusicContextItemSelected(getContext(),item,musiqueList, adapter);
            }
        }
        return super.onContextItemSelected(item);
    }
}
