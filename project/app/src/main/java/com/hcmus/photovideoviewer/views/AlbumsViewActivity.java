package com.hcmus.photovideoviewer.views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hcmus.photovideoviewer.R;
import com.hcmus.photovideoviewer.adapters.PhotosViewAdapter;
import com.hcmus.photovideoviewer.constants.PhotoPreferences;
import com.hcmus.photovideoviewer.models.PhotoModel;
import com.hcmus.photovideoviewer.services.MediaDataRepository;
import com.hcmus.photovideoviewer.viewmodels.AppBarViewModel;
import com.hcmus.photovideoviewer.viewmodels.PhotosFragmentViewModel;

import java.util.ArrayList;
import java.util.function.Function;

public class
AlbumsViewActivity extends AppCompatActivity {
	protected RecyclerView.LayoutManager mLayoutManager;
//	private MutableLiveData<Integer> liveColumnSpan = null;
	Integer currentPosition = null;
	private RecyclerView.LayoutManager layoutManager = null;
	private PhotosFragmentViewModel photosViewModel = null;
	private PhotosViewAdapter photosViewAdapter = null;
	private AppBarViewModel appBarViewModel = null;
	private Function<PhotoModel, Boolean> filterFunc;
	private ArrayList<PhotoModel> photoModels = new ArrayList<>();
	private ArrayList<PhotoModel> photoModelsFavourites = new ArrayList<>();
	private ViewPager2 pager = null;
	private View itemLayoutButton = null;
	private FragmentStateAdapter fragmentStateAdapter = null;
	private BottomNavigationView bottomNavigationView = null;
	//    public  AlbumsViewActivity(AppBarViewModel appBarViewModel) {
//        this.appBarViewModel = appBarViewModel;
//    }
	private Integer currentCol = 1;
	private int spanCount = 1;
	private PhotosFragmentViewModel model;
	private String albumName = "";

	@Override
	public void onStart() {
		super.onStart();

//        photosViewModel.getLivePhotoModels().setValue(MediaDataRepository.getInstance().fetchPhotos());
//
//        this.filterFunc = (photoModel) -> {
//          return !photoModel.isFavorite;
//        };

//		appBarViewModel.liveSortOrder.setValue(appBarViewModel.liveSortOrder.getValue());
		Log.d("ActivityLife", "PhotoFragment start");
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.album_sub_activity);
		RecyclerView recyclerView = (RecyclerView) this.findViewById(R.id.sub_album_recycle_view);

//        appBarViewModel.liveColumnSpan.observe(this, columnSpan -> {
//            recyclerView.setLayoutManager(new GridLayoutManager(this, columnSpan));
//        });
		Intent intent = getIntent();
		photoModels = intent.getParcelableArrayListExtra("photoModels");
		albumName = intent.getStringExtra("albumName");
		photoModelsFavourites = intent.getParcelableArrayListExtra("photoFavoriteData");
		appBarViewModel = new AppBarViewModel();
		MaterialToolbar materialToolbar = findViewById(R.id.topToolBarAlbum);
		materialToolbar.setTitle(albumName);
		materialToolbar.setOnMenuItemClickListener(item -> {
			if (item.getItemId() == R.id.layoutButton) {
				currentCol = appBarViewModel.liveColumnSpan.getValue();
				if (currentCol == null) {
					return false;
				}

				currentCol = currentCol + 1 > 3 ? 1 : currentCol + 1;
				appBarViewModel.liveColumnSpan.setValue(currentCol);

				if (currentCol == 1) {
					item.setIcon(R.drawable.ic_baseline_view_1_column_24);
//                    recyclerView.setLayoutManager(new GridLayoutManager(recyclerView.getContext(),1));
				}
				else if (currentCol == 2) {
					item.setIcon(R.drawable.ic_baseline_view_2_column_24);
//                    recyclerView.setLayoutManager(new GridLayoutManager(recyclerView.getContext(),2));
				}
				else {
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

		photosViewModel = new PhotosFragmentViewModel(photoModels);

		try {
			photosViewModel.getLivePhotoModels().observe(this, photoActivityModels -> {
				if (filterFunc != null) {
					photoActivityModels.removeIf(photoModel -> filterFunc.apply(photoModel));
				}
				appBarViewModel.liveSortOrder.observe(AlbumsViewActivity.this, order -> {
					if (order == 0) {
						photoActivityModels.sort((o1, o2) -> o2.dateModified.compareTo(o1.dateModified));
					}
					else if (order == 1) {
						photoActivityModels.sort((o1, o2) -> o1.dateModified.compareTo(o2.dateModified));
					}

					if (photosViewAdapter != null) {
						photosViewAdapter.notifyDataSetChanged();
					}
				});

				photosViewAdapter = new PhotosViewAdapter(recyclerView.getContext(), photoActivityModels);
				recyclerView.setAdapter(photosViewAdapter);
			});

			appBarViewModel.liveColumnSpan.observe(this, columnSpan -> {
				layoutManager = new GridLayoutManager(this, columnSpan);
				recyclerView.setLayoutManager(layoutManager);
			});
		} catch (Exception exception) {
			Log.e("PhotosFragmentException", exception.getMessage());
		}


	}

	public void setFilterFunc(Function<PhotoModel, Boolean> filterFunc) {
		this.filterFunc = filterFunc;
	}
}