package com.example.ratul.photoshare.model;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by Ratul on 10/21/2015.
 */
@ParseClassName("Photo")
public class Photo extends ParseObject {
    public ParseUser getPhotographer(){
        return getParseUser("photographer");
    }
    public void setPhotographer(ParseUser photographer){
        put("photo",photographer);
    }
    public ParseFile getPhoto(){
        return getParseFile("photo");
    }
    public void setPhoto(ParseFile photo){
        put("photo",photo);
    }

}
