package com.vamsi.popularmovies.modal;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Vamsi Smart on 09-09-2016.
 */
public class Review {

    String author,content;

    public Review(JSONObject object){

        try {

            this.author = object.getString("author");
            this.content = object.getString("content");

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
