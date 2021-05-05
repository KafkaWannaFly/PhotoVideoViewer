package com.hcmus.photovideoviewer.views;

import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hcmus.photovideoviewer.R;
import com.hcmus.photovideoviewer.adapters.AlbumAdapter;
import com.hcmus.photovideoviewer.adapters.ExploreAdapter;
import com.hcmus.photovideoviewer.models.AlbumModel;
import com.hcmus.photovideoviewer.models.ExploreModel;
import com.hcmus.photovideoviewer.models.PhotoModel;
import com.hcmus.photovideoviewer.viewmodels.AlbumsViewModel;
import com.hcmus.photovideoviewer.viewmodels.AppBarViewModel;
import com.hcmus.photovideoviewer.viewmodels.ExploreViewModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class ExploreFragment extends Fragment {
    public static AppBarViewModel appBarViewModel = null;
    private RecyclerView mRecyclerView;
    private ExploreViewModel exploreViewModel;
    private ExploreAdapter exploreAdapter;
    public ExploreFragment(AppBarViewModel appBarViewModel){
        this.appBarViewModel = appBarViewModel;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        exploreViewModel = new ViewModelProvider(this).get(ExploreViewModel.class);

    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.explore_fragment, container, false);
        mRecyclerView = rootView.findViewById(R.id.recycler_view_people);
        return rootView;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        exploreAdapter = new ExploreAdapter(this.getContext(), exploreViewModel.getExploreModels());
        mRecyclerView.setAdapter(exploreAdapter);
    }
}
