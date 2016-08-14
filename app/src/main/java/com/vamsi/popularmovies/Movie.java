package com.vamsi.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Vamsi on 05-08-2016.
 */
public class Movie implements Parcelable{

    String backdrop_path,original_title,overview,poster_path,release_date;
    Double vote_average;


    public Movie(JSONObject object){
        try {
            this.backdrop_path = object.getString("backdrop_path");
            this.original_title = object.getString("original_title");

            this.overview = object.getString("overview");
            this.poster_path = object.getString("poster_path");

            this.release_date = object.getString("release_date");
            this.vote_average = object.getDouble("vote_average");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    protected Movie(Parcel in) {
        backdrop_path = in.readString();
        original_title = in.readString();
        overview = in.readString();
        poster_path = in.readString();
        release_date = in.readString();

        vote_average = in.readDouble();
    }



    public String getBackdrop_path() {
        return backdrop_path;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public Double getVote_average() {
        return vote_average;
    }

    public void setVote_average(Double vote_average) {
        this.vote_average = vote_average;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(backdrop_path);
        parcel.writeString(original_title);

        parcel.writeString(overview);
        parcel.writeString(poster_path);

        parcel.writeString(release_date);
        parcel.writeDouble(vote_average);

    }


    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };


}
