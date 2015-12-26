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
import com.example.ratul.photoshare.model.PhotoTarget;
import com.example.ratul.photoshare.utils.FileUtils;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SendCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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

        //for testing
        //fetchFriends();
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
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_REQUEST_CODE);

    }
    @SuppressWarnings("unused")
    @OnClick(R.id.main_button_view_photos)
    public void onViewPhotosButtonClicked() {
        startActivity(new Intent(this, PhotosActivity.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        // Check which request we're responding to
        if (requestCode == GALLERY_REQUEST_CODE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK && data != null) {
                fetchFriends(new FriendsReadyListener() {
                    @Override
                    public void onFriendsReady(List<ParseUser> friends) {
                        if (friends != null)
                            savePhoto(data.getData(), friends);
                    }
                });
            }
        }
    }
    private void fetchFriends(final FriendsReadyListener listener) {
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        JSONObject responseJson = response.getJSONObject();
                        String[] ids = extractIdsForJSON(responseJson);
                        fetchFriendForId(ids, listener);
                    }
                }
        ).executeAsync();
    }

    private String[] extractIdsForJSON(JSONObject responseJson) {
        JSONArray data = responseJson.optJSONArray("data");
        if (data != null) {
            String[] ids = new String[data.length()];
            for (int i = 0; i < data.length(); i++) {
                ids[i] = data.optJSONObject(i).optString("id");
            }

            return ids;
        }

        return null;
    }

    private void fetchFriendForId(String[] ids, final FriendsReadyListener listener) {
        List<ParseQuery<ParseUser>> friendsQueries = new ArrayList<>();
        for (String id : ids) {
            friendsQueries.add(ParseUser.getQuery()
                    .whereEqualTo("facebookId", id));
        }

        ParseQuery.or(friendsQueries)
                .findInBackground(new FindCallback<ParseUser>() {
                    @Override
                    public void done(List<ParseUser> list, ParseException e) {
                        listener.onFriendsReady(list);
                    }
                });
    }


    private void savePhoto(Uri pathToImage,final List<ParseUser> targets){
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
    private void createPhotoTargets(Photo photo, final List<ParseUser> targets) {
        List<PhotoTarget> targetsToSave = new ArrayList<>();
        for (ParseUser userTarget : targets) {
            PhotoTarget target = new PhotoTarget();
            target.setPhoto(photo);
            target.setTarget(userTarget);
            targetsToSave.add(target);
        }

        ParseObject.saveAllInBackground(targets, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    onTargetsSaved(targets);
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void onTargetsSaved(List<ParseUser> users) {
        List<ParseQuery<ParseInstallation>> pushQueries = new ArrayList<>();
        for (ParseUser user : users) {
            ParseQuery<ParseInstallation> pushNotifQuery = ParseInstallation.getQuery()
                    .whereEqualTo("user", user);
            pushQueries.add(pushNotifQuery);
        }

        pushQueries.add(ParseInstallation.getQuery()
                .whereEqualTo("user", ParseUser.getCurrentUser()));
        ParsePush.sendMessageInBackground("Hey there, you have a new message", ParseQuery.or(pushQueries),
                new SendCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(MainActivity.this, "Sent notifications to friends!", Toast.LENGTH_SHORT).show();
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
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
    private interface FriendsReadyListener {
        void onFriendsReady(List<ParseUser> friends);
    }
}
