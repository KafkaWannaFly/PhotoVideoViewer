package com.hcmus.photovideoviewer.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hcmus.photovideoviewer.R;
import com.hcmus.photovideoviewer.constants.VideoPreferences;
import com.hcmus.photovideoviewer.models.VideoModel;
import com.hcmus.photovideoviewer.views.VideoViewActivity;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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

	@RequiresApi(api = Build.VERSION_CODES.O)
	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		ImageView videoView = holder.getVideoView();
		ImageButton playButton = holder.getPlayButton();
		TextView durationText = holder.getDurationText();

		VideoModel videoModel = videoModels.get(position);

		LocalTime localTime = LocalTime.ofSecondOfDay(videoModel.duration);
		durationText.setText(localTime.format(DateTimeFormatter.ISO_LOCAL_TIME));

		playButton.setOnClickListener(v -> {
			Intent intent = new Intent(context, VideoViewActivity.class);
			intent.putExtra(VideoPreferences.PARCEL_VIDEO_MODEL, videoModel);
			context.startActivity(intent);
		});

		Glide.with(context)
				.load(videoModel.uri)
				.thumbnail(0.1f)
				.placeholder(R.drawable.pussy_cat)
				.into(videoView);

	}

	@Override
	public int getItemCount() {
		return videoModels.size();
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
