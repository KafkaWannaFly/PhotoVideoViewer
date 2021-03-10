package com.hcmus.photovideoviewer.views;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hcmus.photovideoviewer.R;
import com.hcmus.photovideoviewer.models.PhotoModel;

import java.util.ArrayList;

public class PhotoViewAdapter extends RecyclerView.Adapter<PhotoViewAdapter.ViewHolder> {
	private ArrayList<PhotoModel> photoModels;

	public PhotoViewAdapter(ArrayList<PhotoModel> photoModels) {
		this.photoModels = photoModels;
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext())
				         .inflate(R.layout.photo_item_view, parent, false);
		return new ViewHolder(v);
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		ImageView imageView = holder.getImageView();
		imageView.setImageURI(photoModels.get(position).uri);

//		DisplayMetrics displayMetrics = PhotosFragment.displayMetrics;
//		int sideSize = displayMetrics.heightPixels / PhotosFragment.SPAN_COUNT;

//		imageView.setMaxWidth(sideSize);
//		imageView.setMinimumWidth(sideSize);
//		imageView.setMaxHeight(sideSize);
//		imageView.setMinimumHeight(sideSize);
	}

	@Override
	public int getItemCount() {
		return photoModels.size();
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
