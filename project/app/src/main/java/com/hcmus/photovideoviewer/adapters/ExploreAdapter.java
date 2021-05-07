package com.hcmus.photovideoviewer.adapters;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Base64;
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
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.hcmus.photovideoviewer.R;
import com.hcmus.photovideoviewer.models.AlbumModel;
import com.hcmus.photovideoviewer.models.ExploreModel;
import com.hcmus.photovideoviewer.models.PhotoModel;
import com.hcmus.photovideoviewer.services.MediaDataRepository;
import com.hcmus.photovideoviewer.views.AlbumsFragment;
import com.hcmus.photovideoviewer.views.AlbumsViewActivity;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
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
    private Python py = Python.getInstance();

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
        //Glide.with(context).load(bitmapToByte(a)).override(500, 500).fitCenter().into(viewHolder.getImageView());
        viewHolder.getImageView().setImageBitmap(a);
        ArrayList<PhotoModel> dataPhotos = MediaDataRepository.getInstance().getPhotoModels();

        viewHolder.getImageView().setOnClickListener(v->{
            //get Data
            PyObject pyo = py.getModule("myscript");
            Uri _uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            String faceBitmaptoString = getStringImage(exploreModels.get(position).getBitmapAvatar());
            ArrayList<PhotoModel> photoModels = new ArrayList<PhotoModel>();
            for(int i = 0; i < dataPhotos.size(); i++){
                Bitmap bitmap = null;
                long idPerson = dataPhotos.get(i).id;
                String uriPerson = ContentUris.withAppendedId(_uri, idPerson).toString();
                try {
                    bitmap = getBitmapFromUri(Uri.parse(uriPerson));
                }
                catch (Exception e) {
                }
                String imageString = getStringImage(bitmap);
                PyObject obj2 = pyo.callAttr("main",faceBitmaptoString, imageString);
                if(obj2.toBoolean() == true){
                    photoModels.add(dataPhotos.get(i));
                }
                System.out.println("abc");
            }
            //
//            photoModels.add(dataPhotos.get(0));
//            photoModels.add(dataPhotos.get(1));
            Intent viewAlbumIntent = new Intent(this.context , AlbumsViewActivity.class);
            viewAlbumIntent.putParcelableArrayListExtra("photoModels", photoModels);
            //viewAlbumIntent.putExtra("albumName", albumName);
            //viewAlbumIntent.putExtra("currentPosition", position);
            Log.d("onClickCardAlbum", "clicked " + position);
            context.startActivity(viewAlbumIntent);
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
    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                context.getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }
    private String getStringImage(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = android.util.Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return  encodedImage;
    }
}

