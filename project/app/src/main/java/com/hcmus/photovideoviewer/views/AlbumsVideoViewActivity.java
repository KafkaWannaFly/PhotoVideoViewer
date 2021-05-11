package com.hcmus.photovideoviewer.views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.hcmus.photovideoviewer.R;
import com.hcmus.photovideoviewer.adapters.PhotosViewAdapter;
import com.hcmus.photovideoviewer.adapters.VideoViewAdapter;
import com.hcmus.photovideoviewer.models.PhotoModel;
import com.hcmus.photovideoviewer.models.VideoModel;
import com.hcmus.photovideoviewer.viewmodels.AppBarViewModel;
import com.hcmus.photovideoviewer.viewmodels.PhotosFragmentViewModel;
import com.hcmus.photovideoviewer.viewmodels.VideosViewModel;

import java.util.ArrayList;
import java.util.function.Function;

public class AlbumsVideoViewActivity extends AppCompatActivity {
    private ArrayList<VideoModel> videoModels = new ArrayList<>();
    private String albumName = "";
    private AppBarViewModel appBarViewModel = null;
    private VideosViewModel videosViewModel = null;
    private VideoViewAdapter videoViewAdapter = null;
    private RecyclerView.LayoutManager layoutManager = null;

    private Integer currentCol = 1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_video_activity);
        RecyclerView recyclerView = (RecyclerView)this.findViewById(R.id.video_album_recycle_view);

//        appBarViewModel.liveColumnSpan.observe(this, columnSpan -> {
//            recyclerView.setLayoutManager(new GridLayoutManager(this, columnSpan));
//        });
        Intent intent = getIntent();
        videoModels = intent.getParcelableArrayListExtra("videoModels");
        albumName = intent.getStringExtra("albumName");
        appBarViewModel = new AppBarViewModel();
        MaterialToolbar materialToolbar = findViewById(R.id.topToolBarVideoAlbum);
        materialToolbar.setTitle(albumName);
        materialToolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.layoutButton) {
                currentCol = appBarViewModel.liveColumnSpan.getValue();
                if(currentCol == null) {
                    return false;
                }

                currentCol = currentCol + 1 > 3? 1 : currentCol + 1;
                appBarViewModel.liveColumnSpan.setValue(currentCol);

                if(currentCol == 1) {
                    item.setIcon(R.drawable.ic_baseline_view_1_column_24);
//                    recyclerView.setLayoutManager(new GridLayoutManager(recyclerView.getContext(),1));
                } else if(currentCol == 2) {
                    item.setIcon(R.drawable.ic_baseline_view_2_column_24);
//                    recyclerView.setLayoutManager(new GridLayoutManager(recyclerView.getContext(),2));
                } else {
                    item.setIcon(R.drawable.ic_baseline_view_3_column_24);
//                    recyclerView.setLayoutManager(new GridLayoutManager(recyclerView.getContext(),3));
                }

                return true;
            }
            else if (item.getItemId() == R.id.sortButton) {
                Integer currentOrder = appBarViewModel.liveSortOrder.getValue();
                currentOrder = currentOrder ^ 1;

                item.setIcon(currentOrder == 1 ?
                        R.drawable.ic_baseline_arrow_upward_24 : R.drawable.ic_baseline_arrow_downward_24);

                appBarViewModel.liveSortOrder.setValue(currentOrder);
                return true;
            }
            return false;
        });



        //currentPosition = intent.getIntExtra("currentPosition", 0);
//        photosViewAdapter = new PhotosViewAdapter(this, photoModels);

        //photoModels.addAll(photoModelsFavourites);
        videosViewModel = new VideosViewModel(videoModels);
        videosViewModel.getLiveVideoModels().setValue(videoModels);
        try {
            final androidx.lifecycle.Observer<ArrayList<VideoModel>> liveObserver = new androidx.lifecycle.Observer<ArrayList<VideoModel>>() {
                @Override
                public void onChanged(ArrayList<VideoModel> videoActivityModels) {
                    Log.d("ActivityLife", "PhotoFragment data changed");

                    appBarViewModel.liveSortOrder.observe(AlbumsVideoViewActivity.this, order -> {
                        if (order == 0) {
                            videoActivityModels.sort((o1, o2) -> o2.dateModified.compareTo(o1.dateModified));
                        }
                        else if (order == 1) {
                            videoActivityModels.sort((o1, o2) -> o1.dateModified.compareTo(o2.dateModified));
                        }

                        if (videoViewAdapter != null) {
                            videoViewAdapter.notifyDataSetChanged();
                        }
                    });

                    videoViewAdapter = new VideoViewAdapter(recyclerView.getContext(), videoModels);
                    recyclerView.setAdapter(videoViewAdapter);
                }
            };
            videosViewModel.getLiveVideoModels().observe(this, liveObserver);

            appBarViewModel.liveColumnSpan.observe(this, columnSpan -> {
                layoutManager = new GridLayoutManager(this, columnSpan);
                recyclerView.setLayoutManager(layoutManager);
            });
        } catch (Exception exception) {
            Log.e("PhotosFragmentException", exception.getMessage());
        }
    }

}
