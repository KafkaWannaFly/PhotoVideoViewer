package com.hcmus.photovideoviewer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.view.MenuItem;

import com.hcmus.photovideoviewer.adapters.ViewPagerAdapter;

public class MainActivity extends AppCompatActivity
		implements BottomNavigationView.OnNavigationItemSelectedListener {
	static final int READ_EXTERNAL_CODE = 1;
	static public boolean EXTERNAL_PERMISSION = false;

	private ViewPager2 pager = null;
	private BottomNavigationView bottomNavigationView = null;
	private FragmentStateAdapter fragmentStateAdapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);

		pager = findViewById(R.id.pager);
		fragmentStateAdapter = new ViewPagerAdapter(this);
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

//		SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
//		ViewPager viewPager = findViewById(R.id.view_pager);
//		viewPager.setAdapter(sectionsPagerAdapter);
//		TabLayout tabs = findViewById(R.id.tabs);
//		tabs.setupWithViewPager(viewPager);

		checkPermission();
	}

	private void checkPermission() {
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
				    != PackageManager.PERMISSION_GRANTED) {
			requestPermissions(new String[]{
					Manifest.permission.READ_EXTERNAL_STORAGE
			}, READ_EXTERNAL_CODE);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		switch (requestCode) {
			case READ_EXTERNAL_CODE: {
				for (int result : grantResults) {
					if (result != PackageManager.PERMISSION_GRANTED) {
						EXTERNAL_PERMISSION = false;
						return;
					}
				}

				EXTERNAL_PERMISSION = true;
			}
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