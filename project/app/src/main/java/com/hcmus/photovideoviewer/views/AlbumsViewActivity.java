package com.hcmus.photovideoviewer.views;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hcmus.photovideoviewer.R;
import com.hcmus.photovideoviewer.adapters.PhotosViewAdapter;
import com.hcmus.photovideoviewer.models.PhotoModel;
import com.hcmus.photovideoviewer.viewmodels.PhotosFragmentViewModel;
import com.hcmus.photovideoviewer.viewmodels.AppBarViewModel;
import java.util.ArrayList;
import java.util.function.Function;

public class AlbumsViewActivity extends AppCompatActivity{
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager = null;
//	private MutableLiveData<Integer> liveColumnSpan = null;

    private PhotosFragmentViewModel photosViewModel = null;
    private PhotosViewAdapter photosViewAdapter = null;

    private AppBarViewModel appBarViewModel = null;

    private Function<PhotoModel, Boolean> filterFunc;
    private ArrayList<PhotoModel> photoModels = new ArrayList<>();
    Integer currentPosition = null;
    private ViewPager2 pager = null;

    protected RecyclerView.LayoutManager mLayoutManager;
    private FragmentStateAdapter fragmentStateAdapter = null;
    private BottomNavigationView bottomNavigationView = null;

    public  AlbumsViewActivity(AppBarViewModel appBarViewModel) {
        this.appBarViewModel = appBarViewModel;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photos_fragment);
        recyclerView = findViewById(R.id.photo_recycle_view);

        appBarViewModel.liveColumnSpan.observe(this, columnSpan -> {
            recyclerView.setLayoutManager(new GridLayoutManager(this, columnSpan));
        });
        Intent intent = getIntent();
        photoModels = intent.getParcelableArrayListExtra("photoModels");
        currentPosition = intent.getIntExtra("currentPosition", 0);
        photosViewAdapter = new PhotosViewAdapter(this, photoModels);
        recyclerView.setAdapter(photosViewAdapter);



    }
    public void setFilterFunc(Function<PhotoModel, Boolean> filterFunc) {
        this.filterFunc = filterFunc;
    }
}