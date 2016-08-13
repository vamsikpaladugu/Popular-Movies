package com.vamsi.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Vamsi on 05-08-2016.
 */
public class MoviesAdapter extends ArrayAdapter<Movie> {

    Context context;

    public MoviesAdapter(Context context, List<Movie> movies) {

        super(context, 0, movies);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Movie movie = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.single_movie, parent, false);
        }

        // Lookup view for data population

        ImageView ivPoster = (ImageView) convertView.findViewById(R.id.ivMovie);
        //ivPoster.getLayoutParams().width = scr()/2;
        //ivPoster.getLayoutParams().height = (277/185)*scr()/2;
        //ivPoster.requestLayout();//.resize(scr()/2,(277/185)*scr()/2)

        Picasso.with(getContext()).load(Globels.baseImageUrl+""+movie.getPoster_path()).resize(scr()/2,(277/185)*scr()/2).into(ivPoster);

        return convertView;
    }


    public int scr(){

        /*int width= context.getResources().getDisplayMetrics().widthPixels;

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);*/
        return context.getResources().getDisplayMetrics().widthPixels;
    }

}
