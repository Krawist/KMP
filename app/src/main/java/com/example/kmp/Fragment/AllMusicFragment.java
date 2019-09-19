package com.example.kmp.Fragment;


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
import com.example.kmp.Adapter.AllMusicAdapterWithImage;
import com.example.kmp.Helper.Helper;
import com.example.kmp.Modeles.Musique;
import com.example.kmp.Modeles.ThemeColor;
import com.example.kmp.R;
import com.example.kmp.ViewModel.KmpViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllMusicFragment extends Fragment {

    private KmpViewModel model;
    private List<Musique> musiqueList;
    private AllMusicAdapterWithImage adapter;
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
                adapter = new AllMusicAdapterWithImage(getContext(), musiqueList);
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
}
