package com.example.kmp.Fragment;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kmp.Activity.MainActivity;
import com.example.kmp.Adapter.MusicAdapterWithImage;
import com.example.kmp.Helper.Helper;
import com.example.kmp.Modeles.Artiste;
import com.example.kmp.Modeles.Musique;
import com.example.kmp.Modeles.ThemeColor;
import com.example.kmp.R;
import com.example.kmp.ViewModel.KmpViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArtistDetailFragment extends Fragment {

    private Artiste artiste = null;
    KmpViewModel model;
    private List<Musique> musiqueList;
    private RecyclerView recyclerView;
    private MusicAdapterWithImage adapter;
    private static final String ARTIST_TO_DISPLAY = "artiste_a_afficger";

    public ArtistDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle!=null){
            if(bundle.getSerializable(ARTIST_TO_DISPLAY)!=null){
                artiste = (Artiste)bundle.getSerializable(ARTIST_TO_DISPLAY);
            }
        }
        configureViewModel();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(getUserVisibleHint()){
            Helper.handleMusicContextItemSelected(getContext(),item,musiqueList);
        }
        return super.onContextItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_artiste_layout, container, false);

        FloatingActionButton fab = view.findViewById(R.id.floating_button_content_detail_play_all);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getContext()).startPlaylist(musiqueList,null,0,true);
            }
        });

        configureToolbar(view);

        initialiseViews(view);

        configureRecyclerView();

        configureAdapter();

        return view;

    }

    private void configureAdapter() {
        if(musiqueList!=null){
            if(adapter!=null)
                adapter.setList(musiqueList);
            else
                adapter = new MusicAdapterWithImage(getContext(), musiqueList, null);

            recyclerView.setAdapter(adapter);
        }
    }

    private void configureToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);

        ImageView backbutton = view.findViewById(R.id.imageview_backbutton);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        ((TextView)view.findViewById(R.id.toolbar_title)).setText(artiste.getNomArtiste());
    }

    private void initialiseViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerview_simple_list_items);
    }

    private void configureRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void configureViewModel(){
        model = KmpViewModel.getInstance(getActivity().getApplication(), getContext());
        model.getAllArtistMusics(getContext(),artiste);
        model.getAllArtistMusics().observe(this, new Observer<List<Musique>>() {
            @Override
            public void onChanged(List<Musique> musiques) {
                musiqueList = musiques;
                configureAdapter();
            }
        });

        model.getThemeColor().observe(this, new Observer<ThemeColor>() {
            @Override
            public void onChanged(ThemeColor themeColor) {
                if(adapter!=null)
                    adapter.notifyDataSetChanged();
            }
        });
    }

    public static ArtistDetailFragment getInstance(Artiste artiste){
        ArtistDetailFragment artistDetailFragment = new ArtistDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARTIST_TO_DISPLAY, artiste);
        artistDetailFragment.setArguments(bundle);
        return artistDetailFragment;
    }

}
