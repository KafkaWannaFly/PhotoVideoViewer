package com.hcmus.photovideoviewer.views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.hcmus.photovideoviewer.R;
import com.hcmus.photovideoviewer.adapters.VideoViewAdapter;
import com.hcmus.photovideoviewer.constants.PhotoPreferences;
import com.hcmus.photovideoviewer.models.PhotoModel;
import com.hcmus.photovideoviewer.models.VideoModel;
import com.hcmus.photovideoviewer.services.MediaDataRepository;
import com.hcmus.photovideoviewer.viewmodels.AppBarViewModel;
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
    private Function<VideoModel, Boolean> filterFunc;

    private Integer currentCol = 1;

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        albumName = intent.getStringExtra("albumName");
        if(albumName != null)
        {
            if (albumName.equals("Favourites")) {
                videosViewModel.getLiveVideoModels().setValue(MediaDataRepository.getInstance().fetchVideos());
                this.filterFunc = (videoModel) -> {
                    return !videoModel.isFavorite;
                };
            }
        }
    }

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
            else if (item.getItemId() == R.id.slideShowButton) {
                Intent intentSlide = new Intent(this, SlideShowActivity.class);
                intentSlide.putExtra(PhotoPreferences.PARCEL_PHOTOS, MediaDataRepository.getInstance().fetchPhotos());
                startActivity(intentSlide);
            }
            else if (item.getItemId() == R.id.settingButton) {
                Intent intentSetting = new Intent(this, SettingActivity.class);
                startActivity(intentSetting);
            }
            return false;
        });



        //currentPosition = intent.getIntExtra("currentPosition", 0);
//        photosViewAdapter = new PhotosViewAdapter(this, photoModels);

        //photoModels.addAll(photoModelsFavourites);
        videosViewModel = new VideosViewModel(videoModels);
//        videosViewModel.getLiveVideoModels().setValue(videoModels);
        try {
            videosViewModel.getLiveVideoModels().observe(this, videoActivityModels -> {
                if (filterFunc != null) {
                    videoActivityModels.removeIf(videoModel -> filterFunc.apply(videoModel));
                }
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
                videoViewAdapter = new VideoViewAdapter(recyclerView.getContext(), videoActivityModels);
                recyclerView.setAdapter(videoViewAdapter);
            });

            appBarViewModel.liveColumnSpan.observe(this, columnSpan -> {
                layoutManager = new GridLayoutManager(this, columnSpan);
                recyclerView.setLayoutManager(layoutManager);
            });
        } catch (Exception exception) {
            Log.e("PhotosFragmentException", exception.getMessage());
        }
    }
    public void setFilterFunc(Function<VideoModel, Boolean> filterFunc) {
        this.filterFunc = filterFunc;
    }
}
