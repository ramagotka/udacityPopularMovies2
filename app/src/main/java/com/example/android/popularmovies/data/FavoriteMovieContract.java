package com.example.android.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.media.Image;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by hania on 24.02.16.
 */
public class FavoriteMovieContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.popularmovies.app";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH = "favorites";

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH;

        public static final String TABLE_NAME = "favorites";

        public static final String COLUMN_PICTURE_URL = "picture_url";
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_ID = "id";

        public static Uri buildFavoriteMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

}
