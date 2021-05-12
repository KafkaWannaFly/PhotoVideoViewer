package com.hcmus.photovideoviewer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.hcmus.photovideoviewer.adapters.MainPagerAdapter;
import com.hcmus.photovideoviewer.adapters.SlideShowAdapter;
import com.hcmus.photovideoviewer.constants.PhotoPreferences;
import com.hcmus.photovideoviewer.services.MediaDataRepository;
import com.hcmus.photovideoviewer.viewmodels.AppBarViewModel;
import com.hcmus.photovideoviewer.views.SettingActivity;
import com.hcmus.photovideoviewer.views.SlideShowActivity;

import static android.provider.Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION;

public class MainActivity extends AppCompatActivity
		implements BottomNavigationView.OnNavigationItemSelectedListener {

	public static final int EXTERNAL_PERMISSION_CODE = 1;
	public static final int CAMERA_PERMISSION_CODE = 2;
	public static final AppBarViewModel appBarViewModel = new AppBarViewModel();
	public MediaDataRepository mediaDataRepository = MediaDataRepository.getInstance();
	private ViewPager2 pager = null;
	private BottomNavigationView bottomNavigationView = null;
	private FragmentStateAdapter fragmentStateAdapter = null;
	@RequiresApi(api = Build.VERSION_CODES.R)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		boolean switchPref = prefs.getBoolean("setTheme", false);
		if(switchPref){
			AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
		}else{
			AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
//
//		boolean switchPref = prefs.getBoolean("setTheme", false);
//		if(switchPref){
//			AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//		}
//		else{
//			AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//		}
		//set Theme
		//AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

		// Permissions
		checkManageExternalStoragePermission();

		this.refreshMediaStore();

		// Bottom tabs
		pager = findViewById(R.id.pager);
		fragmentStateAdapter = new MainPagerAdapter(this, appBarViewModel);
		pager.setAdapter(fragmentStateAdapter);

		bottomNavigationView = findViewById(R.id.bottomNavigationView);
		bottomNavigationView.setOnNavigationItemSelectedListener(this);

		pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
			@Override
			public void onPageSelected(int position) {
				super.onPageSelected(position);
				switch (position) {
					case 0: {
						bottomNavigationView.setSelectedItemId(R.id.photoTab);
						break;
					}
					case 1: {
						bottomNavigationView.setSelectedItemId(R.id.videoTab);
						break;
					}
					case 2: {
						bottomNavigationView.setSelectedItemId(R.id.albumTab);
						break;
					}
					case 3: {
						bottomNavigationView.setSelectedItemId(R.id.exploreTab);
						break;
					}
				}
			}
		});

		MaterialToolbar materialToolbar = findViewById(R.id.topToolBar);
		materialToolbar.setOnMenuItemClickListener(item -> {
			if (item.getItemId() == R.id.layoutButton) {
				Integer currentCol = appBarViewModel.liveColumnSpan.getValue();
				if (currentCol == null) {
					return false;
				}

				currentCol = currentCol + 1 > 3 ? 1 : currentCol + 1;
				appBarViewModel.liveColumnSpan.setValue(currentCol);

				if (currentCol == 1) {
					item.setIcon(R.drawable.ic_baseline_view_1_column_24);
				}
				else if (currentCol == 2) {
					item.setIcon(R.drawable.ic_baseline_view_2_column_24);
				}
				else {
					item.setIcon(R.drawable.ic_baseline_view_3_column_24);
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
				Intent intent = new Intent(this, SlideShowActivity.class);
				intent.putExtra(PhotoPreferences.PARCEL_PHOTOS, MediaDataRepository.getInstance().fetchPhotos());
				startActivity(intent);
			}else if(item.getItemId() == R.id.settingButton){
				Intent intent = new Intent(this, SettingActivity.class);
				intent.putExtra("Setting", "");
				startActivity(intent);
			}

			return false;
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		mediaDataRepository.fetchData();
		Log.d("ActivityLife", "MainActivity resume");
	}

	@RequiresApi(api = Build.VERSION_CODES.R)
	private void checkManageExternalStoragePermission() {
		boolean needPermission =
				ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
						!= PackageManager.PERMISSION_GRANTED &&
						ContextCompat.checkSelfPermission(this, Manifest.permission.MANAGE_EXTERNAL_STORAGE)
								!= PackageManager.PERMISSION_GRANTED &&
						ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_MEDIA_LOCATION)
								!= PackageManager.PERMISSION_GRANTED;
		if (needPermission) {
			requestPermissions(new String[]{
					Manifest.permission.READ_EXTERNAL_STORAGE,
					Manifest.permission.WRITE_EXTERNAL_STORAGE,
					Manifest.permission.ACCESS_MEDIA_LOCATION,
					Manifest.permission.MANAGE_EXTERNAL_STORAGE,
			}, EXTERNAL_PERMISSION_CODE);

			if (!Environment.isExternalStorageManager()) {
				this.promptMessageForFileManagePermission();
			}
		}
		else {
			mediaDataRepository.fetchData();
		}
	}

	private void promptMessageForFileManagePermission() {
		String title = "Request for Files access permission";
		String content = "We need All Files access permission to work properly. Please turn it on in your settings";
		new AlertDialog.Builder(this)
				.setTitle(title)
				.setMessage(content)
				.setPositiveButton("OK", (dialog, which) -> {
					Intent intent = new Intent(ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
					this.startActivity(intent);
				})
				.setNegativeButton("NO", null)
				.show();
	}

	public void setFabTakePhotoButton(View v) {
//		this.checkCameraPermission();
		Intent intent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
		startActivity(intent);
	}

	public void setFabTakeVideoButton(View v) {
//		this.checkCameraPermission();
		Intent intent = new Intent(MediaStore.INTENT_ACTION_VIDEO_CAMERA);
		startActivity(intent);
	}

	public void onLayoutChanged(View v) {

	}


	private void refreshMediaStore() {
		MediaScannerConnection.scanFile(this,
				new String[]{Environment.getExternalStorageDirectory().toString()},
				null,
				new MediaScannerConnection.OnScanCompletedListener() {
					public void onScanCompleted(String path, Uri uri) {
						Log.d("ExternalStorage", "Scanned " + path + ":");
						Log.d("ExternalStorage", "-> uri=" + uri);
					}
				});
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		switch (requestCode) {
			case EXTERNAL_PERMISSION_CODE: {
				for (int result : grantResults) {
					if (result != PackageManager.PERMISSION_GRANTED) {
						return;
					}
				}

				mediaDataRepository.fetchData();
			}

			case CAMERA_PERMISSION_CODE: {
				for (int result : grantResults) {
					if (result != PackageManager.PERMISSION_GRANTED) {
						return;
					}
				}

				break;
			}
			default:
				throw new IllegalStateException("Unexpected value: " + requestCode);
		}
	}

	@SuppressLint("NonConstantResourceId")
	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item) {
		boolean returnLater = false;

		int itemId = item.getItemId();
		switch (itemId) {
			case R.id.photoTab: {
				pager.setCurrentItem(0);
				returnLater = true;
				break;
			}
			case R.id.videoTab: {
				pager.setCurrentItem(1);
				returnLater = true;
				break;
			}
			case R.id.albumTab: {
				pager.setCurrentItem(2);
				returnLater = true;
				break;
			}
			case R.id.exploreTab: {
				pager.setCurrentItem(3);
				returnLater = true;
				break;
			}
		}

		return returnLater;
	}
}