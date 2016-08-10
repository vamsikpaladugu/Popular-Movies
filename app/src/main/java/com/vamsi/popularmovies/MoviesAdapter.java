package com.vamsi.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Vamsi on 05-08-2016.
 */
public class MoviesAdapter extends ArrayAdapter<Movie> {

    public MoviesAdapter(Context context, List<Movie> movies) {
        super(context, 0, movies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Movie movie = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.single_movie, parent, false);
        }

        // Lookup view for data population
        ImageView ivPoster = (ImageView) convertView.findViewById(R.id.ivMovie);

        ivPoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(),Details.class);

                intent.putExtra("movie",movie);

                intent.putExtra("bp",""+movie.getBackdrop_path());
                intent.putExtra("p",""+movie.getPoster_path());
                intent.putExtra("overview",""+movie.getOverview());
                intent.putExtra("release",""+movie.getRelease_date());
                intent.putExtra("rating",""+movie.getVote_average());
                getContext().startActivity(intent);

            }
        });

        Picasso.with(getContext()).load(Globels.baseImageUrl+""+movie.getPoster_path()).into(ivPoster);

        return convertView;
    }


   /* public void scr(){
        WindowManager windowManager = (WindowManager)getContext().getSystemService(getContext().WINDOW_SERVICE);
        int width = windowManager.getDefaultDisplay().getWidth();

    }*/

}
