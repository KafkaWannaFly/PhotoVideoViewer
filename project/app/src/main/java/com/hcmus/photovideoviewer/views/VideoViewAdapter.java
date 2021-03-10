package com.hcmus.photovideoviewer.views;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hcmus.photovideoviewer.R;
import com.hcmus.photovideoviewer.models.VideoModel;

import java.util.ArrayList;

public class VideoViewAdapter extends RecyclerView.Adapter<VideoViewAdapter.ViewHolder> {
	public static class ViewHolder extends RecyclerView.ViewHolder {
		private VideoView videoView = null;

		public ViewHolder(@NonNull View itemView) {
			super(itemView);
			videoView = itemView.findViewById(R.id.videoView);
		}

		public VideoView getVideoView() {
			return videoView;
		}
	}

	private ArrayList<VideoModel> videoModels = null;

	public VideoViewAdapter(ArrayList<VideoModel> videoModels) {
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
		VideoView videoView = holder.getVideoView();

		VideoModel videoModel = videoModels.get(position);
		videoView.setVideoURI(videoModel.uri);
		videoView.seekTo((int) (videoModel.duration/2*1000));

		Log.d("Videos", videoModels.get(position).toString());
	}

	@Override
	public int getItemCount() {
		return videoModels.size();
	}



}
