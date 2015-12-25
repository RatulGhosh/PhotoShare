package com.example.ratul.photoshare.model;

/**
 * Created by Ratul on 25-Dec-15.
 */
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("PhotoTarget")
public class PhotoTarget extends ParseObject {
    public static ParseQuery<PhotoTarget> getQuery() {
        return new ParseQuery<PhotoTarget>("PhotoTarget");
    }

    public ParseUser getTarget() {
        return getParseUser("Target");
    }

    public void setTarget(ParseUser target) {
        put("Target", target);
    }

    public Photo getPhoto() {
        return (Photo) get("photo");
    }

    public void setPhoto(Photo photo) {
        put("photo", photo);
    }
}