package com.hcmus.photovideoviewer;

import android.os.Bundle;

import com.CodeBoy.MediaFacer.MediaFacer;
import com.CodeBoy.MediaFacer.PictureGet;
import com.CodeBoy.MediaFacer.mediaHolders.pictureContent;
import com.CodeBoy.MediaFacer.mediaHolders.pictureFolderContent;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.hcmus.photovideoviewer.ui.main.SectionsPagerAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
		ViewPager viewPager = findViewById(R.id.view_pager);
		viewPager.setAdapter(sectionsPagerAdapter);
		TabLayout tabs = findViewById(R.id.tabs);
		tabs.setupWithViewPager(viewPager);

//		FloatingActionButton fab = findViewById(R.id.fab);
//		fab.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//						.setAction("Action", null).show();
//			}
//		});

		//get all images in the MediaStore.
		ArrayList<pictureContent> allPhotos;

		allPhotos = MediaFacer
				.withPictureContex(MainActivity.this)
				.getAllPictureContents(PictureGet.externalContentUri);
		System.out.println("All IMGs: " + allPhotos.size());

		//get all folders containing pictures
		ArrayList<pictureFolderContent> pictureFolders = new ArrayList<>();
		pictureFolders.addAll(MediaFacer.withPictureContex(MainActivity.this).getPictureFolders());

		//now load images for the first pictureFolderContent object
		pictureFolders.get(0)
				.setPhotos(MediaFacer
						.withPictureContex(MainActivity.this)
						.getAllPictureContentByBucket_id(pictureFolders.get(0).getBucket_id()));
		System.out.println("All Folders Contains IMG: " + pictureFolders.size());
	}
}