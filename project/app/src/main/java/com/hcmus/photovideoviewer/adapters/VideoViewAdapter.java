package com.hcmus.photovideoviewer.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hcmus.photovideoviewer.R;
import com.hcmus.photovideoviewer.models.VideoModel;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;

public class VideoViewAdapter extends RecyclerView.Adapter<VideoViewAdapter.ViewHolder> {
	private ArrayList<VideoModel> videoModels = null;
	private Context context = null;

	public VideoViewAdapter(Context context, ArrayList<VideoModel> videoModels) {
		this.context = context;
		this.videoModels = videoModels;
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item, parent, false);
		return new ViewHolder(v);
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		ImageView videoView = holder.getVideoView();
		ImageButton playButton = holder.getPlayButton();
		TextView durationText = holder.getDurationText();

		if (position == 0) {
			playButton.setVisibility(View.INVISIBLE);
			playButton.setEnabled(false);

			durationText.setVisibility(View.INVISIBLE);

			Glide.with(context)
					.load(R.drawable.film_cam_icon)
					.placeholder(R.drawable.pussy_cat)
					.into(videoView);
		}
		else {
			VideoModel videoModel = videoModels.get(position-1);

			LocalTime localTime = LocalTime.ofSecondOfDay(videoModel.duration);
			durationText.setText(localTime.format(DateTimeFormatter.ISO_LOCAL_TIME));

			Glide.with(context)
					.load(videoModel.uri)
					.thumbnail(0.1f)
					.placeholder(R.drawable.pussy_cat)
					.into(videoView);
		}


	}

	@Override
	public int getItemCount() {
		return videoModels.size() + 1;
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		private ImageView videoView = null;
		private ImageButton playButton = null;
		private TextView durationText = null;


		public ViewHolder(@NonNull View itemView) {
			super(itemView);
			videoView = itemView.findViewById(R.id.videoView);
			playButton = itemView.findViewById(R.id.playButton);
			durationText = itemView.findViewById(R.id.durationText);
		}

		public ImageButton getPlayButton() {
			return playButton;
		}

		public ImageView getVideoView() {
			return videoView;
		}

		public TextView getDurationText() {
			return durationText;
		}
	}


}
