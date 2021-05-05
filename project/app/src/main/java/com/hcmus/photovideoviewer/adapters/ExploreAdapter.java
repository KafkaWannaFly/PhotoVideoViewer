package com.hcmus.photovideoviewer.adapters;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.CodeBoy.MediaFacer.MediaFacer;
import com.CodeBoy.MediaFacer.mediaHolders.pictureContent;
import com.CodeBoy.MediaFacer.mediaHolders.pictureFolderContent;
import com.CodeBoy.MediaFacer.mediaHolders.videoContent;
import com.bumptech.glide.Glide;
import com.hcmus.photovideoviewer.R;
import com.hcmus.photovideoviewer.models.AlbumModel;
import com.hcmus.photovideoviewer.models.ExploreModel;
import com.hcmus.photovideoviewer.models.PhotoModel;
import com.hcmus.photovideoviewer.views.AlbumsFragment;
import com.hcmus.photovideoviewer.views.AlbumsViewActivity;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;

import static android.app.PendingIntent.getActivity;

/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class ExploreAdapter extends RecyclerView.Adapter<ExploreAdapter.ViewHolder> {
    private static final String TAG = "CustomAdapter";
    Context context;
    private PhotosViewAdapter photosViewAdapter = null;
    private RecyclerView recyclerView;
    static int flag = 0;
    ArrayList<ExploreModel> exploreModels;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView avatarExplore;


        public ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(v1 -> Log.d(TAG, "Element " + getAdapterPosition() + " clicked."));
            avatarExplore = (ImageView) v.findViewById(R.id.avatar_explore);

        }

        public ImageView getImageView(){
            return avatarExplore;
        }
    }
    public ExploreAdapter(Context context, ArrayList<ExploreModel> exploreModels) {
        this.context = context;
        this.exploreModels = exploreModels;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.explore_sub_fragment, viewGroup, false);

        return new ViewHolder(v);
    }
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Log.d(TAG, "Element " + position + " set.");
        //get database
//        Uri _uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//        long idPerson = exploreModels.get(position).getAvatarUrl();
//        Glide.with(context)
//                .asBitmap()
//                .load(ContentUris.withAppendedId(_uri, idPerson).toString())
//                .into(viewHolder.getImageView());
        //viewHolder.getImageView().setImageBitmap(exploreModels.get(position).getBitmapAvatar());
        Bitmap a = exploreModels.get(position).getBitmapAvatar();
        //viewHolder.getImageView().setImageBitmap(Bitmap.createScaledBitmap(a, 1200, 1200, false));
        Glide.with(context).load(bitmapToByte(a)).override(500, 500).fitCenter().into(viewHolder.getImageView());

        viewHolder.getImageView().setOnClickListener(v->{
        });
    }
    @Override
    public int getItemCount() {
        return exploreModels.size();
    }
    private byte[] bitmapToByte(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }
}

