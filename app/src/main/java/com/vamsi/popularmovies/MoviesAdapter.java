package com.vamsi.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import com.vamsi.popularmovies.Modal.Movie;

import java.util.List;

/**
 * Created by Vamsi on 05-08-2016.
 */
public class MoviesAdapter extends ArrayAdapter<Movie> {

    Context context;
    boolean mTwopanal;

    public MoviesAdapter(Context context, List<Movie> movies,boolean mTwopanal) {

        super(context, 0, movies);
        this.context = context;
        this.mTwopanal = mTwopanal;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Movie movie = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.single_movie, parent, false);
        }

        ImageView ivPoster = (ImageView) convertView.findViewById(R.id.ivMovie);



        // scale up imageview to fit the screen
        if (mTwopanal) {
            ivPoster.getLayoutParams().width = screenWidth() / 4;
            ivPoster.getLayoutParams().height = 3 * screenWidth() / 8;
        } else {
            ivPoster.getLayoutParams().width = screenWidth() / 2;
            ivPoster.getLayoutParams().height = 3 * screenWidth() / 4;
        }
        ivPoster.requestLayout();

        Picasso.with(getContext()).load(Globals.baseImageUrl+""+movie.getPoster_path()).into(ivPoster);

        return convertView;
    }


    public int screenWidth(){
        return context.getResources().getDisplayMetrics().widthPixels;
    }

}
