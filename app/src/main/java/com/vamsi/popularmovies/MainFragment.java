package com.vamsi.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment {

    GridView moviesView;
    MoviesAdapter mMovieAdapter;
    ArrayList<Movie> movies;

    SharedPreferences sharedpreferences;

    public MainFragment() {
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        sharedpreferences = getActivity().getSharedPreferences("mypref", Context.MODE_PRIVATE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        moviesView = (GridView) view.findViewById(R.id.gvMovies);

        movies = new ArrayList<>();

        mMovieAdapter = new MoviesAdapter(getActivity(),movies);

        moviesView.setAdapter(mMovieAdapter);

        moviesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //ImageView ivPoster = (ImageView) ((LinearLayout) view).getChildAt(0);

                Intent intent = new Intent(getContext(),Details.class);

                intent.putExtra("movie",movies.get(i));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) getContext(), view, "movie");
                    getContext().startActivity(intent, options.toBundle());

                } else {


                    getContext().startActivity(intent);
                }

            }
        });

        return view;
    }


    @Override
    public void onStart() {

        updateMovies();

        super.onStart();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_main, menu);
if (menu.findItem(sharedpreferences.getInt("sortby", R.id.popular)) !=null)
        menu.findItem(sharedpreferences.getInt("sortby", R.id.popular)).setChecked(true);

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
                    editor.putInt("sortby",item.getItemId());
                    editor.commit();
                    updateMovies();
                }
                return true;


            case R.id.top_rated:

                if (item.isChecked())
                    item.setChecked(false);
                else {
                    item.setChecked(true);
                    editor.putInt("sortby",item.getItemId());
                    editor.commit();
                    updateMovies();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }


    class FatchMovies extends AsyncTask<String,Void,ArrayList<Movie>> {

        String TAG = FatchMovies.class.getSimpleName();

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        //contains the json raw data
        String forecastJsonStr = null;

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {

            if (movies != null) {
                mMovieAdapter.clear();
                mMovieAdapter.addAll(movies);
            }
        }

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {

            String sortby = params[0]; //"top_rated";//"popular";//top_rated

            try {


                String API_PARAM = "api_key";

                //url for openweathermap.org

                Uri buildUri = Uri.parse(Globels.baseURl + sortby + "?").buildUpon()
                        .appendQueryParameter(API_PARAM, Globels.apiKey)
                        .build();

                URL url = new URL(buildUri.toString());


                Log.v(TAG, buildUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                //read inputstream into string

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if (inputStream == null) {
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }

                forecastJsonStr = buffer.toString();


                Log.v(TAG, forecastJsonStr);

                return getMovie(forecastJsonStr);

            } catch (IOException e) {
                Log.e(TAG, "Error ", e);

                return null;
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {

                if (urlConnection != null) {

                    urlConnection.disconnect();

                }
                if (reader != null) {

                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e(TAG, "Error closing stream", e);
                    }

                }


            }


        return null;

        }

        public ArrayList<Movie> getMovie(String json) throws JSONException {

            ArrayList<Movie> movies = new ArrayList<>();

            JSONObject object = new JSONObject(json);

            JSONArray array = object.getJSONArray("results");

            for (int i=0;i<array.length();i++){

                Movie movie = new Movie(array.getJSONObject(i));

                Log.v(TAG,""+array.getJSONObject(i).toString());

                movies.add(movie);

            }


            return movies;

        }

    }


    private void updateMovies(){

        String sortby = "";
        int id = sharedpreferences.getInt("sortby", R.id.popular);

        if (id==R.id.popular)
            sortby = "popular";
        else
            sortby = "top_rated";

        FatchMovies weatherTask = new FatchMovies();
        weatherTask.execute(sortby);

    }


}
