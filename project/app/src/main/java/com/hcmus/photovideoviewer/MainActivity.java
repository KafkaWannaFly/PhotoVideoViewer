package com.hcmus.photovideoviewer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.hcmus.photovideoviewer.models.PhotoModel;
import com.hcmus.photovideoviewer.models.VideoModel;
import com.hcmus.photovideoviewer.services.MediaDataRepository;
import com.hcmus.photovideoviewer.ui.main.SectionsPagerAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
	static final int READ_EXTERNAL_CODE = 1;
	static public boolean EXTERNAL_PERMISSION = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
		ViewPager viewPager = findViewById(R.id.view_pager);
		viewPager.setAdapter(sectionsPagerAdapter);
		TabLayout tabs = findViewById(R.id.tabs);
		tabs.setupWithViewPager(viewPager);

		checkPermission();

		ArrayList<PhotoModel> photoModels = MediaDataRepository.getInstance().getPhotoModels();
		Log.d("Photos", photoModels.toString());
		ArrayList<VideoModel> videoModels = MediaDataRepository.getInstance().getVideoModels();
		Log.d("Videos", videoModels.toString());

//		FloatingActionButton fab = findViewById(R.id.fab);
//		fab.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//						.setAction("Action", null).show();
//			}
//		});
	}

	private void checkPermission() {
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
			!= PackageManager.PERMISSION_GRANTED) {
			requestPermissions(new String[] {
				Manifest.permission.READ_EXTERNAL_STORAGE
			}, READ_EXTERNAL_CODE);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		switch (requestCode) {
			case READ_EXTERNAL_CODE: {
				for (int result: grantResults) {
					if (result != PackageManager.PERMISSION_GRANTED) {
						EXTERNAL_PERMISSION = false;
						return;
					}
				}

				EXTERNAL_PERMISSION = true;
			}
		}
	}
}