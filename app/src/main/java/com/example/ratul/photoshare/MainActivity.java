package com.example.ratul.photoshare;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.example.ratul.photoshare.model.Photo;
import com.example.ratul.photoshare.utils.FileUtils;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private int GALLERY_REQUEST_CODE = 2314;

    @Bind(R.id.main_button_Take_Photo)Button mButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //Button take_photo = (Button)findViewById(R.id.main_button_Take_Photo);
        //facebookSignin.setText("hello");

        //for testing purpose
        /*ParseObject testObject = new ParseObject("TestObject");//name of class is TestObject
        testObject.put("foo", "bar");//document data base : equivalent to foo : bar
        testObject.saveInBackground();*/
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.main_button_Take_Photo)
    public void onTakePhotoButtonClick(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),GALLERY_REQUEST_CODE);

    }
    @SuppressWarnings("unused")
    @OnClick(R.id.main_button_view_photos)
    public void onViewPhotosButtonClicked() {
        startActivity(new Intent(this, PhotosActivity.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == GALLERY_REQUEST_CODE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK && data != null) {
                savePhoto(data.getData());
            }
        }
    }

    private void savePhoto(Uri pathToImage){
        byte[] pictureContents = FileUtils.loadImage(pathToImage, this);

        if(pictureContents != null) {


            /*Instead of using parse object we are creating our custom class
            ParseObject photoObject = new ParseObject("Photo");
            photoObject.put("Photo", new ParseFile(pictureContents));
            photoObject.put("photographer", ParseUser.getCurrentUser());
            photoObject.saveInBackground(new SaveCallback()*/

            Photo photo = new Photo();
            photo.setPhoto(new ParseFile(pictureContents));
            photo.setPhotographer(ParseUser.getCurrentUser());


            photo.saveInBackground(new SaveCallback(){
                @Override
                public void done(ParseException e) {
                    if(e == null){
                        Toast.makeText(MainActivity.this, "Successfully saved Photo", Toast.LENGTH_SHORT).show();

                    }else{
                        e.printStackTrace();
                    }
                }
            });
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
