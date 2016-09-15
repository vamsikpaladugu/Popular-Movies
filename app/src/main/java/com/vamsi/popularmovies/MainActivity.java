package com.vamsi.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.vamsi.popularmovies.modal.Movie;

public class MainActivity extends AppCompatActivity implements MainFragment.OnMovieSelected {

    boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (findViewById(R.id.container) != null) {
            mTwoPane = true;
        } else {
            mTwoPane = false;
        }

        MainFragment mainFragment =  ((MainFragment) getSupportFragmentManager().findFragmentById(R.id.main_fragment));

        mainFragment.setPanelType(mTwoPane);

    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    @Override
    public void onMovieClicked(Movie movie) {

        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putParcelable("movie",movie);

            DetailsFragment fragment = new DetailsFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();

        } else {

            Intent intent = new Intent(this, DetailsActivity.class);
            intent.putExtra("movie",movie);
            startActivity(intent);

        }
    }
}
