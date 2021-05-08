package com.hcmus.photovideoviewer.views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.hcmus.photovideoviewer.R;
import com.hcmus.photovideoviewer.constants.VideoPreferences;
import com.hcmus.photovideoviewer.models.VideoModel;

public class VideoViewActivity extends AppCompatActivity {

	private VideoModel videoModel = null;
	private VideoView videoView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_view);

		Intent intent = getIntent();
		videoModel = intent.getParcelableExtra(VideoPreferences.PARCEL_VIDEO_MODEL);

		videoView = findViewById(R.id.videoView);
		videoView.setVideoURI(videoModel.uri);

		MediaController mediaController = new MediaController(this);
		mediaController.setAnchorView(videoView);

		videoView.setMediaController(mediaController);
		videoView.start();
	}
}