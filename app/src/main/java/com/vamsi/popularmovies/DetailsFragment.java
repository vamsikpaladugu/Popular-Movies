package com.vamsi.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.vamsi.popularmovies.data.MoviesContract.FavoriteEntry;
import com.vamsi.popularmovies.modal.Movie;
import com.vamsi.popularmovies.modal.Trailer;

import at.blogc.android.views.ExpandableTextView;

/**
 * Created by Vamsi Smart on 07-09-2016.
 */

public class DetailsFragment extends Fragment {

    private static final String MOVIE_SHARE_HASHTAG = " #PopularMovie";
    Context context;
    Movie movie;

    TextView tvOverview, tvReleasedate, tvTitle, tvRating, tvReviewTitle, tvTrailerTitle;
    ImageView ivBackposter, ivPoster;
    RatingBar bar;

    RecyclerView rvTrailers;
    RecyclerView.Adapter myTAdapter;
    LinearLayoutManager tlayoutManager;

    AppCompatImageView ivFavorite;

    boolean isFavorite = false;

    LinearLayout llReviews;

    private ShareActionProvider mShareActionProvider;
    private String mForecast;


    public DetailsFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getActivity();

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_details, container, false);

        Bundle arguments = getArguments();
        if (arguments != null) {
            movie = arguments.getParcelable("movie");
        }


        ivFavorite = (AppCompatImageView) rootView.findViewById(R.id.ivfdFavarite);

        ivBackposter = (ImageView) rootView.findViewById(R.id.ivfdBackposter);
        ivPoster = (ImageView) rootView.findViewById(R.id.ivfdPoster);

        tvOverview = (TextView) rootView.findViewById(R.id.tvfdOverview);
        tvReleasedate = (TextView) rootView.findViewById(R.id.tvfdReleasedate);
        tvTitle = (TextView) rootView.findViewById(R.id.tvfdTitle);
        tvRating = (TextView) rootView.findViewById(R.id.tvfdRating);

        tvTrailerTitle = (TextView) rootView.findViewById(R.id.tvfdTrailerTitle);
        tvReviewTitle = (TextView) rootView.findViewById(R.id.tvfdReviewTitle);

        bar = (RatingBar) rootView.findViewById(R.id.ratingbar);

        llReviews = (LinearLayout) rootView.findViewById(R.id.llfdReviews);


        Picasso.with(getContext()).load(Globals.baseImageUrlHD + "" + movie.getBackdrop_path()).into(ivBackposter);
        Picasso.with(getContext()).load(Globals.baseImageUrl + "" + movie.getPoster_path()).into(ivPoster);

        tvTitle.setText("" + movie.getOriginal_title());
        tvOverview.setText("" + movie.getOverview());
        tvRating.setText("(" + movie.getVote_average() + ")");
        tvReleasedate.setText("Released on " + movie.getRelease_date());

        bar.setRating(Float.parseFloat("" + movie.getVote_average()));


        //for stage two
        rvTrailers = (RecyclerView) rootView.findViewById(R.id.rvfdTrailers);
        tlayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rvTrailers.setLayoutManager(tlayoutManager);


        //fatch the movie Trailers and Reviews with loopj library
        fatahTrailers(movie.getId());
        fetchReviews(movie.getId());

        ivFavorite.setImageResource(isMovieFavorite(movie.getId()));


        ivFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isFavorite) {

                    ivFavorite.setImageResource(R.drawable.ic_favorite_border_white_24dp);
                    isFavorite = false;
                    deleteFromDB(movie.getId());


                } else {

                    ivFavorite.setImageResource(R.drawable.ic_favorite_white_24dp);
                    isFavorite = true;
                    saveToDB(movie);

                }

            }
        });

        return rootView;
    }

    private void deleteFromDB(String movie_id) {

        context.getContentResolver().delete(FavoriteEntry.buildWeatherUri(movie_id), null, null);

    }

    private int isMovieFavorite(String movie_id) {

        Cursor c = context.getContentResolver().query(FavoriteEntry.buildWeatherUri(movie_id), null, null, null, null);

        if (c.getCount() > 0) {
            isFavorite = true;

            return R.drawable.ic_favorite_white_24dp;

        } else {
            isFavorite = false;

            return R.drawable.ic_favorite_border_white_24dp;

        }


    }

    private void saveToDB(Movie movie) {

        ContentValues values = new ContentValues();

        values.put(FavoriteEntry.COLUMN_MOVIE_ID, movie.getId());

        values.put(FavoriteEntry.COLUMN_BACKDROP_PATH, movie.getBackdrop_path());
        values.put(FavoriteEntry.COLUMN_ORIGINAL_TITLE, movie.getOriginal_title());
        values.put(FavoriteEntry.COLUMN_OVERVIEW, movie.getOverview());

        values.put(FavoriteEntry.COLUMN_POSTER_PATH, movie.getPoster_path());
        values.put(FavoriteEntry.COLUMN_RELEASE_DATE, movie.getRelease_date());
        values.put(FavoriteEntry.COLUMN_VOTE_AVERAGE, movie.getVote_average());

        context.getContentResolver().insert(FavoriteEntry.CONTENT_URI, values);

    }


    public void fatahTrailers(String movie_id) {

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(String.format(Globals.trials, movie_id, Globals.apiKey), new AsyncHttpResponseHandler() {


            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {

                try {
                    JSONObject object = new JSONObject(new String(responseBody));
                    List<Trailer> list = new ArrayList<Trailer>();

                    for (int i = 0; i < object.getJSONArray("results").length(); i++) {

                        list.add(new Trailer(object.getJSONArray("results").getJSONObject(i)));
                    }
                    myTAdapter = new TrailersAdapter(context, list);
                    rvTrailers.setAdapter(myTAdapter);

                    if (object.getJSONArray("results").length() == 0) {
                        rvTrailers.setVisibility(View.GONE);
                        tvTrailerTitle.setText("Trailers: No Trailers Available");
                    } else {
                        rvTrailers.setVisibility(View.VISIBLE);
                    }

                    if (list.size()>0) {
                        mForecast = "Watch Trailer at " +Globals.youtube_url+list.get(0).getKey();
                    }
                    if (mForecast != null) {
                        mShareActionProvider.setShareIntent(createShareForecastIntent());
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {

            }
        });

    }



    public void fetchReviews(String movie_id) {

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(String.format(Globals.reviews, movie_id, Globals.apiKey), new AsyncHttpResponseHandler() {


            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {

                try {

                    setReviews(new JSONObject(new String(responseBody)));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {

                Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();

            }
        });

    }


    //add reviews dynamically to the llReviews layout
    private void setReviews(JSONObject object) throws JSONException {


        if (llReviews.getChildCount() > 0)
            llReviews.removeAllViews();

        for (int i = 0; i < object.getJSONArray("results").length(); i++) {

            View child = LayoutInflater.from(context).inflate(R.layout.single_review, null);

            LinearLayout llTogle = (LinearLayout) child.findViewById(R.id.llsrReview);
            TextView tvAuthor = (TextView) child.findViewById(R.id.tvsrAuthor);
            final ExpandableTextView tvContent = (ExpandableTextView) child.findViewById(R.id.tvsrContent);
            final TextView tvTogle = (TextView) child.findViewById(R.id.tvsrTogle);

            tvAuthor.setText("" + object.getJSONArray("results").getJSONObject(i).getString("author"));
            tvContent.setText("" + object.getJSONArray("results").getJSONObject(i).getString("content"));

            tvContent.post(new Runnable() {
                @Override
                public void run() {

                    if (tvContent.getLineCount() < 5)
                        tvTogle.setVisibility(View.GONE);
                    else
                        tvTogle.setVisibility(View.VISIBLE);

                }
            });


            llTogle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    tvContent.toggle();
                    tvTogle.setText(tvContent.isExpanded() ? "Read More" : "Read Less");

                }
            });


            llReviews.addView(child);

        }

        if (object.getJSONArray("results").length() == 0) {
            llReviews.setVisibility(View.GONE);
            tvReviewTitle.setText("Reviews: No Reviews Available");
        } else {
            llReviews.setVisibility(View.VISIBLE);
        }


    }




    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_detail, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // If onLoadFinished happens before this, we can go ahead and set the share intent now.
        if (mForecast != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mForecast + MOVIE_SHARE_HASHTAG);
        return shareIntent;
    }



}