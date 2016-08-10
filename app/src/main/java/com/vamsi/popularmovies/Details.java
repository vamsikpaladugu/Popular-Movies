package com.vamsi.popularmovies;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class Details extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {


        public PlaceholderFragment() {
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_details, container, false);

            Movie movie = getActivity().getIntent().getExtras().getParcelable("movie");

            ImageView ivBackposter = (ImageView) rootView.findViewById(R.id.ivfdBackposter);
            ImageView ivPoster = (ImageView) rootView.findViewById(R.id.ivfdPoster);
            TextView tvOverview = (TextView) rootView.findViewById(R.id.tvfdOverview);
            TextView tvReleasedate = (TextView) rootView.findViewById(R.id.tvfdReleasedate);
            TextView tvRating = (TextView) rootView.findViewById(R.id.tvfdRating);
            TextView tvTitle = (TextView) rootView.findViewById(R.id.tvfdTitle);


            Picasso.with(getContext()).load(Globels.baseImageUrlHD+""+movie.getBackdrop_path()).into(ivBackposter);
            Picasso.with(getContext()).load(Globels.baseImageUrl+""+movie.getPoster_path()).into(ivPoster);

            tvTitle.setText(""+movie.getOriginal_title());
            tvOverview.setText(""+movie.getOverview());
            tvReleasedate.setText(""+movie.getRelease_date());
            tvRating.setText(""+movie.getVote_average());


            return rootView;
        }



    }

}
