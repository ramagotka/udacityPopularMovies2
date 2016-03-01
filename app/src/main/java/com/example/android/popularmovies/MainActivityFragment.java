package com.example.android.popularmovies;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
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
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.util.Log.*;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public static String KEY = "";
    private Movies[] movies = null;
    ImageArrayAdapter mMovieAdapter;
    Integer x = 300;
    Integer y = 450;
    SharedPreferences sharedpreferences;

    Callback mCallback;

    public MainActivityFragment() {
    }

    public interface Callback {
        public void onMovieSelected(Movies movie);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        mMovieAdapter = new ImageArrayAdapter(getActivity(), R.layout.item_movie, new ArrayList<Movies>());

        GridView gridView = (GridView) view.findViewById(R.id.gridView);
        gridView.setAdapter(mMovieAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((Callback) getActivity()).onMovieSelected(mMovieAdapter.getItem(position));
            }
        });

        sharedpreferences = getActivity().getPreferences(Context.MODE_PRIVATE);

        Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.spinner_data, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        String position = sharedpreferences.getString(getString(R.string.shered_pref_sort), "popularity");
        if (position.equals("votes")){
            spinner.setSelection(1);
        }
        else if (position.equals("favorites")){
            spinner.setSelection(2);
        }
        else {
            spinner.setSelection(0);
        }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                Log.e("Movies", "" + movies);
                if (position == 1){
                    editor.putString(getString(R.string.shered_pref_sort), "votes");
                    editor.commit();
                    sortMovies("votes");
                }
                else if (position == 0){
                    editor.putString(getString(R.string.shered_pref_sort), "popularity");
                    editor.commit();
                    sortMovies("popularity");
                }
                else if (position == 2){
                    editor.putString(getString(R.string.shered_pref_sort), "favorites");
                    editor.commit();
                    sortMovies("favorites");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }

    public void onStart(){
        super.onStart();
        updateMovies();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private class FetchMoviesTask extends AsyncTask<String, Void, Movies[]>{
        protected Movies[] doInBackground(String... a){
            Movies[] data = null;
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String MovieJson = null;

            try {

                Uri.Builder moviesUrl = new Uri.Builder();
                moviesUrl.scheme("http");
                moviesUrl.authority("api.themoviedb.org");
                moviesUrl.path("3/discover/movie");
                moviesUrl.appendQueryParameter("sort_by", a[0]);
                moviesUrl.appendQueryParameter("api_key", KEY);

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
                data = getMoviesPathsfromJson(MovieJson, 15);


            }
            catch (Exception e) {
                e("FetchMovieTask ", e.toString());
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

            return data;
        }

        @Override
        protected void onPostExecute(Movies[] movieDatas) {
            super.onPostExecute(movieDatas);
            movies = movieDatas;
            mMovieAdapter.clear();
            if (movies != null)
                mMovieAdapter.addAll(movies);
        }
    }

    private Movies[] getMoviesPathsfromJson(String Json, Integer num){
        JSONObject forecastJson = null;
        JSONArray movieArray = null;
        Movies[] tempMovies = new Movies[num];
        for(int i = 0; i < tempMovies.length;++i){
            tempMovies[i] = new Movies();
        }
        try {
            forecastJson = new JSONObject(Json);
            movieArray = forecastJson.getJSONArray("results");

        for (int i = 0; i < num; i++){
            JSONObject singlemovie = movieArray.getJSONObject(i);
            tempMovies[i].setOrginal_title(singlemovie.getString("original_title"));
            tempMovies[i].setUrl(singlemovie.getString("poster_path"));
            tempMovies[i].setOverview(singlemovie.getString("overview"));
            tempMovies[i].setVote_average(singlemovie.getString("vote_average"));
            tempMovies[i].setRelease_date(singlemovie.getString("release_date"));
            tempMovies[i].setPopularity(singlemovie.getString("popularity"));
            tempMovies[i].id = singlemovie.getString("id");
        }
        }
        catch (Exception e) {
            Log.e("getMoviesPathsfromJson ", e.toString());
            return null;
        }

        return tempMovies;
    }

    private void updateMovies(){
        sharedpreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        String sortBy = sharedpreferences.getString(getString(R.string.shered_pref_sort),"popularity");
        if (sortBy.equals("favorites")){
            sortMovies(sortBy);
        }
        else{
            new FetchMoviesTask().execute(sortBy + ".desc");
        }
    }

    private class ImageArrayAdapter extends ArrayAdapter<Movies>
    {
        public ImageArrayAdapter(Context context, int resource, List<Movies> objects) {
            super(context, resource, objects);
        }
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView view = (ImageView) convertView;
            if (view == null) {
                view = new ImageView(getActivity());
            }
            Movies movie = getItem(position);
            view.setAdjustViewBounds(true);

            if (sharedpreferences.getString(getString(R.string.shered_pref_sort), "popularity").equals("favorites")){
                MainActivity.picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w185/" + movie.getUrl()).resize(x, y).centerCrop().into(view);
            }
            else {
                Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w185/" + movie.getUrl()).resize(x, y).centerCrop().into(view);
            }

            return view;
        }
    }

    private void sortMovies(String sortBy){
        if (sortBy.equals("popularity")){
            new FetchMoviesTask().execute("popularity.desc");
//            Arrays.sort(movies, new Comparator<Movies>() {
//                @Override
//                public int compare(Movies lhs, Movies rhs) {
//                    if (lhs.getPopularity() == rhs.getPopularity()) {
//                        return 0;
//                    } else if (Double.parseDouble(lhs.getPopularity()) < Double.parseDouble(rhs.getPopularity())) {
//                        return 1;
//                    } else {
//                        return -1;
//                    }
//                }
//            });
//            mMovieAdapter.clear();
//            mMovieAdapter.addAll(movies);
        }
        else if (sortBy.equals("votes")){
            new FetchMoviesTask().execute("vote_average.desc");
//            Arrays.sort(movies, new Comparator<Movies>() {
//                @Override
//                public int compare(Movies lhs, Movies rhs) {
//                    if (lhs.getVote_average() == rhs.getVote_average()) {
//                        return 0;
//                    } else if (Double.parseDouble(lhs.getVote_average()) < Double.parseDouble(rhs.getVote_average())) {
//                        return 1;
//                    } else {
//                        return -1;
//                    }
//                }
//            });
//            mMovieAdapter.clear();
//            mMovieAdapter.addAll(movies);
        }
        else if (sortBy.equals("favorites")){
            Movies[] favoriteMovies = null;
            Cursor cursor = getActivity().getContentResolver().query(FavoritesTable.CONTENT_URI,null,null,null,null);
            if (cursor.moveToFirst()){
                List<Movies> rows = FavoritesTable.getRows(cursor,false);
                favoriteMovies = rows.toArray(new Movies[rows.size()]);
            }
            mMovieAdapter.clear();
            if (favoriteMovies != null) {
                mMovieAdapter.addAll(favoriteMovies);
            }
        }
    }

}
