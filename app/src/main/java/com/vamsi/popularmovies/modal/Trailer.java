package com.vamsi.popularmovies.Modal;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Vamsi Smart on 09-09-2016.
 */
public class Trailer {

    String key,name,type;

    public Trailer(JSONObject object){

        try {

            this.key = object.getString("key");
            this.name = object.getString("name");
            this.type = object.getString("type");

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
