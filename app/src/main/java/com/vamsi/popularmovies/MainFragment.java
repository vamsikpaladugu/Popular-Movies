package com.vamsi.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.vamsi.popularmovies.Data.MoviesContract.FavoriteEntry;
import com.vamsi.popularmovies.modal.Movie;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment {

    GridView moviesView;
    MoviesAdapter mMovieAdapter;
    ArrayList<Movie> movies;

    public int selecteditemPosition = 0;

    boolean mTwoPane = false;

    SharedPreferences sharedpreferences;

    Context context;

    public MainFragment() {
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        context = getActivity();
        sharedpreferences = getActivity().getSharedPreferences("mypref", Context.MODE_PRIVATE);

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to Listview.INVALID_POSITION,
        // so check for that before storing.
        if (selecteditemPosition != GridView.INVALID_POSITION) {
            outState.putInt("selecteditemPosition", selecteditemPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (savedInstanceState != null){
            selecteditemPosition = savedInstanceState.getInt("selecteditemPosition");
        }

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        moviesView = (GridView) view.findViewById(R.id.gvfmMovies);
        moviesView.setEmptyView(view.findViewById( R.id.tvfmEmpty ));

        movies = new ArrayList<>();



        moviesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                selecteditemPosition = i;

                ((OnMovieSelected) getActivity()).onMovieClicked(movies.get(i));

            }
        });

        return view;
    }


    public void setPanelType(boolean mTwoPane) {
        this.mTwoPane = mTwoPane;
        mMovieAdapter = new MoviesAdapter(getActivity(), movies, mTwoPane);
        moviesView.setAdapter(mMovieAdapter);

    }


    @Override
    public void onStart() {

        updateMovies();
        super.onStart();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_main, menu);

        MenuItem item = menu.findItem(sharedpreferences.getInt("sortby", R.id.popular));

        if (item != null)
            item.setChecked(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        SharedPreferences.Editor editor = sharedpreferences.edit();

        switch (item.getItemId()) {

            case R.id.popular:

                if (item.isChecked())
                    item.setChecked(false);
                else {
                    item.setChecked(true);
                    editor.putInt("sortby", item.getItemId());
                    editor.commit();
                }

                selecteditemPosition=0;
                updateMovies();
                return true;


            case R.id.top_rated:

                if (item.isChecked())
                    item.setChecked(false);
                else {
                    item.setChecked(true);
                    editor.putInt("sortby", item.getItemId());
                    editor.commit();
                }

                selecteditemPosition=0;
                updateMovies();
                return true;


            case R.id.favorite:

                if (item.isChecked())
                    item.setChecked(false);
                else {
                    item.setChecked(true);
                    editor.putInt("sortby", item.getItemId());
                    editor.commit();
                }

                selecteditemPosition=0;
                updateMovies();
                return true;


            default:
                return super.onOptionsItemSelected(item);

        }

    }


    //read saved movies from database
    public void getFavoriteMovies() {


        mMovieAdapter.clear();

        Cursor c = getActivity().getContentResolver().query(FavoriteEntry.CONTENT_URI, null, null, null, null);


        if (c.moveToFirst()) {

            ArrayList<Movie> movies = new ArrayList<>();

            do {

                Movie movie = new Movie();

                movie.setId(c.getString(c.getColumnIndex(FavoriteEntry.COLUMN_MOVIE_ID)));
                movie.setBackdrop_path(c.getString(c.getColumnIndex(FavoriteEntry.COLUMN_BACKDROP_PATH)));
                movie.setOriginal_title(c.getString(c.getColumnIndex(FavoriteEntry.COLUMN_ORIGINAL_TITLE)));

                movie.setOverview(c.getString(c.getColumnIndex(FavoriteEntry.COLUMN_OVERVIEW)));
                movie.setPoster_path(c.getString(c.getColumnIndex(FavoriteEntry.COLUMN_POSTER_PATH)));
                movie.setRelease_date(c.getString(c.getColumnIndex(FavoriteEntry.COLUMN_RELEASE_DATE)));

                movie.setVote_average(c.getDouble(c.getColumnIndex(FavoriteEntry.COLUMN_VOTE_AVERAGE)));

                movies.add(movie);

            } while (c.moveToNext());

            mMovieAdapter.addAll(movies);


        }

        initMovieSelection(selecteditemPosition);


    }


    private void updateMovies() {

        if (mMovieAdapter!=null){
            mMovieAdapter.clear();
        }

        String sortby = "";
        int id = sharedpreferences.getInt("sortby", R.id.popular);

        //for popular or top_rated movies we need to call loopj
        if (id == R.id.top_rated || id == R.id.popular) {

            if (id == R.id.popular)
                sortby = "popular";
            else if (id == R.id.top_rated)
                sortby = "top_rated";

            if (Globals.isOnline(context)) {

                fatchMovies(sortby);

            } else {

                Toast.makeText(getActivity(), "No Internet! try again", Toast.LENGTH_SHORT).show();

            }


            //for favorites movies we need to call ContentProvider
        } else {

            getFavoriteMovies();

        }
    }


    //fetch movies with loopj library
    public void fatchMovies(String sort_by) {

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(String.format(Globals.movies, sort_by, Globals.apiKey), new AsyncHttpResponseHandler() {


            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {

                try {

                    ArrayList<Movie> movies = new ArrayList<>();

                    JSONObject object = new JSONObject(new String(responseBody));

                    JSONArray array = object.getJSONArray("results");

                    for (int i = 0; i < array.length(); i++) {
                        movies.add(new Movie(array.getJSONObject(i)));
                    }

                    if (movies != null) {
                        mMovieAdapter.clear();
                        mMovieAdapter.addAll(movies);

                        initMovieSelection(selecteditemPosition);
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


    public void initMovieSelection(int selecteditemPosition) {

        if(selecteditemPosition != GridView.INVALID_POSITION){
             moviesView.smoothScrollToPosition(selecteditemPosition);

        }

        if (mTwoPane) {
            if (mMovieAdapter.getCount()>0) {
                ((OnMovieSelected) getActivity()).onMovieClicked(movies.get(selecteditemPosition));
                moviesView.setItemChecked(selecteditemPosition, true);
            }
        }

    }


    //interface for interacting with other fragments
    public interface OnMovieSelected {

        void onMovieClicked(Movie movie);

    }


}
