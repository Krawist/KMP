package com.example.kmp.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kmp.Adapter.ArtistAdapter;
import com.example.kmp.Helper.Helper;
import com.example.kmp.Activity.MainActivity;
import com.example.kmp.Modeles.Artiste;
import com.example.kmp.R;
import com.example.kmp.ViewModel.KmpViewModel;

import java.util.List;

public class ArtistFragment extends Fragment {
    private KmpViewModel model;
    private List<Artiste> artistes;
    private ArtistAdapter adapter;
    private RecyclerView recyclerView;

    public ArtistFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.simple_list,container,false);

        recyclerView = view.findViewById(R.id.recyclerview_simple_list_items);
        view.findViewById(R.id.floating_button_simple_list_shuffle_play).setVisibility(View.GONE);

        configureRecyclerView();

        if(artistes ==null)
            configureModel();

        configureAdapter();

        return view;
    }

    private void configureModel() {
        model = KmpViewModel.getInstance(getActivity().getApplication(), getContext());
        model.getAllArtistes().observe(this, new Observer<List<Artiste>>() {
            @Override
            public void onChanged(List<Artiste> cursor) {
                ArtistFragment.this.artistes = cursor;
                configureAdapter();
            }
        });
        artistes = model.getAllArtistes().getValue();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(getUserVisibleHint() && isVisible() && isResumed() && !((MainActivity)getContext()).isFragmentUnder){
            Artiste artiste = artistes.get(item.getGroupId());
            Helper.handleMusicListContextItemSelected(getContext(),item,model.getAllArtistMusics(getContext(), artiste).getValue());
        }
        return super.onContextItemSelected(item);
    }

    private void configureAdapter() {
        if(artistes !=null){
            if(adapter!=null){
                adapter.setList(artistes);
            }else{
                adapter = new ArtistAdapter(getContext(),artistes);
            }

            recyclerView.setAdapter(adapter);
        }else{
            artistes = model.getAllArtistes().getValue();
        }
    }

    private void configureRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
    }
}
