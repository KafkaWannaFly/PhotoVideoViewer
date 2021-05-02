package com.hcmus.photovideoviewer.adapters;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
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
import com.hcmus.photovideoviewer.R;
import com.hcmus.photovideoviewer.models.AlbumModel;
import com.hcmus.photovideoviewer.models.PhotoModel;
import com.hcmus.photovideoviewer.views.AlbumsFragment;
import com.hcmus.photovideoviewer.views.AlbumsViewActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import static android.app.PendingIntent.getActivity;

/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {
    private static final String TAG = "CustomAdapter";
    Context context;
    private PhotosViewAdapter photosViewAdapter = null;
    private RecyclerView recyclerView;
    static int flag = 0;
    //    private String[] mDataSet;
    ArrayList<AlbumModel> albumData;
    ArrayList<PhotoModel> dataPhotoFavourite;
    // BEGIN_INCLUDE(recyclerViewSampleViewHolder)
    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView title_of_album;
        private final ImageView imgTitle_of_album;
        private final TextView quantity_of_album;
        private final View album_view;


        public ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element " + getAdapterPosition() + " clicked.");
                }
            });
            title_of_album = (TextView) v.findViewById(R.id.title_of_album);
            imgTitle_of_album = (ImageView) v.findViewById(R.id.my_image_glide);
            quantity_of_album = (TextView) v.findViewById(R.id.quantity_of_album);
            album_view = v.findViewById(R.id.albumView);

        }

        public TextView getTextView() {
            return title_of_album;
        }
        public ImageView getImageView(){
            return imgTitle_of_album;
        }
        public TextView getQuantityAlbums(){
            return quantity_of_album;
        }
        public View getAlbumView(){return album_view;}
    }
//    public AlbumAdapter(ArrayList<AlbumModel> albumModels) {
//        this.albumData = albumModels;
//    }
    public AlbumAdapter(Context context, ArrayList<AlbumModel> albumModels, ArrayList<PhotoModel> dataPhotoFavourite) {
        this.context = context;
        this.albumData = albumModels;
        this.dataPhotoFavourite = dataPhotoFavourite;
        Log.d("Abc", this.albumData.size() + "");
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.albums_sub_fragment, viewGroup, false);

        return new ViewHolder(v);
    }
    // END_INCLUDE(recyclerViewOnCreateViewHolder)

    // BEGIN_INCLUDE(recyclerViewOnBindViewHolder)
    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
            Log.d(TAG, "Element " + position + " set.");
            Log.d("Test data album", "" + albumData.get(position));
            viewHolder.getTextView().setText(albumData.get(position).getAlbumName());
            viewHolder.getQuantityAlbums().setText(albumData.get(position).getQuantity() + "");
            Picasso.get()
                    .load(albumData.get(position).getImageUrl().uri)
                    .resize(700, 650)
                    .centerCrop()
                    .into(viewHolder.getImageView());
            flag++;
        viewHolder.getAlbumView().setOnClickListener(v->{
            ArrayList<PhotoModel> photoModels = new ArrayList<>();
            TextView titleText = v.findViewById(R.id.title_of_album);
            if(titleText.getText().toString().compareTo("Favourites") == 0){
//                Intent intentFavourites = new Intent();
//                photoModels = intentFavourites.getParcelableArrayListExtra("photoFavoriteData");
                photoModels = this.dataPhotoFavourite;
                System.out.println("aa");
            }
            else if(titleText.getText().toString().compareTo("Privacies") == 0)
            {

            }
            else{
                ArrayList<pictureContent> allPhotosAlbum;
                ArrayList<videoContent> allVideosAlbum;
                ArrayList<pictureFolderContent> pictureFolders = new ArrayList<>();
                pictureFolders.addAll(MediaFacer.withPictureContex(context).getPictureFolders());
                allPhotosAlbum = MediaFacer.withPictureContex(this.context).getAllPictureContentByBucket_id(pictureFolders.get(position).getBucket_id());
                photoModels = fitPhotoLibrary(allPhotosAlbum);
            }
            Intent viewAlbumIntent = new Intent(this.context , AlbumsViewActivity.class);
            viewAlbumIntent.putParcelableArrayListExtra("photoModels", photoModels);
            viewAlbumIntent.putExtra("albumName", albumData.get(position).getAlbumName());
            viewAlbumIntent.putExtra("currentPosition", position);
            Log.d("onClickCardAlbum", "clicked " + position);
            context.startActivity(viewAlbumIntent);
        });
    }
    @Override
    public int getItemCount() {
        return albumData.size();
    }
    public ArrayList<PhotoModel> fitPhotoLibrary(ArrayList<pictureContent> allPhotosAlbum){
        ArrayList<PhotoModel> photoModels = new ArrayList<>();
        Uri _uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        PhotoModel photoModel;
        for(int i = 0; i < allPhotosAlbum.size(); i++){
            photoModel = new PhotoModel();
            photoModel.id = allPhotosAlbum.get(i).getPictureId();
            photoModel.dateModified = new Date(allPhotosAlbum.get(i).getDate_modified() * 1000);
            photoModel.displayName = allPhotosAlbum.get(i).getPicturName();
            photoModel.size = allPhotosAlbum.get(i).getPictureSize();
            photoModel.uri = ContentUris.withAppendedId(_uri, photoModel.id).toString();
            photoModels.add(photoModel);
        }
        return photoModels;
    }
}

