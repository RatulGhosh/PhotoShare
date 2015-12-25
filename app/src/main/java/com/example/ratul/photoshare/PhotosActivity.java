package com.example.ratul.photoshare;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.ratul.photoshare.model.Photo;
import com.example.ratul.photoshare.model.PhotoTarget;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PhotosActivity extends AppCompatActivity {
    @Bind(R.id.photos_recycyler_view)
    RecyclerView mPhotosRecyclerView;

    private PhotosRecyclerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);
        ButterKnife.bind(this);

        mAdapter = new PhotosRecyclerAdapter(new ArrayList<PhotoTarget>(), this);
        mPhotosRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mPhotosRecyclerView.setAdapter(mAdapter);


        //we want fetch all the photos whose target is that particular user
        PhotoTarget.getQuery()
                .whereEqualTo("Target", ParseUser.getCurrentUser())
                .include("photo")
                .findInBackground(new FindCallback<PhotoTarget>() {
                    @Override
                    public void done(List<PhotoTarget> list, ParseException e) {
                        if (e == null) {
                            if (mAdapter != null) {
                                mAdapter.setPhotos(list);
                                mAdapter.notifyDataSetChanged();
                            }
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_photos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //in normal listview we put the object inside the <>
    private static class PhotosRecyclerAdapter extends RecyclerView.Adapter<PhotosRecyclerAdapter.ViewHolder> {
        public static class ViewHolder extends RecyclerView.ViewHolder {
            ImageView photoImageView;

            public ViewHolder(View itemView) {
                super(itemView);
                photoImageView = (ImageView) itemView.findViewById(R.id.photo_image_view);
            }
        }

        private ArrayList<PhotoTarget> mPhotosList;
        private Context mContext;

        //to store the object we create a custom constructor
        public PhotosRecyclerAdapter(ArrayList<PhotoTarget> photosList, Context context) {
            mPhotosList = photosList;
            mContext = context;
        }

        @Override
        public PhotosRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View rootView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_layout_photo, null);
            return new ViewHolder(rootView);
        }

        @Override
        public void onBindViewHolder(final PhotosRecyclerAdapter.ViewHolder holder, int position) {
            Photo photo = mPhotosList.get(position).getPhoto();


            //here we are not caching the data,thus if there are two same url we will two time even if we need only one
           /* photo.getPhoto().getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, ParseException e) {

                //here we are decoding the large byte array on the main thread.
                    Bitmap photoBitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    if(holder.photoImageView !=null && photoBitmap != null)
                        holder.photoImageView.setImageBitmap(photoBitmap);
                }
            });*/
            if (photo.getPhoto() != null && photo.getPhoto().getUrl() != null) {
                String photoUrl = photo.getPhoto().getUrl();

                //picasso will take care of caching,asynchronous operation etc.
                Picasso.with(mContext)
                        .load(photoUrl)
                        .fit().centerCrop()
                        .into(holder.photoImageView);
            }
        }

        @Override
        public int getItemCount() {
            return mPhotosList.size();
        }

        public void setPhotos(List<PhotoTarget> photos) {
            mPhotosList.clear();
            mPhotosList.addAll(photos);
        }
    }
}