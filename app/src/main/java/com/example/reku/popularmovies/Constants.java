package com.example.reku.popularmovies;

import android.util.Log;

/**
 * Created by rakeshkalyankar on 26/12/15.
 */
public final class Constants {

    public Constants(){
        Log.i("Constants", "Constants constructor");
    }

    public static final String IMG_BASE_URL = "http://image.tmdb.org/t/p/";
    public static final String API_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
    public static final String META_BASE_URL = "http://api.themoviedb.org/3/movie/";
    public static final int POSTER_WIDTH = 500;
    public static final int BACKDROP_WIDTH = 780;
    public static final String DEFAULT_POSTER_WIDTH = "w" + POSTER_WIDTH;
    public static final String DEFAULT_BACKDROP_WIDTH = "w" + BACKDROP_WIDTH;
    public static final String SELECTED_MOVIE_INTENT = "selectedMovieIntent";

    public static final String VALID_TILL = "validTill";

    public static final int FETCH_METAINFO = 1;
    public static final int FETCH_VIDEOS = 2;
    public static final int FETCH_REVIEWS = 3;

    public static final String MOVIE_SHARE_HASHTAG = "#PopularMovies";

    public static final int PREF_HIGH_RATED = 0;
    public static final int PREF_MOST_POPULAR = 1;
    public static final int PREF_FAVORITE = 2;

    public static final String DETAILFRAG_KEY = "mUri";
    public static final String DETAILFRAGMENT_TAG = "DFTAG";
}
