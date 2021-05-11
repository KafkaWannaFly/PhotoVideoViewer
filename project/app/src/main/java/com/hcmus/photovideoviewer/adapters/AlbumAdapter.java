package com.hcmus.photovideoviewer.adapters;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.CodeBoy.MediaFacer.MediaFacer;
import com.CodeBoy.MediaFacer.mediaHolders.pictureContent;
import com.CodeBoy.MediaFacer.mediaHolders.pictureFolderContent;
import com.CodeBoy.MediaFacer.mediaHolders.videoContent;
import com.bumptech.glide.Glide;
import com.hcmus.photovideoviewer.R;
import com.hcmus.photovideoviewer.constants.PhotoPreferences;
import com.hcmus.photovideoviewer.models.AlbumModel;
import com.hcmus.photovideoviewer.models.PhotoModel;
import com.hcmus.photovideoviewer.models.VideoModel;
import com.hcmus.photovideoviewer.services.MediaDataRepository;
import com.hcmus.photovideoviewer.viewmodels.PhotoViewViewModel;
import com.hcmus.photovideoviewer.viewmodels.PhotosFragmentViewModel;
import com.hcmus.photovideoviewer.views.AlbumsFragment;
import com.hcmus.photovideoviewer.views.AlbumsViewActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

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

    String albumName = "";
    ArrayList<PhotoModel> photoModels = new ArrayList<>();
    ArrayList<VideoModel> videoModels = new ArrayList<>();
    //    private String[] mDataSet;
    ArrayList<AlbumModel> albumData;
    // BEGIN_INCLUDE(recyclerViewSampleViewHolder)
    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView title_of_album;
        private final ImageView imgTitle_of_album;
        private final TextView quantity_of_album;
        private final View album_view;
        private final TextView quantity_video;

        public ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(v1 -> Log.d(TAG, "Element " + getAdapterPosition() + " clicked."));
            title_of_album = (TextView) v.findViewById(R.id.title_of_album);
            imgTitle_of_album = (ImageView) v.findViewById(R.id.my_image_glide);
            quantity_of_album = (TextView) v.findViewById(R.id.quantity_of_album);
            album_view = v.findViewById(R.id.albumView);
            quantity_video = v.findViewById(R.id.quantity_video);
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
        public TextView getQuantityVideo(){
            return quantity_video;
        }
    }
//    public AlbumAdapter(ArrayList<AlbumModel> albumModels) {
//        this.albumData = albumModels;
//    }
    public AlbumAdapter(Context context, ArrayList<AlbumModel> albumModels) {
        this.context = context;
        this.albumData = albumModels;
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
            if(albumData.get(position).getAlbumName().equals(PhotoPreferences.PRIVATE)){
                viewHolder.getTextView().setText(albumData.get(position).getAlbumName());
                //String quantityPhoto = viewHolder.getQuantityAlbums().getText().toString();
                viewHolder.getQuantityAlbums().setText("");
                //String quatityVideo = viewHolder.getQuantityVideo().getText().toString();
                viewHolder.getQuantityVideo().setText("");
                viewHolder.getImageView().setImageResource(R.drawable.pussy_cat);
//=======
//                //viewHolder.getImageView().setImageResource(R.drawable.ic_baseline_privacy_tip_24);
//                viewHolder.getImageView().set
//>>>>>>> Stashed changes
//                Picasso.get()
//                        .load(R.drawable.ic_baseline_privacy_tip_24)
//                        .placeholder(R.drawable.ic_baseline_privacy_tip_24)
//                        .error(R.drawable.ic_baseline_privacy_tip_24)
//                        .fit()
//                        .centerCrop()
//                        .into(viewHolder.getImageView());
            }
            else{
                viewHolder.getTextView().setText(albumData.get(position).getAlbumName());
                //String quantityPhoto = viewHolder.getQuantityAlbums().getText().toString();
                viewHolder.getQuantityAlbums().setText(albumData.get(position).getQuantityPhoto() + " " + "Ảnh");
                //String quatityVideo = viewHolder.getQuantityVideo().getText().toString();
                viewHolder.getQuantityVideo().setText(albumData.get(position).getQuantityVideo() + " " + "Video");
                Uri _uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                String uri = ContentUris.withAppendedId(_uri, albumData.get(position).getImageUrl()).toString();
                Picasso.get()
                        .load(uri)
                        .resize(700, 650)
                        .centerCrop()
                        .into(viewHolder.getImageView());
            }
        //Picasso.get().load(albumData.get(position).getImageUrl()).fit().into(viewHolder.getImageView());

//        Glide.with(this.context).load(albumData.get(position).getImageUrl()).into(viewHolder.getImageView());
            flag++;
            viewHolder.getAlbumView().setOnClickListener(v->{
                TextView titleText = v.findViewById(R.id.title_of_album);
                albumName = "";
                photoModels = new ArrayList<>();
                videoModels = new ArrayList<>();
                ArrayList<PhotoModel> dataPhotos = MediaDataRepository.getInstance().getPhotoModels();
                if(albumData.get(position).getAlbumName().equals("Favourites")){
                    ArrayList<PhotoModel> dataPhotoFavourite = new ArrayList<PhotoModel>();
                    for(int i = 0; i < dataPhotos.size(); i++){
                        if(dataPhotos.get(i).isFavorite){
                            dataPhotoFavourite.add(dataPhotos.get(i));
                        }
                    }
                    photoModels = dataPhotoFavourite;
                    AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
                    builder.setTitle("Chọn chế độ xem");
                    builder.setPositiveButton("Ảnh", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (photoModels.size() > 0) {
                                System.out.println("aa");
                                albumName = albumData.get(position).getAlbumName();
                                Intent viewAlbumIntent = new Intent(context , AlbumsViewActivity.class);
                                viewAlbumIntent.putParcelableArrayListExtra("photoModels", photoModels);
                                viewAlbumIntent.putExtra("albumName", albumName);
                                //viewAlbumIntent.putExtra("currentPosition", position);
                                Log.d("onClickCardAlbum", "clicked " + position);
                                context.startActivity(viewAlbumIntent);
                            }
                            else{
                                Toast.makeText(context, "Không có ảnh để hiện thị", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    builder.setNegativeButton("Video", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(videoModels.size() > 0){

                            }else{
                                Toast.makeText(context, "Không có video để hiện thị", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    builder.show();

                }
                else if(albumData.get(position).getAlbumName().equals("Private"))
                {
                    SharedPreferences sharePassPrivate = this.context.getSharedPreferences("PasswordPrivate", Context.MODE_PRIVATE);
                    Map<String, String> mapPass = (Map<String,String>)sharePassPrivate.getAll();
                    if(mapPass.size() == 0){
                        AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
                        builder.setTitle("Nhập mật khẩu cho lần đầu");
                        final EditText input = new EditText(this.context);
                        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        builder.setView(input);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String m_Text = input.getText().toString();
                                if (m_Text.length() > 0) {
                                    SharedPreferences.Editor editor = sharePassPrivate.edit();
                                    editor.putString("Password", m_Text);
                                    editor.commit();
                                }
                                else{
                                    Toast.makeText(context, "Mật khẩu không được để trống.", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        builder.show();
                    }else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
                        builder.setTitle("Nhập mật khẩu");
                        final EditText input = new EditText(this.context);
                        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        builder.setView(input);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String m_Text = input.getText().toString();
                                if (m_Text.equals(mapPass.get("Password"))) {
                                    ArrayList<PhotoModel> dataPhotoPrivate = new ArrayList<PhotoModel>();
                                    for (int i = 0; i < dataPhotos.size(); i++) {
                                        if (dataPhotos.get(i).isSecret) {
                                            dataPhotoPrivate.add(dataPhotos.get(i));
                                            dataPhotoPrivate.get(dataPhotoPrivate.size() - 1).isSecret = false;
                                        }
                                    }
                                    photoModels = dataPhotoPrivate;
                                    System.out.println("aa");
                                    albumName = albumData.get(position).getAlbumName();
                                    Intent viewAlbumIntent = new Intent(context, AlbumsViewActivity.class);
                                    viewAlbumIntent.putParcelableArrayListExtra("photoModels", photoModels);
                                    viewAlbumIntent.putExtra("albumName", albumName);
                                    //viewAlbumIntent.putExtra("currentPosition", position);
                                    Log.d("onClickCardAlbum", "clicked " + position);
                                    context.startActivity(viewAlbumIntent);
                                }
                                else{
                                    Toast.makeText(context, "Mật khẩu sai, vui lòng thực hiện lại", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        builder.show();
                    }
                }
                else{
//                    ArrayList<videoContent> allVideosAlbum;
//                    ArrayList<pictureFolderContent> pictureFolders = new ArrayList<>();
//                    pictureFolders.addAll(MediaFacer.withPictureContex(context).getPictureFolders());
//                    allPhotosAlbum = MediaFacer.withPictureContex(this.context).getAllPictureContentByBucket_id(pictureFolders.get(position).getBucket_id());
//                    photoModels = fitPhotoLibrary(allPhotosAlbum);
                    ArrayList<PhotoModel> allPhotosAlbum = new ArrayList<PhotoModel>();
                    albumName = albumData.get(position).getAlbumName();
                    String test = dataPhotos.get(0).uri;
                    for(int i = 0; i < dataPhotos.size(); i++){
                        String[] cutNameAlbum = dataPhotos.get(i).uri.split("/");
                        String nameCuted = cutNameAlbum[cutNameAlbum.length - 2];
                        if(albumData.get(position).getAlbumName().equals(nameCuted)){
                            allPhotosAlbum.add(dataPhotos.get(i));
                        }
                    }
                    photoModels = allPhotosAlbum;
                    System.out.println("abc");
                    Intent viewAlbumIntent = new Intent(this.context , AlbumsViewActivity.class);
                    viewAlbumIntent.putParcelableArrayListExtra("photoModels", photoModels);
                    viewAlbumIntent.putExtra("albumName", albumName);
                    //viewAlbumIntent.putExtra("currentPosition", position);
                    Log.d("onClickCardAlbum", "clicked " + position);
                    context.startActivity(viewAlbumIntent);
                }
            });
    }
    @Override
    public int getItemCount() {
        return albumData.size();
    }
//    public ArrayList<PhotoModel> fitPhotoLibrary(ArrayList<pictureContent> allPhotosAlbum){
//        ArrayList<PhotoModel> photoModels = new ArrayList<>();
//        Uri _uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//        PhotoModel photoModel;
//        for(int i = 0; i < allPhotosAlbum.size(); i++){
//            photoModel = new PhotoModel();
//            photoModel.id = allPhotosAlbum.get(i).getPictureId();
//            photoModel.dateModified = new Date(allPhotosAlbum.get(i).getDate_modified() * 1000);
//            photoModel.displayName = allPhotosAlbum.get(i).getPicturName();
//            photoModel.size = allPhotosAlbum.get(i).getPictureSize();
//            photoModel.uri = ContentUris.withAppendedId(_uri, photoModel.id).toString();
//            photoModels.add(photoModel);
//        }
//        return photoModels;
//    }
}

