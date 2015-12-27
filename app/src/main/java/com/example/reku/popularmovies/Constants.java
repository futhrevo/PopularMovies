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
    public static final int POSTER_WIDTH = 500;
    public static final int BACKDROP_WIDTH = 300;
    public static final String DEFAULT_POSTER_WIDTH = "w" + POSTER_WIDTH;
    public static final String DEFAULT_BACKDROP_WIDTH = "w" + BACKDROP_WIDTH;
    public static final String SAVED_INST_KEY_FRAG = "movies";

}
