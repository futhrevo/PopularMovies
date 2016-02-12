package com.example.reku.popularmovies;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.ByteArrayOutputStream;

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
    public static final String SAVED_INST_KEY_MP_FRAG = "moviesMostPopular";
    public static final String SAVED_INST_KEY_UR_FRAG = "moviesUserRating";
    public static final String SELECTED_MOVIE_INTENT = "selectedMovieIntent";

    public static final int FETCH_METAINFO = 1;
    public static final int FETCH_VIDEOS = 2;
    public static final int FETCH_REVIEWS = 3;

    public static final String MOVIE_SHARE_HASHTAG = "#PopularMovies";
    public static String getSortStringPath(String type){
        if(type == "0")
            return "vote_average.desc";
        else{
            return "popularity.desc";
        }
    }

    public static byte[] drawableToByteArray(Drawable d) {

        if (d != null) {
            Bitmap imageBitmap = ((BitmapDrawable) d).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] byteData = baos.toByteArray();

            return byteData;
        } else
            return null;

    }


    public static Drawable byteToDrawable(byte[] data) {

        if (data == null)
            return null;
        else
            return new BitmapDrawable(BitmapFactory.decodeByteArray(data, 0, data.length));
    }
}
