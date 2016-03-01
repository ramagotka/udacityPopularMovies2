package com.example.android.popularmovies.data;

import android.media.Image;

import java.io.Serializable;

import ckm.simple.sql_provider.annotation.SimpleSQLColumn;
import ckm.simple.sql_provider.annotation.SimpleSQLTable;

/**
 * Created by hania on 24.02.16.
 */

@SimpleSQLTable(table = FavoriteMovieContract.MovieEntry.TABLE_NAME, provider = "MovieProvider")
public class Movies implements Serializable {
    @SimpleSQLColumn(FavoriteMovieContract.MovieEntry.COLUMN_VOTE_AVERAGE)
    public String vote_average;
    @SimpleSQLColumn(FavoriteMovieContract.MovieEntry.COLUMN_PICTURE_URL)
    public String picture_url;
    @SimpleSQLColumn(FavoriteMovieContract.MovieEntry.COLUMN_RELEASE_DATE)
    public String release_date;
    @SimpleSQLColumn(FavoriteMovieContract.MovieEntry.COLUMN_POPULARITY)
    public String popularity;
    @SimpleSQLColumn(FavoriteMovieContract.MovieEntry.COLUMN_OVERVIEW)
    public String overview;
    @SimpleSQLColumn(FavoriteMovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE)
    public String original_title;
    @SimpleSQLColumn(FavoriteMovieContract.MovieEntry.COLUMN_ID)
    public String id;

    public String getUrl() {
        return picture_url;
    }

    public void setUrl(String url) {
        this.picture_url = url;
    }

    public String getOrginal_title() {
        return original_title;
    }

    public void setOrginal_title(String orginal_title) {
        this.original_title = orginal_title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getVote_average() {
        return vote_average;
    }

    public void setVote_average(String vote_average) {
        this.vote_average = vote_average;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getPopularity() {
        return popularity;
    }

    public void setPopularity(String popularity) {
        this.popularity = popularity;
    }

}
