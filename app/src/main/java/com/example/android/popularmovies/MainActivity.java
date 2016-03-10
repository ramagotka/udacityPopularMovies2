package com.example.android.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.popularmovies.data.FavoritesTable;
import com.example.android.popularmovies.data.Movies;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import junit.framework.Test;

import java.net.PortUnreachableException;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.Callback{

    public boolean mTwoPane;
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    public static Picasso picasso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
  //      Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        OkHttpDownloader okHttpDownloader = new OkHttpDownloader(this, Integer.MAX_VALUE);
        picasso = new Picasso.Builder(this).downloader(okHttpDownloader).build();

        if (findViewById(R.id.detail_conteiner) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_conteiner, new DetailActivity2Fragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
//            getSupportActionBar().setElevation(0f);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onMovieSelected(Movies movie) {
        if (mTwoPane){
            Bundle args = new Bundle();
            args.putSerializable("Movies", movie);
            DetailActivity2Fragment fragment = new DetailActivity2Fragment();
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_conteiner, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        }
        else{
            Intent details = new Intent(this, DetailActivity2.class).putExtra("Movies", movie);
            startActivity(details);
        }
    }
}
