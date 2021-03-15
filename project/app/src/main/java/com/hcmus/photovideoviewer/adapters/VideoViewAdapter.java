package com.hcmus.photovideoviewer.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hcmus.photovideoviewer.R;
import com.hcmus.photovideoviewer.models.VideoModel;

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

		VideoModel videoModel = videoModels.get(position);

		Glide.with(context)
				.load(videoModel.uri)
				.thumbnail(0.1f)
				.placeholder(R.drawable.pussy_cat)
				.into(videoView);

		Log.d("Videos", videoModels.get(position).toString());
	}

	@Override
	public int getItemCount() {
		return videoModels.size();
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		private ImageView videoView = null;

		public ViewHolder(@NonNull View itemView) {
			super(itemView);
			videoView = itemView.findViewById(R.id.videoView);
		}

		public ImageView getVideoView() {
			return videoView;
		}
	}


}
