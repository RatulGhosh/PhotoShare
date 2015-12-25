package com.example.ratul.photoshare;

import android.app.Application;

import com.example.ratul.photoshare.model.Photo;
import com.example.ratul.photoshare.model.PhotoTarget;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;

/**
 * Created by Ratul on 10/17/2015.
 */
public class PhotoShareApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "K7CZZOiArTAavi88SJkkNoszDMXGfCHWuCsBCgjY", "XQeoVC8Ui3Ey3VsBHkiJcloa7IVmMOsKzjeAgaW0");
        ParseFacebookUtils.initialize(this);
        ParseObject.registerSubclass(Photo.class);
        ParseObject.registerSubclass(PhotoTarget.class);

    }
}
