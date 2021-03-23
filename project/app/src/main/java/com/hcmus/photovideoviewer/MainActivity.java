package com.hcmus.photovideoviewer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.hcmus.photovideoviewer.adapters.ViewPagerAdapter;
import com.hcmus.photovideoviewer.services.MediaDataRepository;

public class MainActivity extends AppCompatActivity
		implements BottomNavigationView.OnNavigationItemSelectedListener {

	public static final int READ_EXTERNAL_CODE = 1;
	public static final int CAMERA_PERMISSION_CODE = 2;

	private ViewPager2 pager = null;
	private BottomNavigationView bottomNavigationView = null;
	private FragmentStateAdapter fragmentStateAdapter = null;

	public MediaDataRepository mediaDataRepository = null;

//	private FloatingActionsMenu fabMenu = null;
//	private FloatingActionButton fabTakePhotoButton = null;
//	private FloatingActionButton fabTakeVideoButton = null;
//	private View backgroundForFabButton = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);

		// Permissions
		checkReadExternalPermission();

		// Bottom tabs
		pager = findViewById(R.id.pager);
		fragmentStateAdapter = new ViewPagerAdapter(this, mediaDataRepository);
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

//		fabMenu = findViewById(R.id.fabMenuButton);
//		fabTakePhotoButton = findViewById(R.id.fabPhotoCamButton);
//		fabTakeVideoButton = findViewById(R.id.fabVideoCamButton);
//		backgroundForFabButton = findViewById(R.id.backgroundForFabButton);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mediaDataRepository.fetchData();
		Log.d("ActivityLife", "MainActivity resume");
	}

	private void checkReadExternalPermission() {
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
				    != PackageManager.PERMISSION_GRANTED) {
			requestPermissions(new String[]{
					Manifest.permission.READ_EXTERNAL_STORAGE
			}, READ_EXTERNAL_CODE);
		}
		else {
			mediaDataRepository = MediaDataRepository.getInstance();
			mediaDataRepository.fetchData();
		}
	}

	private void checkCameraPermission() {
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
				    != PackageManager.PERMISSION_GRANTED) {
			requestPermissions(new String[]{
					Manifest.permission.CAMERA
			}, CAMERA_PERMISSION_CODE);
		}
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

//	public void setFabMenu(View v) {
//		Log.d("Fab", "Fab menu click");
//		int visibilityValue = Math.abs(backgroundForFabButton.getVisibility() + 4 - 8);
//		backgroundForFabButton.setVisibility(visibilityValue);
//	}
//
//	public void unFocusFabMenu(View v) {
//		backgroundForFabButton.setVisibility(View.INVISIBLE);
//	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		switch (requestCode) {
			case READ_EXTERNAL_CODE: {
				for (int result : grantResults) {
					if (result != PackageManager.PERMISSION_GRANTED) {
						return;
					}
				}

				mediaDataRepository = MediaDataRepository.getInstance();
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