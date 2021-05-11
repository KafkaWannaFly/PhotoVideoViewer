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
import androidx.lifecycle.Observer;
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
import com.hcmus.photovideoviewer.services.MediaDataRepository;
import com.hcmus.photovideoviewer.viewmodels.AlbumsViewModel;
import com.hcmus.photovideoviewer.viewmodels.AppBarViewModel;
import com.hcmus.photovideoviewer.viewmodels.ExploreViewModel;
import com.hcmus.photovideoviewer.viewmodels.PhotosFragmentViewModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class ExploreFragment extends Fragment {
    public static AppBarViewModel appBarViewModel = null;
    private RecyclerView mRecyclerView;
    private ExploreViewModel exploreViewModel;
    private ExploreAdapter exploreAdapter;
    private PhotosFragmentViewModel photosViewModel = null;
    ArrayList<ExploreModel> exploreModels = new ArrayList<ExploreModel>();
    protected AlbumsFragment.LayoutManagerType mCurrentLayoutManagerType;
    protected RecyclerView.LayoutManager mLayoutManager;
    public ExploreFragment(AppBarViewModel appBarViewModel){
        this.appBarViewModel = appBarViewModel;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //exploreViewModel = new ViewModelProvider(this).get(ExploreViewModel.class);
        photosViewModel = new PhotosFragmentViewModel(MediaDataRepository.getInstance().getPhotoModels());
    }
    @Override
    public void onStart() {
        super.onStart();
        photosViewModel.getLivePhotoModels().setValue(MediaDataRepository.getInstance().fetchPhotos());
//		albumModels = MediaDataRepository.getInstance().fetchAlbums(photoModels);
//		CompletableFuture.supplyAsync(() -> {
//			// ABC
//			return 0;
//		}).thenAccept(u -> {
//
//		});
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
        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            mCurrentLayoutManagerType = (AlbumsFragment.LayoutManagerType) savedInstanceState
                    .getSerializable("layoutManager");
        }
        try{
            photosViewModel.getLivePhotoModels().observe(ExploreFragment.this, new Observer<ArrayList<PhotoModel>>() {
                @Override
                public void onChanged(ArrayList<PhotoModel> photoModels) {
                    Log.d("ActivityLife", "AlbumsFragment data changed");
                    exploreModels = MediaDataRepository.getInstance().fetchExplores(photoModels);
//                    albumAdapter = new AlbumAdapter(getContext(), albumModels);
//                    mRecyclerView.setAdapter(albumAdapter);
                    exploreAdapter = new ExploreAdapter(getContext(), exploreModels);
                    mRecyclerView.setAdapter(exploreAdapter);
                }
            });

//            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
//            mRecyclerView.setLayoutManager(layoutManager);
            appBarViewModel.liveColumnSpan.observe(getViewLifecycleOwner(), columnSpan -> {
                mLayoutManager = new GridLayoutManager(getActivity(), columnSpan);
                mRecyclerView.setLayoutManager(mLayoutManager);
            });
        } catch (Exception exception) {
            Log.e("PhotosFragmentException", exception.getMessage());
        }


    }
}
