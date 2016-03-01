package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.android.popularmovies.data.FavoriteMovieContract;
import com.example.android.popularmovies.data.FavoritesTable;
import com.example.android.popularmovies.data.Movies;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static android.util.Log.e;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivity2Fragment extends Fragment {

    private Movies message;
    private TrailerArrayAdapter mVideoAdapter;
    private ReviewArrayAdapter mReviewAdpater;
    private ShareActionProvider mShareActionProvider;
    private String shareTrailer;

    private LinearLayout mReviewList;
    private LinearLayout mVideoList;

    public DetailActivity2Fragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail2, container, false);

        Intent intent = getActivity().getIntent();

        if (intent == null) {
            return rootView;
        }
        else
            {

                mVideoAdapter = new TrailerArrayAdapter(getActivity(), R.layout.item_video, "");
                mReviewAdpater = new ReviewArrayAdapter(getActivity(), R.layout.item_review);

                mReviewList = (LinearLayout) rootView.findViewById(R.id.review_layout);
                mVideoList = (LinearLayout) rootView.findViewById(R.id.video_layout);

                if (intent.getSerializableExtra("Movies") != null) {
                    message = (Movies) intent.getSerializableExtra("Movies");
                }
                else {
                    Log.e("DFragment", "2");
                    Bundle arguments = getArguments();
                    if (arguments != null) {
                        message = (Movies) arguments.getSerializable("Movies");
                    }
                    else {
                        return inflater.inflate(R.layout.blank, container, false);
                    }
                }

                new FetchVideosTask().execute();
                new FetchReviewTask().execute();

                TextView title = (TextView) rootView.findViewById(R.id.header);
                title.setText(message.getOrginal_title());
                ImageView image = (ImageView) rootView.findViewById(R.id.image_fragment);
                TextView relese_date = (TextView) rootView.findViewById(R.id.release_date);
                relese_date.setText(message.getRelease_date());
                TextView rating = (TextView) rootView.findViewById(R.id.user_rating);
                rating.setText(message.getVote_average() + "/10");
                TextView overview = (TextView) rootView.findViewById(R.id.overview);
                overview.setText(message.getOverview());

                final ImageButton buttonStar = (ImageButton) rootView.findViewById(R.id.button_star);
                Boolean isInDataBase = false;

                Cursor cursor = getActivity().getContentResolver().query(FavoritesTable.CONTENT_URI,null,null,null,null);
                if (cursor.moveToFirst()){
                    List<Movies> testRows = FavoritesTable.getRows(cursor,false);
                    for(Movies movie: testRows)
                    {
                        if (movie.original_title.equals(message.getOrginal_title()))
                        {
                            isInDataBase = true;
                        }
                    }
                }
                cursor.close();

                if (isInDataBase)
                {
                    buttonStar.setBackgroundResource(R.drawable.star);
                    MainActivity.picasso.load("http://image.tmdb.org/t/p/w185/" + message.getUrl()).into(image);
                }
                else {
                    Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w185/" + message.getUrl()).into(image);
                }

                buttonStar.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        Boolean isInDataBase2 = false;

                        Cursor cursor = getActivity().getContentResolver().query(FavoritesTable.CONTENT_URI, null, null, null, null);
                        if (cursor.moveToFirst()) {
                            List<Movies> testRows = FavoritesTable.getRows(cursor, false);
                            for (Movies movie : testRows) {
                                if (movie.original_title.equals(message.original_title)) {
                                    isInDataBase2 = true;
                                    getActivity().getContentResolver().delete(FavoritesTable.CONTENT_URI, FavoriteMovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE + " = ?", new String[]{message.original_title});
                                    buttonStar.setBackgroundResource(R.drawable.star_grey);
                                    break;
                                }
                            }
                        }
                        cursor.close();

                        if (!isInDataBase2) {
                            Movies movies = new Movies();
                            ContentValues movieValues = new ContentValues();

                            movieValues.put(FavoriteMovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, message.getOrginal_title());
                            movieValues.put(FavoriteMovieContract.MovieEntry.COLUMN_OVERVIEW, message.getOverview());
                            movieValues.put(FavoriteMovieContract.MovieEntry.COLUMN_PICTURE_URL, message.getUrl());
                            movieValues.put(FavoriteMovieContract.MovieEntry.COLUMN_POPULARITY, message.getPopularity());
                            movieValues.put(FavoriteMovieContract.MovieEntry.COLUMN_RELEASE_DATE, message.getRelease_date());
                            movieValues.put(FavoriteMovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, message.getVote_average());
                            movieValues.put(FavoriteMovieContract.MovieEntry.COLUMN_ID, message.id);

                            getActivity().getContentResolver().insert(FavoritesTable.CONTENT_URI, movieValues);

                            MainActivity.picasso.load("http://image.tmdb.org/t/p/w185/" + message.getUrl()).fetch();

                            buttonStar.setBackgroundResource(R.drawable.star);
                        }
                    }
                });
            }

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_detail_fragment, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);

        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);;

        if (shareTrailer != null)
            setShareIntent(createShareIntent());

    }

    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    private Intent createShareIntent(){
        Log.e("create", "tutaj!");
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/html");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareTrailer);
        return shareIntent;
    }

    private class FetchVideosTask extends AsyncTask<Void, Void, String[]> {

        @Override
        protected String[] doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String MovieJson = null;
            String[] result = null;

            try {
                Uri.Builder moviesUrl = new Uri.Builder();
                moviesUrl.scheme("http");
                moviesUrl.authority("api.themoviedb.org");
                moviesUrl.path("3/movie/" + message.id + "/videos");
                moviesUrl.appendQueryParameter("api_key", MainActivityFragment.KEY);

                URL url = new URL(moviesUrl.build().toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
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
                MovieJson = buffer.toString();
                result = setVideo(MovieJson);
            }
            catch (Exception e)
            {
                Log.e("Test FetchDetailsTask", e.toString());
            }
            finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        e("MainActivityFragment", "Error closing stream", e);
                    }
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            mVideoAdapter.clear();
            if (strings != null && strings.length > 0) {
                mVideoAdapter.addAll(strings);
                mVideoList.removeAllViews();
                shareTrailer = strings[0];
                for (int i = 0; i < mVideoAdapter.getCount(); i++){
                    mVideoList.addView(mVideoAdapter.getView(i,null,null));
                }
                setShareIntent(createShareIntent());
            }
        }
    }

    private class FetchReviewTask extends AsyncTask<Void, Void, String[][]> {

        @Override
        protected String[][] doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String MovieJson = null;
            String[][] result = null;

            try {
                Uri.Builder moviesUrl = new Uri.Builder();
                moviesUrl.scheme("http");
                moviesUrl.authority("api.themoviedb.org");
                moviesUrl.path("3/movie/" + message.id + "/reviews");
                moviesUrl.appendQueryParameter("api_key", MainActivityFragment.KEY);

                URL url = new URL(moviesUrl.build().toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
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
                MovieJson = buffer.toString();
                result = setReviews(MovieJson);
            }
            catch (Exception e)
            {
                Log.e("Test FetchDetailsTask", e.toString());
            }
            finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        e("MainActivityFragment", "Error closing stream", e);
                    }
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(String[][] strings) {
            mReviewAdpater.clear();
            if (strings != null) {
                mReviewAdpater.addAll(strings);
                mReviewList.removeAllViews();
                for (int i = 0; i < mReviewAdpater.getCount(); i++){
                    mReviewList.addView(mReviewAdpater.getView(i,null,null));
                }
            }
        }
    }

    private String[] setVideo(String Json){
        JSONObject videoJson = null;
        JSONArray videoArray = null;
        String[] array = null;
        try {
            videoJson = new JSONObject(Json);
            videoArray = videoJson.getJSONArray("results");

            array = new String[videoArray.length()];

            for (int i = 0; i < videoArray.length(); i++){
                array[i] = "https://www.youtube.com/watch?v=" + videoArray.getJSONObject(i).getString("key");
            }
        }
        catch (Exception e){
            Log.e("setVideo", e.toString());
        }
        return array;
    }

    private String[][] setReviews(String Json){
        JSONObject reviewJson = null;
        JSONArray reviewArray = null;
        String[][] array = null;
        try {
            reviewJson = new JSONObject(Json);
            reviewArray = reviewJson.getJSONArray("results");

            array = new String[reviewArray.length()][2];

            for (int i = 0; i < reviewArray.length(); i++){
                array[i][0] = reviewArray.getJSONObject(i).getString("content");
                array[i][1] = reviewArray.getJSONObject(i).getString("author");
            }
        }
        catch (Exception e){
            Log.e("setReviews", e.toString());
        }
        return array;
    }

    private class TrailerArrayAdapter extends ArrayAdapter<String>{

        public TrailerArrayAdapter(Context context, int resource, String string) {
            super(context, resource);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final String array = getItem(position);

            View rowView = convertView;

            if (rowView == null) {

                LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowView = vi.inflate(R.layout.item_video, null);
            }

            TextView text = (TextView) rowView.findViewById(R.id.video_item_text);
            ImageButton button = (ImageButton) rowView.findViewById(R.id.video_button);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(array)));
                }
            });

            text.setText("Trailer " + position); //TODO dodac 1
            text.setTextSize(30);

            return rowView;
        }
    }

    private class ReviewArrayAdapter extends ArrayAdapter<String[]>{

        public ReviewArrayAdapter(Context context, int resource) {
            super(context, resource);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final String[] array = getItem(position);
            View rowView = convertView;

            if (rowView == null) {

                LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowView = vi.inflate(R.layout.item_review, null);
            }

            TextView textAutor = (TextView) rowView.findViewById(R.id.item_review_autor);
            TextView textContent = (TextView) rowView.findViewById(R.id.item_review_content);

            textAutor.setText(array[1]);
            textContent.setText(array[0]);

            return rowView;

        }
    }
}
