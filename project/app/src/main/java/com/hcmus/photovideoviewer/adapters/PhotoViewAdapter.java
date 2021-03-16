package com.hcmus.photovideoviewer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hcmus.photovideoviewer.R;
import com.hcmus.photovideoviewer.models.PhotoModel;

import java.util.ArrayList;

public class PhotoViewAdapter extends RecyclerView.Adapter<PhotoViewAdapter.ViewHolder> {
	private ArrayList<PhotoModel> photoModels = null;
	private Fragment fragment = null;
	Context context = null;

	public PhotoViewAdapter(Context context, ArrayList<PhotoModel> photoModels) {
		this.context = context;
		this.photoModels = photoModels;
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext())
				         .inflate(R.layout.photo_item, parent, false);
		return new ViewHolder(v);
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		ImageView imageView = holder.getImageView();
		if (position == 0) {
			Glide.with(context)
					.load(R.drawable.photo_cam_icon)
					.placeholder(R.drawable.pussy_cat)
					.into(imageView);
		}
		else {
			Glide.with(context)
					.load(photoModels.get(position-1).uri)
					.placeholder(R.drawable.pussy_cat)
					.into(imageView);
		}
	}

	@Override
	public int getItemCount() {
		return photoModels.size() + 1; // Photos and 1 camera icon
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		private final ImageView imageView;

		public ViewHolder(@NonNull View itemView) {
			super(itemView);
			imageView = itemView.findViewById(R.id.imageView);
		}

		public ImageView getImageView() {
			return imageView;
		}
	}
}
